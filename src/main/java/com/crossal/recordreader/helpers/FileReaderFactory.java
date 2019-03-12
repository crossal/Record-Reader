package com.crossal.recordreader.helpers;

import java.io.File;

public class FileReaderFactory {
    public FileJsonObjReader getJsonObjReader(File file) {
        return new FileJsonObjReader(new FileStreamFactory(), file);
    }
}
