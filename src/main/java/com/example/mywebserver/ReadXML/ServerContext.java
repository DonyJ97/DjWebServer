package com.example.mywebserver.ReadXML;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ Author     ：djq.
 * @ Date       ：Created in 11:53 2020/3/13
 * @ Description：
 * @ Modified By：
 * @Version: $
 */
public class ServerContext {
    Map<String,String> servletMap = new HashMap<>();
    public static String getServletName(String path) throws DocumentException {
        //创建dom4j解析器
        SAXReader reader = new SAXReader();
        //加载document对象
        Document document = reader.read("F:/workspace/mywebserver/src/main/resources/servletMapping.xml");
        //拿到根节点
        Element root = document.getRootElement();
        //拿到所有的子节点
        List<Element> list = root.elements("servlet");
        for(int i = 0; i < list.size(); i ++){
            String tmpURL = list.get(i).attributeValue("url");
            if(tmpURL.equals(path)){
                return list.get(i).attributeValue("className");
            }
        }
        return null;
    }
}
