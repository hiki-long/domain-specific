package com.company.project.web;

import at.favre.lib.crypto.bcrypt.BCrypt;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Result delete(@RequestParam Integer id) {
        userService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(User user) {
        userService.update(user);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
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

    @PostMapping("/test")
    public Result functest(@RequestParam Map<String, String> params, HttpServletRequest request) throws Exception {
        String data = params.get("data");
        User user = userService.findBy("email",data);
        if(user != null){
            return ResultGenerator.genSuccessResult(user.getUuid());
        }else {
            return ResultGenerator.genFailResult("unfound");
        }
    }
}
