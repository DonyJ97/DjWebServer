package com.example.mywebserver.http;

/**
 * @ Author     ：djq.
 * @ Date       ：Created in 9:51 2020/3/13
 * @ Description：http协议规定的相关内容
 * @ Modified By：
 * @Version: $
 */
/*
* 创建HttpContext类，来管辖这些与http协议的相关内容
1.修改要发送的状态代码，不再是写死的200，这个值要改变成可以设置的
2.不用的状态代码有不同的描述，我们定义一个map来保存转态代码和描述，然后设置状态代码后自动找到对应的描述
3.改进响应中的响应头操作，根据用户客户端请求的不同实际静态资源来响应其类型值
*/
import java.util.HashMap;
import java.util.Map;

public class HttpContext {
    /**
     * 状态代码与对应的描述
     * key:状态代码
     * value:状态代码的描述
     */
    private static Map<Integer, String> STATUS_MAPPING=new HashMap<Integer,String>();
    /**
     * 介质类型映射
     * Key:文件名的后缀
     * value:Content-Type对应的值
     */
    private static Map<String, String>  MIME_TYPE_MAPPING=new HashMap<String,String>();
    public static Map<Integer, String> getSTATUS_MAPPING() {
        return STATUS_MAPPING;
    }
    static{
        //初始化
        initStatusMapping();
        initMineTypeMapping();
    }
    /**
     * 初始化状态代码和对应的描述
     */
    private static void initStatusMapping() {
        STATUS_MAPPING.put(200, "ok");
        STATUS_MAPPING.put(404, "Not Found");
        STATUS_MAPPING.put(500, "Internal Server Error");
    }
    private static void initMineTypeMapping() {
        MIME_TYPE_MAPPING.put("html", "text/html");
        MIME_TYPE_MAPPING.put("css", "text/css");
        MIME_TYPE_MAPPING.put("png", "image/png");
        MIME_TYPE_MAPPING.put("gif", "image/gif");
        MIME_TYPE_MAPPING.put("jpg", "image/jepg");
        MIME_TYPE_MAPPING.put("js", "application/javascript");

    }
    /**
     * 根据给定的状态代码获得对应的状态描述
     */
    public static String getStatusReason(int code){
        return STATUS_MAPPING.get(code);
    }
    /**
     * 根据资源后缀获取相对应的Content-type值
     * @param ext
     * @return
     */
    public static String getMimeType(String ext){
        return MIME_TYPE_MAPPING.get(ext);
    }
    public static void main(String[] args) {
        String fileName="jquery-1.8.3.min.js";
        String ext=fileName.substring(fileName.lastIndexOf(".")+1);
        String line=getMimeType(ext);
        System.out.println(line);
    }
}



