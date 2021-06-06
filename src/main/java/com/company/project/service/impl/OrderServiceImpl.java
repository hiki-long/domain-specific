package com.company.project.service.impl;

import com.company.project.dao.OrderListMapper;
import com.company.project.model.Orderlist;
import com.company.project.service.OrderService;
import com.company.project.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
@Service
@Transactional
public class OrderServiceImpl extends AbstractService<Orderlist> implements OrderService {
    @Resource
    private OrderListMapper orderListMapper;

}
