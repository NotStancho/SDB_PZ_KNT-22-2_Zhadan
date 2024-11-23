package org.example.sdb_knt222_zhadan.dao;

import org.example.sdb_knt222_zhadan.model.User;

import java.util.List;

public interface UserDAO {
    List<User> getAllUsers();
    User getUserById(int userId);
    User getUserByEmail(String email);
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(int userId);
}
