package com.company.project.dao;

import com.company.project.core.Mapper;
import com.company.project.model.Item;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


public interface ItemMapper extends Mapper<Item> {
    public int updateByVersion(@Param("remain") int remain, @Param("newversion") int newversion, @Param("uuid") String uuid, @Param("oldversion") int oldversion);

}