package com.crossal.recordreader;

import com.crossal.recordreader.customer.CustomerService;
import com.crossal.recordreader.customer.CustomerServiceImpl;
import com.crossal.recordreader.customer.helpers.CustomerStreamFactory;
import org.apache.log4j.Logger;

import java.io.File;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            return;
        }

        logger.info("Input: " + args[0]);

        CustomerStreamFactory streamFactory = new CustomerStreamFactory();
        CustomerService customerService = new CustomerServiceImpl(streamFactory);

        try {
            File file = new File(args[0]);
//            File file = new File("C:/Users/Alan/workspace/RecordReader/src/main/resources/customers.txt");
            customerService.saveCustomersWithinKms(file, 100);
            customerService.printCustomers();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
