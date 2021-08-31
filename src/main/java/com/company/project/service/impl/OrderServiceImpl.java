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
import com.company.project.web.OrderListController;
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


    public Orderlist createOrder(String orderlist,String userUUID) {
        List<ItemNumber> itemNumbers = new ArrayList<>();
        String sellers=new String();
        JSONArray json = JSONObject.parseArray(orderlist);
        for (int i = 0; i < json.size(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            ItemNumber itemNumber = new ItemNumber();
            itemNumber.itemUUID = jsonObject.get("itemUUID").toString();
            itemNumber.number = Integer.parseInt(jsonObject.get("number").toString());
            itemNumber.owner = jsonObject.get("owner").toString();
            itemNumber.price = itemService.findById(itemNumber.itemUUID).getPrice();
            sellers+=itemNumber.owner+",";
            itemNumbers.add(itemNumber);
            itemService.reduceItem(itemNumber.itemUUID,itemNumber.number);
        }
        Date time = new Date();
        Orderlist orderList = new Orderlist();
        orderList.setUuid(UUID.randomUUID().toString());
        orderList.setItems(itemNumbers.toString());
        orderList.setBuyer(userUUID);
        orderList.setDelivery("暂无数据");
        orderList.setPrice(getTotalPrice(itemNumbers));
        orderList.setSeller(sellers);
        orderList.setTime(time);
        orderList.setPaid(true);
        orderList.setFinish(false);
        return orderList;
    }

    public float getTotalPrice(List<ItemNumber> itemNumbers) {
        int res = 0;
        for (ItemNumber i : itemNumbers) {
            res += i.number * i.price;
        }
        return res;
    }
}
