package com.imooc.o2o.util;

import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.junit.Test;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.Position;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ImageUtil
{
    //basePath = "/Users/renjie/Desktop/image/"
    private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final Random r = new Random();

    /**
     * 返回缩略图的相对路径
     * 把生成的缩略图放到对应的文件夹下面
     * @param thumbnailInputStream
     * @param targetAddr
     * @return
     * @throws IOException
     */
    public static String generateThumbnail(InputStream thumbnailInputStream, String fileName,String targetAddr) throws IOException
    {
        String readFileName = getRandomFileName();
        String extension = getFileExtension(fileName);
        //System.out.println(extension);
        makeDirPath(targetAddr);
        String relativeAddr = targetAddr+readFileName+extension;
        File dest = new File(PathUtil.getImageBasePath()+relativeAddr);
        try {
            Thumbnails.of(thumbnailInputStream).size(200,200).watermark(Positions.BOTTOM_RIGHT,
                    ImageIO.read(new File(basePath+"/watermark.jpg")), 0.25f).outputQuality(0.8f).toFile(dest);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //System.out.println(relativeAddr);//upload/item/shop/13/2020062820380198749.jpg
        return relativeAddr;

    }

    /**
     * 创建目标路径所涉及的目录即/home/work/xiangze/xxx.jpg
     * 那么home work xiangze这三个文件夹都要自动创建
     * @param targetAddr
     */
    public static void makeDirPath(String targetAddr)
    {
        String realFileParentPath = PathUtil.getImageBasePath()+targetAddr;
        File dirPath = new File(realFileParentPath);
        if(!dirPath.exists())
        {
            dirPath.mkdirs();
        }
    }

    /**
     * 获取输入文件流的扩展名
     * 获得文件的后缀，比如.jpg
     * @param fileName
     * @return
     */
    public static String getFileExtension(String fileName)
    {


        return fileName.substring(fileName.lastIndexOf("."));

    }


    /**
     * 生成随机文件名，当前年月日小时分钟秒+五位随机数
     * @return
     */
    public static String getRandomFileName()
    {
        //获取随机的五位数
        int rannum = r.nextInt(89999)+10000;
        //获取当前时间，按年月日秒返回
        String nowTimeStr = sDateFormat.format(new Date());

        return nowTimeStr+rannum;
    }

    public static void main(String[] args) throws IOException {

        Thumbnails.of(new File("/Users/renjie/Desktop/SSM到Spring Boot-从零开发校园商铺平台/images/item/test/1.jpg")).
                size(200,200).watermark(Positions.BOTTOM_RIGHT,
                ImageIO.read(new File("/Users/renjie/Desktop/SSM到Spring-Boot-从零开发校园商铺平台/images/item/test/1.jpg")),
                0.25f).outputQuality(0.8f).toFile("/Users/renjie/Desktop/SSM到Spring Boot-从零开发校园商铺平台/images/item/test/3.jpg");

    }

    /**
     * storePath是文件的路径还是目录的路径
     * 如果storePath是文件路径则删除该文件
     * 如果storePath是目录路径则删除该目录下的所有文件
     * @param storePath
     */
    public static void deleteFileOrPath(String storePath)
    {

        File fileOrPath = new File(PathUtil.getImageBasePath()+storePath);
        if(fileOrPath.exists())
        {
            if(fileOrPath.isDirectory())
            {
                File files[] = fileOrPath.listFiles();
                for(int i=0;i<files.length;i++)
                {
                    files[i].delete();
                }
            }
            fileOrPath.delete();
        }


    }
}
