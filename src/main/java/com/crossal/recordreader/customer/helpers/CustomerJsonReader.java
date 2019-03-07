package com.crossal.recordreader.customer.helpers;

import com.crossal.recordreader.customer.Customer;
import com.crossal.recordreader.customer.helpers.CustomerReader;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CustomerJsonReader implements CustomerReader {

//    private File input;
//
//    public CustomerJsonReader(File input) {
//        this.input = input;
//    }

    public Iterable<Customer> read(Reader reader, int pageSize) {
        List<Customer> customers = new ArrayList<>();
        return null;
    }
}
