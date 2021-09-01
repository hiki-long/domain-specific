package com.company.project.core;

import com.company.project.datasource.DatabaseContextHolder;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author Lixuhang
 * @date 2021/9/1
 * @whatItFor
 */
@Aspect
@Component
public class DataSourceAop {
    @Before("@annotation(com.company.project.dao.ReadDataSource)")
    public void readPoint(){
        DatabaseContextHolder.setDataBaseSlave();
    }
    @Before("@annotation(com.company.project.dao.MasterDataSource)")
    public void writePoint(){
        DatabaseContextHolder.setDataBaseMaster();
    }


}
