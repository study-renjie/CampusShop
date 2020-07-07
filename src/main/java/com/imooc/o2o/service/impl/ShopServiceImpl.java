package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.ShopDao;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Service
public class ShopServiceImpl implements ShopService
{

    @Autowired
    private ShopDao shopDao;

    @Override
    @Transactional
    public ShopExecution addShop(Shop shop, InputStream shopImgInputStream, String fileName)
    {
        //空值判断
        if(shop == null)
        {
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }

        try
        {
            //给店铺信息赋初始值
            shop.setEnableStatus(0);
            shop.setCreateTime(new Date());
            shop.setLastEditTime(new Date());
            //添加店铺信息
            int effectedNum = shopDao.insertShop(shop);
            if(effectedNum<0)
            {
                //如果RuntimeException异常的时候，事务可能才会回滚，否则不会回滚。
                throw new ShopOperationException("店铺创建失败");
            }
            else
            {
                if(shopImgInputStream !=null)
                {
                    try {
                        //存储图片
                        addShopImg(shop, shopImgInputStream,fileName);
                        // shop.getShopImg();
                    }
                    catch (Exception e)
                    {
                        throw new ShopOperationException("addShopImg error："+e.getMessage());
                    }
                    //更新店铺的图片地址
                    effectedNum = shopDao.updateShop(shop);
                    if(effectedNum<=0)
                    {
                        throw new ShopOperationException("更新图片地址失败");
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new ShopOperationException("addShop error:"+e.getMessage());
        }
        return new ShopExecution(ShopStateEnum.CHECK,shop);
    }

    @Override
    public Shop getByShopId(long shopId) {
        return shopDao.queryByShopId(shopId);
    }

    @Override
    public ShopExecution modifyShop(Shop shop, InputStream shopImgInputStream, String fileName) throws ShopOperationException {
        if(shop == null || shop.getShopId()==null)
        {
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }
        else
        {
            //1、判断是否需要处理图片
            try
            {
                if(shopImgInputStream!=null && fileName!=null && !"".equals(fileName))
                {
                    //通过shopId获取之前的Shop对象。
                    Shop tempShop = shopDao.queryByShopId(shop.getShopId());
                    if(tempShop.getShopImg()!=null)
                    {
                        ImageUtil.deleteFileOrPath(tempShop.getShopImg());
                    }
                    addShopImg(shop, shopImgInputStream, fileName);
                }
                //2、更新店铺信息
                shop.setLastEditTime(new Date());
                int effectedNum = shopDao.updateShop(shop);
                if(effectedNum<=0)
                {
                    return new ShopExecution(ShopStateEnum.INNER_ERROR);
                }
                else
                {
                    shop = shopDao.queryByShopId(shop.getShopId());
                    return new ShopExecution(ShopStateEnum.SUCCESS,shop);
                }
            }
            catch (Exception e)
            {
                throw new ShopOperationException("modifyShop error:"+e.getMessage());
            }


        }


    }

    private void addShopImg(Shop shop, InputStream shopImgInputStream,String fileName) throws IOException {
        //获取shop图片目录的相对路径
        String dest = PathUtil.getShopImagePath(shop.getShopId());
        String shopImgAddr = ImageUtil.generateThumbnail(shopImgInputStream, fileName,dest);
        shop.setShopImg(shopImgAddr);
    }
}
