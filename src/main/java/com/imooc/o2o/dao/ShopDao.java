package com.imooc.o2o.dao;

import com.imooc.o2o.entity.Shop;
import org.springframework.stereotype.Component;


public interface ShopDao
{
    /**
     * 功能：新增店铺
     *返回值为1，表示插入成功，-1表示插入失败
     * @param shop
     * @return
     */
    int insertShop(Shop shop);

    /**
     * 更新店铺信息
     * @param shop
     * @return
     */
    int updateShop(Shop shop);

    /**
     * 通过shop id查询店铺
     * @param shopId
     * @return
     */
    Shop queryByShopId(long shopId);


}
