package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.ClientePath;
import com.revoktek.reysol.core.constants.request.ProductoPath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.PrecioClienteDTO;
import com.revoktek.reysol.services.ProductoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @GetMapping(ProductoPath.FIND_ALL_BY_CLIENT)
    public ResponseEntity<?> findAllByClient(@RequestParam Long idCliente) throws ServiceLayerException {
        return ResponseEntity.ok(productoService.findAllByClient(idCliente));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(ProductoPath.SAVE_CLIENT_PRICE)
    public void saveClientPrice(@RequestBody PrecioClienteDTO precioClienteDTO) throws ServiceLayerException {
        productoService.saveClientPrice(precioClienteDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(ProductoPath.DELETE_CLIENT_PRICE)
    public void deleteClientPrice(@RequestBody PrecioClienteDTO precioClienteDTO) throws ServiceLayerException {
        productoService.deleteClientPrice(precioClienteDTO.getIdPrecioCliente());
    }


}
