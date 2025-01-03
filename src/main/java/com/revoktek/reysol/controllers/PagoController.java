package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.PagoPath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.PagoDTO;
import com.revoktek.reysol.services.PagoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(PagoPath.CONTROLLER)
public class PagoController {

    private PagoService pagoService;

    @PostMapping(PagoPath.SAVE_PAYMENT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void savePayment(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PagoDTO pagoDTO) throws ServiceLayerException {
        pagoService.savePayment(pagoDTO, token);
    }

}
