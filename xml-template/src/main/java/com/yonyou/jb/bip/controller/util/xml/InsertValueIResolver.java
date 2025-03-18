package com.yonyou.jb.bip.controller.util.xml;

import org.w3c.dom.Node;

public interface InsertValueIResolver {
//    boolean resolveValue(Element element, Attr attr);
    boolean resolveValue(Node node);

    /**
     * name$[小红],则s[0]="name",s[1]="小红";
     * name,则s[0]="name",s[1]=""
     * @param nodeValue
     * @return
     */
    default String[] getTargetDefaultValue(String nodeValue){
        String[] res = new String[2];
        res[0]=nodeValue;
        if(nodeValue.length()>3&&nodeValue.charAt(nodeValue.length()-1)==']'){
            for (int i = nodeValue.length()-2; i >=0; i--) {
                if(nodeValue.charAt(i)=='['&&nodeValue.charAt(i-1)=='$'){
                    res[1]=nodeValue.substring(i+1,nodeValue.length()-1);
                    res[0]=nodeValue.substring(0,i-1);
                }
            }
        }
        return res;
    }
}
