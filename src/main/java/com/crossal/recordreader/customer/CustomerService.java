package com.crossal.recordreader.customer;

import java.io.File;

public interface CustomerService {
    void saveCustomersWithinKms(File file, int kms);
    void printCustomers();
}
