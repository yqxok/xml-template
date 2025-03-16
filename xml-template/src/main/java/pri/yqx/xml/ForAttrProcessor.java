package pri.yqx.xml;

import org.w3c.dom.*;
import pri.yqx.json.JsonListNode;
import pri.yqx.json.JsonNode;

import java.util.Arrays;

public class ForAttrProcessor extends AbstractXmlProcessor{
    private static final String MID_KEY_WORD="in";
    private final JsonNode jsonNode;
    public ForAttrProcessor(JsonNode jsonNode){
        this.jsonNode=jsonNode;
    }
    @Override
    protected void handleElement(Element element) {
        Attr attr = element.getAttributeNode(SpecialAttrEnum.FOR_ATTR);
        if(attr==null) return;
        String nodeValue = attr.getNodeValue().trim();
        String[] s = nodeValue.split(" ");
        //检查for语法是否正确
        checkFor(s,element);

        //列表元素对象和引用的列表对象
        String item=s[0],list = s[2];
        Object node = jsonNode.getNode(list);
        if(node==null){
            throw new RuntimeException("节点"+element.getNodeName()+"未找到相应对象");
        }else if(!(node instanceof JsonListNode)){
            throw new RuntimeException("节点"+element.getNodeName()+" :for引用的不是列表对象");
        }
        JsonListNode jsonListNode=(JsonListNode) node;
        Node parentNode = element.getParentNode();
        if(parentNode==null){
            throw new RuntimeException("节点"+element.getNodeName()+"不存在父节点");
        }
        //赋值节点模板
        Node copyNode = element.cloneNode(true);
        for (int i = 0; i < jsonListNode.getSize(); i++) {
            String suffix="["+i+"]";
            Element cloneEle=element;
            if(i!=0){
                Element node1 =(Element) copyNode.cloneNode(true);
                parentNode.appendChild(node1);
                cloneEle=node1;
            }
            cloneEle.removeAttribute(SpecialAttrEnum.FOR_ATTR);
            //替换插值和for表达式
            replaceInsertValue(item,list+suffix,cloneEle);

        }
    }

    private void replaceInsertValue(String item, String list, Element element) {
        NamedNodeMap attrs = element.getAttributes();
        //对属性节点进行插值替换
        for (int i = 0;attrs!=null&&i <attrs.getLength(); i++) {
            Attr attr =(Attr) attrs.item(i);
            String res=attr.getValue();
            String insertValue=getInsertValue(attr);

            if(insertValue!=null){
                if(insertValue.equals(item)){
                     res="{{"+list+"}}";
                }else if(insertValue.startsWith(item+".")){
                    res="{{"+ list+ insertValue.substring(item.length())+"}}";
                }
                attr.setValue(res);
            }else if(attr.getNodeName().equals(SpecialAttrEnum.FOR_ATTR)){
                //for循环里面不允许存在插值表达式
                String[] s = attr.getNodeValue().trim().split(" ");
                checkFor(s,element);
                if(s[2].equals(item)){
                    s[2]=list;
                }else if(s[2].startsWith(item+".")){
                    s[2]=list+s[2].substring(item.length());
                }
                attr.setNodeValue(Arrays.stream(s).reduce((a,b)->a+" "+b).get());
            }
        }
        //对文本节点进行插值替换
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item1 = childNodes.item(i);
            if(item1.getNodeType()!=Node.TEXT_NODE)
                continue;
            String res=item1.getNodeValue();
            String insertValue=getInsertValue(item1);
            if(insertValue==null){
               continue;
            }else if(insertValue.equals(item)){
                res="{{"+list+"}}";
            }else if(insertValue.startsWith(item+"."))
                res="{{"+ list+ insertValue.substring(item.length())+"}}";
            item1.setNodeValue(res);
        }
//        NodeList childNodes = element.getChildNodes();
//        if(childNodes==null) return;
        //递归
        for (int i = 0; i<childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if(node instanceof Element){
                replaceInsertValue(item,list,(Element)node);
            }
        }
    }
    private String getInsertValue(Node node){
        String str = node.getNodeValue().trim();
        if (str.length()>4&&str.charAt(0)=='{'&&str.charAt(1)=='{'
                &&str.charAt(str.length()-1)=='}'&&str.charAt(str.length()-2)=='}') {
            return str.substring(2,str.length()-2).trim();
        }
        return null;
    }
    private void checkFor(String[] s,Element element){
        if(s.length!=3||!s[1].equals(MID_KEY_WORD)){
            throw new RuntimeException("节点"+element.getNodeName()+" :for语法出错");
        }
    }
}
