package bc.classblockfilewriters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ProjectInfoSerializer {

	private List<PublicMethodInfo> addedMethods = new ArrayList<PublicMethodInfo>();
	public static String SERIALIZE_FILE_NAME = "project_meta_inf.xml";

	public Document createFreshDocument(String rootTag) throws ParserConfigurationException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        DOMImplementation domI = db.getDOMImplementation();

		return domI.createDocument("", rootTag, null);
	}

	public void addAddedMethods(List<PublicMethodInfo> methods){
		this.addedMethods.addAll(methods);
	}

	public void print(String baseDirPath) throws ParserConfigurationException, IOException, TransformerException{
		File file = new File(baseDirPath + "/" + SERIALIZE_FILE_NAME);
		Document doc = createFreshDocument("ProjectInfo");
		Node root = doc.getFirstChild();
		Node addedMethodsNode = doc.createElement("AddedMethod");
		for(PublicMethodInfo method : addedMethods){
			addedMethodsNode.appendChild(method.createMethodElement(doc));
		}
		root.appendChild(addedMethodsNode);

		Node addedClassesNode = doc.createElement("ProjectClasses");
		root.appendChild(addedClassesNode);

		FileWriter writer = new FileWriter(file);
		writer.write(getNodeString(root));
		writer.close();
	}

	public String getNodeString(Node node) throws TransformerException{
		StringWriter writer = new StringWriter();
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(new DOMSource(node), new StreamResult(writer));

		return writer.toString();
	}
}
