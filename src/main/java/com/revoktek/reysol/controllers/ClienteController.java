package com.revoktek.reysol.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.revoktek.reysol.core.constants.request.ClientePath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.ClienteDTO;
import com.revoktek.reysol.services.ClienteService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ClientePath.CONTROLLER)
public class ClienteController {

    private ClienteService clienteService;


    @GetMapping(ClientePath.FIND_ALL_BY_FILTER)
    public ResponseEntity<?> findAllByFilter(@RequestParam(required = false) String busqueda, @RequestParam(required = false) Integer estatus ) throws ServiceLayerException {
        return ResponseEntity.ok(clienteService.findAllByFilter(busqueda, estatus));
    }

    @GetMapping(ClientePath.FIND_BY_ID)
    public ResponseEntity<?> findById(@RequestParam Long idCliente) throws ServiceLayerException {
        return ResponseEntity.ok(clienteService.findById(idCliente));
    }

    @PostMapping(ClientePath.CHANGE_ESTATUS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeEstatus(@RequestBody ClienteDTO clienteDTO) throws ServiceLayerException {
        clienteService.changeEstatus(clienteDTO.getIdCliente());
    }

    @PostMapping(ClientePath.SAVE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(@RequestBody ClienteDTO save) throws ServiceLayerException {
       clienteService.save(save);
    }

    @PostMapping(ClientePath.UPDATE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody ClienteDTO save) throws ServiceLayerException {
       clienteService.update(save);
    }

}
