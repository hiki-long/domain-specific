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
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
@Service
@Transactional
public class WishlistServiceImpl extends AbstractService<Wishlist> implements WishlistService {
    @Resource
    private WishlistMapper wishlistMapper;

    @Override
    public String removeWishlist(String wishlist, Wishlist findWishlist) {
        ArrayList<String> removeItems = new ArrayList<>();
        JSONArray tempjsonArray1 = JSONObject.parseArray(wishlist);
        for (int i = 0; i < tempjsonArray1.size(); i++) {
            JSONObject jsonObject = (JSONObject) tempjsonArray1.get(i);
            Map<String, Object> map = jsonObject.getInnerMap();
            String removeItem = (String) map.get("uuid");
            removeItems.add(removeItem);
        }
        boolean hasRemove = false;
        if (findWishlist == null) {
            return null;
        } else {
            JSONArray jsonArray = JSONObject.parseArray(findWishlist.getItems());
            ArrayList<Map<String, Object>> list = new ArrayList<>();

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Map<String, Object> curMap = jsonObject.getInnerMap();
                if (removeItems.contains((String) (curMap.get("id")))) {
                    hasRemove = true;
                } else {
                    list.add(curMap);
                }
            }
            if (!hasRemove) {
                return null;
            }
            String result = JSONObject.toJSONString(list);
            return result;
        }
    }
}
