package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.TemplatePath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.services.TemplateService;
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
@RequestMapping(TemplatePath.CONTROLLER)
public class TemplateController {

    private TemplateService templateService;

    @GetMapping(TemplatePath.FIND_BY_NAME)
    public ResponseEntity<?> getTicket(@RequestParam Long idPedido) throws ServiceLayerException {
        return ResponseEntity.ok(templateService.getTicket(idPedido));
    }




}
