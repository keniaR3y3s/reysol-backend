package com.revoktek.reysol.services;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;

import java.util.List;

public interface CatalogueService {

    List<?> findAll(String entityName) throws ServiceLayerException;
    List<?> findAllActive(String entityName) throws ServiceLayerException;

    void save(String entityName, Object entity) throws ServiceLayerException;

    Object findByID(String entityName, String id) throws ServiceLayerException;

    void update(String entityName, String id, Object o) throws ServiceLayerException;

    void changeStatus(String entityName, String id, Object entity) throws ServiceLayerException;;
}
