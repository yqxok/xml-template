package pri.yqx.json;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JsonListNode extends AbstractJsonNode {

    public JsonListNode(){
        super(false);
    }
    public JsonListNode(String key, Object value){
        super(false,key,value);

    }
    public JsonListNode(Map<String,Object> map){
        super(false,map);
    }
    public static JsonListNode newInstance(String json){
        return AbstractJsonNode.JsonBuilder.createListNode(json);
    }

    public List<Object> getList() {
        return Arrays.asList(getMap().entrySet().toArray());
    }

    public Object getNode(int index){
        return getMap().get(index);
    }
    public<T> T getNode(int index,Class<T> tClass){
        return tClass.cast(getMap().get(index));
    }
    public int getSize(){
        return getMap().size();
    }


}
