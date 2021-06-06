package com.company.project.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.model.Item;
import com.company.project.model.Order;
import com.company.project.model.User;
import com.company.project.service.ItemService;
import com.company.project.service.OrderService;
import com.company.project.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.Serializable;
import java.sql.Struct;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by CodeGenerator on 2021/04/21.
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private ItemService itemService;

    private class ItemNumber implements Serializable {
        String itemUUID;
        Integer number;
        String owner;
        String buyer;
        double price;

        @Override
        public String toString(){
            return "itemUUID="+itemUUID+",number="+number;
        }
    }

    @PostMapping("/add")
    public Result add(Order order) {
        orderService.save(order);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam String id) {
        orderService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Order order) {
        orderService.update(order);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/detail")
    public Result detail(@RequestParam String id) {
        Order order = orderService.findById(id);
        return ResultGenerator.genSuccessResult(order);
    }

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Order> list = orderService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @CrossOrigin
    @PostMapping("/createOrder")
    public Result createOrder(@RequestParam String orderlist) {
        List<ItemNumber> itemNumbers = new ArrayList<>();
        String sellers=new String();
        JSONArray json = JSONObject.parseArray(orderlist);
        for (int i = 0; i < json.size(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            ItemNumber itemNumber = new ItemNumber();
            itemNumber.itemUUID = jsonObject.get("itemUUID").toString();
            itemNumber.number = Integer.parseInt(jsonObject.get("number").toString());
            itemNumber.owner = jsonObject.get("owner").toString();
            itemNumber.buyer = jsonObject.get("buyer").toString();
            itemNumber.price = itemService.findById(itemNumber.itemUUID).getPrice();
            sellers+=itemNumber.owner+",";
            itemNumbers.add(itemNumber);
        }


        Date time = new Date();
        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setItems(itemNumbers.toString());
        System.out.println(itemNumbers.toString());
        order.setBuyer(itemNumbers.get(0).buyer);
        order.setDelivery("暂无数据");
        order.setPrice(getTotalPrice(itemNumbers));
        order.setSeller(sellers );
        order.setTime(time);
        order.setPaid(true);
        order.setFinish(false);
        try {
            orderService.save(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("failed");
        }
        return ResultGenerator.genSuccessResult("success");
    }

    @CrossOrigin
    @PostMapping("/setBill")
    public Result setBill(@RequestParam String orderUUID, String billUUID) {
        Order order = orderService.findById(orderUUID);
        order.setBill(billUUID);
        order.setPaid(true);
        return ResultGenerator.genSuccessResult("success");
    }

    @CrossOrigin
    @PostMapping("/setFinish")
    public Result setFinish(@RequestParam String orderUUID) {
        Order order = orderService.findById(orderUUID);
        order.setFinish(true);
        return ResultGenerator.genSuccessResult("success");
    }

    @CrossOrigin
    @GetMapping("/listOrderByUUID")
    public Result listOrderByUUID(@RequestParam String buyer, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        Condition condition = new Condition(Item.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("buyer", buyer);
        List<Order> list = orderService.findByCondition(condition);
        int length = list.size();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    public float getTotalPrice(List<ItemNumber> itemNumbers) {
        int res = 0;
        for (ItemNumber i : itemNumbers) {
            res += i.number * i.price;
        }
        return res;
    }

}
