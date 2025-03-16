package pri.yqx.xml;

import org.w3c.dom.Node;
import pri.yqx.json.JsonNode;

/**
 * {{person.name${小明}}},person.name将从json数据中寻找,若找到则将替代插值,若没有则使用${}内的值替代
 */
public class JsonInserValueResolver implements InsertValueIResolver{

    private final JsonNode jsonNode;

    public JsonInserValueResolver(JsonNode jsonNode){
        this.jsonNode=jsonNode;
    }
    @Override
    public boolean resolveValue(Node node) {
        String nodeValue=node.getNodeValue();
        //解析字符串
        String targetStr=nodeValue,defaultStr="";
        if(nodeValue.length()>3&&nodeValue.charAt(nodeValue.length()-1)==']'){
            for (int i = nodeValue.length()-2; i >=0; i--) {
                if(nodeValue.charAt(i)=='['&&nodeValue.charAt(i-1)=='$'){
                    defaultStr=nodeValue.substring(i+1,nodeValue.length()-1);
                    targetStr=nodeValue.substring(0,i-1);
                }
            }
        }
        Object value = jsonNode.getNode(targetStr);
        if(value==null){
            node.setNodeValue(defaultStr);
        }else if(value instanceof JsonNode){
            throw new RuntimeException("对象不能作为插值");
        }else
            node.setNodeValue(value.toString());
        return true;
    }

}
