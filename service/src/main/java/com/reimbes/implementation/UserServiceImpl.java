package com.reimbes.implementation;

import com.reimbes.User;
import com.reimbes.UserRepository;
import com.reimbes.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public User login(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new Exception(username);
        else if(password == null) throw new Exception("Password cant be null");
        else if (!encoder.encode(password).equals(user.getPassword()))
            throw new Exception("Username and Password isn't match");
        return user;
    }

    @Override
    public User create(User user) throws Exception {
        if (userRepository.findByUsername(user.getUsername()) != null)
            throw new Exception("Username not unique");

        //do password encoding
        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(User.Role.USER);
        return userRepository.save(user);
    }

    @Override
    public User update(User newUserData, long id) throws Exception {
        User oldUser = userRepository.findOne(id);

        if (oldUser == null) throw new Exception("User not found");

        // do validation (check username should be unique)
        if (oldUser != userRepository.findByUsername(newUserData.getUsername()))
            throw new Exception("Username should be unique");

        // update data, need password encoding
        oldUser.setPassword(encoder.encode(newUserData.getPassword()));
        oldUser.setRole(newUserData.getRole());
        oldUser.setPassword(newUserData.getPassword());
        oldUser.setUsername(newUserData.getUsername());

        return userRepository.save(oldUser);
    }

    @Override
    public void delete(long id) throws Exception {
        if (userRepository.findOne(id) == null) throw new Exception("User not found");
        userRepository.delete(id);
    }

    @Override
    public List<User> deleteAll() {
        userRepository.deleteAll();
        return userRepository.findAll();
    }

    @Override
    public User getRandomUser() {
        return userRepository.findAll().get(0);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
