package bc.classblockfilewriters;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class DocumentBuilder {

	public Document createEmptyDocument(){
        javax.xml.parsers.DocumentBuilder documentBuilder = null;
        try {
             documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
             e.printStackTrace();
        }
        return documentBuilder.newDocument();
	}

	public void addAttributeToElement(Element element, Map<String, String> attributes){
		for(String attributeKey : attributes.keySet()){
			element.setAttribute(attributeKey, attributes.get(attributeKey));
		}
	}

	public Element createElementWithTextContent(String elementName, String text){
		Element element = (Element) new org.jdom2.Element(elementName);
		element.setTextContent(text);
		return element;
	}

	public Element createElement(String elementName){
		return (Element) new org.jdom2.Element(elementName);
	}

	public boolean write(File file, Document document, String enc){
        // Transformerインスタンスの生成
        Transformer transformer = null;
        try {
             TransformerFactory transformerFactory = TransformerFactory
                       .newInstance();
             transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
             e.printStackTrace();
             return false;
        }

        // Transformerの設定
        transformer.setOutputProperty("indent", "yes"); //改行指定
        transformer.setOutputProperty("encoding", enc); // エンコーディング

        // XMLファイルの作成
        try {
             transformer.transform(new DOMSource(document), new StreamResult(
                       file));
        } catch (TransformerException e) {
             e.printStackTrace();
             return false;
        }

        return true;
	}

}
