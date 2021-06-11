package com.company.project.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.company.project.core.Auth;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.model.Item;
import com.company.project.model.Orderlist;
import com.company.project.service.ItemService;
import com.company.project.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
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
    @Resource
    private ItemService itemService;
    private Auth auth;


    private class ItemNumber implements Serializable {
        String itemUUID;
        Integer number;
        String owner;
        double price;

        @Override
        public String toString(){
            return "itemUUID="+itemUUID+",number="+number;
        }
    }

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
    @PostMapping("/createOrder")
    public Result createOrder(@RequestParam String orderlist,HttpServletRequest request) throws Exception {
        List<ItemNumber> itemNumbers = new ArrayList<>();
        String sellers=new String();
        JSONArray json = JSONObject.parseArray(orderlist);
        String userUUID=getUserSession(request);
        for (int i = 0; i < json.size(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            ItemNumber itemNumber = new ItemNumber();
            itemNumber.itemUUID = jsonObject.get("itemUUID").toString();
            itemNumber.number = Integer.parseInt(jsonObject.get("number").toString());
            itemNumber.owner = jsonObject.get("owner").toString();
            itemNumber.price = itemService.findById(itemNumber.itemUUID).getPrice();
            sellers+=itemNumber.owner+",";
            itemNumbers.add(itemNumber);
        }
        Date time = new Date();
        Orderlist orderList = new Orderlist();
        orderList.setUuid(UUID.randomUUID().toString());
        orderList.setItems(itemNumbers.toString());
        orderList.setBuyer(userUUID);
        orderList.setBuyer("");
        orderList.setDelivery("暂无数据");
        orderList.setPrice(getTotalPrice(itemNumbers));
        orderList.setSeller(sellers);
        orderList.setTime(time);
        orderList.setPaid(true);
        orderList.setFinish(false);
        try {
            orderService.save(orderList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("failed");
        }
        return ResultGenerator.genSuccessResult(orderList.getUuid()+","+orderList.getPrice());
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
        String userUUID=getUserSession(request);
        Condition condition = new Condition(Orderlist.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("uuid", userUUID);
//        criteria.andEqualTo("buyer", "408b1cfb-ce0f-4f41-b773-e916378e35f5");
        List<Orderlist> list = orderService.findByCondition(condition);
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
