package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.persistence.repositories.MetodoPagoRepository;
import com.revoktek.reysol.services.MetodoPagoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private MetodoPagoRepository metodoPagoRepository;

}
