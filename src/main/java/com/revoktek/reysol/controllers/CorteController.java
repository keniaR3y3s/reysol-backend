package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.CortePath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.CorteDTO;
import com.revoktek.reysol.services.CorteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(CortePath.CONTROLLER)
public class CorteController {

    private CorteService corteService;


    @GetMapping(CortePath.FIND_BY_ID)
    public ResponseEntity<?> findById(@RequestParam Integer idTipoCorte) throws ServiceLayerException {
        return ResponseEntity.ok(corteService.findById(idTipoCorte));
    }

    @GetMapping(CortePath.CALCULATE)
    public ResponseEntity<?>  calculate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam Integer idTipoCorte, @RequestParam Integer cantidad, @RequestParam Boolean almacen) throws ServiceLayerException {
        return ResponseEntity.ok(corteService.calculate(idTipoCorte, cantidad, almacen, token));
    }

    @PostMapping(CortePath.SAVE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(@RequestBody CorteDTO corteDTO) throws ServiceLayerException {
        corteService.save(corteDTO);
    }



}
