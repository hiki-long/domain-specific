package com.company.project.service.impl;

import com.company.project.dao.ItemMapper;
import com.company.project.model.Item;
import com.company.project.service.ItemService;
import com.company.project.core.AbstractService;
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

}
