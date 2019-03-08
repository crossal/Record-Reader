package com.crossal.recordreader.customer;

import com.crossal.recordreader.customer.helpers.CustomerStreamFactory;
import com.crossal.recordreader.utils.Locations;
import com.crossal.recordreader.utils.MathUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class);

    private static final int CUSTOMER_SAVE_BATCH_SIZE = 5;
    private static final int CUSTOMER_GET_BATCH_SIZE = 5;

    private int distance;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerStreamFactory streamFactory;

    public void deleteCustomers() {
        customerRepository.deleteAll();
    }

    public void saveCustomersWithinKms(File file, int kms) {

        distance = kms;

        try (BufferedReader reader = streamFactory.getReader(file)) {
            ObjectMapper mapper = new ObjectMapper();
            String line = reader.readLine();

            // read file line by line
            int batchSize = 0;
            while (line != null) {
                Customer newCustomer = mapper.readValue(line, Customer.class);
                if (MathUtil.distanceWithinKms(Locations.HQ_LAT, Locations.HQ_LONG, newCustomer.getLatitude(), newCustomer.getLongitude(), kms)) {
                    customerRepository.save(newCustomer);
                    batchSize++;
                }
                if (batchSize >= CUSTOMER_SAVE_BATCH_SIZE) {
                    batchSize = 0;
                }
                line = reader.readLine();
            }

        } catch (IOException e) {
            logger.error("save customers error: "+e);
        } finally {
//            tx.commit();
//            em.close();
        }
    }

    public void printCustomers() {
        Pageable pageable = PageRequest.of(0, CUSTOMER_GET_BATCH_SIZE, Sort.by("id").ascending());
        logger.info("Printing customers within " + distance + " kms...");
        while (true) {
            Page<Customer> page = customerRepository.findAll(pageable);
            page.getContent().forEach(customer -> logger.info(customer.getName() + " - " + customer.getId()));
            if (!page.hasNext()) {
                break;
            }
            pageable = page.nextPageable();
        }
        logger.info("...complete");
    }
}
