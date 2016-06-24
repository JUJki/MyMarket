package com.mymarket.gcm.DAO;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by julien on 24/06/2016.
 */
public interface IDAO<T,ID> {
    public void open() throws SQLException;
    public void close();
    public void create(T t) throws Exception;
    public T retrieve(ID id) throws Exception;
    public T retrieveByName(String name) throws Exception;
    public List<T> findAll() throws Exception;
    public void update(T t) throws Exception;
    public void delete(ID id) throws Exception;
}
