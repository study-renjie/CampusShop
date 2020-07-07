package com.imooc.o2o.util;

//处理地址的工具类
public class PathUtil
{
    //返回项目图片的根路径
    private static String seperator = System.getProperty("file.separator");

    /**
     * 返回存放缩略图的文件夹路径
     * @return
     */
    public static String getImageBasePath()
    {
        String os = System.getProperty("os.name");
        String basePath = "";
        if(os.toLowerCase().startsWith("win"))
        {
            basePath = "D:/image/";
        }
        else
        {
            //basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"image";
            //System.out.println(basePath);
            basePath = "/Users/renjie/Desktop/CampusShop/image/";
        }
        basePath = basePath.replace("/", seperator);
        return basePath;

    }


    /**
     * 根据不同的项目需求返回项目图片的子路径
     * @param shopId
     * @return
     */
    public static String getShopImagePath(long shopId)
    {
        String imagePath = "upload/item/shop/"+shopId+"/";
        return imagePath.replace("/", seperator);
    }

}
