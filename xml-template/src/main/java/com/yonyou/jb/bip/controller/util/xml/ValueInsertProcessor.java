package com.yonyou.jb.bip.controller.util.xml;

import org.w3c.dom.*;

public class ValueInsertProcessor extends AbstractXmlProcessor {
    private InsertValueIResolver resolver;
    public ValueInsertProcessor(){
        resolver=new ValueResolverComposite();
    }
    public ValueInsertProcessor(InsertValueIResolver composite){
        this.resolver=composite;
    }

    @Override
    protected void handleElement(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        if(attributes==null) return;
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attr =(Attr) attributes.item(i);
            if(containValueInsert(attr)) {
                //对属性插值进行解析
                resolver.resolveValue(attr);
            }
        }
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            //对文本节点进行插值解析
            if(item.getNodeType()==Node.TEXT_NODE&&containValueInsert(item)){
                resolver.resolveValue(item);
            }
        }
    }

    //是否存在插值,存在则去掉外围括号
    private boolean containValueInsert(Node attr){
        String str = attr.getNodeValue().trim();
        if (str.length()>4&&str.charAt(0)=='{'&&str.charAt(1)=='{'
                &&str.charAt(str.length()-1)=='}'&&str.charAt(str.length()-2)=='}') {
            String value = str.substring(2, str.length() - 2);
            attr.setNodeValue(value);
            return true;
        }
        return false;
    }
}
