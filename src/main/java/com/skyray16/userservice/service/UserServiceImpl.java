package com.skyray16.userservice.service;

import com.skyray16.userservice.dao.UserDao;
import com.skyray16.userservice.entity.User;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User createUser(User user) {
        return userDao.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User updateUser(User user) {
        return userDao.update(user);
    }

    @Override
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }
}