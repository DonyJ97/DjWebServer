package com.example.mywebserver.http;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
/**
 * @ Author     ：djq.
 * @ Date       ：Created in 22:44 2020/3/12
 * @ Description：构造http请求
 * * 请求对象
 *  * 该类的每一个实例用于表示客户端发送过来的一个实际的HTTP请求内容
 *  * 每个请求由三部分组成
 *  * 1.请求行
 *  * 2.请求头
 *  * 3.消息正文（可以不包含）
 * @ Modified By：
 * @Version: $
 */
public class HttpRequest {
    /*
     * 请求行相关消息定义
     */
    //请求方式
    private String method;
    //请求的抽象路径
    private String url;
    //请求使用的协议版本
    private String protocol;

    // url中的请求路径部分
    private String requestURI;
    // url中的参数部分
    private String queryString;
    // 每一个参数
    private Map<String, String> parameters = new HashMap<>();

    /*
     * 请求头的相关信息定义
     */
    private Map<String,String> headers=new HashMap<String,String>();

    /*
     * 定义与连接相关的属性
     */
    private Socket socket;
    private InputStream in;

    public HttpRequest(Socket socket){
        try {
            this.socket=socket;
            this.in=socket.getInputStream();
            /*
             *实例化一个HttpRequest要解析客户端发送过来的请求内容，并且分析其中每个部分
             *1.解析请求行
             *2.解析消息头
             *3.解析消息正文
             */
            parseRequestLine();
            parseHeaders();
            parseContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 解析请求行
     */
    private void parseRequestLine(){
        System.out.println("开始解析请求行");
        /**解析请求行：
         * 读一行字符串，即请求行的内容，将字符串按空格拆分三部分
         * 分别设置到method,url,protocol属性上即可
         */
        try {
            String line=readLine();
            System.out.println("请求行内容"+line);
            /*
             *下面代码可能抛出数组下标越界，这是由于空请求引起
             */
            String data[] = line.split(" ");
            this.method = data[0];
            this.url = data[1];
            this.protocol = data[2];

            //进一步解析url
            parseURL();
            System.out.println("method   "+method);
            System.out.println("url  "+url);
            System.out.println("protocol  "+protocol);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("请求行解析完毕");
    }
    /*
    * 进一步解析URL
    * */
    private void parseURL() {
        System.out.println("进一步解析URL");
        /*
         * 实现思路 首先判断当前url是否需要进一步解析 而判定的标准是看url中是否含有"?",如果含有 则说明需要解析，若没有则不需要进一步解析
         *
         * 如果不需要进一步解析，那么直接将url的值赋值 给requestURI即可。
         *
         * 若需要解析，则应进行如下操作 先将url按照"?"拆分为两部分 第一部分设置到requestURI上，第二部分设置到 queryString属性上
         *
         * 然后进一步拆分参数: 将queryString按照"&"拆分为若干个参数。 每个参数再按照"="拆分为参数名与参数值，再将
         * 各参数的名字作为key，参数的值作为value保存 到parameters这个Map类型的属性上。
         *
         * url可能存在的情况如下: /myweb/reg.html /myweb/reg?username=xxx&password=xxx&....
         *
         */
        // 是否含有"?"
        if (url.contains("?")) {
            String[] data = url.split("\\?");
            requestURI = data[0];
            if (data.length > 1) {
                queryString = data[1];
                // 拆分每一个参数
                data = queryString.split("&");
                // paraLine: username=zhangsan
                for (String paraLine : data) {
                    String[] paras = paraLine.split("=");
                    if (paras.length > 1) {
                        parameters.put(paras[0], paras[1]);
                    } else {
                        parameters.put(paras[0], null);
                    }
                }
            }
        } else {
            // 不含有"?"
            requestURI = url;
        }

        System.out.println("requestURI:" + requestURI);
        System.out.println("queryString:" + queryString);
        System.out.println("parameters:" + parameters);
        System.out.println("进一步解析URL完毕");
    }

    public String getMethod() {
        return method;
    }
    public String getProtocol() {
        return protocol;
    }
    public String getUrl() {
        return url;
    }
    /**
     * 解析消息头
     */
    private void parseHeaders(){
        System.out.println("开始解析消息头...");
        try {
            String line = null;
            while (true) {
                line = readLine();
                if ("".equals(line)) {
                    // 单独读取了CRLF
                    break;
                }
                /*
                 * 将消息头按照": "拆分为两项 将详细头名字作为key，消息头的值作为 value保存到headers中
                 */
                String[] data = line.split(": ");
                headers.put(data[0], data[1]);

            }
            System.out.println("headers:" + headers);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("消息头解析完毕!");
    }
    /**
     * 解析消息正文
     */

    private void parseContext(){
        System.out.println("开始解析消息正文");
        System.out.println("消息正文解析完毕");
    }

    /**
     * 通过输入流读取客户端发送的一行字符串
     * 该方法会连续的读取若干字符，当连续读到CRLF的时候停止读取，
     * 并将之前读到的所有字符以一个字符串的形式返回
     *
     * @return
     * @throws IOException
     */
    private String readLine() throws IOException {
        StringBuilder builder = new StringBuilder();
        int cur = -1;// 本次读取的字符
        int pre = -1;// 上次读取的字符
        while ((cur = in.read()) != -1) {
            // 若上次读取回车符，本次读取换行符就停止读取
            if (pre == 13 && cur == 10) {
                break;
            }
            builder.append((char) cur);
            pre = cur;
        }
        // 返回时要去除空白字符(最后会有一个回车符)
        return builder.toString().trim();
    }
    /**
     * 根据给定的消息头名字获取对应的值
     *
     * @param name
     * @return
     */
    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    /**
     * 根据给定的参数名获取对应的参数值
     *
     * @param name
     * @return String
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }
}

