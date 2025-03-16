package pri.sample;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.XmlUtil;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileUtil {
    public static Map<String,String> readMap(String classPath){
        ClassPathResource resource = new ClassPathResource(classPath);
        BufferedReader reader = resource.getReader(Charset.defaultCharset());
        Map<String, String> map = new HashMap<>();
        try {
            while (reader.ready()){
                String s = reader.readLine();
                String[] split = s.split("=");
                map.put(split[0].trim(),split[1].trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
    public static ConcurrentHashMap<String,String> readConcurrentHashMap(String classPath){
        ClassPathResource resource = new ClassPathResource(classPath);
        BufferedReader reader = resource.getReader(Charset.defaultCharset());
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        try {
            while (reader.ready()){
                String s = reader.readLine();
                String[] split = s.split("=");
                map.put(split[0].trim(),split[1].trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
    public static Document readXml(String classPath){
        return XmlUtil.readXML(new ClassPathResource(classPath).getStream());
    }
    public static String readJson(String classPath){
        StringBuilder builder = null;
        try {
            ClassPathResource resource = new ClassPathResource(classPath);
            BufferedReader reader = resource.getReader(Charset.defaultCharset());
            builder = new StringBuilder();
            while (reader.ready()){
                builder.append( reader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }
    public static String documentToString(Document document) throws Exception {
        // 创建 TransformerFactory
        return XmlUtil.toStr(document,"UTF-8",true);
    }
    public static void writeFile(String path,String value){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(value);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
