package com.yonyou.jb.bip.controller.util.xml;

import com.yonyou.jb.bip.controller.util.json.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.Set;

public class CalculateValueResolver implements InsertValueIResolver {
    private final JsonNode jsonNode;
    private static final Set<String> countSet;
    private static final Logger log= LoggerFactory.getLogger(CalculateValueResolver.class);
    static {
        countSet=new HashSet<>();
        countSet.add(" + ");countSet.add(" - ");countSet.add(" * ");countSet.add(" / ");
    }
    public CalculateValueResolver(JsonNode jsonNode){
        this.jsonNode=jsonNode;
    }

    @Override
    public boolean resolveValue(Node node) {
        String nodeValue = node.getNodeValue().trim();
        for (String s : countSet) {
            if (nodeValue.contains(s)) {
                String[] split = nodeValue.split(" ");
                String[] targetDefaultValue = getTargetDefaultValue(split[2]);
                String res=targetDefaultValue[1];
                Number num1 = null;
                Number num2 = null;
                try {
                    num1 = jsonNode.getNode(split[0],Number.class);
                    num2 = jsonNode.getNode(targetDefaultValue[0],Number.class);
                    if(num1==null||num2==null)
                        throw new RuntimeException();
                } catch (Exception e) {
                    log.error("节点{}插值表达式找不到对应Number",node.getNodeName());
                    node.setNodeValue(res);
                    return true;
                }
                if(s.equals(" + ")){
                    res=Double.toString(num1.doubleValue()+num2.doubleValue());
                }else if(s.equals(" * ")){
                    res=Double.toString(num1.doubleValue()*num2.doubleValue());
                }else if(s.equals(" - ")){
                    res=Double.toString(num1.doubleValue()-num2.doubleValue());
                }else
                    res=Double.toString(num1.doubleValue()/num2.doubleValue());
                node.setNodeValue(res);
                return true;
            }
        }
        return false;
    }
}
