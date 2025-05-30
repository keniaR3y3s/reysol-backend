package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
import com.revoktek.reysol.core.utils.ApplicationUtil;
import com.revoktek.reysol.core.utils.MapperUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class CatalogueServiceImpl implements com.revoktek.reysol.services.CatalogueService {

    private ApplicationUtil applicationUtil;
    private EntityManager entityManager;
    private MapperUtil mapperUtil;
    private MessageProvider messageProvider;

    @Override
    public List<?> findAll(String entityName) throws ServiceLayerException {
        try {
            log.info("PARAMETRO entityName:{}", entityName);
            Class<?> entityClass = getEntity(entityName);
            log.info("entityClass :{}", entityClass);

            String query = "SELECT e FROM " + entityClass.getSimpleName() + " e ";

            log.info("QUERY:{}", query);

            return entityManager.createQuery(query, entityClass).getResultList();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException("Error interno al obtener la información.");
        }
    }

    @Override
    public List<?> findAllActive(String entityName) throws ServiceLayerException {
        try {
            log.info("Obteniendo entidades activas para: {}", entityName);

            Class<?> entityClass = getEntity(entityName);
            String query = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.estatus = true";

            return entityManager.createQuery(query, entityClass).getResultList();
        } catch (Exception e) {
            log.error("Error al listar entidades activas: {}", e.getMessage(), e);
            throw new ServiceLayerException("Error al obtener entidades activas");
        }
    }


    @Override
    @Transactional
    public void save(String entityName, Object entity) throws ServiceLayerException {
        try {
            log.info("Datos front save.entityName : {}", entityName);
            log.info("Datos front save.entity : {}", applicationUtil.toJson(entity));

            Class<?> entityClass = getEntity(entityName);
            Object entityPersist = entityClass.getDeclaredConstructor().newInstance();

            entityPersist = mapperUtil.parseBetweenObject(entityPersist.getClass(), entity);

            entityManager.persist(entityPersist);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException("Datos incorrectos o duplicados.");
        }
    }

    @Override
    public Object findByID(String entityName, String id) throws ServiceLayerException {
        try {

            log.info("Datos front findByID.entityName : {}", entityName);
            log.info("Datos front findByID.id : {}", id);

            Class<?> entityClass = getEntity(entityName);

            Field idField = getIdField(entityClass);
            String idFieldName = (idField != null) ? idField.getName() : "id";

            Optional<?> entity = entityManager.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + idFieldName + " = :id", entityClass)
                    .setParameter("id", id)
                    .getResultList()
                    .stream()
                    .findFirst();
            if (entity.isEmpty())
                throw new ServiceLayerException(messageProvider.getMessageNotFound(id));
            return entity.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException("Error interno al obtener la información.");
        }
    }


    @Override
    @Transactional
    public void update(String entityName, String id, Object entity) throws ServiceLayerException {
        try {

            log.info("Datos front update.entityName : {}", entityName);
            log.info("Datos front update.id : {}", id);
            log.info("Datos front update.entity : {}", applicationUtil.toJson(entity));

            Class<?> entityClass = getEntity(entityName);

            Object entityUpdate = entityClass.getDeclaredConstructor().newInstance();
            entityUpdate = mapperUtil.parseBetweenObject(entityUpdate.getClass(), entity);


            entityManager.merge(entityUpdate);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException("Datos incorrectos o duplicados.");
        }
    }

    @Override
    @Transactional
    public void changeStatus(String entityName, String id, Object o) throws ServiceLayerException {
        try {
            log.info("Cambio de estatus para entidad {} con ID {}", entityName, id);

            Class<?> entityClass = getEntity(entityName);
            Field idField = getIdField(entityClass);
            if (idField == null) {
                throw new ServiceLayerException("No se encontró un campo ID en la entidad " + entityName);
            }

            // Buscar la entidad por ID
            Object entity = entityManager.createQuery(
                            "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + idField.getName() + " = :id",
                            entityClass)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new ServiceLayerException("Entidad no encontrada con ID: " + id));

            Field statusField = entityClass.getDeclaredField("estatus");
            statusField.setAccessible(true);

            Boolean currentStatus = (Boolean) statusField.get(entity);
            statusField.set(entity, currentStatus == null ? Boolean.FALSE : !currentStatus);

            entityManager.merge(entity);

            log.info("Estatus actualizado de {} a {}", currentStatus, !currentStatus);

        } catch (ServiceLayerException e) {
            throw e;
        } catch (NoSuchFieldException e) {
            log.error("El campo 'estatus' no existe en la entidad {}", entityName);
            throw new ServiceLayerException("La entidad no tiene un campo 'estatus'");
        } catch (Exception e) {
            log.error("Error cambiando el estatus: {}", e.getMessage(), e);
            throw new ServiceLayerException("Error al cambiar el estatus");
        }
    }



    private Class<?> getEntity(String entityName) throws ServiceLayerException {
        try {
            final String FOLDER = "com.revoktek.reysol.persistence.entities.";
            ManagedType<?> managedType = entityManager.getMetamodel().managedType(Class.forName(FOLDER + entityName));
            return managedType.getJavaType();
        } catch (ClassNotFoundException ex) {
            throw new ServiceLayerException("Entity with name " + entityName + " not found.");
        }

    }


    private Field getIdField(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        return null;
    }


}