import csv



def sort_key(list_elem):
    arrival_time = list_elem[1]
    arrival_hour, arrival_minute = parse_time(arrival_time)

    return arrival_hour * 60 + arrival_minute


def parse_time(time_str):
    hour_minutes_seconds = time_str.split(":")
    
    hour = int(hour_minutes_seconds[0])
    minutes = int(hour_minutes_seconds[1])

    return hour, minutes


def time_diff(prev_arrival_time, next_arrival_time):
    prev_hour, prev_minutes = parse_time(prev_arrival_time)
    next_hour, next_minutes = parse_time(next_arrival_time)
    
    # if < 0 then make apropriate ternary instruction and reverse relation
    return (next_hour - prev_hour) * 60 + (next_minutes - prev_minutes)


def load_stops(filename):
    stops = {}

    with open(filename, 'r', encoding='utf-8') as csv_file:
        csv_stops = csv.reader(csv_file, delimiter=',')
        next(csv_stops)

        for row in csv_stops:
            stops[row[0]] = row[2]

    return stops


def make_nodes_file(nodes_filename, stops_filename, stops):
    stop_lines_dict = {}
    edges = []

    with open(nodes_filename, 'w+') as import_file:
        import_file_writer = csv.writer(import_file, delimiter=',')
        import_file_writer.writerow(['line_stop_id', 'stop_id', 'stop_name', 'line_id', 'arrival_hour', 'arrival_minute'])

        with open(stops_filename) as csv_file:
            csv_stop_times = csv.reader(csv_file, delimiter=',')
            next(csv_stop_times)

            prev_line_id = None
            prev_line_stop_id = None
            prev_arrival_time = None

            for row in csv_stop_times:
                if 'service_1' in row[0]:
                    line_id = row[0]
                    stop_id = row[3]
                    stop_name = stops.get(row[3])
                    arrival_time = row[1]
                    arrival_hour, arrival_minute = parse_time(arrival_time)
                    line_stop_id = line_id + '-' + stop_id                    

                    import_file_writer.writerow([
                        line_stop_id,
                        stop_id,
                        stop_name,
                        line_id,
                        arrival_hour,
                        arrival_minute
                    ])

                    if prev_line_id and line_id == prev_line_id:
                        edge = {}

                        edge['prev_node'] = prev_line_stop_id
                        edge['next_node'] = line_stop_id
                        edge['minutes'] = time_diff(prev_arrival_time, arrival_time)

                        edges.append(edge)

                    if stop_lines_dict.get(stop_id):
                        stop_lines_dict[stop_id].append((line_stop_id, arrival_time))
                    else:
                        stop_lines_dict[stop_id] = []
                        stop_lines_dict[stop_id].append((line_stop_id, arrival_time))


                    prev_arrival_time = arrival_time
                    prev_line_id = line_id
                    prev_line_stop_id = line_stop_id
    
    return stop_lines_dict, edges 


def make_edges_file(filename, edges):
    with open(filename, 'w+') as import_file:
        import_file_writer2 = csv.writer(import_file, delimiter=',')
        import_file_writer2.writerow(['prev_node', 'next_node', 'minutes', ])

        for edge in edges:
            import_file_writer2.writerow([
                edge['prev_node'],
                edge['next_node'],
                edge['minutes']
            ])


def append_additional_edges(stop_lines_dict, edges, edge_filename):
    with open(edge_filename, 'a+') as import_file:
        import_file_writer = csv.writer(import_file, delimiter=',')
            
        for stop_lines_item in stop_lines_dict.items():
            stop_lines = stop_lines_item[1]

            stop_lines.sort(key=sort_key)

            prev_arrival_time = None
            prev_line_stop_id = None
        
            for i in range(len(stop_lines)):
                next_arrival_time = stop_lines[i][1]
                next_line_stop_id = stop_lines[i][0]
                
                if prev_arrival_time:
                    minutes = time_diff(prev_arrival_time, next_arrival_time)
                    
                    if minutes < 0:
                        print('error')

                    import_file_writer.writerow([
                        prev_line_stop_id,
                        next_line_stop_id,
                        minutes
                    ])

                prev_arrival_time = next_arrival_time
                prev_line_stop_id = next_line_stop_id
        

if __name__ == '__main__':
    # load stops ids with names
    stops = load_stops('data/stops.txt')

    # load bus arrival times at certain bus stop and make apropriate data structures
    stop_lines_dict, edges = make_nodes_file(
        'data/import_nodes.txt', 'data/stop_times.txt', stops)

    # print edges into csv file
    make_edges_file('data/import_edges.txt', edges)

    # edged between nodes which have the same stop
    append_additional_edges(stop_lines_dict, edges, 'data/import_edges.txt')

    