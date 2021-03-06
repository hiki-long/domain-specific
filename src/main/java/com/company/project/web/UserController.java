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
import org.apache.ibatis.executor.ReuseExecutor;
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

import java.util.UUID;

/**
 * Created by CodeGenerator on 2021/04/21.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    private Auth auth;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;




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

    @GetMapping("/detail")
    public Result detail(@RequestParam String id) {
        User user = userService.findById(id);
        return ResultGenerator.genSuccessResult(user);
    }

    @CrossOrigin
    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<User> list = userService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @CrossOrigin
    @PostMapping("/countUser")
    public Result countUser(){
        List<User> list=userService.findAll();
        int length=list.size();
        return ResultGenerator.genSuccessResult(length);
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
        user.setUserRole("buyer");
        user.setAvatar("default");
        user.setUserRank(5.0F);
        user.setUuid(UUID.randomUUID().toString());
        try {
            userService.save(user);
        } catch (Exception e){
            return ResultGenerator.genFailResult("failed");
        }
        return ResultGenerator.genSuccessResult("success");
    }

    @GetMapping("/isLogin")
    public Result isLogin(HttpServletRequest request){
        HttpSession session=null;
        session=request.getSession();
        auth=Auth.getInstance(stringRedisTemplate);
        if(session!=null){
            String tryUUID= (String) session.getAttribute("uuid");
            if(tryUUID!=null){
                if(auth.hasSession(tryUUID)){
                    return ResultGenerator.genSuccessResult("User is login");
                }else{
                    return ResultGenerator.genFailResult("user is not exist");
                }
            }else{
                return ResultGenerator.genFailResult("User is not login");
            }
        }else{
            return ResultGenerator.genFailResult("User is not login");
        }
    }

    @PostMapping("/login")
    public Result login(@RequestParam Map<String,String> params,HttpServletRequest request)  {

         HttpSession session=null;//???????????????????????????
         session=request.getSession();
         auth=Auth.getInstance(stringRedisTemplate);
         if(session!=null){
             String tryUUID= (String) session.getAttribute("uuid");
             if(tryUUID!=null){
                 if(auth.hasSession(tryUUID)){
                     return ResultGenerator.genSuccessResult("???????????????");
                 }
             }
         }
         else {
             return ResultGenerator.genFailResult("????????????");
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
    ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    ????????????:passwd???newpasswd
    ?????????????????????????????????????????????????????????????????????
    ???????????????????????????????????????????????????
     */
    @PostMapping("/changePasswd")
    public Result changePasswd(@RequestParam Map<String,String> params,HttpServletRequest request){
            String passwd = params.get("passwd");
            String newpasswd = params.get("newpasswd");
            String userUUID=getUserSession(request);
            if(userUUID==null){
                return ResultGenerator.genFailResult("?????????session????????????????????????");
            }
            User findUser = null;
            if ((findUser = userService.findBy("uuid", userUUID)) != null) {
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
    ????????????????????????
    ????????????:username
    ?????????????????????????????????????????????
     */
    @PostMapping("/changeUsername")
    public Result changeUsername(@RequestParam Map<String,String> param,HttpServletRequest request){

        String username=param.get("username");
        String userUUID=getUserSession(request);
        if(userUUID==null){
            return ResultGenerator.genFailResult("?????????session????????????????????????");
        }
        User findUser=null;
        if((findUser=userService.findBy("uuid",userUUID))!=null){
            findUser.setUsername(username);
            userService.updateUserUsername(findUser);
            return ResultGenerator.genSuccessResult("success");
        }
        else{
            return  ResultGenerator.genFailResult("find user error");
        }

    }
    /*
    ?????????????????????
    ????????????:email???username???newpasswd
    ?????????????????????????????????????????????????????????
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
    ???????????????attribute???session????????????
    ???????????????key???redis??????????????????
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
    ??????????????????,
     */
    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception{
        if(file.isEmpty()){
            return ResultGenerator.genFailResult("????????????,???????????????");
        }
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
            result.append("??????????????????");
        }catch (IOException e){
            e.printStackTrace();
        }

        User findUser=null;
        String uuid=getUserSession(request);
        if(uuid==null){
            return ResultGenerator.genFailResult("?????????session????????????????????????");
        }
        findUser = userService.findBy("uuid", uuid);
        if (findUser != null) {//???????????????????????????
            findUser.setAvatar(avatarUri);
            userService.update(findUser);
            return ResultGenerator.genSuccessResult("??????????????????");
        }
        return ResultGenerator.genFailResult("???????????????????????????");

    }
    /*
    ?????????????????????
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
                return ResultGenerator.genFailResult("?????????????????????Url");
            }
            return ResultGenerator.genFailResult("???????????????????????????");
        }
        return ResultGenerator.genFailResult("session??????????????????uuid");




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
        auth=Auth.getInstance(stringRedisTemplate);
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
