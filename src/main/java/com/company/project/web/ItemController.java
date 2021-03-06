package com.company.project.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.company.project.core.Auth;
import com.company.project.core.Feedback;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.model.Item;
import com.company.project.model.User;
import com.company.project.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import okhttp3.*;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;
//import org.springframework.http.MediaType;
import javax.annotation.Resource;
import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    private Auth auth;

    private static final int recommendNum = 4;

    private String getUTF8(String s) {
        return new String(s.getBytes(), StandardCharsets.UTF_8);
    }

    public Item getItemInfo(Map<String, String> params) {
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

    @CrossOrigin
    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Item> list = itemService.findAll();
        int length = list.size();
        PageInfo<Item> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
    @CrossOrigin
    @PostMapping("/countItem")
    public Result countItem(){
        List<Item> list = itemService.findAll();
        int length = list.size();
        return ResultGenerator.genSuccessResult(length);
    }

    @CrossOrigin
    @PostMapping("/addItem")
    public Result addItem(@RequestParam Map<String, String> params, HttpServletRequest request) {
        Item item = getItemInfo(params);
        String userUUID = getUserSession(request);
        item.setOwner(userUUID);
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
    @PostMapping
    public Result changeItemState(@RequestParam String uuid){
        Item item=itemService.findById(uuid);
        Boolean saleState=item.getOnsale();
        item.setOnsale(!saleState);
        itemService.update(item);
        return ResultGenerator.genSuccessResult("change sale state success");
    }

    @CrossOrigin
    @GetMapping("/listAll")
    public Result listAllItem(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size, HttpServletRequest request) {
        PageHelper.startPage(page, size);
        Condition condition = new Condition(Item.class);
        Example.Criteria criteria = condition.createCriteria();
        listItemFilter(criteria);
        List<Item> list = itemService.findByCondition(condition);
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
    ????????????url????????????
     */
    @PostMapping("/storePicture")
    public Result storePicture(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        if (file.isEmpty()) {
            return ResultGenerator.genFailResult("????????????,???????????????");
        }
        String fileName = file.getOriginalFilename();
        int idx = fileName.lastIndexOf(".");
        String extention = fileName.substring(idx);
        String uuidFileName = UUID.randomUUID().toString().replace("-", "") + extention;
        String filePath = System.getProperty("user.dir") + "/picture/avatar/";
        String avatarUri = filePath + uuidFileName;
        File dest = new File(avatarUri);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultGenerator.genSuccessResult(uuidFileName);

    }

    /*
    ?????????????????????
     */

    @PostMapping(value = "getAvatar", produces = org.springframework.http.MediaType.IMAGE_PNG_VALUE)
    public Result getAvatar(@RequestParam String item, HttpServletRequest request) {//??????????????????

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
        return ResultGenerator.genFailResult("?????????????????????Url");
    }
    @GetMapping(value = "getItemRecommend")
    public Result getItemRecommend(@RequestParam(value = "itemUUID") String itemUUID, HttpServletRequest request) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String theUrl = "http://45.77.21.236:8087/api/item/"+itemUUID+"/neighbors?n="+recommendNum;
        Request theRequest = new Request.Builder()
                .url(theUrl)
                .method("GET",null)
                .build();
        Response response = client.newCall(theRequest).execute();
        if(response.code()!=200){
            return ResultGenerator.genFailResult("fail");
        }
        String json = response.body().string();
        JSONArray jsonArray = JSONArray.parseArray(json);
        List<String> list = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            list.add((String)jsonObject.get("Id"));
        }
        return ResultGenerator.genSuccessResult(list);
    }

    @GetMapping(value = "getRecommend")
    public Result getRecommend(HttpServletRequest request) throws IOException {
        String userUUID = getUserSession(request);
        userUUID = "408b1cfb-ce0f-4f41-b773-e916378e35f5";
        String theUrl = "";
        Request theRequest = null;
        RequestBody requestBody = null;
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        if(null == userUUID){
            theUrl = "http://45.77.21.236:8087/api/popular?n="+recommendNum;
            theRequest = new Request.Builder()
                    .url(theUrl)
                    .method("GET",requestBody)
                    .build();
            response = client.newCall(theRequest).execute();
            if(response.code() != 200){
                return ResultGenerator.genFailResult("fail");
            }
            String json = response.body().string();
            JSONArray jsonArray = JSONArray.parseArray(json);
            List<String> list = new ArrayList<>();
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add((String)jsonObject.get("Id"));
            }
            return ResultGenerator.genSuccessResult(list);
        }
        else{
            theUrl = "http://45.77.21.236:8087/api/recommend/"+userUUID+"?&n="+recommendNum;
            theRequest = new Request.Builder()
                    .url(theUrl)
                    .method("GET",requestBody)
                    .build();
            response = client.newCall(theRequest).execute();
            if(response.code() != 200){
                return ResultGenerator.genFailResult("fail");
            }
            String json = response.body().string();
            int length = json.length();
            String str = json.substring(1,length-1);
            String[] array = str.split(",");
            List<String> result = new ArrayList<>();
            for(int i=0;i<array.length;i++){
                if(i!=array.length-1){
                int theLength = array[i].length();
                result.add(array[i].substring(3,theLength-1));
                }
                else {
                    int theLength = array[i].length();
                    result.add(array[i].substring(3,theLength-2));
                }
            }
            return ResultGenerator.genSuccessResult(result);
        }
    }

    @PostMapping(value = "recordLike")
    public Result recordLike(@RequestParam(value = "itemUUID") String itemUUID, HttpServletRequest request) throws IOException {
        String userUUID = getUserSession(request);
        Feedback.feedback(userUUID,itemUUID,"like");
        return ResultGenerator.genSuccessResult();
    }



    private String getUserSession(javax.servlet.http.HttpServletRequest request){
        HttpSession session=null;
        session=request.getSession();
        String redisuuid=null;
        String uuid=null;
        auth= Auth.getInstance();
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

