package com.crossal.recordreader;

import com.crossal.recordreader.customer.CustomerService;
import com.crossal.recordreader.customer.CustomerServiceImpl;
import com.crossal.recordreader.customer.helpers.CustomerStreamFactory;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            return;
        }

        CustomerStreamFactory streamFactory = new CustomerStreamFactory();
        CustomerService customerService = new CustomerServiceImpl(streamFactory);

        try {
            File file = new File(args[0]);
            customerService.saveCustomersWithinKms(file, 100);
            customerService.printCustomers();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
