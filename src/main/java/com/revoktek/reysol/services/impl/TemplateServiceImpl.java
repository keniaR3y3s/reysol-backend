package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.enums.EstatusPedidoEnum;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.dto.PedidoDTO;
import com.revoktek.reysol.dto.PedidoProductoDTO;
import com.revoktek.reysol.dto.TemplateDTO;
import com.revoktek.reysol.persistence.entities.Template;
import com.revoktek.reysol.persistence.repositories.TemplateRepository;
import com.revoktek.reysol.services.PedidoService;
import com.revoktek.reysol.services.TemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
@AllArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final ApplicationUtil applicationUtil;
    private final PedidoService pedidoService;
    private final TemplateRepository templateRepository;

    @Override
    public TemplateDTO getTicket(Long idPedido) throws ServiceLayerException {
        try {
            String nombre = "TICKET";
            Template template = templateRepository.findByNombre(nombre);
            if (template == null) {
                throw new ServiceLayerException("El template no existe");
            }
            PedidoDTO pedidoDTO = pedidoService.findByID(idPedido);

            Map<String, String> params = new HashMap<>();
            params.put("P_ID_PEDIDO", pedidoDTO.getClave());
            params.put("P_CLIENTE", pedidoDTO.getCliente().getAlias());
            params.put("P_FECHA_SOLICITUD", applicationUtil.formatDate(pedidoDTO.getFechaSolicitud(), 1));
            params.put("P_ESTATUS", pedidoDTO.getEstatusPedido().getNombre());
            params.put("P_ABONADO", applicationUtil.formatMoney(pedidoDTO.getAbonado()));
            params.put("P_PENDIENTE", applicationUtil.formatMoney(pedidoDTO.getPendiente()));
            params.put("P_TOTAL", applicationUtil.formatMoney(pedidoDTO.getTotal()));
            StringBuilder products = new StringBuilder("<tr><td colspan=\"4\">Sin productos solicitados</td></tr>");
            if (applicationUtil.nonEmptyList(pedidoDTO.getProductos())) {
                products = new StringBuilder();
                for (PedidoProductoDTO item : pedidoDTO.getProductos()) {
                    if (Objects.equals(pedidoDTO.getEstatusPedido().getIdEstatusPedido(), EstatusPedidoEnum.ENTREGADO.getValue())) {
                        products.append("<tr>");
                        products.append("<td>").append(item.getProducto().getNombre()).append("</td>");
                        products.append("<td>").append(applicationUtil.formatBigDecimal(item.getCantidadEntregada())).append("</td>");
                        products.append("<td>").append(applicationUtil.formatBigDecimal(item.getPesoEntregado())).append("kg</td>");
                        products.append("<td>").append(applicationUtil.formatMoney(item.getSubtotal())).append("</td>");
                        products.append("<tr>");
                    } else if (
                            (Objects.equals(pedidoDTO.getEstatusPedido().getIdEstatusPedido(), EstatusPedidoEnum.DESPACHADO.getValue())) ||
                                    (Objects.equals(pedidoDTO.getEstatusPedido().getIdEstatusPedido(), EstatusPedidoEnum.ASIGNADO.getValue()))
                    ) {
                        products.append("<tr>");
                        products.append("<td>").append(item.getProducto().getNombre()).append("</td>");
                        products.append("<td>").append(applicationUtil.formatBigDecimal(item.getCantidadDespachada())).append("</td>");
                        products.append("<td>").append(applicationUtil.formatBigDecimal(item.getPesoDespachado())).append("kg</td>");
                        products.append("<td>").append(applicationUtil.formatMoney(item.getSubtotal())).append("</td>");
                        products.append("<tr>");
                    } else if (
                            (Objects.equals(pedidoDTO.getEstatusPedido().getIdEstatusPedido(), EstatusPedidoEnum.PENDIENTE.getValue())) ||
                                    (Objects.equals(pedidoDTO.getEstatusPedido().getIdEstatusPedido(), EstatusPedidoEnum.CANCELADO.getValue()))

                    ) {
                        products.append("<tr>");
                        products.append("<td>").append(item.getProducto().getNombre()).append("</td>");
                        products.append("<td>").append(applicationUtil.formatBigDecimal(item.getCantidadSolicitada())).append("</td>");
                        products.append("<td>").append(applicationUtil.formatBigDecimal(item.getPesoSolicitado())).append("kg</td>");
                        products.append("<td>").append(applicationUtil.formatMoney(item.getSubtotal())).append("</td>");
                        products.append("<tr>");
                    }

                }
            }
            params.put("P_PRODUCTOS", products.toString());

            String templateFinal = template.getTemplate();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                templateFinal = templateFinal.replace(entry.getKey(), entry.getValue());
            }
            TemplateDTO templateDTO = TemplateDTO.builder()
                    .idTemplate(template.getIdTemplate())
                    .nombre(template.getNombre())
                    .template(templateFinal)
                    .build();
//            System.out.println(templateDTO.getTemplate());
            return templateDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }
}