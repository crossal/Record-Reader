package com.crossal.recordreader.customer.helpers;

import java.io.*;

public class CustomerStreamFactory {
    public BufferedReader getReader(File file) throws IOException {
        return new BufferedReader(new FileReader(file));
    }
}
