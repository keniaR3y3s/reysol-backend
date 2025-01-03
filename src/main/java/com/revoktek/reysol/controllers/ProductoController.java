package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.ProductoPath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.services.ProductoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ProductoPath.CONTROLLER)
public class ProductoController {

    private ProductoService productoService;

    @GetMapping(ProductoPath.FIND_ALL)
    public ResponseEntity<?> findAll() throws ServiceLayerException {
        return ResponseEntity.ok(productoService.findAll());
    }


}
