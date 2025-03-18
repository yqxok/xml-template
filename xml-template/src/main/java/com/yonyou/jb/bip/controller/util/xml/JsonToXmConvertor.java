package com.yonyou.jb.bip.controller.util.xml;

import com.yonyou.jb.bip.controller.util.json.AbstractJsonNode;
import com.yonyou.jb.bip.controller.util.json.JsonListNode;
import com.yonyou.jb.bip.controller.util.json.JsonNode;
import jdk.internal.org.xml.sax.InputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import com.yonyou.jb.bip.controller.util.json.JsonMapNode;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JsonToXmConvertor {
    private static final Logger log= LoggerFactory.getLogger(JsonToXmConvertor.class);

    private final JsonNode jsonNode;
    private final List<XmlProceesor> xmlProceesors=new ArrayList<>();
    public<T extends AbstractJsonNode> JsonToXmConvertor(String json, String xml, Class<T> jsonType){
        if(jsonType.equals(JsonMapNode.class)){
            jsonNode= JsonMapNode.newInstance(json);
        } else {
            jsonNode=JsonListNode.newInstance(json);
        }
        init();


    }


    public JsonToXmConvertor(JsonNode jsonNode,Document document){
        this.jsonNode=jsonNode;
        init();
    }
    private void init() {
        //添加:for处理器器
        xmlProceesors.add(new ForAttrProcessor(jsonNode));
        //添加插值处理器
        CalculateValueResolver calculateValueResolver = new CalculateValueResolver(jsonNode);
        DefaultValueResolver defaultValueResolver = new DefaultValueResolver(jsonNode);
        ValueResolverComposite composite = new ValueResolverComposite(calculateValueResolver, defaultValueResolver);
        xmlProceesors.add(new ValueInsertProcessor(composite));
        //添加特殊属性清除处理器
        xmlProceesors.add(new ClearProcessor());
    }
    public void startParse(Document document){
        for (XmlProceesor proceesor : xmlProceesors) {
            proceesor.doProcess(document);
        }
//        return document;
    }

    private Document parseDocument(String xml){
        try {
            // 创建DocumentBuilderFactory实例
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // 可选：设置工厂属性，例如是否忽略空白节点
            // factory.setIgnoringElementContentWhitespace(true);

            // 创建DocumentBuilder实例
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 将XML字符串转换为InputSource
            InputSource is = new InputSource(new StringReader(xml));

            // 解析XML并返回Document对象
            Document document = builder.parse(String.valueOf(is));

            // 规范化文档（可选）
            document.getDocumentElement().normalize();

            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
