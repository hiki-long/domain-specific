package com.company.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.company.project.core.ResultGenerator;
import com.company.project.dao.WishlistMapper;
import com.company.project.model.Wishlist;
import com.company.project.service.WishlistService;
import com.company.project.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
@Service
@Transactional
public class WishlistServiceImpl extends AbstractService<Wishlist> implements WishlistService {
    @Resource
    private WishlistMapper wishlistMapper;

    /**
     *
     * @param wishlist
     * @param findWishlist
     * 注意，需要考虑清楚传递的是删除的数字还是设定后结果的数字（应该设置后的数字）
     * @return
     */
    @Override
    public String removeWishlist(String wishlist, Wishlist findWishlist) {
        //将移出的对象进行列表,移出对象需要用Map来表示
        Map<String,Integer> removeItems = new HashMap<>();
        JSONArray tempjsonArray1 = JSONObject.parseArray(wishlist);
        for (int i = 0; i < tempjsonArray1.size(); i++) {
            JSONObject jsonObject = (JSONObject) tempjsonArray1.get(i);
            Map<String, Object> map = jsonObject.getInnerMap();
            String removeItem = (String) map.get("uuid");
            Integer removeNum = (Integer) map.get("number");
            removeItems.put(removeItem,removeNum);
        }
        if (findWishlist == null) {
            return null;
        } else {
            JSONArray jsonArray = JSONObject.parseArray(findWishlist.getItems());
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            Set<String> removeNames = removeItems.keySet();
            int removeSize = removeNames.size();
            JSONArray newJsonArray = new JSONArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Map<String, Object> curMap = jsonObject.getInnerMap();
                String UUID = (String)curMap.get("id");
                if (removeNames.contains(UUID)){
                    Integer beforeNum = (Integer) curMap.get("num");
                    Integer changeNum = removeItems.get(UUID);
                    if(beforeNum - changeNum < 0){
                        throw new RuntimeException();
                    }
                    Integer afterNum = beforeNum - changeNum;
                    //修改数字
                    curMap.put(UUID,afterNum);
                    if(afterNum != 0){
                        newJsonArray.add(jsonObject);
                    }
                    removeSize--;
                }
                else {
                    newJsonArray.add(jsonObject);
                }
            }
            if(removeSize != 0){
                throw new RuntimeException("删除数和购物车数不匹配");
            }
            String result =newJsonArray.toJSONString();
            return result;
        }
    }

}
