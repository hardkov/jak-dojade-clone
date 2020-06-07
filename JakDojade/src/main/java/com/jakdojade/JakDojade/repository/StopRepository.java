package com.jakdojade.JakDojade.repository;

import com.jakdojade.JakDojade.model.Stop;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;

public interface StopRepository extends Neo4jRepository<Stop, Long> {

    @Query("MATCH (n:Stop) RETURN n")
    Collection<Stop> getAll();
}
