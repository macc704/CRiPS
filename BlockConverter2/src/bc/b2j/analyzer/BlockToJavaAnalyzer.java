package bc.b2j.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bc.BlockConverter;
import bc.b2j.model.BlockModel;
import bc.b2j.model.PageModel;
import bc.b2j.model.PrivateVariableBlockModel;
import bc.b2j.model.ProgramModel;

public class BlockToJavaAnalyzer {

	private ProgramModel programModel = new ProgramModel();
	private static Map<Integer, BlockModel> blockModels = new HashMap<Integer, BlockModel>();
	private String fileURI;// #ohata constructorblockにURLを渡したいので変数を用意

	// project,継承メソッド一覧

	public void setProjectMethods(Map<String, String> methods) {
		BlockConverter.projectMethods = methods;
	}

	// private static LinkedList privateNumberIdList = new LinkedList(); aaaaa

	public BlockToJavaAnalyzer() {
	}

	public BlockToJavaAnalyzer(String uri) {// #ohata
											// コンストラクタでファイル名をセットする　ファイル名はコンストラクタブロックの分析の際利用s
		int index = uri.indexOf(".");
		fileURI = new String(uri.substring(0, index));
	}

	public static BlockModel getBlock(int id) {
		return blockModels.get(id);
	}

	/**
	 * 
	 * @return
	 */
	public ProgramModel getProgramModel() {
		return programModel;
	}

	/*************************
	 * treeWalk開始
	 ************************/

	public void visit(Document document) {
		if (document.hasChildNodes()) {
			parsePage(document);
		} else {
		}

	}

	/**
	 * Pageノードを解析
	 * 
	 * @param node
	 */

	private void parsePage(Node node) {
		Node page = node;
		
		Pattern attrExtractor = Pattern.compile("\"(.*)\"");
		Matcher nameMatcher;

		//codeblocksノード
		while (page.getNodeName() != "cb:CODEBLOCKS") {
			page = page.getFirstChild();
		}
		//Pagesノードの代入
		page = getNamedNode("Pages", page.getChildNodes());
		//Pageノードの代入		
		page = getNamedNode("Page", page.getChildNodes());

		while (page != null) {
			NamedNodeMap pageAttrs = page.getAttributes();
			String className = null;
			String superClass = null;
			// first, parse out the attributes
			if(pageAttrs != null){
				nameMatcher = attrExtractor.matcher(pageAttrs.getNamedItem(
						"page-name").toString());
				if (nameMatcher.find()) {
					className = nameMatcher.group(1);
				}

				PageModel model = new PageModel(className, superClass);
				
				Node pageBlocks = getNamedNode("PageBlocks", page.getChildNodes());
				
				if (pageBlocks.getFirstChild() != null) {
					resolveBlock(pageBlocks.getFirstChild(), model);
				}
				programModel.addPage(model);	
			}
			page = page.getNextSibling();
		}
	}

	/**
	 * Blockノードを解析
	 * 
	 * @param node
	 * @param className
	 */

