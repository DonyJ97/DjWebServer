package com.example.mywebserver.http;

/**
 * @ Author     ：djq.
 * @ Date       ：Created in 23:09 2020/3/12
 * @ Description：响应对象 该类的每个实例用于表示发送给客户端的一个HTTP响应内容
 * @ Modified By：
 * @Version: $
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpResponse {
    /*
     * 状态行相关信息定义
     */
    //状态代码
    private int StatusCode = 200;
    //状态描述
    private String StatusReason = "OK";
    /*
     * 响应头相关信息定义
     */
    private Map<String,String> headers = new HashMap<String,String>();
    /*
     * 响应正文相关信息定义
     */
    private File entity;// 响应的实体文件

    /*
     * 定义与连接相关的属性
     */
    private Socket socket;
    private OutputStream out;

    public HttpResponse(Socket socket) {
        try {
            this.socket=socket;
            out = socket.getOutputStream();
            //flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 将当前响应对象内容以HTTP响应格式发送给客户端
     */
    public void flush() {
        /*
         * 发送状态行，响应头，响应正文
         */
        sendStatusLine();
        sendHeaders();
        sendContent();
    }

    /**
     * 发送状态行
     */
    private void sendStatusLine() {
        // 发送状态行
        try {
            String line = "HTTP/1.1"+" "+ StatusCode+" "+StatusReason;
            out.write(line.getBytes("ISO8859-1"));
            out.write(13);
            out.write(10);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("状态行发送完毕");
    }

    /**
     * 发送响应头
     */

    private void sendHeaders() {
        try {
            //遍历headers将所有的响应头发送给客户端
            Set<Entry<String, String>> header=headers.entrySet();
            for(Entry<String, String> e:header){
                String name=e.getKey();
                String value=e.getValue();
                String line=name+": "+value;
                out.write(line.getBytes("ISO8859-1"));
                out.write(13);
                out.write(10);
            }

            // 单独发送CRLF表示响应头发送完毕
            out.write(13);
            out.write(10);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("响应头发送完毕");
    }

    /**
     * 发送响应正文
     */

    private void sendContent() {
        try (FileInputStream fis = new FileInputStream(entity);) {
            int len = -1;
            byte[] data = new byte[1024 * 10];
            while ((len = fis.read(data)) != -1) {
                out.write(data, 0, len);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("响应正文发送完毕");
    }

    public File getEntity() {
        return entity;
    }

    /**
     * 设置响应正文的实体文件
     * 设置该文件，则意味着这个响应是包含正文的，而一个响应只要包含正文，一定会包含两个响应头
     * Content-Type和Content-Length，用于告知客户端正文的数据类型以及字节量
     *
     * @param entity
     */
    public void setEntity(File entity) {
        this.entity = entity;
        //Content-Length
        headers.put("Content-Length", entity.length()+"");

        //Content-Type
        //1获取该资源文件的后缀名
        String fileName=entity.getName();
        int index=fileName.lastIndexOf(".")+1;
        String ext=fileName.substring(index);
        //2根据后缀获得对应的Content-Type的值
        String line=HttpContext.getMimeType(ext);
        headers.put("Content-Type", line);
    }
    /**
     * 设置指定的状态代码，同时会制动设置对应的状态描述
     *
     *
     */

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
        this.StatusReason = HttpContext.getStatusReason(statusCode);
    }

    public void setStatusReason(String statusReason) {
        this.StatusReason = statusReason;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public String getStatusReason() {
        return StatusReason;
    }

    /**
     * 添加指定响应头
     *
     * @param name
     * @param value
     */
    public void putHeader(String name, String value) {
        this.headers.put(name, value);
    }

    /**
     * 获取指定的响应头的值
     *
     * @param name
     * @return
     */
    public String getHeader(String name) {
        return this.headers.get(name);
    }
}

