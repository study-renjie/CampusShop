package com.imooc.o2o.web.shopAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController
{

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopCategoryService shopCategoryService;

    @Autowired
    private AreaService areaService;

    /**
     * 修改店铺信息。
     * 修改店铺的时候，图片可以为null，也就是表示使用原来的图片，不修改。
     * @param request
     * @return
     */
    @RequestMapping(value = "/modifyshop",method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object>  modifyShop(HttpServletRequest request) {

        //1、接收并转化相应的参数，包括店铺信息以及图片信息
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        //获取前端传来的店铺信息
        String shopStr = HttpServletRequestUtil.getString(request, "shopStr");

        //jackson-databind中的类
        ObjectMapper mapper = new ObjectMapper();
        Shop shop = null;
        try {
            //将店铺信息转化为实体类
            shop = mapper.readValue(shopStr, Shop.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
            return modelMap;
        }
        CommonsMultipartFile shopImg = null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //判断请求是否包含文件
        if (commonsMultipartResolver.isMultipart(request)) {

            //获取前端传来的文件流，返回给shopImg
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");

        }

        //2、修改店铺信息
        if (shop != null && shop.getShopId() != null) {

            ShopExecution se = null;
            try {

                //图片可以为空
                if (shopImg == null) {
                    //不修改图片
                    se = shopService.modifyShop(shop, null, null);
                } else {
                    se = shopService.modifyShop(shop, shopImg.getInputStream(), shopImg.getOriginalFilename());

                }

                if (se.getState() == ShopStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMasg", se.getStateInfo());

                }
            } catch (IOException e) {
                modelMap.put("success", false);
                modelMap.put("errMasg", se.getStateInfo());
            }


            return modelMap;


        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入店铺信息");
            return modelMap;
        }
    }


    /**
     * 根据Id获取店铺信息，以及获取区域信息
      * @param request
     * @return
     */
    @RequestMapping(value = "/getshopbyid",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopById(HttpServletRequest request)
    {
        Map<String,Object> modelMap = new HashMap<String, Object>(); //返回值对象
        Long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        if(shopId > -1)
        {
            try
            {
                Shop shop = shopService.getByShopId(shopId);
                List<Area> areaList = areaService.getAreaList();
                modelMap.put("shop", shop);
                modelMap.put("areaList", areaList);
                modelMap.put("success",true);

            }
            catch (Exception e)
            {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
            }
        }
        else
        {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty shopId");
        }
        return modelMap;


    }


    @RequestMapping(value = "/getshopinitinfo",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopInfo()
    {
        Map<String,Object> modelMap = new HashMap<String, Object>();
        List<ShopCategory>  shopCategoryList = new ArrayList<>();
        List<Area> areaList = new ArrayList<>();
        try
        {
            shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory());
            areaList = areaService.getAreaList();
            modelMap.put("shopCategoryList",shopCategoryList);
            modelMap.put("areaList",areaList);
            modelMap.put("success", true);



        }
        catch (Exception e)
        {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
        }
        return modelMap;
    }



    /**
     * 注册店铺，注册店铺的时候，图片不能为null/
     * 使用到jackson-databind包，将实体类转换成json，或者将json转换成实体类。
     *
     *
      * @param request
     * @return
     */
    @RequestMapping(value = "/registershop",method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> registerShop(HttpServletRequest request)
    {

        //1、接收并转化相应的参数，包括店铺信息以及图片信息
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(!CodeUtil.checkVerifyCode(request))
        {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        //获取前端传来的店铺信息
        String shopStr = HttpServletRequestUtil.getString(request, "shopStr");


        ObjectMapper mapper = new ObjectMapper();
        Shop shop = null;
        try
        {
            //将店铺信息转化为实体类
            shop = mapper.readValue(shopStr, Shop.class);
        }
        catch (Exception e)
        {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
            return modelMap;
        }
        CommonsMultipartFile shopImg = null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //判断请求是否包含文件
        if(commonsMultipartResolver.isMultipart(request))
        {

            //获取前端传来的文件流，返回给shopImg
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
            shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");

        }
        else
        {
            modelMap.put("success", false);
            modelMap.put("errMsg", "上传图片不能为空");
            return modelMap;
        }
        //2、注册店铺
        if(shop!=null && shopImg!=null)
        {
            PersonInfo owner = (PersonInfo) request.getSession().getAttribute("user");

            //Session TODO
            //Tomcat默认的Session过期时间为30minutes

            shop.setOwner(owner);


            ShopExecution se = null;
            try {
                se = shopService.addShop(shop, shopImg.getInputStream(),shopImg.getOriginalFilename());
                if(se.getState() == ShopStateEnum.CHECK.getState())
                {
                    modelMap.put("success",true);
                    //该用户可以操作的店铺列表
                    @SuppressWarnings("unchecked")
                    List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
                    if(shopList == null || shopList.size() == 0)
                    {
                        shopList = new ArrayList<Shop>();

                    }
                    shopList.add(se.getShop());
                    request.getSession().setAttribute("shopList", shopList);
                }
                else
                {
                    modelMap.put("success", false);
                    modelMap.put("errMasg", se.getStateInfo());

                }
            } catch (IOException e) {
                modelMap.put("success", false);
                modelMap.put("errMasg", se.getStateInfo());
            }


            return modelMap;


        }
        else
        {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入店铺信息");
            return modelMap;
        }



    }

//    private static void inputStreamToFile(InputStream ins, File file)
//    {
//        FileOutputStream os = null;
//        try
//        {
//            os = new FileOutputStream(file);
//            int bytesRead = 0;
//            byte[] buffer = new byte[1024];
//            while((bytesRead =ins.read(buffer))!=-1)
//            {
//                os.write(buffer,0,bytesRead);
//            }
//
//        }
//        catch (Exception e)
//        {
//            throw new RuntimeException("调用inputStreamToFile产生异常"+e.getMessage());
//        }
//        finally
//        {
//            try
//            {
//                if(os!=null)
//                {
//                    os.close();
//                }
//                if(ins!=null)
//                {
//                    ins.close();
//                }
//            }
//            catch (IOException e)
//            {
//                throw new RuntimeException("inputStreamToFile关闭io产生异常："+e.getMessage());
//            }
//
//        }
//    }
}
