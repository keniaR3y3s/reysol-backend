package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.EmpleadoPath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.EmpleadoDTO;
import com.revoktek.reysol.services.EmpleadoService;
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
@RequestMapping(EmpleadoPath.CONTROLLER)
public class EmpleadoController {

    private EmpleadoService empleadoService;

    @GetMapping(EmpleadoPath.FIND_ALL_BY_ROL)
    public ResponseEntity<?> findAllByRol(@RequestParam Long idRol) throws ServiceLayerException {
        return ResponseEntity.ok(empleadoService.findAllByRol(idRol));
    }
    @GetMapping(EmpleadoPath.FIND_ALL_BY_FILTER)
    public ResponseEntity<?> findAllByFilter(@RequestParam String busqueda) throws ServiceLayerException {
        return ResponseEntity.ok(empleadoService.findAllByFilter(busqueda));
    }
    @GetMapping(EmpleadoPath.FIND_BY_ID)
    public ResponseEntity<?> findById(@RequestParam Long idEmpleado) throws ServiceLayerException {
        return ResponseEntity.ok(empleadoService.findByID(idEmpleado));
    }

    @PostMapping(EmpleadoPath.SAVE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(@RequestBody EmpleadoDTO empleadoDTO) throws ServiceLayerException {
       empleadoService.save(empleadoDTO);
    }

    @PostMapping(EmpleadoPath.UPDATE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody EmpleadoDTO empleadoDTO) throws ServiceLayerException {
        empleadoService.update(empleadoDTO);
    }


}
