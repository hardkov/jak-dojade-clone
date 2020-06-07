from neo4j import GraphDatabase
from pandas import DataFrame

def parse_time(time_str):
    hour_minutes_seconds = time_str.split(":")
    
    hour = int(hour_minutes_seconds[0])
    minutes = int(hour_minutes_seconds[1])

    return hour, minutes


def get_shortest_path_raw(shortest_paths_data):
    prev_time = -1

    for i in range(len(shortest_paths_data)):
        time = shortest_paths_data[i]['time']

        if time < prev_time:
            return shortest_paths_data[:i]

        prev_time = time
    
    print("Shortest path is too big, change the query limit")


if __name__ == '__main__':
    # connecting to database
    uri = "bolt://localhost:7687"
    driver = GraphDatabase.driver(uri, auth=("neo4j", "admin"), encrypted=False)
    session = driver.session() 

    time = input("Podaj godzine")
    time_hour, time_minute = parse_time(time)

    start_stop_name = input("Podaj nazwe przystanku startowego: \n")

    available_start_stops = session.run(f"""match 
        (s: Stop {{stop_name: '{start_stop_name}'}})
        return distinct s.stop_id""").data()

    for i, start_stop in enumerate(available_start_stops):
        print(f'{i} - {start_stop["s.stop_id"]}')

    start_stop_idx = int(input("Wybierz konkretny przystanek: \n"))

    start_stop_id = available_start_stops[start_stop_idx]['s.stop_id']

    end_stop_name = input("Podaj nazwe przystanku koncowego: \n")

    start_node_data = session.run(f"""
        match (start: Stop {{ stop_id: "{start_stop_id}" }}) 
        where start.arrival_hour > {time_hour} or (start.arrival_hour = {time_hour} and start.arrival_minute >= {time_minute}) 
        return start.line_stop_id
        order by start.arrival_hour, start.arrival_minute 
        limit 1""").data()
    
    start_line_stop_id = start_node_data[0]['start.line_stop_id']

    shortest_path_cipher = ( 
        f"""
        MATCH (start:Stop {{line_stop_id: "{start_line_stop_id}"}}) 
        CALL {{
            MATCH
            (end:Stop {{stop_name: "{end_stop_name}"}})
            WHERE end.arrival_hour > 10 or (end.arrival_hour = 10 and end.arrival_minute >= 45) 
            RETURN end 
            ORDER BY end.arrival_hour, end.arrival_minute 
        }}
        
        CALL gds.alpha.shortestPath.stream({{
        nodeProjection: 'Stop',
        relationshipProjection: {{
            TIME: {{
            type: 'TIME',
            properties: 'minutes',
            orientation: 'NATURAL'
            }}
        }},
        startNode: start,
        endNode: end,
        relationshipWeightProperty: 'minutes'
        }})

        YIELD nodeId, cost
        RETURN gds.util.asNode(nodeId).stop_name AS stop, gds.util.asNode(nodeId).line_name AS line, cost as time
        LIMIT 100
        """
    )

    shortest_paths_data =  session.run(shortest_path_cipher).data()

    shortest_path_raw = get_shortest_path_raw(shortest_paths_data)

    DataFrame(shortest_path_raw)

    driver.close()