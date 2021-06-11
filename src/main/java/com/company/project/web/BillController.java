package com.company.project.web;

import com.company.project.core.Auth;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.model.Bill;
import com.company.project.model.Orderlist;
import com.company.project.service.BillService;
import com.company.project.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

/**
 * Created by CodeGenerator on 2021/04/21.
 */
@RestController
@RequestMapping("/bill")
public class BillController {
    @Resource
    private BillService billService;
    @Resource
    private OrderService orderService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private Auth auth;

    @PostMapping("/add")
    public Result add(Bill bill) {
        billService.save(bill);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam String id) {
        billService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Bill bill) {
        billService.update(bill);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/detail")
    public Result detail(@RequestParam String id) {
        Bill bill = billService.findById(id);
        return ResultGenerator.genSuccessResult(bill);
    }

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Bill> list = billService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @CrossOrigin
    @PostMapping("/createBill")
    public Result createBill(@RequestParam String uuid,HttpServletRequest request){
        Bill bill=new Bill();
        bill.setUuid(UUID.randomUUID().toString());
        bill.setPrice(orderService.findById(uuid).getPrice());
        bill.setType("wechat");
        bill.setPayment(UUID.randomUUID().toString());
        try{
            billService.save(bill);
        }catch(Exception e){
            return ResultGenerator.genFailResult("Failed");
        }
        return ResultGenerator.genSuccessResult(bill.getUuid());
    }

}
