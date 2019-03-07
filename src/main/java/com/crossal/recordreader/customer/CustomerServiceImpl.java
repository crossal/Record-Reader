package com.crossal.recordreader.customer;

import com.crossal.recordreader.customer.helpers.CustomerStreamFactory;
import com.crossal.recordreader.customer.queries.GetCustomersQuery;
import com.crossal.recordreader.database.JPAUtil;
import com.crossal.recordreader.utils.Locations;
import com.crossal.recordreader.utils.MathUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class);

    private static final int CUSTOMER_SAVE_BATCH_SIZE = 5;
    private static final int CUSTOMER_GET_BATCH_SIZE = 5;

    private int distance;
    private CustomerStreamFactory customerStreamFactory;

    public CustomerServiceImpl(CustomerStreamFactory customerStreamFactory) {
        this.customerStreamFactory = customerStreamFactory;
    }

    public void saveCustomersWithinKms(File file, int kms) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        distance = kms;

        try (BufferedReader reader = customerStreamFactory.getReader(file)) {
            tx.begin();

            ObjectMapper mapper = new ObjectMapper();
            String line = reader.readLine();

            // read file line by line
            int batchSize = 0;
            while (line != null) {
                Customer newCustomer = mapper.readValue(line, Customer.class);
                if (MathUtil.distanceWithinKms(Locations.HQ_LAT, Locations.HQ_LONG, newCustomer.getLatitude(), newCustomer.getLongitude(), kms)) {
                    em.persist(newCustomer);
                    batchSize++;
                }
                if (batchSize >= CUSTOMER_SAVE_BATCH_SIZE) {
                    tx.commit();
                    tx.begin();
                    em.clear();
                    batchSize = 0;
                }
                line = reader.readLine();
            }

        } catch (IOException e) {
            logger.error("save customers error: "+e);
        } finally {
            tx.commit();
            em.close();
        }
    }

    public void printCustomers() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        GetCustomersQuery query = new GetCustomersQuery(em);
        List<Customer> customers = query.getCustomers(null, CUSTOMER_GET_BATCH_SIZE);
        if (customers.isEmpty()) {
            logger.error("No customers have been read.");
            return;
        }
        logger.info("Printing customers within " + distance + " kms...");
        while (!customers.isEmpty()) {
            customers.forEach(customer -> logger.info(customer.getName() + " - " + customer.getId()));
            customers = query.getCustomers(customers.get(customers.size() - 1), CUSTOMER_GET_BATCH_SIZE);
        }
        logger.info("Complete");
    }
}
