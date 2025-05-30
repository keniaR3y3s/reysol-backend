package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.CataloguePath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.services.CatalogueService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(CataloguePath.CONTROLLER)
public class CatalogoController {

    private CatalogueService iCatalogueService;

    @GetMapping(CataloguePath.FIND_ALL)
    public ResponseEntity<?> findAll(@PathVariable String entityName) throws ServiceLayerException {
        return ResponseEntity.ok(iCatalogueService.findAll(entityName));
    }

    @GetMapping(CataloguePath.FIND_ALL_ACTIVE)
    public ResponseEntity<?> findAllActive(@PathVariable String entityName) throws ServiceLayerException {
        return ResponseEntity.ok(iCatalogueService.findAllActive(entityName));
    }

    @PutMapping(CataloguePath.SAVE)
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@PathVariable String entityName, @RequestBody Object entity) throws ServiceLayerException {
        iCatalogueService.save(entityName, entity);
    }


    @GetMapping(CataloguePath.FIND_BY_ID)
    public ResponseEntity<?> findById(@PathVariable String entityName, @PathVariable String id) throws ServiceLayerException {
        return ResponseEntity.ok(iCatalogueService.findByID(entityName, id));
    }


    @PostMapping(CataloguePath.UPDATE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String entityName, @PathVariable String id, @RequestBody Object entity) throws ServiceLayerException {
        iCatalogueService.update(entityName, id, entity);
    }

    @PostMapping(CataloguePath.CHANGE_STATUS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeStatus(@PathVariable String entityName, @PathVariable String id, @RequestBody Object entity) throws ServiceLayerException {
        iCatalogueService.changeStatus(entityName, id, entity);
    }


}
