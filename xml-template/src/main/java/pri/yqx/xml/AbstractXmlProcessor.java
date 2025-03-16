package pri.yqx.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractXmlProcessor implements XmlProceesor {
    @Override
    public void doProcess(Document document) {
        Element element = document.getDocumentElement();
        dfs(element);
    }
    protected abstract void handleElement(Element element);
    public void dfs(Node node){
        if(node.getNodeType()!=Node.ELEMENT_NODE)
            return;
        Element  element=(Element) node;
        handleElement(element);
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            dfs(childNodes.item(i));
        }
    }


}
