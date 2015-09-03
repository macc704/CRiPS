package bc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import bc.BlockConverter;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

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
}
