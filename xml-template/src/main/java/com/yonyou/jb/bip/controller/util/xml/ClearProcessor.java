package com.yonyou.jb.bip.controller.util.xml;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * 用于清理特殊属性
 */
public class ClearProcessor extends AbstractXmlProcessor {
    @Override
    protected void handleElement(Element element) {
        ArrayList<String> rmAttrs = new ArrayList<>();
        NamedNodeMap attributes = element.getAttributes();
        for (int j = 0; j < attributes.getLength(); j++) {
            Node attr = attributes.item(j);
            if(attr.getNodeName().startsWith(SpecialAttrEnum.SPECIAL_SIGN)){
                rmAttrs.add(attr.getNodeName());
            }
        }
        //删除特殊属性
        for (String rmAttr : rmAttrs) {
            attributes.removeNamedItem(rmAttr);
        }
    }
}
