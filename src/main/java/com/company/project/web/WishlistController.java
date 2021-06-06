package com.company.project.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.company.project.core.Auth;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.model.Wishlist;
import com.company.project.service.WishlistService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map;

/**
 * Created by CodeGenerator on 2021/04/21.
 */
@RestController
@RequestMapping("/wishlist")
public class WishlistController {
    @Resource
    private WishlistService wishlistService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private Auth auth;

    @PostMapping("/add")
    public Result add(Wishlist wishlist) {
        wishlistService.save(wishlist);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam String id) {
        wishlistService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Wishlist wishlist) {
        wishlistService.update(wishlist);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam String id) {
        Wishlist wishlist = wishlistService.findById(id);
        return ResultGenerator.genSuccessResult(wishlist);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Wishlist> list = wishlistService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
    @PostMapping("/addWishlist")
    public Result addWishlist(@RequestParam(value = "itemUUID",required = true) String itemUUID,@RequestParam(value = "number",required = true) String number,HttpServletRequest request) throws Exception {
        HttpSession httpSession=null;
        httpSession=request.getSession();
        if(httpSession==null){
            return ResultGenerator.genFailResult("连接断开了");
        }
        String tryUUID=(String)httpSession.getAttribute("uuid");
        if(tryUUID==null){
            return ResultGenerator.genFailResult("Not logged in");
        }
        String ownerUUID =auth.getSession(tryUUID);
        if(ownerUUID==null){
            return ResultGenerator.genFailResult("redis中没有存相应的uuid");
        }
        Wishlist findwishlist=null;
        findwishlist=wishlistService.findBy("owner",ownerUUID);
        int addNum=Integer.parseInt(number);
        String newItems=null;
        if(findwishlist==null) {//当购物车中没有响应的数据
            Wishlist wishlist = new Wishlist();
            wishlist.setOwner(ownerUUID);
            Map<String,Object> map= new HashMap<>();
            map.put("num",addNum);
            map.put("id",itemUUID);
            JSONArray jsonArray=new JSONArray();
            jsonArray.add(map);
            newItems=jsonArray.toJSONString();
            wishlist.setItems(newItems);
            wishlistService.save(wishlist);
            return ResultGenerator.genSuccessResult("Successfully insert");
        }
        else{//当购物车有相应的数据
            JSONArray jsonArray=JSONObject.parseArray(findwishlist.getItems());
            ArrayList<Map<String,Object>> list=new ArrayList<>();
            boolean hasItem=false;
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                Map<String,Object> curMap=jsonObject.getInnerMap();
                if(((String)(curMap.get("id"))).equals(ownerUUID)){
                    hasItem=true;
                    Integer oldNum= (Integer) curMap.get("num");
                    Integer newNum=oldNum+addNum;
                    curMap.put("num",newNum);
                }
                list.add(curMap);
            }
            if(!hasItem){//其中没有相应的uuid时
                Map<String,Object> newMap=new HashMap<>();
                newMap.put("id",itemUUID);
                newMap.put("num",addNum);
                list.add(newMap);
            }
            String result=JSONObject.toJSONString(list);
            findwishlist.setItems(result);
            wishlistService.update(findwishlist);
        }
        return ResultGenerator.genSuccessResult("successfully insert");
    }
    /*
    需要传入的参数，删除的是什么，删除的数量
     */
    @PostMapping("removeWishlist")
    public Result removeWishlist(@RequestParam(value = "itemUUID",required = true) String itemUUID,@RequestParam(value = "number",required = true) String number,HttpServletRequest request) throws Exception {
        HttpSession httpSession=null;
        httpSession=request.getSession();
        if(httpSession==null){
            return ResultGenerator.genFailResult("连接断开了");
        }
        String tryUUID=(String)httpSession.getAttribute("uuid");
        if(tryUUID==null){
            return ResultGenerator.genFailResult("Not logged in");
        }
        String ownerUUID =auth.getSession(tryUUID);
        if(ownerUUID==null){
            return ResultGenerator.genFailResult("redis中没有存相应的uuid");
        }
        Wishlist findwishlist=null;
        findwishlist=wishlistService.findBy("owner",ownerUUID);
        int minNum=Integer.parseInt(number);
        boolean hasItem=false;
        boolean success=true;
        if(findwishlist==null){
            return ResultGenerator.genFailResult("没有相应的数据");
        }
        else {
            JSONArray jsonArray=JSONObject.parseArray(findwishlist.getItems());
            ArrayList<Map<String,Object>> list=new ArrayList<>();

            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                Map<String,Object> curMap=jsonObject.getInnerMap();
                if(((String)(curMap.get("id"))).equals(ownerUUID)){
                    hasItem=true;
                    Integer oldNum= (Integer) curMap.get("num");
                    if(oldNum>=minNum) {
                        Integer newNum = oldNum - minNum;
                        curMap.put("num", newNum);
                    }
                    else {
                        success=false;
                    }
                }
                list.add(curMap);
            }

            String result=JSONObject.toJSONString(list);
            findwishlist.setItems(result);
            wishlistService.update(findwishlist);

        }
        if(!hasItem||!success){
            return  ResultGenerator.genFailResult("不存在Item或者数据不足");
        }
        else {
            return ResultGenerator.genSuccessResult("成功插入");
        }

    }

    @GetMapping("listItem")
    public Result listItem(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        Auth auth = new Auth(stringRedisTemplate);
        String keyUUID=null;
        keyUUID= (String) session.getAttribute("uuid");
        String userUUID=null;
        userUUID=auth.getSession(keyUUID);
        Wishlist wishlist =wishlistService.findBy("owner",auth.getSession((String) session.getAttribute("uuid")));
        //JSONArray json = JSONObject.parseArray(wishlist.getItems());
        return ResultGenerator.genSuccessResult(wishlist.getItems());
    }
}


