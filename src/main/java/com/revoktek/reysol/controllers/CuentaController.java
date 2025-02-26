package com.revoktek.reysol.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revoktek.reysol.core.constants.request.CuentaPath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.services.CuentaService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController

@RequestMapping(CuentaPath.CONTROLLER)
public class CuentaController {

    private CuentaService cuentaService;


    @GetMapping(CuentaPath.FIND_ALL_BY_FILTER)
    public ResponseEntity<?> findAllByFilter(@RequestParam(required = false) String busqueda) throws ServiceLayerException {
        return ResponseEntity.ok(cuentaService.findAllByFilter(busqueda));
    }

    @GetMapping(CuentaPath.FIND_BY_ID)
    public ResponseEntity<?> findById(@RequestParam Long idCliente) throws ServiceLayerException {
        return ResponseEntity.ok(cuentaService.findById(idCliente));
    }
   
    

}
