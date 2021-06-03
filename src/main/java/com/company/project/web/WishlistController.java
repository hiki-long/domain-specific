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
    public Result addWishlist(@RequestParam(value = "itemUUID",required = true) String itemUUID,@RequestParam(value = "number",required = true) String number,HttpServletRequest request){
        HttpSession httpSession=request.getSession();
        Object tryUUID=httpSession.getAttribute("uuid");
        if(tryUUID==null){
            return ResultGenerator.genFailResult("Not logged in");
        }
        String ownerUUID =tryUUID.toString();
        Wishlist findwishlist=null;
        findwishlist=wishlistService.findBy("uuid",ownerUUID);
        int addNum=Integer.parseInt(number);
        if(findwishlist==null) {//当购物车中没有响应的数据
            Wishlist wishlist = new Wishlist();
            wishlist.setOwner(ownerUUID);
            StringBuilder builder=new StringBuilder();
            String newItemUUID=itemUUID+"#";
            for(int i=0;i<addNum;i++){
                builder.append(newItemUUID);
            }
            wishlist.setItems(builder.toString());
            wishlistService.save(wishlist);
        }
        else{
             StringBuilder stringBuilder=new StringBuilder(findwishlist.getItems());
             stringBuilder.append(itemUUID+"#");
             findwishlist.setItems(stringBuilder.toString());
            wishlistService.update(findwishlist);
        }
        return ResultGenerator.genSuccessResult("successfully insert");
    }
    /*
    需要传入的参数，删除的是什么，删除的数量
     */
    @PostMapping("removeWishlist")
    public Result removeWishlist(@RequestParam(value = "itemUUID",required = true) String itemUUID,@RequestParam(value = "number",required = true) String number,HttpServletRequest request){
        HttpSession httpSession=request.getSession();
        if(httpSession!=null) {
            Object tryUUID = httpSession.getAttribute("uuid");
            if (tryUUID == null) {
                return ResultGenerator.genFailResult("Not logged in");
            }
            String ownerUUID = tryUUID.toString();
            Wishlist findWishlist = null;
            if((findWishlist=wishlistService.findBy("owner",ownerUUID))!=null){
                String oldItemUUID =findWishlist.getItems();
                String[] record = oldItemUUID.split("#");
                int deleteNum=Integer.parseInt(number);
                StringBuilder stringBuilder=new StringBuilder();
                for(int i=0;i<record.length;i++){
                    if(record[i].equals(itemUUID)){
                        deleteNum--;
                        if(deleteNum==0){
                            break;
                        }
                    }
                    else {
                        stringBuilder.append(record[i]);
                    }
                }
                findWishlist.setItems(stringBuilder.toString());
                wishlistService.update(findWishlist);
                return ResultGenerator.genSuccessResult("successfully delete");
            }
            else{
                return ResultGenerator.genFailResult("not exsit");
            }


        }
        else {
            return ResultGenerator.genFailResult("error");
        }
    }

    @GetMapping("listItem")
    public Result listItem(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        Auth auth = new Auth(stringRedisTemplate);
        Wishlist wishlist =wishlistService.findBy("owner",auth.getSession((String) session.getAttribute("uuid")));
        //JSONArray json = JSONObject.parseArray(wishlist.getItems());
        return ResultGenerator.genSuccessResult(wishlist.getItems());
    }
}
