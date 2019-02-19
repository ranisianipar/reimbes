package com.reimbes;

import java.util.List;

public interface UserService {
    User login(String username, String password) throws Exception;
    User create(User user) throws Exception;
    User update(User user, long id) throws Exception;
    void delete(long id) throws Exception;
    List<User> deleteAll();

    // nanti ini dibuang
    User getRandomUser();
    List<User> getAllUsers();
}
