package com.jakdojade.JakDojade.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.OffsetTime;
import java.util.List;

@NodeEntity
public class Stop {

    @Id
    @GeneratedValue
    private Long id;
    private Integer arrival_hour;
    private Integer arrival_minute;
    private String stop_name;
    private String line_name;
    private String line_stop_id;
    private String line_id;
    private String stop_id;

    //@Relationship (type = "TIME", direction = Relationship.INCOMING)
    //private List<Stop> stops;

    public Stop() {

    }
    public Long getId() {
        return id;
    }

    public Integer getArrival_hour() {
        return arrival_hour;
    }

    public Integer getArrival_minute() {
        return arrival_minute;
    }

    public String getLine_name() {
        return line_name;
    }

    public String getLine_stop_id() {
        return line_stop_id;
    }

    public String getLine_id() {
        return line_id;
    }

    public String getStop_id() {
        return stop_id;
    }

    public String getStop_name() { return stop_name; }
}
