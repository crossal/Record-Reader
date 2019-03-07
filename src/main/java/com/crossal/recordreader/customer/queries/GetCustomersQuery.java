package com.crossal.recordreader.customer.queries;

import com.crossal.recordreader.customer.Customer;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class GetCustomersQuery {

    private EntityManager em;

    public GetCustomersQuery(EntityManager em) {
        this.em = em;
    }

    public List<Customer> getCustomers(Customer lastIndex, int pageSize) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
        Root<Customer> root = query.from(Customer.class);
        if (lastIndex != null) {
            query.where(cb.greaterThan(root.get("id"), lastIndex.getId()));
        }
        query.orderBy(cb.asc(root.get("id")));

        return em.createQuery(query).setMaxResults(pageSize).getResultList();
    }
}
