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
    @Resource
    private ItemService itemService;

    @PostMapping("/add")
    public Result add(Item item) {
        itemService.save(item);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        itemService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Item item) {
        itemService.update(item);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
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
        item.setUuid(UUID.randomUUID().toString());
        try {
            itemService.save(item);
        } catch (Exception e) {
            return ResultGenerator.genFailResult("failed");
        }
        return ResultGenerator.genSuccessResult("success");
    }
}
