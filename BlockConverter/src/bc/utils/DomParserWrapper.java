package bc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import bc.BlockConverter;

@SuppressWarnings("restriction")
public class DomParserWrapper {

	public static Document parse(String filePath) {
		try {
			DOMParser domParser = new DOMParser();

			// 日本語パスでエラーが出るので改良（松）
			// domParser.parse(filePath);

			InputSource xml = new InputSource();
			xml.setByteStream(new FileInputStream(new File(filePath)));
			xml.setEncoding(BlockConverter.ENCODING_BLOCK_XML);
			domParser.parse(xml);

			return domParser.getDocument();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static String getAttribute(Node node, String attributeName) {
		assert node != null;
		assert attributeName != null;

		Node attrNode = node.getAttributes().getNamedItem(attributeName);
		if (attrNode == null)
			return null;
		return attrNode.getNodeValue();
	}
	
	public static Iterable<Node> eachChild(Node node) {
		final NodeList list = node.getChildNodes();
		final int length = list.getLength();
		return new Iterable<Node>() {

			@Override
			public Iterator<Node> iterator() {
				return new Iterator<Node>() {

					private int index = -1;

					@Override
					public Node next() {
						index = nextIndex();
						return list.item(index);
					}

					@Override
					public boolean hasNext() {
						return nextIndex() >= 0;
					}

					private int nextIndex() {
						for (int start = index + 1; start < length; start++) {
							Node node = list.item(start);
							if (node.getNodeName().startsWith("#"))
								continue;
							return start;
						}
						return -1;
					}

					@Override
					public void remove() {
					}
				};
			}
		};
	}

	public static Node getChildNode(Node node, String... nodeName) {
		outer: for (int depth = 0; depth < nodeName.length; depth++) {
			for (Node item : eachChild(node)) {
				if (item.getNodeName().equals(nodeName[depth])) {
					node = item;
					continue outer;
				}
			}
			return null;
		}
		return node;
	}
	
	public static void doAnythingToNodeList(Node node, String nodeName, Consumer<Node> process){
		for(int i = 0 ; i < node.getChildNodes().getLength();i++){
			if(nodeName.equals(node.getChildNodes().item(i).getNodeName())){
				process.accept(node.getChildNodes().item(i));
			}
		}
	}
}
