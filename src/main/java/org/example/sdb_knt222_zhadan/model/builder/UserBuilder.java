package org.example.sdb_knt222_zhadan.model.builder;

import org.example.sdb_knt222_zhadan.model.User;

public class UserBuilder {
    private final User user;

    public UserBuilder() {
        this.user = new User();
    }

    public UserBuilder setUserId(int userId) {
        user.setUserId(userId);
        return this;
    }

    public UserBuilder setFirstname(String firstname) {
        user.setFirstname(firstname);
        return this;
    }

    public UserBuilder setLastname(String lastname) {
        user.setLastname(lastname);
        return this;
    }

    public UserBuilder setEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public UserBuilder setPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public UserBuilder setPhone(String phone) {
        user.setPhone(phone);
        return this;
    }

    public UserBuilder setRole(String role) {
        user.setRole(role);
        return this;
    }

    public User build() {
        return user;
    }
}
