package com.jakdojade.JakDojade.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.OffsetTime;
import java.util.List;

@NodeEntity
public class Przystanek {

    @Id
    @GeneratedValue
    private Long id;
    private OffsetTime godzina;
    private Integer linia; //String jezeli linie sa typu 29b, a nie same numery
    private Integer przystanek; //String jezeli przystanki maja nazwy zamiast numerow

    //@Relationship (type = "TIME", direction = Relationship.INCOMING)
    //private List<Przystanek> przystanekList;

    public Przystanek() {

    }

    //public List<Przystanek> getPrzystanekList() {
    //    return przystanekList;
    //}

    public Long getId() {
        return id;
    }

    public OffsetTime getGodzina() {
        return godzina;
    }

    public Integer getLinia() {
        return linia;
    }

    public Integer getPrzystanek() {
        return przystanek;
    }
}
