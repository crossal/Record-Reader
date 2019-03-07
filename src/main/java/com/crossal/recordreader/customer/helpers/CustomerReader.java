package com.crossal.recordreader.customer.helpers;

import com.crossal.recordreader.customer.Customer;

import java.io.Reader;

public interface CustomerReader {
    Iterable<Customer> read(Reader reader, int pageSize);
}
