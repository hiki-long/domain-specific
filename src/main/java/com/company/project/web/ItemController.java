package com.company.project.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.model.Item;
import com.company.project.model.User;
import com.company.project.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    private String getUTF8(String s) {
        return new String(s.getBytes(), StandardCharsets.UTF_8);
    }

    private Item getItemInfo(Map<String, String> params) {
        Item item = new Item();
        item.setName(getUTF8(params.get("name")));
        item.setOwner(getUTF8(params.get("owner")));
        item.setRemain(Integer.parseInt(params.get("remain")));
        item.setType(params.get("type"));
        item.setOnsale(Boolean.parseBoolean(params.get("onSale")));
        item.setDescription(getUTF8(params.get("description")));
        item.setImage("[\"" + params.get("image") + "\"]");
        item.setPrice(Double.parseDouble(params.get("price")));
        return item;
    }

    private void listItemFilter(Example.Criteria criteria) {
        criteria.andEqualTo("onsale", true);
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

    @GetMapping("/detail")
    public Result detail(@RequestParam String id) {
        Item item = itemService.findById(id);
        return ResultGenerator.genSuccessResult(item);
    }

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Item> list = itemService.findAll();
        int length = list.size();
        PageInfo<Item> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @CrossOrigin
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

    @CrossOrigin
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

    @CrossOrigin
    @GetMapping("/listByOwner")
    public Result listOwnerItem(@RequestParam String ownerName, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size, HttpServletRequest request) {
        PageHelper.startPage(page, size);
        Condition condition = new Condition(Item.class);
        Example.Criteria criteria = condition.createCriteria();
        listItemFilter(criteria);
        criteria.andEqualTo("owner", ownerName);
        List<Item> list = itemService.findByCondition(condition);
        PageInfo<Item> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @CrossOrigin
    @GetMapping("/listAll")
    public Result listAllItem(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size, HttpServletRequest request) {
        PageHelper.startPage(page, size);
        Condition condition = new Condition(Item.class);
        Example.Criteria criteria = condition.createCriteria();
        listItemFilter(criteria);
        List<Item> list = itemService.findByCondition(condition);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getDescription());
            System.out.println(getUTF8(list.get(i).getDescription()));
        }
        PageInfo<Item> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @CrossOrigin
    @GetMapping("/listItemByType")
    public Result listItemByType(@RequestParam String type, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size, HttpServletRequest request) {
        PageHelper.startPage(page, size);
        Condition condition = new Condition(Item.class);
        Example.Criteria criteria = condition.createCriteria();
        listItemFilter(criteria);
        criteria.andLike("type", "%" + type + "%");
        List<Item> list = itemService.findByCondition(condition);
        PageInfo<Item> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @CrossOrigin
    @GetMapping("/listItemByPrice")
    public Result listItemByPrice(@RequestParam float lowPrice, @RequestParam float highPrice, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        Condition condition = new Condition(Item.class);
        Example.Criteria criteria = condition.createCriteria();
        listItemFilter(criteria);
        criteria.andBetween("price", lowPrice, highPrice);
        List<Item> list = itemService.findByCondition(condition);
        PageInfo<Item> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @CrossOrigin
    @GetMapping("/listItemByName")
    public Result listItemByName(@RequestParam String name, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        Condition condition = new Condition(Item.class);
        Example.Criteria criteria = condition.createCriteria();
        listItemFilter(criteria);
        criteria.andLike("name", "%" + name + "%");
        criteria.andLike("name", name + "%");
        criteria.andLike("name", "%" + name);
        List<Item> list = itemService.findByCondition(condition);
        PageInfo<Item> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /*
    将商品的url传给前端
     */
    @PostMapping("/storePicture")
    public Result storePicture(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        if (file.isEmpty()) {
            return ResultGenerator.genFailResult("上传失败,请选择文件");
        }
        String fileName = file.getOriginalFilename();
        int idx = fileName.lastIndexOf(".");
        String extention = fileName.substring(idx);
        String uuidFileName = UUID.randomUUID().toString().replace("-", "") + extention;
        String filePath = System.getProperty("user.dir") + "/src/main/resource/mystatic/";
        String avatarUri = filePath + uuidFileName;
        File dest = new File(avatarUri);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(uuidFileName);
        return ResultGenerator.genSuccessResult(uuidFileName);

    }

    /*
    获取相应的图片
     */
    @PostMapping(value = "getAvatar", produces = MediaType.IMAGE_PNG_VALUE)
    public Result getAvatar(@RequestParam String item, HttpServletRequest request) {//记得改返回值

        String pictureUrl = null;
        FileInputStream picInput = null;
        Item currItem = itemService.findById(item);
        pictureUrl = currItem.getImage();
        if (pictureUrl != null) {
            try {
                picInput = new FileInputStream(pictureUrl);
                return ResultGenerator.genSuccessResult(picInput.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    picInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ResultGenerator.genFailResult("没有相应地址的Url");
    }

}

