package com.example.mywebserver.servlet;

import com.example.mywebserver.http.HttpRequest;
import com.example.mywebserver.http.HttpResponse;

/**
 * @ Author     ：djq.
 * @ Date       ：Created in 12:27 2020/3/13
 * @ Description：
 * @ Modified By：
 * @Version: $
 */
public abstract class HttpServlet {
    public void service(HttpRequest request, HttpResponse response){};
}
