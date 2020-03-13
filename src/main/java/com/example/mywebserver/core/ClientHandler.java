package com.example.mywebserver.core;

import com.example.mywebserver.ReadXML.ServerContext;
import com.example.mywebserver.http.HttpRequest;
import com.example.mywebserver.http.HttpResponse;
import com.example.mywebserver.servlet.HttpServlet;

import java.io.File;

import java.net.Socket;

/**
 * @ Author     ：djq.
 * @ Date       ：Created in 22:36 2020/3/12
 * @ Description：用户处理客户端请求并予以响应的处理类
 * @ Modified By：
 * @Version: $
 */
public class ClientHandler implements Runnable{
    private Socket socket;
    public ClientHandler(Socket socket){
        this.socket=socket;
    }
    public void run() {
        System.out.println("一个客户端连接了");
        try {
            /**
             * 获取输入流，用户读取客户端发送过来的内容，
             * 由于客户端（浏览器）发送过来的内容是Http协议规定的请求
             * 请求的内容大部分为文本数据，且字符集为ISO8859-1，内容为英文很数字符号。还可能包含二进制数据
             * 所以这里我们不能使用流链接字符的高级流，否则读取二进制数据部分时可能出现问题
             */
            /**
             * 处理客户端的请求分三步
             * 1.解析请求
             * 2.处理请求
             * 3.发送响应
             */
            //解析请求
            HttpRequest request=new HttpRequest(socket);
            //创建响应对象
            HttpResponse response = new HttpResponse(socket);
            //获取请求的抽象路径
            String path=request.getRequestURI();
            //判断该请求是否请求某个业务？还是只是请求静态页面资源
            //利用dom4j解析XML
            String servletName = ServerContext.getServletName(path);

            //利用反射来创建servlet处理业务
            if(servletName!=null){
                System.out.println("ClientHandler : 正在加载......"+servletName);
                Class cls = Class.forName(servletName);
                HttpServlet servlet = (HttpServlet)cls.newInstance();
                servlet.service(request,response);
            }else{
                //去webapps目录下面找对应的资源
                File file=new File("F://workspace/mywebserver/src/main/java/com/example/mywebserver/webapps"+path);
                System.out.println("path:"+path);
                //判断资源是否存在
                if(file.exists()){
                    System.out.println("资源找到了");
                    response.setEntity(file);
                }else{
                    System.out.println("资源未找到");
                    response.setStatusCode(404);
                    response.setEntity(new File("F://workspace/mywebserver/src/main/java/com/example/mywebserver/webapps/404NotFound.html"));
                }
            }


//            //是否为请求一个业务
//            if("/reg.html".equals(path)) {
//                //用户注册业务
//                RegServlet servlet = new RegServlet();
//                servlet.service(request, response);
//            }else if("/login.html".equals(path)){
//                LoginServlet servlet = new LoginServlet();
//                servlet.service(request, response);
//            }else{
//                //去webapps目录下面找对应的资源
//                File file=new File("F://workspace/mywebserver/src/main/java/com/example/mywebserver/webapps"+path);
//                System.out.println("path:"+path);
//                //判断资源是否存在
//                if(file.exists()){
//                    System.out.println("资源找到了");
//                    response.setEntity(file);
//                }else{
//                    System.out.println("资源未找到");
//                    response.setStatusCode(404);
//                    response.setEntity(new File("F://workspace/mywebserver/src/main/java/com/example/mywebserver/webapps/404NotFound.html"));
//                }
//            }
            response.flush();
            //String line=readLine(in);
            //System.out.println("line:"+line);
        } catch (Exception  e) {
            e.printStackTrace();
        }finally {
            try{
                //最后要与客户端断开连接
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
//    //在ClientHander里面添加一个方法：readLine，用于测试通过输入流读取一行客户端发送过来的字符串
//    private String readLine(InputStream in) throws IOException{
//        StringBuilder builder=new StringBuilder();
//        int cur=-1;//本次读的字符串
//        int pre=-1;//上次读的字符串
//        while((cur=in.read())!=-1){
//            if(cur==10&&pre==13){
//                break;
//            }
//            builder.append((char)cur);
//            pre=cur;
//        }
//        return builder.toString().trim();
//    }


}
