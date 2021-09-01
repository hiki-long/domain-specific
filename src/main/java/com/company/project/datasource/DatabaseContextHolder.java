package com.company.project.datasource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Lixuhang
 * @date 2021/9/1
 * @whatItFor
 */
public class DatabaseContextHolder {
    private static final ThreadLocal<DatabaseType> contextHolder = new ThreadLocal<>();
    private static final AtomicInteger counter = new AtomicInteger(-1);

    public static void set(DatabaseType databaseType){
        contextHolder.set(databaseType);
    }
    public static DatabaseType get(){
        return contextHolder.get();
    }

    public static void setDataBaseMaster(){
        set(DatabaseType.master);
        System.out.println("====>切换到master");
    }
    public static void setDataBaseSlave(){
        int index = counter.getAndIncrement() % 2;
        if(counter.get() > 9999){
            counter.set(-1);
        }
        if(index == 0){
            set(DatabaseType.slave1);
            System.out.println("====>切换到slave1");
        }
        else {
            set(DatabaseType.slave2);
            System.out.println("====>切换到slave2");
        }
    }

}
