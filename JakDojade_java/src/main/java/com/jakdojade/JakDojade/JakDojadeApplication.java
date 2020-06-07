package com.jakdojade.JakDojade;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;


@SpringBootApplication
public class JakDojadeApplication {
	public static void main(String[] args) {
		SpringApplication.run(JakDojadeApplication.class, args);
	}
}
