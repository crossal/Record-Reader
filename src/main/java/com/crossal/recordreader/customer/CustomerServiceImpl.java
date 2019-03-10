package com.crossal.recordreader.customer;

import com.crossal.recordreader.customer.helpers.CustomerStreamFactory;
import com.crossal.recordreader.utils.Locations;
import com.crossal.recordreader.utils.MathUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class);

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int CUSTOMER_SAVE_BATCH_SIZE;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int CUSTOMER_GET_BATCH_SIZE;

    private int distance;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerStreamFactory streamFactory;
    @Autowired
    private MathUtil mathUtil;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    @Override
    public void deleteCustomers() {
        customerRepository.deleteAll();
    }

    @Override
    public void saveCustomersWithinKms(File file, int kms) {
        distance = kms;
        Session session = getSession();

        try (BufferedReader reader = streamFactory.getReader(file)) {
            ObjectMapper mapper = new ObjectMapper();
            String line = reader.readLine();

            // read file line by line
            int batchSize = 0;
            while (line != null) {
                Customer newCustomer = null;
                try {
                    newCustomer = mapper.readValue(line, Customer.class);
                    newCustomer.validate();
                } catch (Exception e) {
                    logger.info("A customer was unparseable: " + e.getMessage());
                    continue;
                } finally {
                    line = reader.readLine();
                }

                if (mathUtil.distanceWithinKms(Locations.HQ_LAT, Locations.HQ_LONG, newCustomer.getLatitude(), newCustomer.getLongitude(), kms)) {
                    customerRepository.save(newCustomer);
                    batchSize++;
                }
                if (batchSize >= CUSTOMER_SAVE_BATCH_SIZE) {
                    session.flush();
                    session.clear();
                    batchSize = 0;
                }
            }

        } catch (IOException e) {
            logger.error("save customers error: " + e.getMessage());
        }
    }

    @Override
    public void printCustomers() {
        Pageable pageable = PageRequest.of(0, CUSTOMER_GET_BATCH_SIZE, Sort.by("id").ascending());
        System.out.println("Printing customers within " + distance + " kms...");
        while (true) {
            Page<Customer> page = customerRepository.findAll(pageable);
            page.getContent().forEach(customer -> System.out.println(customer.getName() + " - " + customer.getId()));
            if (!page.hasNext()) {
                break;
            }
            pageable = page.nextPageable();
        }
        System.out.println("...complete");
    }
}
