# JakDojade

Mini project for Database course at AGH UST - DataBases 2020  
Technologies used:
* Neo4j
* Python
* Cypher (query language)

## Neo4j - model danych i sposób ich przechowywania
Żeby w prosty sposób móc przechowywać informację o przystankach, a także móc w łatwy sposób znajdywać najkrótsze połączenia między przystankami postanowiliśmy skorzystać z poniższego modelu bazy:
```java
private Long id; //ID pojedynczego wierzchołka
private Integer arrival_hour; //godzina przyjazdu konkretnej linii na przystanek
private Integer arrival_minute; //minuta przyjazdu konkretnej linii na przystanek
private String stop_name; //nazwa przystanku
private String line_name; //nazwa linii
private String line_stop_id; //ID pozwalające na jednoznaczną identyfikację przystanku i linii 
                             //index założony na to pole, jest unique - służy do łatwego rozróżnienia node'ów
private String line_id; //ID linii
private String stop_id; //ID przystanku
```
Pojedynczy przystanek występuje zatem wiele razy - dla każdej linii i godziny dotarcia autobusu mamy zatem nowy node.  
Przystanki połączone są ze sobą relacją TIME, której wartość jest odległością w minutach jakiej potrzebujemy na przebycie drogi określoną linią. 
 
### Powyższy sposób zapisu ma swoje wady i zalety:  
**Wadą** jest redundancja danych - jak łatwo zauważyć zamiast 2997 unikatowych przystanków w bazie do Krakowa utworzonych jest ich 180 835, czyli 60 razy więcej  
**Zaletą**, która wynika z wady jest łatwość znajdywania najkrótszej drogi dla poszczególnych przystanków, ponieważ możemy zastosować istniejące funkcje dla neo4j w łatwy sposób do znalezienia najkrótszej drogi między 2 dowolnymi przystankami np.
* gds.alpha.shortestPath.stream()
* apoc.algo.dijkstra()

Upraszcza to znacznie problem znalezienia najkrótszej ścieżki 

## Neo4j - wygenerowanie bazy przystanków
Utworzenie bazy przystanków z Krakowa wymagało [TODO]

Na podstawie uzyskanej w ten sposób listy przystanków i połączeń z użyciem skryptu *real_script.py* otrzymujemy 2 pliki 
* data/import_edges.txt
* data/import_nodes.txt

Na podstawie, których tworzymy pełną bazę danych przystanków dla Krakowa

**Utworzenie wszystkich przystanków:**
```cypher
load csv with headers from "file:///import_nodes.txt" as csvline 
create(s: Stop {
line_stop_id: csvline.line_stop_id, 
stop_id: csvline.stop_id,
stop_name: csvline.stop_name,
line_id: csvline.line_id,
line_name: csvline.line_name, 
arrival_hour: toInteger(csvline.arrival_hour),
arrival_minute: toInteger(csvline.arrival_minute)
})
```
**Utworzenie indeksu na line_stop_id w celu przyspieszenia wyszukiwań:**
```cypher 
create index line_stop_id_index for (n: Stop) on (n.line_stop_id)
```
**Utworzenie wszystkich połączeń:**
```cypher 
load csv with headers from "file:///import_edges.txt" as csvline
match (prev: Stop {line_stop_id: csvline.prev_node}), (next: Stop {line_stop_id: csvline.next_node})
create (prev)-[:TIME {minutes: toInteger(csvline.minutes)}]->(next)
```

## Wygląd bazy w neo4j
**Dany przystanek w bazie:**  
![Przystanek Szwedzka](./ReadMeImg/szwedzka.png)

**Dana linia w bazie:**  
![Przykładowa linia](./ReadMeImg/linia.png)

**Jak możemy zauważyć - każdy przystanek sprowadza się do 2 akcji:**
* Poczekanie na następny autobus - ten sam przystanek - następna godzina
* Pozostanie w linii i podjechanie na następny przystanek


## Przykładowe query z wynikami
```cypher
MATCH (start:Stop {line_stop_id: "block_1_trip_11_service_1-stop_422_59101"}), 
(end:Stop {line_stop_id: "block_1_trip_11_service_1-stop_1201_281201"})
CALL gds.alpha.shortestPath.stream({
  nodeProjection: 'Stop',
  relationshipProjection: {
    TIME: {
      type: 'TIME',
      properties: 'minutes',
      orientation: 'NATURAL'
    }
  },
  startNode: start,
  endNode: end,
  relationshipWeightProperty: 'minutes'
})
YIELD nodeId, cost
RETURN gds.util.asNode(nodeId).stop_name AS stop, gds.util.asNode(nodeId).line_name AS line, cost as time
```
![Query result 1](./ReadMeImg/query1.png)  
![Query result 2](./ReadMeImg/query2.png)

```cypher
query v2 TODO

```

## Skrypt python korzystający z bazy do wyszukiwania połączenia

## Przykładowe użycie skryptu pythonowego

## Written by:
- [@what-ewer](https://github.com/what-ewer)
- [@hardkov](https://github.com/hardkov)

