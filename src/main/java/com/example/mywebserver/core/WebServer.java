package com.example.mywebserver.core;

import com.sun.xml.internal.ws.handler.ClientSOAPHandlerTube;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ Author     ：djq.
 * @ Date       ：Created in 22:26 2020/3/12
 * @ Description：
 * @ Modified By：
 * @Version: $
 */
public class WebServer {
    private ServerSocket server;
    public WebServer(){
        //初始化server
        try {
            server = new ServerSocket(8082);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    // 3.创建start方法，用来接收请求，处理业务，响应
    public void start() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            while(true){
                // 每有一个新的客户端连接，都睡启动一个新的线程处理该客户端交互
                Socket socket = server.accept();
                //将socket传递给ClientHander
                ClientHandler handler=new ClientHandler(socket);
                executorService.submit(handler);
                //t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WebServer server = new WebServer();
        System.out.println("服务端开始启动");
        server.start();
    }
}
