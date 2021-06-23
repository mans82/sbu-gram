package com.mans.sbugram.server.dao;

import com.mans.sbugram.server.exceptions.PersistentDataDoesNotExistException;
import com.mans.sbugram.server.exceptions.PersistentOperationException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface Dao<T, I> {

    Optional<T> get(I id) throws PersistentOperationException;

    void save(T data) throws PersistentOperationException;

    void update(I id, T newData) throws PersistentOperationException, PersistentDataDoesNotExistException;

    default Reader getFileReader(String path) throws FileNotFoundException {
        return new FileReader(path);
    }

    default Writer getFileWriter(String path) throws IOException {
        return new FileWriter(path);
    }

    default List<String> getDirectoryFiles(String path) {
        File[] result =  new File(path).listFiles(File::isFile);

        if (result == null) {
            return new ArrayList<>(0);
        }

        return Arrays.stream(result)
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }

}
