package com.crossal.recordreader.customer;

import java.io.File;

public interface CustomerService {
    void deleteCustomers();
    void saveCustomersWithinKms(File file, int kms);
    void printCustomers();
}
