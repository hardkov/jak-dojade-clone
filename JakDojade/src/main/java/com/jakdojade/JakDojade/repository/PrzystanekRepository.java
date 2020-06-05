package com.jakdojade.JakDojade.repository;

import com.jakdojade.JakDojade.model.Przystanek;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;

public interface PrzystanekRepository extends Neo4jRepository<Przystanek, Long> {

    @Query("MATCH (n:Przystanek) RETURN n LIMIT 25")
    Collection<Przystanek> getAll();
}