	private void resolveBlock(Node node, PageModel pageModel) {

		Node blockNode = node;
		String parentBlockID = null;

		while (blockNode.getNodeName() != "Block"
				&& blockNode.getNodeName() != "BlockStub") {
			blockNode = blockNode.getNextSibling();
		}

		while (blockNode != null) {
			Node block = blockNode;

			// NodeNameがBlockになるまで
			if (block.getNodeName() == "BlockStub") {
				block = block.getFirstChild();
				while (block.getNodeName() != "Block") {
					if (block.getNodeName() == "StubParentID") {
						parentBlockID = block.getTextContent();
					}
					block = block.getNextSibling();
				}
			}

			NamedNodeMap BlockAttrs = block.getAttributes();
			if(BlockAttrs!= null){
				String genusName = BlockAttrs.getNamedItem("genus-name")
						.getNodeValue();
				
				if ("procedure".equals(genusName)) {
					parseBlockNode(block);
//					ProcedureBlockModel model = new ProcedureBlockModel();
//					parseBlock(block, model);
//					pageModel.addProcedure(model);
//					blockNode = blockNode.getNextSibling();
				} else if ("constructor".equals(genusName)) {// #ohata
					parseBlockNode(block);
//					ConstructorBlockModel model = new ConstructorBlockModel();
//					parseBlock(block, model);
//					model.setLabel(fileURI);
//					model.setURI(fileURI);
//					pageModel.addConstructor(model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("proc-param")) {
					parseBlockNode(block);
//					ProcedureParamBlockModel model = new ProcedureParamBlockModel();
//					parseBlock(block, model);
					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("caller")) {// isMethodCall
//																// isProjectMethodの前にやらないとエラーが発生する可能性有り
					parseBlockNode(block);				
//					CallMethodBlockModel model = new CallMethodBlockModel(true);
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (isMethodCallBlock(genusName) || isProjectMethod(block)) {
					parseBlockNode(block);				
//					CallMethodBlockModel model = new CallMethodBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (isDataBlock(genusName)) {// ここが変数の参照ブロックを解析するはず
					parseBlockNode(block);				
//					NoProcparamDataBlockModel model = new NoProcparamDataBlockModel();
//					parseBlock(block, model);
//					if (parentBlockID != null) {
//						model.setStubParentID(parentBlockID);
//					}
//					blockNode = blockNode.getNextSibling();
				} else if (isInfixCommandBlock(genusName)) {
					parseBlockNode(block);				
//					InfixCommandBlockModel model = new InfixCommandBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("local-var-")) {
					parseBlockNode(block);
//					LocalVariableBlockModel model = new LocalVariableBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("private")) {// #ohata
					parseBlockNode(block);				
//					PrivateVariableBlockModel model = new PrivateVariableBlockModel();
//					parseBlock(block, model);
//					pageModel.addPrivateVariableBlock(model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("setter")
						|| genusName.startsWith("thissetter")) {
					parseBlockNode(block);				
//					SetterVariableBlockModel model = new SetterVariableBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if ("while".equals(genusName)) {
					parseBlockNode(block);				
//					WhileBlockModel model = new WhileBlockModel(false);
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if ("dowhile".equals(genusName)) {
					parseBlockNode(block);				
//					WhileBlockModel model = new WhileBlockModel(true);
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if ("if".equals(genusName) || "ifelse".equals(genusName)) {
					parseBlockNode(block);				
//					IfBlockModel model = new IfBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if ("repeat".equals(genusName)) {
					parseBlockNode(block);				
//					RepeatBlockModel model = new RepeatBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if ("abstraction".equals(genusName)) {
					parseBlockNode(block);				
//					AbstractionBlockModel model = new AbstractionBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("inc")
						&& genusName.endsWith("number")) {// #matsuzawa 何で特別扱い？
					parseBlockNode(block);				
//					PostfixExpressionModel model = new PostfixExpressionModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("not")) {// #matsuzawa
															// とりあえずadhocに追加
					parseBlockNode(block);				
//					NotExpressionModel model = new NotExpressionModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("callActionMethod")
						|| genusName.startsWith("callGetterMethod")
						/* ! */|| genusName.startsWith("callBooleanMethod")
						|| genusName.startsWith("callDoubleMethod")
						|| genusName.startsWith("callStringMethod")
						|| genusName.startsWith("callObjectMethod")
						|| genusName.startsWith("callThisActionMethod")) {
					parseBlockNode(block);				
					
//					ReferenceBlockModel model = new ReferenceBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("special")) {// special,
																// special-expression
					parseBlockNode(block);				
					// とりあえず，call methodと同じで実装 #matsuzawa 2012.11.07 //
//					SpecialBlockModel model = new SpecialBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("return")) {// #matsuzawa return
					parseBlockNode(block);				
//					ReturnBlockModel model = new ReturnBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("break")) {// #matsuzawa
					parseBlockNode(block);				
//					BreakBlockModel model = new BreakBlockModel("break");
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("continue")) {// #matsuzawa
					parseBlockNode(block);				
//					BreakBlockModel model = new BreakBlockModel("continue");
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("super")) {
					parseBlockNode(block);				
//					CallMethodBlockModel model = new CallMethodBlockModel(false);
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else if (genusName.startsWith("type-object")) {
					parseBlockNode(block);				
//					TypeBlockModel model = new TypeBlockModel();
//					parseBlock(block, model);
//					blockNode = blockNode.getNextSibling();
				} else {
					throw new RuntimeException("not supported blockName: "
							+ genusName);
				}
				
			}
			blockNode = blockNode.getNextSibling();
		}

	}

	private boolean isProjectMethod(Node node) {
		String paramNum = getBlockSocketsNumber(node);

		String label = getJavaLabel(node);
		String methodName = label + "(" + paramNum + ")";

		if (BlockConverter.projectMethods.get(methodName) != null) {
			return true;
		}
		return false;
	}

	private boolean isDataBlock(String blockName) {
		if (blockName.startsWith("new-")) {
			return true;
		}

		if (blockName.startsWith("getter") || blockName.contains("this")
				|| blockName.equals("gettersuper")) {
			return true;
		}
		// とりあえず
		if (blockName.endsWith("FromObject")) {
			return true;
		}

		for (String name : BlockConverter.ALL_DATA_BLOCKNAMES) {
			if (name.equals(blockName)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInfixCommandBlock(String blockName) {
		for (String name : BlockConverter.INFIX_COMMAND_BLOCKS) {
			if (name.equals(blockName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMethodCallBlock(String blockName) {
		for (String name : BlockConverter.METHOD_CALL_BLOCKS) {
			if (name.equals(blockName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param node
	 * @param b2j
	 *            .model
	 */
	private void parseBlock(Node node, BlockModel model) {

		NamedNodeMap blockAttrs = node.getAttributes();
		String blockName = blockAttrs.getNamedItem("genus-name").getNodeValue();
		int blockId = Integer.parseInt(blockAttrs.getNamedItem("id")
				.getNodeValue());

		model.setName(blockName);
		model.setId(blockId);

		if (model instanceof PrivateVariableBlockModel
				&& blockName.contains("final")) {
			((PrivateVariableBlockModel) model).setModifer("static final");
		}

		Node blockInfo = node.getFirstChild();

		while (blockInfo != null) {
			if (blockInfo.getNodeName() == "Label") {
				model.setLabel(blockInfo.getTextContent());
			} else if (blockInfo.getNodeName() == "HeaderLabel") {
				model.setType(blockInfo.getTextContent());
			} else if (blockInfo.getNodeName() == "Collapsed") {
				model.setCollapsed(true);
			} else if (blockInfo.getNodeName() == "ParameterizedType") {
				NodeList parameterizedTypes = blockInfo.getChildNodes();
				ArrayList<String> types = new ArrayList<String>();
				for (int i = 0; i < parameterizedTypes.getLength(); i++) {
					if (parameterizedTypes.item(i).getNodeName().equals("Type")) {
						types.add(parameterizedTypes.item(i).getTextContent());
					}
				}
				model.setParameterizedType(types);
			} else if (blockInfo.getNodeName() == "ParentID") {
				model.setParentID((Integer.parseInt(blockInfo.getTextContent())));
			} else if (blockInfo.getNodeName() == "BeforeBlockId") {
				model.setBeforeID(Integer.parseInt(blockInfo.getTextContent()));
			} else if (blockInfo.getNodeName() == "AfterBlockId") {
				model.setAfterID(Integer.parseInt(blockInfo.getTextContent()));
			} else if (blockInfo.getNodeName() == "JavaType") {
				model.setJavaType(blockInfo.getTextContent());
			} else if (blockInfo.getNodeName() == "JavaLabel") {
				model.setJavaLabel(blockInfo.getTextContent());
			} else if (blockInfo.getNodeName() == "Plug") {
				BlockConnectorModel conn = parseBlockConnector(blockInfo
						.getFirstChild());
				model.setPlug(conn);
			} else if (blockInfo.getNodeName() == "LineComment") {// #ohata
				model.setComment(blockInfo.getTextContent());
			} else if (blockInfo.getNodeName() == "Location") {
				int x = Integer.parseInt(blockInfo.getFirstChild()
						.getTextContent());
				int y = Integer.parseInt(blockInfo.getLastChild()
						.getTextContent());
				model.setPosition(x, y);
			} else if (blockInfo.getNodeName() == "Sockets") {
				Node blockConnectorInfo = blockInfo.getFirstChild();
				while (blockConnectorInfo != null) {
					BlockConnectorModel conn = parseBlockConnector(blockConnectorInfo);
					model.addConnector(conn);
					blockConnectorInfo = blockConnectorInfo.getNextSibling();
				}
			}
			/*
			 * else if (blockInfo.getNodeName() == "Comment"){ Node
			 * blockCommentInfo = blockInfo.getFirstChild();
			 * while(blockCommentInfo != null){
			 * if(blockCommentInfo.getNodeName() == "Text"){
			 * model.setText(blockCommentInfo.getTextContent()); } } }
			 */
			blockInfo = blockInfo.getNextSibling();
		}
		blockModels.put(model.getId(), model);
	}
	
	private void parseBlockNode(Node node){

		NamedNodeMap blockAttrs = node.getAttributes();
		String blockName = blockAttrs.getNamedItem("genus-name").getNodeValue();
		int blockId = Integer.parseInt(blockAttrs.getNamedItem("id")
				.getNodeValue());
		System.out.println("GenusName:" + blockName);
		System.out.println("block id :" + blockId);
		
		Node blockInfo = node.getFirstChild();

		while (blockInfo != null) {
			if (blockInfo.getNodeName() == "Label") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());
			} else if (blockInfo.getNodeName() == "HeaderLabel") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());				
			} else if (blockInfo.getNodeName() == "Collapsed") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());				
			} else if (blockInfo.getNodeName() == "ParameterizedType") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());				
			} else if (blockInfo.getNodeName() == "ParentID") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());				
			} else if (blockInfo.getNodeName() == "BeforeBlockId") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());				
			} else if (blockInfo.getNodeName() == "AfterBlockId") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());				
			} else if (blockInfo.getNodeName() == "Type") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());				
			} else if (blockInfo.getNodeName() == "Name") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());				
			} else if (blockInfo.getNodeName() == "Plug") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());
				parseBlockConnectorElement(getNamedNode("BlockConnector", blockInfo.getChildNodes()));
			} else if (blockInfo.getNodeName() == "LineComment") {// #ohata
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());				
			} else if (blockInfo.getNodeName() == "Location") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getTextContent());				
			} else if (blockInfo.getNodeName() == "Sockets") {
				System.out.println(blockInfo.getNodeName()  + ":" + blockInfo.getAttributes().getNamedItem("num-sockets"));
				Node blockConnectorInfo = getNamedNode("BlockConnector", blockInfo.getChildNodes());
				while(blockConnectorInfo != null){
					parseBlockConnectorElement(blockConnectorInfo);
					blockConnectorInfo = blockConnectorInfo.getNextSibling();
				}
			}
			/*
			 * else if (blockInfo.getNodeName() == "Comment"){ Node
			 * blockCommentInfo = blockInfo.getFirstChild();
			 * while(blockCommentInfo != null){
			 * if(blockCommentInfo.getNodeName() == "Text"){
			 * model.setText(blockCommentInfo.getTextContent()); } } }
			 */
			blockInfo = blockInfo.getNextSibling();
		}
		
	}
	
	
	private Node getNamedNode(String name, NodeList nodeList){
		if(nodeList.getLength()<1){
			return null;
		}
		
		Node node = nodeList.item(0);
		for(int i = 0 ;i < nodeList.getLength();i++){
			if(node.getNodeName().equals(name)){
				return node;
			}
			node = nodeList.item(i);
		}
		
		return null;
	}

	private String getBlockSocketsNumber(Node node) {
		int num = 0;
		Node blockInfo = node.getFirstChild();

		while (blockInfo != null) {
			if (blockInfo.getNodeName() == "Sockets") {
				Node blockConnectorInfo = blockInfo.getFirstChild();
				while (blockConnectorInfo != null) {
					num++;
					blockConnectorInfo = blockConnectorInfo.getNextSibling();
				}
			}
			blockInfo = blockInfo.getNextSibling();
		}

		if (num == 0) {
			return "";
		}

		return String.valueOf(num);
	}

	// private String getBlockLabel(Node node) {
	//
	// Node blockInfo = node.getFirstChild();
	//
	// while (blockInfo != null) {
	// if (blockInfo.getNodeName() == "Label") {
	// return blockInfo.getTextContent();
	// }
	// blockInfo = blockInfo.getNextSibling();
	// }
	//
	// return null;
	// }

	private String getJavaLabel(Node node) {

		Node blockInfo = node.getFirstChild();

		while (blockInfo != null) {
			if (blockInfo.getNodeName() == "JavaLabel") {
				return blockInfo.getTextContent();
			}
			blockInfo = blockInfo.getNextSibling();
		}

		return null;
	}

	/**
	 * 
	 * @param blockInfo
	 * @param b2j
	 *            .model
	 * @return
	 */
	private BlockConnectorModel parseBlockConnector(Node blockInfo) {
		BlockConnectorModel conn = new BlockConnectorModel();
		NamedNodeMap blockConnecterAttrs = blockInfo.getAttributes();
		Node id = blockConnecterAttrs.getNamedItem("con-block-id");
		if (id != null) {
			conn.setId(Integer.parseInt(id.getNodeValue()));
		}
		Node type = blockConnecterAttrs.getNamedItem("connector-type");
		if (type != null) {
			conn.setType(type.getNodeValue());
		}
		return conn;
	}
	
	private void parseBlockConnectorElement(Node blockInfo) {
		NamedNodeMap blockConnecterAttrs = blockInfo.getAttributes();
		if(blockConnecterAttrs != null){
			for(int i = 0;i<blockConnecterAttrs.getLength();i++){
				System.out.println(blockConnecterAttrs.item(i).getNodeName() +  blockConnecterAttrs.item(i));
			}
			
//			Node id = blockConnecterAttrs.getNamedItem("con-block-id");
//			if (id != null) {
//				System.out.println(id.getNodeName() + ":" + id.getTextContent());
//			}
//			Node type = blockConnecterAttrs.getNamedItem("connector-type");
//			if (type != null) {
//				System.out.println(type.getNodeName() + ":" +type .getTextContent());
//			}	
		}
	}
}
