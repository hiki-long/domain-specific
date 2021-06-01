package com.company.project.service.impl;

import com.company.project.dao.UserMapper;
import com.company.project.model.User;
import com.company.project.service.UserService;
import com.company.project.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
@Service
@Transactional
public class UserServiceImpl extends AbstractService<User> implements UserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public void updateUserPasswd(User user) {
        userMapper.updateUserPasswd(user);
    }

    @Override
    public void updateUserUsername(User user) {
        userMapper.updateUserUsername(user);
    }

    @Override
    public void deleteById(String id) {

    }

    @Override
    public User findById(String id) {
        return null;
    }
}
