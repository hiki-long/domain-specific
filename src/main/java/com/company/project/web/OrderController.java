package com.company.project.web;

import com.alibaba.fastjson.JSON;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.model.Item;
import com.company.project.model.Order;
import com.company.project.model.User;
import com.company.project.service.OrderService;
import com.company.project.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by CodeGenerator on 2021/04/21.
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

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

    @PostMapping("/detail")
    public Result detail(@RequestParam String id) {
        Order order = orderService.findById(id);
        return ResultGenerator.genSuccessResult(order);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Order> list = orderService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @PostMapping("/createOrder")
    public Result createOrder(@RequestParam Map<String,String> params){
        String itemData=params.get("itemData");
        List<Item> items=JSON.parseArray(itemData,Item.class);
        String buyer=params.get("buyer");
        String delivery=params.get("delivery");
        Date time=new Date();
        int paid=Integer.parseInt(params.get("paid"));
        //TODO:finish项的类型需要确定
        String comment=params.get("comment");

        Order order=new Order();
        order.setItems(JSON.toJSONString(items));
        order.setBuyer(buyer);
        order.setDelivery(delivery);
        order.setTime(time);
        order.setPaid(false);
        order.setFinish(false);
        order.setComment(comment);
        try {
            orderService.save(order);
        } catch (Exception e) {
            return ResultGenerator.genFailResult("failed");
        }
        return ResultGenerator.genSuccessResult("success");
    }

    @PostMapping("/setBill")
    public Result setBill(@RequestParam String orderUUID,String billUUID){
        Order order=orderService.findById(orderUUID);
        order.setBill(billUUID);
        order.setPaid(true);
        return ResultGenerator.genSuccessResult("success");
    }

    @PostMapping("/setFinish")
    public Result setFinish(@RequestParam String orderUUID){
        Order order=orderService.findById(orderUUID);
        order.setFinish(true);
        return ResultGenerator.genSuccessResult("success");
    }

    @PostMapping("/listOrderByUUID")
    public Result listOrderByUUID(@RequestParam String UUID){

        return ResultGenerator.genSuccessResult("succeess");
    }
}
