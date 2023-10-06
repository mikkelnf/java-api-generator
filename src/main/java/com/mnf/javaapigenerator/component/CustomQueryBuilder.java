package com.mnf.javaapigenerator.component;

import com.mnf.javaapigenerator.component.dto.*;
import org.springframework.stereotype.Component;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.*;

@Component
public class CustomQueryBuilder<T> {
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<T> criteriaQuery;
    private Root<T> root;
    private List<Predicate> andPredicateList;
    private List<Predicate> orPredicateList;
    private List<Order> orderList;
    private Class<T> entityClass;
    private String operator = "AND";
    private Integer limit;

    public CustomQueryBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public CustomQueryBuilder<T> buildQuery(Class<T> entityClass){
        this.entityClass = entityClass;

        return this;
    }

    public CustomQueryBuilder<T> start(){
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(entityClass);
        this.root = criteriaQuery.from(entityClass);
        this.andPredicateList = new ArrayList<>();
        this.orPredicateList = new ArrayList<>();
        this.orderList = new ArrayList<>();

        return this;
    }

    public CustomQueryBuilder<T> equals(String columnName, Object searchValue){
        if(searchValue != null){
            Predicate predicate = criteriaBuilder.equal(root.get(columnName), searchValue);
            if("AND".equals(operator)){
                andPredicateList.add(predicate);
            }else if("OR".equals(operator)){
                orPredicateList.add(predicate);
            }
        }

        return this;
    }

    public CustomQueryBuilder<T> like(String columnName, Object searchValue){
        if(searchValue != null){
            Predicate predicate = criteriaBuilder.like(root.get(columnName), "%" + searchValue + "%");
            if("AND".equals(operator)){
                andPredicateList.add(predicate);
            }else if("OR".equals(operator)){
                orPredicateList.add(predicate);
            }
        }

        return this;
    }

    public CustomQueryBuilder<T> or(){
        this.operator = "OR";

        return this;
    }

    public CustomQueryBuilder<T> and(){
        this.operator = "AND";

        return this;
    }

    public CustomQueryBuilder<T> limit(Integer limit){
        this.limit = limit;

        return this;
    }

    public CustomQueryBuilder<T> sortAscBy(String columnName){
        this.orderList.add(criteriaBuilder.asc(root.get(columnName)));

        return this;
    }

    public CustomQueryBuilder<T> sortDescBy(String columnName){
        this.orderList.add(criteriaBuilder.desc(root.get(columnName)));

        return this;
    }

    public CustomQueryBuilder<T> end(){
        if(andPredicateList.isEmpty() && orPredicateList.isEmpty()){
            criteriaQuery.select(root);
        }else{
            List<Predicate> finalPredicates = new ArrayList<>();

            if(!andPredicateList.isEmpty()){
                finalPredicates.add(criteriaBuilder.and(andPredicateList.toArray(new Predicate[0])));
            }
            if(!orPredicateList.isEmpty()){
                finalPredicates.add(criteriaBuilder.or(orPredicateList.toArray(new Predicate[0])));
            }

            criteriaQuery.where(finalPredicates.toArray(new Predicate[0]));
        }

        if(!orderList.isEmpty()){
            criteriaQuery.orderBy(orderList);
        }

        return this;
    }

    public Optional<T> getOne(){
        try{
            T singleResult = entityManager.createQuery(criteriaQuery).getSingleResult();

            return Optional.ofNullable(singleResult);
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

    public List<T> getAll(){
        if(this.limit != null){
            TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery).setMaxResults(this.limit);

            return typedQuery.getResultList();
        }else{
            return entityManager.createQuery(criteriaQuery).getResultList();
        }
    }

    public GetPaginationResponseDto<T> getAllWithPagination(GetPaginationRequestDto<?> getPaginationRequestDto){
        Integer pageSize = getPaginationRequestDto.getSize();
        Integer currentPage = getPaginationRequestDto.getPage();
        String sortType = getPaginationRequestDto.getSortType();
        String sortBy = getPaginationRequestDto.getSortBy();

        if("asc".equals(sortType)){
            this.orderList.add(criteriaBuilder.asc(root.get(sortBy)));
        }else if("desc".equals(sortType)){
            this.orderList.add(criteriaBuilder.desc(root.get(sortBy)));
        }

        if(!orderList.isEmpty()){
            criteriaQuery.orderBy(orderList);
        }

        int offset = pageSize * (currentPage - 1);

        List<T> paginatedEntityList = entityManager.createQuery(criteriaQuery).setFirstResult(offset).setMaxResults(pageSize).getResultList();

        int totalItems = countItems();
        Double totalPages = Math.ceil(totalItems/pageSize.doubleValue());

        return new GetPaginationResponseDto<>(paginatedEntityList, new PaginationInfoDto(currentPage, totalPages.intValue(), pageSize, totalItems, sortBy, sortType));
    }

    private Integer countItems(){
        CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> criteriaQueryRoot = countCriteriaQuery.from(entityClass);

        if(andPredicateList.isEmpty() && orPredicateList.isEmpty()){
            countCriteriaQuery.select(criteriaBuilder.count(criteriaQueryRoot));
        }else{
            List<Predicate> finalPredicates = new ArrayList<>();

            if(!andPredicateList.isEmpty()){
                finalPredicates.add(criteriaBuilder.and(andPredicateList.toArray(new Predicate[0])));
            }
            if(!orPredicateList.isEmpty()){
                finalPredicates.add(criteriaBuilder.or(orPredicateList.toArray(new Predicate[0])));
            }

            countCriteriaQuery.select(criteriaBuilder.count(criteriaQueryRoot)).where(andPredicateList.toArray(new Predicate[0]));
        }

        return entityManager.createQuery(countCriteriaQuery).getSingleResult().intValue();
    }
}
