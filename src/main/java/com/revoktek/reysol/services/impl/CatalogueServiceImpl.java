package com.revoktek.reysol.services.impl;

import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import com.revoktek.reysol.core.i18n.MessageProvider;
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
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void save(String entityName, Object entity) throws ServiceLayerException {
        try {

            Class<?> entityClass = getEntity(entityName);
            Object entityPersist = entityClass.getDeclaredConstructor().newInstance();

            entityPersist = mapperUtil.parseBetweenObject(entityPersist.getClass(), entity);

            entityManager.persist(entityPersist);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public Object findByID(String entityName, String id) throws ServiceLayerException {
        try {
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
            throw new ServiceLayerException(e);
        }
    }


    @Override
    @Transactional
    public void update(String entityName, String id, Object entity) throws ServiceLayerException {
        try {

            Class<?> entityClass = getEntity(entityName);

            Object entityUpdate = entityClass.getDeclaredConstructor().newInstance();
            entityUpdate = mapperUtil.parseBetweenObject(entityUpdate.getClass(), entity);


            entityManager.merge(entityUpdate);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceLayerException(e);
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