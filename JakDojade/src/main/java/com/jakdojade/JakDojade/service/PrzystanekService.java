package com.jakdojade.JakDojade.service;

import com.jakdojade.JakDojade.model.Przystanek;
import com.jakdojade.JakDojade.repository.PrzystanekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PrzystanekService {
    @Autowired
    PrzystanekRepository przystanekRepository;

    public Collection<Przystanek> getAll() {
        return przystanekRepository.getAll();
    }


}
