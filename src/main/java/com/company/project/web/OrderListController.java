package com.company.project.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.company.project.core.Auth;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.model.Item;
import com.company.project.model.Orderlist;
import com.company.project.model.Wishlist;
import com.company.project.service.ItemService;
import com.company.project.service.OrderService;
import com.company.project.service.WishlistService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.*;

/**
 * Created by CodeGenerator on 2021/04/21.
 */
@RestController
@RequestMapping("/order")
public class OrderListController {
    @Resource
    private OrderService orderService;

    private Auth auth;

    @Autowired
    private WishlistService wishlistService;


    @PostMapping("/add")
    public Result add(Orderlist orderList) {
        orderService.save(orderList);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam String id) {
        orderService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Orderlist orderList) {
        orderService.update(orderList);
        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin
    @GetMapping("/detail")
    public Result detail(@RequestParam String id) {
        Orderlist orderList = orderService.findById(id);
        return ResultGenerator.genSuccessResult(orderList);
    }

    @CrossOrigin
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Orderlist> list = orderService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @CrossOrigin
    @GetMapping("/countOrder")
    public Result countOrder(){
        List<Orderlist> list=orderService.findAll();
        int length=list.size();
        return ResultGenerator.genSuccessResult(length);
    }

    @CrossOrigin
    @PostMapping("/createOrder")
    @Transactional
    public Result createOrder(@RequestParam String orderlist, @RequestParam String wishlist, HttpServletRequest request) throws Exception {
        //创建订单分为两步：创建订单和删除购物车（以及尚未存在的减少库存之类的操作）
        //创建订单
        String userUUID = getUserSession(request);
        Result resultOrder=orderService.createOrder(orderlist,userUUID);
        if(resultOrder.getCode()==400){
            return ResultGenerator.genFailResult("createOrder fail");
        }
        Orderlist orderList=(Orderlist)resultOrder.getData();
        if (null == resultOrder) {
            return ResultGenerator.genFailResult("failed");
        }
        orderService.save(orderList);
        //删除相应的购物车
        Wishlist findwishlist = wishlistService.findBy("owner", userUUID);
        if (null == findwishlist) {
            return ResultGenerator.genFailResult("failed");
        }
        //这里需要将分成两个接口的参数放在一起传递
        String result = wishlistService.removeWishlist(wishlist, findwishlist);
        if (null == result) {
            return ResultGenerator.genFailResult("failed");
        }
        findwishlist.setItems(result);
        wishlistService.update(findwishlist);
        return ResultGenerator.genSuccessResult(orderList.getUuid() + "," + orderList.getPrice());
    }

    @CrossOrigin
    @PostMapping("/setBill")
    public Result setBill(@RequestParam String orderUUID, @RequestParam String billUUID) {
        Orderlist orderList = orderService.findById(orderUUID);
        orderList.setBill(billUUID);
        orderList.setPaid(true);
        return ResultGenerator.genSuccessResult("success");
    }

    @CrossOrigin
    @PostMapping("/setFinish")
    public Result setFinish(@RequestParam String orderUUID) {
        Orderlist orderList = orderService.findById(orderUUID);
        orderList.setFinish(true);
        return ResultGenerator.genSuccessResult("success");
    }


    @CrossOrigin
    @GetMapping("/listOrderByUser")
    public Result listOrderByUser(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size, HttpServletRequest request) throws Exception {
        PageHelper.startPage(page, size);
        String userUUID = getUserSession(request);
        Condition condition = new Condition(Orderlist.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("buyer", userUUID);
        List<Orderlist> list = orderService.findByCondition(condition);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }


    private String getUserSession(HttpServletRequest request) {
        HttpSession session = null;
        session = request.getSession();
        String redisuuid = null;
        String uuid = null;
        auth = Auth.getInstance();
        if (auth == null) {
            return null;
        }
        if (session != null) {
            redisuuid = (String) session.getAttribute("uuid");
            if (redisuuid != null) {
                try {
                    uuid = auth.getSession(redisuuid);
                    if (uuid != null) {
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
