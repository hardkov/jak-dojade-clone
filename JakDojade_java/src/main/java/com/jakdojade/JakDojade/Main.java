package com.jakdojade.JakDojade;
import org.neo4j.driver.*;
import scala.Int;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import static org.neo4j.driver.Values.parameters;

public class Main implements AutoCloseable
{
    private final Driver driver;

    public Main( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void getNodes(Integer h, Integer m, Integer start, Integer end)
    {
        try ( Session session = driver.session() )
        {
            String query = "CALL {\n" +
                    "  MATCH(start:Przystanek {przystanek: " + start + "})\n" +
                    "  WHERE start.godzina > time({hour: " + h + ", minute: " + m + "})\n" +
                    "  RETURN start\n" +
                    "  ORDER BY start.godzina\n" +
                    "  LIMIT 1\n" +
                    "}\n" +
                    "MATCH (start:Przystanek), (end:Przystanek {przystanek: " + end + "})\n" +
                    "CALL gds.alpha.shortestPath.stream({\n" +
                    "  nodeProjection: 'Przystanek',\n" +
                    "  relationshipProjection: {\n" +
                    "    TIME: {\n" +
                    "      type: 'TIME',\n" +
                    "      properties: 'minutes',\n" +
                    "      orientation: 'NATURAL'\n" +
                    "    }\n" +
                    "  },\n" +
                    "  startNode: start,\n" +
                    "  endNode: end,\n" +
                    "  relationshipWeightProperty: 'minutes'\n" +
                    "})\n" +
                    "YIELD nodeId, cost\n" +
                    "RETURN gds.util.asNode(nodeId).przystanek AS NrPrzystanku, gds.util.asNode(nodeId).linia AS LiniaAutobusowa, cost as CzasDojazdu";
            String results = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run(query);
                    StringBuilder res = new StringBuilder();
                    Boolean tmp = true;
                    while(result.hasNext()) {
                        List<Value> vals = result.next().values();
                        if (Math.abs(vals.get(2).asDouble()) < 0.5) if (tmp) tmp = false; else break;
                        tmp = false;
                        Integer przystanek = vals.get(0).asInt();
                        Integer linia = vals.get(1).asInt();
                        Double minutes = vals.get(2).asDouble();
                        res.append("Przystanek: " + przystanek + "\tLinia: " + linia + "\tIlość minut: " + minutes.intValue() + "\n");
                    }
                    return res.toString();
                    //return result.list().toString();
                }
            } );
            System.out.println(results);
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( Main greeter = new Main( "bolt://localhost:7687", "neo4j", "test" ) )
        {
            while(true) {
                Scanner in = new Scanner(System.in);
                System.out.println("Podaj godzine:");
                int h = in.nextInt();
                System.out.println("Podaj minuty:");
                int m = in.nextInt();
                System.out.println("Podaj przystanek startowy:");
                int start = in.nextInt();
                System.out.println("Podaj przystanek docelowy:");
                int end = in.nextInt();
                greeter.getNodes(h, m, start, end);
            }
        }
    }
}