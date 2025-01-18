package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.InventarioPath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.InventarioDTO;
import com.revoktek.reysol.dto.filter.FilterDTO;
import com.revoktek.reysol.services.InventarioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(InventarioPath.CONTROLLER)
public class InventarioController {

    private InventarioService inventarioService;

    @PostMapping(InventarioPath.FIND_ALL_BY_FILTER)
    public ResponseEntity<?> findAllByFilter(@RequestBody(required = false) FilterDTO filterDTO) throws ServiceLayerException {
        return ResponseEntity.ok(inventarioService.findAllByFilter(filterDTO));
    }

    @PostMapping(InventarioPath.FIND_ALL_MOVEMENTS)
    public ResponseEntity<?> findAllMovements(@RequestBody(required = false)  FilterDTO filterDTO) throws ServiceLayerException {
        return ResponseEntity.ok(inventarioService.findAllMovements(filterDTO));
    }

    @PostMapping(InventarioPath.SAVE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody InventarioDTO inventarioDTO) throws ServiceLayerException {
        inventarioService.save(inventarioDTO, token);
    }

}
