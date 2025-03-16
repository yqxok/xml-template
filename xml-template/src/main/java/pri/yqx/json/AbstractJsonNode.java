package pri.yqx.json;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractJsonNode implements JsonNode{

    private static final Logger log= LoggerFactory.getLogger(AbstractJsonNode.class);
    public static char SPLIT_CHAR='.';
    private static final Map<Character,Character> match=new HashMap<>();

    static {
        match.put('{','}');
        match.put('[',']');
    }
    private Map<Object,Object> map=new HashMap<>();
    private boolean isMap;

    AbstractJsonNode(boolean isMap){
        this.isMap=isMap;
    }
    AbstractJsonNode(boolean isMap,String key,Object value){
         this.isMap=isMap;
         addMapNode(key,value);
    }
    AbstractJsonNode(boolean isMap,Map<String,Object> map){
         this.isMap=isMap;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            addMapNode(entry.getKey(),entry.getValue());
        }
    }



    /**
     * 初始化节点
     * @param key
     * @param value
     */

    /**
     * 使用表达式添加节点
     * @param key
     * @param value
     */
    @Override
    public final void addMapNode(String key,Object value){
        KeyType head = split(key);
        if((!head.getKey().equals("")&&this instanceof JsonListNode)||(head.getNum()>=0&&this instanceof JsonMapNode)){
            log.error("类型不匹配");
            return;
        }
        if (!head.hasNext()) {
            addNode(head,value);
        }else{
            Object node = getNode(head);
            if(node==null){
                Object node1;
                KeyType rest = split(head.getRest());
                if (rest.getNum()>=0&&this instanceof JsonMapNode) {
                    node1=new JsonListNode(head.getRest(),value);
                }else
                    node1=new JsonMapNode(head.getRest(),value);
                addNode(head,node1);
            } else
                ((AbstractJsonNode)node).addMapNode(head.getRest(),value);
        }

    }
    @Override
    public final Map<String,Object> toKeyValue(){
        Map<String, Object> map1 = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        dfsMap(this, map1,builder);
        return map1;
    }

    /**
     * 直接添加节点
     * @param key
     * @param node
     */
    @Override
    public final void addNode(Object key, Object node){
        if((isMap&&key instanceof String)||(!isMap && key instanceof Integer)){
            getMap().put(key,node);
            return;
        }
        log.info("索引无效,添加节点失败");
    }

    @Override
    public final Map<Object, Object> getMap() {
        return this.map;
    }

    @Override
    public final String toJsonString() {
        return JSONUtil.toJsonStr(transToMapOrList());
    }
    @Override
    public final <T> T getNode(String key,Class<T> clazz){
        return clazz.cast(getNode(key));
    }
    @Override
    public final Object getNode(String key) {
        KeyType head = split(key);
        Object node = getNode(head);
        Object res=node;
        if(head.hasNext()&&node instanceof AbstractJsonNode){
            res=((AbstractJsonNode)node).getNode(head.getRest());
        }
        return res;
    }
    private  Object transToMapOrList(){
        HashMap<Object, Object> map1 = null;
        List<Object> list=null;
        if(isMap)
            map1=new HashMap<>();
        else
            list=new ArrayList<>();
        for (Map.Entry<Object, Object> entry : getMap().entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            Object res=value;
            if(value instanceof AbstractJsonNode ){{
                res=((AbstractJsonNode)value).transToMapOrList();
            }}
            if(isMap)
                map1.put(key,res);
            else
                list.add(res);
        }
        return isMap?map1:list;
    };
    private void dfsMap(AbstractJsonNode node, Map<String,Object> map1,StringBuilder builder){
        Map<Object, Object> source = node.getMap();
        for (Map.Entry<Object, Object> entry : source.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if(key.equals("purchaseOrders"))
                System.out.println("");
            //左闭右开
            int l=builder.length();
            if(node instanceof JsonMapNode ){
                builder.append(key);
            }else
                builder.append("[").append(key).append("]");
            if(value instanceof AbstractJsonNode){
                if(value instanceof JsonMapNode)
                    builder.append(SPLIT_CHAR);
                dfsMap(((AbstractJsonNode)value),map1,builder );
            }else{
                map1.put(builder.toString(),value);
            }
            builder.delete(l,builder.length());
        }
    }
    private Object getNode(KeyType head){
        if(isMap)
            return getMap().get(head.getKey());
        return getMap().get(head.getNum());
    }
    private void addNode(KeyType head,Object value){
        if(isMap)
            getMap().put(head.getKey(),value);
        else
            getMap().put(head.getNum(),value);
    }
    private  KeyType split(String str){
        if(!str.contains(String.valueOf(SPLIT_CHAR))&&!str.contains("["))
            return new KeyType(str,false,"",-1);
        if(str.charAt(0)=='['){
            int r=str.indexOf(']');
            int val=Integer.valueOf(str.substring(1,r));
            boolean hasNext=false;
            String rest="";
            if(str.length()-1>r){
                if(str.charAt(r+1)==SPLIT_CHAR)
                    rest=str.substring(r+2);
                else
                    rest=str.substring(r+1);
                hasNext=true;
            }
            return new KeyType("",hasNext,rest,val);
        }
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i)==SPLIT_CHAR||str.charAt(i)=='[') {
                String curKey = str.substring(0, i );
                String rest = str.substring(i);
                if(str.charAt(i)==SPLIT_CHAR)
                    rest=str.substring(i+1);
                return new KeyType(curKey,true,rest,-1);
            }
        }
        return null;
    }
    static class JsonBuilder{
        /**
         * json数据反序列化为XmlJsonMapNode
         * @param json
         * @return
         */
        public static JsonMapNode createMapNode(String json){

            return parseToJsonMapNode(JSONUtil.parseObj(json));
        }

        /**
         * json数据反序列化为XmlJsonListNode
         * @param json
         * @return
         */
        public static JsonListNode createListNode(String json){

            return parseToJsonListNode(JSONUtil.parseArray(json));
        }
        public static JsonMapNode parseToJsonMapNode(JSONObject obj){
            JsonMapNode jsonMapNode=new JsonMapNode();
            for (Map.Entry<String, Object> entry : obj.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                Object res=null;
                if(value instanceof JSONArray){
                    res=parseToJsonListNode((JSONArray) value);
                }else if(value instanceof JSONObject){
                    res=parseToJsonMapNode((JSONObject)value );
                }else
                    res=value;
                jsonMapNode.addNode(key,res);
            }
            return jsonMapNode;
        }
        public static JsonListNode parseToJsonListNode(JSONArray array){
            JsonListNode jsonListNode = new JsonListNode();
            int i=0;
            for (Object value : array) {
                Object res=null;
                if(value instanceof JSONArray){
                    res=parseToJsonListNode((JSONArray) value);
                }else if(value instanceof JSONObject){
                    res=parseToJsonMapNode((JSONObject)value );
                }else
                    res=value;
                jsonListNode.addNode(i,res);
                i++;
            }
            return jsonListNode;
        }
    }
     class KeyType {
        private String key;
        private boolean hasNext;
        private int num;
        private String rest;

        public String getRest() {
            return rest;
        }

        public KeyType(String key, boolean hasNext, String rest, int num) {
            this.key = key;
            this.hasNext = hasNext;
            this.num = num;
            this.rest = rest;
        }

        public String getKey() {
            return key;
        }

        public int getNum() {
            return this.num;
        }

        public boolean hasNext() {
            return hasNext;
        }

    }
}
