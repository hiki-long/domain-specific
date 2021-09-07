package com.company.project.service.impl;

import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.dao.ItemMapper;
import com.company.project.model.Item;
import com.company.project.service.ItemService;
import com.company.project.core.AbstractService;
import com.company.project.web.ItemController;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
@Service
@Transactional
public class ItemServiceImpl extends AbstractService<Item> implements ItemService {
    @Resource
    private ItemMapper itemMapper;

    private ItemController itemController;

    private ItemService itemService;

    private static int tryTime = 5;

    public void reduceItem(String uuid,int number) {
        int thisTryTime = tryTime;
        while (thisTryTime > 0) {
            Result result = itemController.detail(uuid);
            Item item = (Item) result.getData();
            if (item != null) {
                int oldversion = item.getVersion();
                int remain = item.getRemain();
                int newversion = oldversion + 1;
                remain -= number;
                if (remain >= 0) {
                    int affect = itemMapper.updateByVersion(remain, newversion, uuid, oldversion);
                    if (affect != 0) {
                        return;
                    } else {
                        thisTryTime--;
                    }
                } else {
                    throw new RuntimeException();
                }

            } else {
                throw new RuntimeException();
            }
        }
    }



}
