package com.crossal.recordreader.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class FileJsonObjReader<T> implements Closeable {

    private static final Logger logger = Logger.getLogger(FileJsonObjReader.class);

    private FileStreamFactory fileStreamFactory;
    private File file;
    private BufferedReader reader;

    public FileJsonObjReader(FileStreamFactory fileStreamFactory, File file) {
        this.fileStreamFactory = fileStreamFactory;
        this.file = file;
    }

    private void openFile() throws IOException {
        if (reader == null) {
            reader = fileStreamFactory.getReader(file);
        }
    }

    public T readObj(Class<T> clazz) throws IOException {
        openFile();

        ObjectMapper mapper = new ObjectMapper();

        String line = reader.readLine();

        while (line != null) {
            try {
                T newObj = mapper.readValue(line, clazz);
                return newObj;
            } catch (Exception e) {
                logger.info("A obj of type " + clazz + " was unparseable: " + e.getMessage());
            }
            line = reader.readLine();
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
