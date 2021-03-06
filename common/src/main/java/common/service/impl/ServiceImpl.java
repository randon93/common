package common.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import common.service.ConverterService;
import common.service.Service;
import common.service.ValidationService;
import common.types.DomainBean;
import common.types.Entity;
import common.validation.groups.Delete;
import common.validation.groups.Insert;
import common.validation.groups.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;



public abstract class ServiceImpl<D extends DomainBean<ID>, E extends Entity<ID>, ID extends Serializable>
        implements Service<D, ID> {

    @Autowired
    private ConverterService converterService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ValidationService validationService;
    private Class<E> entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    private Class<D> domainClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private Class<ID> typeId = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];

    public ServiceImpl() {
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public void save(D domainBean) {
        E entity = (E) this.converterService.convertTo(domainBean, this.entityClass);
        this.getDao().save(entity);
        domainBean.setId(entity.getId());
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public ID insert(D domainBean) {
        this.validationService.validate(domainBean, Insert.class);
        E entity = (E) this.converterService.convertTo(domainBean, this.entityClass);
        if (domainBean.getId() == null) {
            if (typeId.getSimpleName().equals(UUID.class.getSimpleName())) {
                entity.setId((ID) UUID.randomUUID());
            }
        }
        else if (isMerged()) {
            E entityDb = this.getDao().findById(domainBean.getId()).orElse(null);
        }

        this.getDao().save(entity);
        return entity.getId();
    }


    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public List<D> insert(List<D> domainBean) {
        List<E> entities = new ArrayList<>();
        this.validationService.validate(domainBean, Insert.class);
        domainBean.forEach(d -> {
            E entity = (E) this.converterService.convertTo(d, this.entityClass);
            if (d.getId() == null) {
                if (typeId.getSimpleName().equals(UUID.class.getSimpleName())) {
                    entity.setId((ID) UUID.randomUUID());
                }
            }
            else if (isMerged()) {
                E entityDb = this.getDao().findById(d.getId()).orElse(null);
            }

            entities.add(entity);
        });
        Iterable<E> entitiesSaved = getDao().saveAll(entities);
        return Streams
                .stream(entitiesSaved)
                .map(e -> {
                    return getConverterService().convertTo(e, domainClass);
                })
                .collect(Collectors.toList());
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public void update(D domainBean) {
        this.validationService.validate(domainBean, Update.class);
        E entity = (E) this.converterService.convertTo(domainBean, this.entityClass);
        if (isMerged()) {
            E entityBd = this.getDao().findById(domainBean.getId()).orElse(null);
        }
        this.getDao().save(entity);
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public void update(List<D> domainBean) {
        List<E> entities = new ArrayList<>();
        this.validationService.validate(domainBean, Update.class);
        domainBean.forEach(d -> {
            E entity = (E) this.converterService.convertTo(d, this.entityClass);
            if (d.getId() == null) {
                if (typeId.getSimpleName().equals(UUID.class.getSimpleName())) {
                    entity.setId((ID) UUID.randomUUID());
                }
            }
            else if (isMerged()) {
                E entityDb = this.getDao().findById(d.getId()).orElse(null);
            }
            entities.add(entity);
        });
        getDao().saveAll(entities);
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public List<D> updateBatchInList(List<D> domainBean) {
        List<E> entities = new ArrayList<>();
        this.validationService.validate(domainBean, Update.class);
        domainBean.forEach(d -> {
            E entity = (E) this.converterService.convertTo(d, this.entityClass);
            if (d.getId() == null) {
                if (typeId.getSimpleName().equals(UUID.class.getSimpleName())) {
                    entity.setId((ID) UUID.randomUUID());
                }
            }
            else if (isMerged()) {
                E entityDb = this.getDao().findById(d.getId()).orElse(null);
            }
            entities.add(entity);
        });
        Iterable<E> entitiesSaved = getDao().saveAll(entities);
        return Streams
                .stream(entitiesSaved)
                .map(e -> {
                    return converterService.convertTo(e, domainClass);
                })
                .collect(Collectors.toList());
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public void update(ID id, D domainBean) {
        domainBean.setId(id);
        this.validationService.validate(domainBean, Update.class);
        E entity = (E) this.converterService.convertTo(domainBean, this.entityClass);
        if (isMerged()) {
            E entityDb = this.getDao().findById(domainBean.getId()).orElse(null);
        }
        this.getDao().save(entity);
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public void delete(ID id) {
        Optional.ofNullable(this.findById(id)).ifPresent((obj) -> {
            this.validationService.validate(obj, Delete.class);
            E entity = getConverterService().convertTo(obj, entityClass);
        });
        this.getDao().deleteById(id);
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public void deleteAllById(List<ID> ids) {
        ids.forEach(id -> delete(id));
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public void delete(D object) {
        this.validationService.validate(object, Delete.class);
        this.delete(object.getId());
    }

    public D findById(ID id) {
        return (D) this.findById(id, this.domainClass);
    }

    public <R> R findById(ID id, Class<R> classType) {
        Optional<E> entity = this.getDao().findById(id);
        return entity.map((e) -> {
            return this.converterService.convertTo(e, classType);
        }).orElse(null);
    }

    public List<D> findAll() {
        return this.findAll(this.domainClass);
    }

    public <R> List<R> findAll(Class<R> classType) {
        Iterable<E> entities = this.getDao().findAll();
        List<R> result = Lists.newArrayList();
        Iterator var4 = entities.iterator();

        while (var4.hasNext()) {
            E ent = (E) var4.next();
            result.add(this.converterService.convertTo(ent, classType));
        }

        return result;
    }

    private void auditoria(E entity) {
        if (isAuditoria()) {
        }
    }

    protected abstract CrudRepository<E, ID> getDao();

    protected ConverterService getConverterService() {
        return this.converterService;
    }

    protected ValidationService getValidationService() {
        return this.validationService;
    }

    protected MessageSource getMessageSource() {
        return this.messageSource;
    }


    protected String getModulo() {
        return domainClass.getSimpleName();
    }

    protected boolean isAuditoria() {
        return true;
    }

    protected boolean isMerged() {
        return true;
    }

}