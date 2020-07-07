package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Area;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AreaDaoTest extends BaseTest
{

    @Autowired
    private AreaDao areaDao;

    @Test
    @Ignore
    public void testQueryArea()
    {
        List<Area> areaList = areaDao.queryArea();
        if(areaList.size() == 2)
        {
            System.out.println("成功");
        }
    }
}

