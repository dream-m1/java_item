package com.socket;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析XML
 */
public class JXML {
    private Map<String, Accunts> map = new HashMap<>();
    private File xmlFile = new File("accounts.xml");
    private SAXReader reader = new SAXReader();
    private Document xmlDoc;
    {
        try {
            xmlDoc = reader.read(xmlFile);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    private Element root = xmlDoc.getRootElement();

    // 获取里面所有的内容
    public Map<String, Accunts> getXML(){
        List<Element> bankElList = root.elements();
        for (Element bankEl:bankElList) {
            Accunts accunts = zhuan(bankEl);
            map.put(accunts.getId(),accunts);
        }
        return map;
    }

    // 修改文件内容
    public void modify(String element,String content,String id){
        List<Element> bookElList = xmlDoc.selectNodes("//accunts/account[id="+id+"]");
        Element element1 = bookElList.get(0);
        element1.element(element).setText(content);
        FileWriter fw = null;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            fw = new FileWriter(xmlFile);
            XMLWriter writer = new XMLWriter(fw,format);
            writer.write(xmlDoc);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    // 查询文件卡号
//    public Accunts selectId(String id){
//        List<Element> bookElList = xmlDoc.selectNodes("//accunts/account[id="+id+"]");
//        if(bookElList.isEmpty()){
//            return null;
//        }else {
//            return zhuan(bookElList.get(0));
//        }
//    }

    // 将Element转换成Accunts对象
    private Accunts zhuan(Element element){
        String id = element.elementText("id");
        String passWord = element.elementText("password");
        String money = element.elementText("money");
        String state = element.elementText("state");
        return new Accunts(id,passWord,Integer.parseInt(money),(state.equals("0")?true:false));
    }
}

//class TestXMl{
//    public static void main(String[] args) {
//        JXML j = new JXML();
//        System.out.println(j.getXML());
//        System.out.println(j.selectId("10001"));
//        j.modify("state","100","10001");
//    }
//}
