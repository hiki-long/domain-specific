package com.company.project.dao;

import com.company.project.core.Mapper;
import com.company.project.model.User;

public interface UserMapper extends Mapper<User> {
    public void updateUserPasswd(User user);
    public void updateUserUsername(User user);
}