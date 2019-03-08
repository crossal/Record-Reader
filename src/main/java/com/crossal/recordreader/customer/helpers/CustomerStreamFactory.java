package com.crossal.recordreader.customer.helpers;

import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class CustomerStreamFactory {
    public BufferedReader getReader(File file) throws IOException {
        return new BufferedReader(new FileReader(file));
    }
}
