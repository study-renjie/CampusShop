package com.imooc.o2o.dao;

import com.imooc.o2o.entity.ShopCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopCategoryDao
{
    /**
     * 返回所有的shopCategory即所有店铺总类
     * ShopCategory类有一个属性，是ShopCategory parent，
     * 表示这个类别的父类别
     * 表示这个查询如果传入的shopCategoryCondition不为null，且有父类别，那么就返回父类别的数据
     * @param shopCategoryCondition
     * @return
     */
    List<ShopCategory> queryShopCategory(@Param("shopCategoryCondition")ShopCategory shopCategoryCondition);
}
