package com.crossal.recordreader;

import com.crossal.recordreader.customer.CustomerServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.io.File;

@SpringBootApplication
@Profile("!test")
public class Main implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(Main.class);

    @Autowired
    private CustomerServiceImpl customerService;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        if (args.length == 0) {
            logger.error("A record file path was not specified.");
            return;
        }

        Integer kms = null;
        if (args.length > 1) {
            kms = Integer.valueOf(args[1]);
        }

        try {
            File file = new File(args[0]);
            customerService.deleteCustomers();
            customerService.saveCustomersWithinKms(file, kms == null ? 100 : kms);
            customerService.printCustomers();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
