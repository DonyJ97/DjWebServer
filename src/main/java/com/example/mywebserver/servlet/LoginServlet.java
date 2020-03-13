package com.example.mywebserver.servlet;

/**
 * @ Author     ：djq.
 * @ Date       ：Created in 11:32 2020/3/13
 * @ Description：处理登录业务
 * @ Modified By：
 * @Version: $
 */
import com.example.mywebserver.http.HttpRequest;
import com.example.mywebserver.http.HttpResponse;

import java.io.File;
import java.io.RandomAccessFile;
public class LoginServlet extends HttpServlet{
    public void service(HttpRequest request, HttpResponse response) {
        //1获取用户信息
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println("username:"+username);
        System.out.println("password:"+password);

        //2
        try (
                RandomAccessFile raf
                        = new RandomAccessFile("user.dat","r");
        ){
            //表示登录是否成功
            boolean check = false;
            for(int i=0;i<raf.length()/100;i++) {
                //移动指针到每条记录的开始位置
                raf.seek(i*100);
                //读取用户名
                byte[] data = new byte[32];
                raf.read(data);
                String name = new String(data,"UTF-8").trim();
                //判断是否为此用户
                if(name.equals(username)) {
                    //匹配密码
                    raf.read(data);
                    String pwd = new String(data,"UTF-8").trim();
                    if(pwd.equals(password)) {
                        //登录成功
                        response.setEntity(new File("F://workspace/mywebserver/src/main/java/com/example/mywebserver/webapps/login_success.html"));
                        check = true;
                    }
                    break;
                }
            }
            //判断登录失败
            if(!check) {
                //跳转登录失败页面
                response.setEntity(new File("F://workspace/mywebserver/src/main/java/com/example/mywebserver/webapps/login_fail.html"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
