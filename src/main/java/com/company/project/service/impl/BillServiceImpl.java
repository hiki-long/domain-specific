package com.company.project.service.impl;

import com.company.project.dao.BillMapper;
import com.company.project.model.Bill;
import com.company.project.service.BillService;
import com.company.project.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
@Service
@Transactional
public class BillServiceImpl extends AbstractService<Bill> implements BillService {
    @Resource
    private BillMapper billMapper;

}
