package com.example.dao;

import java.util.List;

public interface Dao<T> {
    List<T> findAll();
    T findById(int id);
    boolean save(T entity);
    boolean update(T entity);
    boolean delete(int id);
}