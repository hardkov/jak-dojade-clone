package com.jakdojade.JakDojade.service;

import com.jakdojade.JakDojade.model.Stop;
import com.jakdojade.JakDojade.repository.StopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PrzystanekService {
    @Autowired
    StopRepository przystanekRepository;

    public Collection<Stop> getAll() {
        return przystanekRepository.getAll();
    }


}
