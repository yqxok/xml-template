package com.yonyou.jb.bip.controller.util.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JsonMapNode extends AbstractJsonNode {
    private static final Logger log= LoggerFactory.getLogger(JsonMapNode.class);


    public JsonMapNode(){
        super(true);
    }
    public JsonMapNode(String key, Object value){
        super(true,key,value);

    }
    public JsonMapNode(Map<String,Object> map){
        super(true,map);

    }
    public static JsonMapNode newInstance(String json){
        return AbstractJsonNode.JsonBuilder.createMapNode(json);
    }






    /**
     * 转成KeyValue格式数据
     * @return
     */
//    public Map<String,Object> toKeyValue(){
//        Map<String, Object> map1 = new HashMap<>();
//        StringBuilder builder = new StringBuilder();
//        dfsMap(getMap(), map1,builder);
//        return map1;
//    }

    /**
     * 深度遍历节点
     * @param source
     * @param map1
     * @param builder
     */
    private void dfsMap(Map<Object,Object> source, Map<String,Object> map1,StringBuilder builder){

        for (Map.Entry<Object, Object> entry : source.entrySet()) {
            String key =(String) entry.getKey();
            Object value = entry.getValue();
            //左闭右开
            int l=builder.length();
            builder.append(key);
            if(value instanceof JsonListNode){
                dfsList(((JsonListNode)value).getMap(),map1,builder );
            }else if(value instanceof JsonMapNode){
                builder.append(SPLIT_CHAR);
                dfsMap(((JsonMapNode)value).getMap(),map1,builder);
            }else{
                map1.put(builder.toString(),value);
            }
            builder.delete(l,builder.length());
        }
    }
    private void dfsList(Map<Object,Object> listMap,Map<String,Object> map1, StringBuilder builder){
        for (Map.Entry<Object, Object> entry : listMap.entrySet()) {
            Integer i=(Integer)entry.getKey();
            int l=builder.length();
            builder.append("[").append(i).append("]");
            Object value=listMap.get(i);
            if(value instanceof JsonListNode){
                dfsList(((JsonListNode)value).getMap(),map1,builder );
            }else if(value instanceof JsonMapNode){
                builder.append(SPLIT_CHAR);
                dfsMap(((JsonMapNode) value).getMap(),map1,builder);
            }else{
                map1.put(builder.toString(),value);
            }
            builder.delete(l,builder.length());
        }


    }

}
