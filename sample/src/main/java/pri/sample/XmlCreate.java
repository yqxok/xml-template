package pri.sample;

import org.junit.Test;
import org.w3c.dom.Document;
import com.yonyou.jb.bip.controller.util.json.JsonMapNode;
import com.yonyou.jb.bip.controller.util.xml.JsonToXmConvertor;

public class XmlCreate {
    @Test
    public void test() throws Exception {
        Document document = FileUtil.readXml("xml/test.xml");
        String s = FileUtil.readJson("xml/test.json");
        JsonMapNode jsonMapNode = JsonMapNode.newInstance(s);
        JsonToXmConvertor convertor = new JsonToXmConvertor(jsonMapNode, document);
        convertor.startParse(document);
        System.out.println(FileUtil.documentToString(document));
       // FileUtil.writeFile("D:\\code\\java\\juc_test\\xml_template\\src\\main\\resources\\xml\\res.xml",FileUtil.documentToString(document));
//        FileUtil.
    }
}
