package com.company.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.dao.OrderListMapper;
import com.company.project.model.Orderlist;
import com.company.project.service.ItemService;
import com.company.project.service.OrderService;
import com.company.project.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
@Service
@Transactional
public class OrderServiceImpl extends AbstractService<Orderlist> implements OrderService {
    @Resource
    private OrderListMapper orderListMapper;
    @Resource
    private ItemService itemService;

    private class OrderItemInfo implements Serializable {
        String itemUUID;
        Integer number;
        String owner;
        double totalPrice;
        String url;
        String name;

        public String getItemUUID(){
            return itemUUID;
        }
        public String getOwner(){
            return owner;
        }
        public Integer getNumber(){
            return number;
        }
        public double getTotalPrice(){
            return totalPrice;
        }
        public String getUrl(){
            return url;
        }
        public String getName(){
            return name;
        }
    }


    public Result createOrder(String orderlist,String userUUID) {
        float totalPrice=0;
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();
        String sellers=new String();
        JSONArray json = JSONObject.parseArray(orderlist);
        for (int i = 0; i < json.size(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            OrderItemInfo orderItemInfo = new OrderItemInfo();
            orderItemInfo.itemUUID = jsonObject.get("itemUUID").toString();
            orderItemInfo.number = Integer.parseInt(jsonObject.get("number").toString());
            orderItemInfo.owner = jsonObject.get("owner").toString();
            orderItemInfo.totalPrice = itemService.findById(orderItemInfo.itemUUID).getPrice()*orderItemInfo.number;
            orderItemInfo.url=itemService.findById(orderItemInfo.itemUUID).getImage();
            orderItemInfo.name=itemService.findById(orderItemInfo.itemUUID).getName();
            sellers+= orderItemInfo.owner+",";
            Result result=itemService.reduceItem(orderItemInfo.itemUUID, orderItemInfo.number);
            if(result.getCode()==400){
                return result;
            }
            orderItemInfos.add(orderItemInfo);
            totalPrice+=orderItemInfo.totalPrice;
        }
        Date time = new Date();
        Orderlist orderList = new Orderlist();
        orderList.setUuid(UUID.randomUUID().toString());
        orderList.setItems(JSONObject.toJSONString(orderItemInfos));
        orderList.setBuyer(userUUID);
        orderList.setDelivery("暂无数据");
        orderList.setPrice(totalPrice);
        orderList.setSeller(sellers);
        orderList.setTime(time);
        orderList.setPaid(true);
        orderList.setFinish(false);
        return ResultGenerator.genSuccessResult(orderList);
    }
}
