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

    @Override
    public Result reduceItem(String uuid, int number){
        Result result =itemController.detail(uuid);
        Item item=(Item)result.getData();
        if(item!=null){
            int remain=item.getRemain();
            remain-=number;
            if(remain>=0){
                item.setRemain(remain);
                itemService.update(item);
            }else{
                throw new RuntimeException();
            }
        }else{
            throw new RuntimeException();
        }
        return ResultGenerator.genSuccessResult("success");
    }

}
