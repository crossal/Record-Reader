package com.crossal.recordreader.helpers;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
public class FileStreamFactory {
    public BufferedReader getReader(File file) throws IOException {
        return new BufferedReader(new FileReader(file));
    }
}
