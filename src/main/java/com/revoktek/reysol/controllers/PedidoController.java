package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.constants.request.PedidoPath;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.dto.filter.FilterPedidoDTO;
import com.revoktek.reysol.services.PedidoService;
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
@RequestMapping(PedidoPath.CONTROLLER)
public class PedidoController {

    private PedidoService pedidoService;

    @PostMapping(PedidoPath.FIND_ALL_BY_FILTER)
    public ResponseEntity<?> findAllByFilter(@RequestBody FilterPedidoDTO filterPedido) throws ServiceLayerException {
        return ResponseEntity.ok(pedidoService.findAllByFilter(filterPedido));
    }

    @GetMapping(PedidoPath.FIND_BY_ID)
    public ResponseEntity<?> findById(@RequestParam Long id) throws ServiceLayerException {
        return ResponseEntity.ok(pedidoService.findByID(id));
    }

    @PostMapping(PedidoPath.SAVE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PedidoDTO pedidoDTO) throws ServiceLayerException {
        pedidoService.save(pedidoDTO, token);
    }

    @PostMapping(PedidoPath.SAVE_DISPATCH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveDispatch(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PedidoDTO pedidoDTO) throws ServiceLayerException {
        pedidoService.saveDispatch(pedidoDTO, token);
    }

    @PostMapping(PedidoPath.SAVE_EMPLEADO_ENTREGA)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveEmpleadoEntrega(@RequestBody PedidoDTO pedidoDTO) throws ServiceLayerException {
        pedidoService.saveEmpleadoEntrega(pedidoDTO);
    }

    @PostMapping(PedidoPath.REMOVE_EMPLEADO_ENTREGA)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeEmpleadoEntrega(@RequestBody PedidoDTO pedidoDTO) throws ServiceLayerException {
        pedidoService.removeEmpleadoEntrega(pedidoDTO);
    }

    @PostMapping(PedidoPath.SAVE_PEDIDO_EXTEMPORANEO)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveExtemporaneo(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PedidoDTO pedidoDTO) throws ServiceLayerException {
        pedidoService.saveExtemporaneo(pedidoDTO, token);
    }


    @PostMapping(PedidoPath.SAVE_DELIVERY)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveDelivery(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PedidoDTO pedidoDTO) throws ServiceLayerException {
        pedidoService.saveDelivery(pedidoDTO, token);
    }

}
