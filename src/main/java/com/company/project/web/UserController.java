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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Autowired
    private Auth auth;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

     {
        auth=new Auth(stringRedisTemplate);
    }
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
        String passwd = params.get("password");
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
    public Result login(@RequestParam Map<String,String> params,HttpServletRequest request)  {

         HttpSession session=null;//先判断是否已经登录
         session=request.getSession();
         if(session!=null){
             String tryUUID= (String) session.getAttribute("uuid");
             if(tryUUID!=null){
                 if(auth.hasSession(tryUUID)){
                     return ResultGenerator.genFailResult("已经登录了");
                 }
             }
         }
         else {
             return ResultGenerator.genFailResult("连接失败");
         }
        String email=params.get("email");
        String passwd=params.get("passwd");
        User findUser=null;
        if((findUser=userService.findBy("email",email))!=null){
            if (BCrypt.verifyer().verify(passwd.toCharArray(),findUser.getPasswd().toCharArray()).verified){
                String userUUID=findUser.getUuid();
                String keyUUID=null;
                try {
                     keyUUID= auth.setSession(userUUID);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                session=request.getSession();
                if(session!=null){
                    session.setAttribute("uuid",keyUUID);
                }

                Map<String,String> map=new HashMap<>();
                map.put("uuid",keyUUID);

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
            String email = params.get("email");
            String passwd = params.get("passwd");
            String newpasswd = params.get("newpasswd");
            User findUser = null;
            if ((findUser = userService.findBy("email", email)) != null) {
                if (BCrypt.verifyer().verify(passwd.toCharArray(), findUser.getPasswd().toCharArray()).verified) {
                    findUser.setPasswd(BCrypt.withDefaults().hashToString(12, newpasswd.toCharArray()));
                    userService.updateUserPasswd(findUser);
                    return ResultGenerator.genSuccessResult("success");
                } else {
                    return ResultGenerator.genFailResult("old password wrong");
                }
            } else {
                return ResultGenerator.genFailResult("find user error");
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
    /*
    ①将相应的attribute从session中移除。
    ②将相应的key从redis中进行移除。
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request){
        Enumeration em = request.getSession().getAttributeNames();
        String attribute=null;
        String keyid=null;
        while(em.hasMoreElements()){
            attribute=em.nextElement().toString();
            if(attribute=="uuid"){
                keyid=(String) request.getSession().getAttribute(attribute);
                request.getSession().removeAttribute(attribute);
            }
        }
        if(keyid!=null) {
            stringRedisTemplate.delete(keyid);
        }

        return ResultGenerator.genSuccessResult("successfully quit");

    }
    /*
    用于上传头像,
     */
    @CrossOrigin(allowCredentials = "true")
    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception{
        if(file.isEmpty()){
            return ResultGenerator.genFailResult("上传失败,请选择文件");
        }
//        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        response.setHeader("P3P", "CP=CAO PSA OUR");
//        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
//            response.addHeader("Access-Control-Allow-Methods", "POST,GET,TRACE,OPTIONS");
//            response.addHeader("Access-Control-Allow-Headers", "Content-Type,Origin,Accept");
//            response.addHeader("Access-Control-Max-Age", "120");
//        }
        String fileName = file.getOriginalFilename();
        int idx=fileName.lastIndexOf(".");
        String extention=fileName.substring(idx);
        String uuidFileName=UUID.randomUUID().toString().replace("-","")+extention;
        String filePath = System.getProperty("user.dir")+"/picture/avatar/";
        String avatarUri =filePath+uuidFileName;
        File dest=new File(avatarUri);
        StringBuffer result=new StringBuffer();
        try {
            file.transferTo(dest);
            result.append("成功上传图片");
        }catch (IOException e){
            e.printStackTrace();
        }

        User findUser=null;
        HttpSession session=null;
        String uuid=null;
        String redisuuid=null;
        session=request.getSession();
        if(session!=null){//连接无误
            redisuuid=(String)session.getAttribute("uuid");
            if(redisuuid!=null){//redis中存在相应的登录信息
                uuid=auth.getSession(redisuuid);
                if(uuid!=null) {
                    findUser = userService.findBy("uuid", uuid);
                    if (findUser != null) {//是否找到对应的用户
                        findUser.setAvatar(avatarUri);
                        userService.update(findUser);
                        return ResultGenerator.genSuccessResult("成功更新头像");
                    }
                    return ResultGenerator.genFailResult("没有找到对应的对象");
                }
                return ResultGenerator.genFailResult("redis中没有相应的登录信息");
            }
            return ResultGenerator.genFailResult("没有正确的登录信息");
        }
        return ResultGenerator.genFailResult("没有正确的进行连接");
    }

    /*
    获取相应的图片
     */
    @PostMapping(value = "getAvatar",produces = MediaType.IMAGE_PNG_VALUE)
    public Result getAvatar(HttpServletRequest request){
        String uuid=null;
        uuid=getUserSession(request);
        User findUser=null;
        String pictureUrl=null;
        FileInputStream picInput=null;
        if(uuid!=null){
            findUser=userService.findById(uuid);
            if(findUser!=null){
                pictureUrl=findUser.getAvatar();
                if(pictureUrl!=null){
                    try {
                        picInput=new FileInputStream(pictureUrl);
                        return ResultGenerator.genSuccessResult(picInput.readAllBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            picInput.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return ResultGenerator.genFailResult("没有相应地址的Url");
            }
            return ResultGenerator.genFailResult("没有找到相应的用户");
        }
        return ResultGenerator.genFailResult("session中没有相应的uuid");




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

    private String getUserSession(HttpServletRequest request){
        HttpSession session=null;
        session=request.getSession();
        String redisuuid=null;
        String uuid=null;
        if(session!=null){
            redisuuid=(String)session.getAttribute("uuid");
            if(redisuuid!=null){
                try {
                    uuid=auth.getSession(redisuuid);
                    if(uuid!=null){
                       return uuid;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }


}
