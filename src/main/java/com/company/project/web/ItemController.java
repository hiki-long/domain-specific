package com.company.project.web;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.model.Item;
import com.company.project.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by CodeGenerator on 2021/04/21.
 */



@RestController
@RequestMapping("/item")
public class ItemController {
    private class PageData{
        int length;
        PageInfo pageInfo;

        PageData(int length,PageInfo pageInfo){
            this.length=length;
            this.pageInfo=pageInfo;
        }
    }
    @Resource
    private ItemService itemService;

    private Item getItemInfo(Map<String, String> params) {
        String name = params.get("name");
        String owner = params.get("owner");
        int remain = Integer.valueOf(params.get("remain"));
        boolean onsale = Boolean.parseBoolean(params.get("onsale"));
        String description = params.get("description");
        Item item = new Item();
        item.setName(name);
        item.setOwner(owner);
        item.setDescription(description);
        item.setOnsale(onsale);
        item.setRemain(remain);
        return item;
    }

    @PostMapping("/add")
    public Result add(Item item) {
        itemService.save(item);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam String id) {
        itemService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Item item) {
        try {
            itemService.update(item);
        } catch (Exception e) {
            return ResultGenerator.genFailResult("failed");
        }
        return ResultGenerator.genSuccessResult("success");
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam String id) {
        Item item = itemService.findById(id);
        return ResultGenerator.genSuccessResult(item);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Item> list = itemService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @PostMapping("/addItem")
    public Result addItem(@RequestParam Map<String, String> params, HttpServletRequest request) {
        Item item = getItemInfo(params);
        item.setUuid(UUID.randomUUID().toString());
        try {
            itemService.save(item);
        } catch (Exception e) {
            return ResultGenerator.genFailResult("failed");
        }
        return ResultGenerator.genSuccessResult("success");
    }

    @PostMapping("/updateItem")
    public Result updateItem(@RequestParam Map<String, String> params, HttpServletRequest request) {
        Item item = getItemInfo(params);
        try {
            itemService.update(item);
        } catch (Exception e) {
            return ResultGenerator.genFailResult("failed");
        }
        return ResultGenerator.genSuccessResult("success");

    }

    @PostMapping("/listAll")
    public Result listOwnerItem(@RequestParam String UUID, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size, HttpServletRequest request) {
        PageHelper.startPage(page, size);
        List<Item> list = itemService.findByIds(UUID);
        int length=list.size();
        PageInfo pageInfo = new PageInfo(list);
        PageData pageData=new PageData(length,pageInfo);
        return ResultGenerator.genSuccessResult(pageData);
    }

    @PostMapping("/listItemByType")
    public Result listItemByType(@RequestParam String type, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size, HttpServletRequest request) {
        PageHelper.startPage(page, size);
        List<Item> list = itemService.findByIds(type);
        int length=list.size();
        PageInfo pageInfo = new PageInfo(list);
        PageData pageData=new PageData(length,pageInfo);
        return ResultGenerator.genSuccessResult(pageData);
    }

}
