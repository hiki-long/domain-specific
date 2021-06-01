package com.company.project.web;


import at.favre.lib.crypto.bcrypt.BCrypt;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.core.Auth;
import com.company.project.model.User;
import com.company.project.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.UUID;

/**
 * Created by CodeGenerator on 2021/04/21.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Autowired
    private  StringRedisTemplate stringRedisTemplate;

    @PostMapping("/add")
    public Result add(User user) {
        userService.save(user);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam String id) {
        userService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(User user) {
        userService.update(user);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam String id) {
        User user = userService.findById(id);
        return ResultGenerator.genSuccessResult(user);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<User> list = userService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @PostMapping("/register")
    public Result regieter(@RequestParam Map<String,String> params,HttpServletRequest request){
        String email = params.get("email");
        String username = params.get("username");
        String passwd = params.get("passwd");
        if(userService.findBy("email",email)!=null){
            return ResultGenerator.genFailResult("email has been used");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswd(BCrypt.withDefaults().hashToString(12,passwd.toCharArray()));
        user.setRole("buyer");
        user.setAvatar("default");
        user.setRank(5.0F);
        user.setUuid(UUID.randomUUID().toString());
        try {
            userService.save(user);
        } catch (Exception e){
            return ResultGenerator.genFailResult("failed");
        }
        return ResultGenerator.genSuccessResult("success");
    }
    @PostMapping("/login")
    public Result login(@RequestParam Map<String,String> params,HttpServletRequest request){
        String email=params.get("email");
        String passwd=params.get("passwd");

        User findUser=null;
        if((findUser=userService.findBy("email",email))!=null){
            if (BCrypt.verifyer().verify(passwd.toCharArray(),findUser.getPasswd().toCharArray()).verified){
                String uuid=findUser.getUuid();
                Map<String,String> map=new HashMap<>();
                map.put("uuid",uuid);
                return ResultGenerator.genSuccessResult(JSON.toJSONString(map));
            }
        }
        return ResultGenerator.genFailResult("login failed");

    }
    /*
    模块：修改密码（登录后状态，需要和忘记密码的修改密码做出区分）
    传入参数:email、passwd、newpasswd
    返回信息：旧密码错误（失败）、修改成功（成功）、发生错误（没有找到对应邮箱的用户，但一般不会发生这种错误，因为已经登录）
    注意：密码的格式需要在前台进行筛选
     */

    @PostMapping("/changePasswd")
    public Result changePasswd(@RequestParam Map<String,String> params,HttpServletRequest request){
        String email=params.get("email");
        String passwd=params.get("passwd");
        String newpasswd=params.get("newpasswd");
        User findUser=null;
        if((findUser=userService.findBy("email",email))!=null){
            if (BCrypt.verifyer().verify(passwd.toCharArray(),findUser.getPasswd().toCharArray()).verified){
                findUser.setPasswd(BCrypt.withDefaults().hashToString(12,newpasswd.toCharArray()));
                userService.updateUserPasswd(findUser);
                return ResultGenerator.genSuccessResult("success");
            }
            else{
                return ResultGenerator.genFailResult("old password wrong");
            }
        }
        else{
            return  ResultGenerator.genFailResult("find user error");
        }
    }
    /*
    模块：修改用户名
    传入参数:email、username
    返回信息：成功修改、找不到用户
     */
    @PostMapping("/changeUsername")
    public Result changeUsername(@RequestParam Map<String,String> param,HttpServletRequest request){
        String email=param.get("email");
        String username=param.get("username");
        User findUser=null;
        if((findUser=userService.findBy("email",email))!=null){
            findUser.setUsername(username);
            userService.updateUserUsername(findUser);
            return ResultGenerator.genSuccessResult("success");
        }
        else{
            return  ResultGenerator.genFailResult("find user error");
        }

    }
    /*
    模块：忘记密码
    传入参数:email、username、newpasswd
    返回信息：成功改了新的密码、找不到用户
     */
    @PostMapping("/forgetPasswd")
    public Result forgetPasswd(@RequestParam Map<String,String> param,HttpServletRequest request){
        String email=param.get("email");
        String username=param.get("username");
        String newpasswd=param.get("newpasswd");
        Condition condition=new Condition(User.class);
        Example.Criteria criteria=condition.createCriteria();
        criteria.andEqualTo("email",email);
        criteria.andEqualTo("username",username);
        List<User> list=null;
        list=userService.findByCondition(condition);
        if(list.size()!=1){
            return ResultGenerator.genFailResult("find user error");
        }
        User findUser=null;
        findUser=list.get(0);
        findUser.setPasswd(BCrypt.withDefaults().hashToString(12,newpasswd.toCharArray()));
        userService.updateUserPasswd(findUser);
        return ResultGenerator.genSuccessResult("reset success");
    }
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request){
        Enumeration em = request.getSession().getAttributeNames();
        while(em.hasMoreElements()){
            request.getSession().removeAttribute((em.nextElement().toString()));
        }
        return ResultGenerator.genSuccessResult("successfully quit");

    }
    @PostMapping("/test")
    public Result functest(@RequestParam Map<String, String> params, HttpServletRequest request) throws Exception {
        String data = params.get("data");
        HttpSession session = request.getSession();
        HashMap<String,String> result = new HashMap<>();
        Auth auth = new Auth(stringRedisTemplate);
        try {
            String key = auth.setSession(data);
            result.put(key, auth.getSession(key));
            session.setAttribute("uuid",key);
        } catch(Exception e){
            System.out.println(e);
        }
        return ResultGenerator.genSuccessResult(result);
    }

}
