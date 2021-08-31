package com.company.project.service;

import com.company.project.model.User;
import com.company.project.core.Service;

import java.util.Map;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
public interface UserService extends Service<User> {
    public void updateUserPasswd(User user);
    public void updateUserUsername(User user);





}
