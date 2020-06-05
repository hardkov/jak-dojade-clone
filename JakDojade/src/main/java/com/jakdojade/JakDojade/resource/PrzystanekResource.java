package com.jakdojade.JakDojade.resource;

import com.jakdojade.JakDojade.model.Przystanek;
import com.jakdojade.JakDojade.service.PrzystanekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/rest/neo4j/przystanek")
public class PrzystanekResource {

    @Autowired
    PrzystanekService przystanekService;

    @GetMapping
    public Collection<Przystanek> getAll() {
        return przystanekService.getAll();
    }
}
