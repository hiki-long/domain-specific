package com.company.project.service;

import com.company.project.model.Orderlist;
import com.company.project.core.Service;


/**
 * Created by CodeGenerator on 2021/04/21.
 */
public interface OrderService extends Service<Orderlist> {

    Orderlist createOrder(String orderlist, String userUUID);


}
