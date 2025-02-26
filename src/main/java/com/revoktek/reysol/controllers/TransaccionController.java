package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.TransaccionPath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.services.TransaccionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(TransaccionPath.CONTROLLER)
public class TransaccionController {

    private TransaccionService transaccionService;

    @GetMapping(TransaccionPath.FIND_BY_ID)
    public ResponseEntity<?> findById(@RequestParam Long idTransaccion) throws ServiceLayerException {
        return ResponseEntity.ok(transaccionService.findById(idTransaccion));
    }


}
