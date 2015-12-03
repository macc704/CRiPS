package bc.j2b.analyzer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bc.apps.JavaToBlockMain;



public class MethodsCollector {

	private Map<String, String> calcReturnType = new HashMap<String, String>();

	public static void main(String[] args) {
		MethodsCollector collector = new MethodsCollector();
		collector.main();
	}

	public Map<String, String> getCalcReturnType(){
		return this.calcReturnType;
	}

	public void main() {
		DOMParser parser = new DOMParser();
		// lang_def.xmlを読み込む
		try {
			parser.parse(JavaToBlockMain.LANG_DEF_PATH);

			Document doc = parser.getDocument();
			Element root = doc.getDocumentElement();
			Pattern attrExtractor = Pattern.compile("\"(.*)\"");
			Matcher nameMatcher;
			NodeList genusNodes = root.getElementsByTagName("BlockGenus");
			Node genusNode;

			for (int i = 0; i < genusNodes.getLength(); i++) { // find them
				genusNode = genusNodes.item(i);

				if (isShouldCheckFile(genusNode.getBaseURI().substring(
						genusNode.getBaseURI().lastIndexOf("/") + 1))) {
					if (genusNode.getNodeName().equals("BlockGenus")) {
						// entry
						nameMatcher = attrExtractor.matcher(genusNode
								.getAttributes().getNamedItem("kind")
								.toString());
						if (nameMatcher.find()) {
							if (nameMatcher.group(1).equals("command")) {
								nameMatcher = attrExtractor.matcher(genusNode
										.getAttributes().getNamedItem("name")
										.toString());
								if (nameMatcher.find()) {
									int paramNum = getParameterNum(genusNode);
									calcReturnType.put(convertMethodName(nameMatcher.group(1),paramNum), "void");
								}
							} else if (nameMatcher.group(1).equals("function")) {
								nameMatcher = attrExtractor.matcher(genusNode
										.getAttributes().getNamedItem("name")
										.toString());
								if (nameMatcher.find()) {
									String returnType = getReturnType(genusNode);
									int paramNum = getParameterNum(genusNode);
									calcReturnType.put(convertMethodName(nameMatcher.group(1),paramNum), returnType);
								}
							}
						}
					}
				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String convertMethodName(String methodName, int paramNum){
		//@マークの数をカウント
		String convertedName = new String(methodName);
		String count;
		if(paramNum == 0){
			  count = "";;
		}else{
			count = String.valueOf(paramNum);
		}

		if(convertedName.contains("[")){
			convertedName = convertedName.substring(0,convertedName.indexOf("["));
		}

	    convertedName = convertedName + "(" + count + ")";
		return convertedName;
	}

	private String getReturnType(Node genusNode) {
		NodeList genusChildren = genusNode.getChildNodes();
		Node genusChild;

		for (int j = 0; j < genusChildren.getLength(); j++) {
			genusChild = genusChildren.item(j);
			if (genusChild.getNodeName().equals("BlockConnectors")) {
				return parsePlugInfo(genusChild.getChildNodes());
			}
		}
		return null;
	}

	private int getParameterNum(Node genusNode){
		NodeList genusChildren = genusNode.getChildNodes();
		Node genusChild;

		for (int j = 0; j < genusChildren.getLength(); j++) {
			genusChild = genusChildren.item(j);
			if (genusChild.getNodeName().equals("BlockConnectors")) {
				return parseSocketInfo(genusChild.getChildNodes());
			}
		}
		return 0;
	}

	private int parseSocketInfo(NodeList connectors){
		Pattern attrExtractor = Pattern.compile("\"(.*)\"");
		Matcher nameMatcher;
		Node connector;
		int socketNum = 0;
		for (int k = 0; k < connectors.getLength(); k++) {
			connector = connectors.item(k);
			if (connector.getNodeName().equals("BlockConnector")) {
				nameMatcher = attrExtractor.matcher(connector
						.getAttributes().getNamedItem("connector-kind")
						.toString());
				if (nameMatcher.find() && nameMatcher.group(1).equals("socket")){
					nameMatcher = attrExtractor.matcher(connector
							.getAttributes().getNamedItem("connector-type")
							.toString());
					if (nameMatcher.find()) { // will be true
						socketNum++;
					}
				}
			}
		}
		return socketNum;
	}

	private String parsePlugInfo(NodeList connectors){
		Pattern attrExtractor = Pattern.compile("\"(.*)\"");
		Matcher nameMatcher;
//		Node opt_item;
		Node connector;
		for (int k = 0; k < connectors.getLength(); k++) {
			connector = connectors.item(k);
			if (connector.getNodeName().equals("BlockConnector")) {
				nameMatcher = attrExtractor.matcher(connector
						.getAttributes().getNamedItem("connector-kind")
						.toString());
				if (nameMatcher.find() && nameMatcher.group(1).equals("plug")){
					nameMatcher = attrExtractor.matcher(connector
							.getAttributes().getNamedItem("connector-type")
							.toString());
					if (nameMatcher.find()) { // will be true
						return convertJavaType(nameMatcher.group(1));
					}
				}
			}
		}
		return null;
	}


	private String convertJavaType(String type){
		if("number".equals(type)){
			return "int";
		}else if("double-number".equals(type)){
			return "double";
		}else if("string".equals(type)){
			return "String";
		}else if("boolean".equals(type)){
			return "boolean";
		}else if("object".equals(type)){
			return "Object";
		}else{
			return null;
		}
	}


	private boolean isShouldCheckFile(String name) {
		if (name.equals("lang_def_genuses_calc.xml")
				|| name.equals("lang_def_genuses_class.xml")
				|| name.equals("lang_def_genuses_datatypes.xml")
				|| name.equals("lang_def_genuses_math.xml")
				|| name.equals("lang_def_genuses_object.xml")
				|| name.equals("lang_def_genuses_procedure.xml")
				|| name.equals("lang_def_genuses_unused.xml")
				|| name.equals("lang_def_genuses_variable.xml")
				|| name.equals("lang_def_genuses_stubs.xml")
				|| name.equals("lang_def_genuses.xml")) {
			return false;
		}
		return true;
	}

}
