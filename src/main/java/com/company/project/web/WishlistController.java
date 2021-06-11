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
        String userUUID=getUserSession(request);
        if(userUUID==null){
            return ResultGenerator.genFailResult("没有找到相应的登录数据");
        }
        Wishlist findwishlist=null;
        findwishlist=wishlistService.findBy("owner",userUUID);
        int addNum=Integer.parseInt(number);
        String newItems=null;
        if(findwishlist==null) {//当购物车中没有响应的数据
            Wishlist wishlist = new Wishlist();
            wishlist.setOwner(userUUID);
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
                if(((String)(curMap.get("id"))).equals(itemUUID)){
                    hasItem=true;
                    Integer newNum=addNum;
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
    public Result removeWishlist(@RequestParam(value = "wishlist",required = true) String wishlist ,HttpServletRequest request) throws Exception {
        String userUUID=getUserSession(request);
        if(userUUID==null){
            return ResultGenerator.genFailResult("没有找到相应的登录数据");
        }
        ArrayList<String> removeItems=new ArrayList<>();
        JSONArray tempjsonArray1=JSONObject.parseArray(wishlist);
        for(int i=0;i<tempjsonArray1.size();i++){
            JSONObject jsonObject= (JSONObject) tempjsonArray1.get(i);
            Map<String,Object> map=jsonObject.getInnerMap();
            String removeItem= (String) map.get("uuid");
            removeItems.add(removeItem);
        }
        Wishlist findwishlist=null;
        findwishlist=wishlistService.findBy("owner",userUUID);
        boolean hasRemove=false;
        if(findwishlist==null){
            return ResultGenerator.genFailResult("没有相应的数据");
        }
        else {
            JSONArray jsonArray=JSONObject.parseArray(findwishlist.getItems());
            ArrayList<Map<String,Object>> list=new ArrayList<>();

            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                Map<String,Object> curMap=jsonObject.getInnerMap();
                if(removeItems.contains((String)(curMap.get("id")))){
                    hasRemove=true;
                }
                else {
                    list.add(curMap);
                }
            }
            if(!hasRemove){
                return ResultGenerator.genFailResult("没有能从购物车移除");
            }
            String result=JSONObject.toJSONString(list);
            findwishlist.setItems(result);
            wishlistService.update(findwishlist);
        }
            return ResultGenerator.genSuccessResult("成功插入");
    }

    @GetMapping("listItem")
    public Result listItem(HttpServletRequest request) throws Exception {
        String userUUID=getUserSession(request);
        if(userUUID==null){
            return ResultGenerator.genFailResult("没有找到相应的登录数据");
        }
        Wishlist wishlist =wishlistService.findBy("owner",userUUID);
        //JSONArray json = JSONObject.parseArray(wishlist.getItems());
        return ResultGenerator.genSuccessResult(wishlist.getItems());
    }

    private String getUserSession(HttpServletRequest request){
        HttpSession session=null;
        session=request.getSession();
        String redisuuid=null;
        String uuid=null;
        auth=Auth.getInstance();
        if(auth==null){
            return null;
        }
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


