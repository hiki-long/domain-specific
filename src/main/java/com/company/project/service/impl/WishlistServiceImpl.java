package com.company.project.service.impl;

import com.company.project.dao.WishlistMapper;
import com.company.project.model.Wishlist;
import com.company.project.service.WishlistService;
import com.company.project.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
@Service
@Transactional
public class WishlistServiceImpl extends AbstractService<Wishlist> implements WishlistService {
    @Resource
    private WishlistMapper wishlistMapper;

}
