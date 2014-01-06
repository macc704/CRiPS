package bc.b2j.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import bc.BlockConverter;
import bc.b2j.model.AbstractionBlockModel;
import bc.b2j.model.BlockModel;
import bc.b2j.model.BreakBlockModel;
import bc.b2j.model.CallMethodBlockModel;
import bc.b2j.model.ConstructorBlockModel;
import bc.b2j.model.IfBlockModel;
import bc.b2j.model.InfixCommandBlockModel;
import bc.b2j.model.LocalVariableBlockModel;
import bc.b2j.model.NoProcparamDataBlockModel;
import bc.b2j.model.NotExpressionModel;
import bc.b2j.model.PageModel;
import bc.b2j.model.PostfixExpressionModel;
import bc.b2j.model.PrivateProcedureBlockModel;
import bc.b2j.model.PrivateVariableBlockModel;
import bc.b2j.model.ProcedureBlockModel;
import bc.b2j.model.ProcedureParamBlockModel;
import bc.b2j.model.ProgramModel;
import bc.b2j.model.ReferenceBlockModel;
import bc.b2j.model.RepeatBlockModel;
import bc.b2j.model.ReturnBlockModel;
import bc.b2j.model.SetterVariableBlockModel;
import bc.b2j.model.SpecialBlockModel;
import bc.b2j.model.TypeBlockModel;
import bc.b2j.model.WhileBlockModel;

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

		while (page.getNodeName() != "Page") {
			page = page.getFirstChild();
		}

		while (page != null) {
			NamedNodeMap pageAttrs = page.getAttributes();
			String className = null;
			String superClass = null;
			// first, parse out the attributes
			nameMatcher = attrExtractor.matcher(pageAttrs.getNamedItem(
					"page-name").toString());
			if (nameMatcher.find()) {
				className = nameMatcher.group(1);
			}
			// second, parse out the attributes
			nameMatcher = attrExtractor.matcher(pageAttrs.getNamedItem(
					"page-superClass").toString());
			if (nameMatcher.find()) {
				superClass = nameMatcher.group(1);
			}

			PageModel model = new PageModel(className, superClass);

			if (page.getFirstChild() != null) {
				resolveBlock(page.getFirstChild(), model);
			}
			programModel.addPage(model);
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

		while (blockNode.getNodeName() != "Block"
				&& blockNode.getNodeName() != "BlockStub") {
			blockNode = blockNode.getFirstChild();
		}

		while (blockNode != null) {
			Node block = blockNode;

			// NodeNameがBlockになるまで
			if (block.getNodeName() == "BlockStub") {
				block = block.getFirstChild();
				while (block.getNodeName() != "Block") {
					block = block.getNextSibling();
				}
			}

			NamedNodeMap BlockAttrs = block.getAttributes();
			String genus_name = BlockAttrs.getNamedItem("genus-name")
					.getNodeValue();

			if ("procedure".equals(genus_name)) {
				ProcedureBlockModel model = new ProcedureBlockModel();
				parseBlock(block, model);
				pageModel.addProcedure(model);
				blockNode = blockNode.getNextSibling();

			} else if ("constructor".equals(genus_name)) {// #ohata
															// コンストラクタブロックの処理
				ConstructorBlockModel model = new ConstructorBlockModel();
				parseBlock(block, model);
				model.setLabel(fileURI);
				model.setURI(fileURI);
				pageModel.addConstructor(model);
				blockNode = blockNode.getNextSibling();
			} else if ("private-procedure".equals(genus_name)) {// #ohata
																// 使わない　プライベート変数宣言用の手続き型ブロック
				PrivateProcedureBlockModel model = new PrivateProcedureBlockModel();
				parseBlock(block, model);
				// pageModel.addPrivateProcedure(model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("proc-param")) {
				ProcedureParamBlockModel model = new ProcedureParamBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (isMethodCallBlock(genus_name)
					|| isProjectMethod(genus_name, block)) {
				CallMethodBlockModel model = new CallMethodBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (isDataBlock(genus_name)) {
				NoProcparamDataBlockModel model = new NoProcparamDataBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (isInfixCommandBlock(genus_name)) {
				InfixCommandBlockModel model = new InfixCommandBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("local-var-")) {
				LocalVariableBlockModel model = new LocalVariableBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("private")) {// #ohata
				PrivateVariableBlockModel model = new PrivateVariableBlockModel();
				parseBlock(block, model);
				pageModel.addPrivateVariableBlock(model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("setter")
					|| genus_name.startsWith("this-setter")) {
				SetterVariableBlockModel model = new SetterVariableBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if ("while".equals(genus_name)) {
				WhileBlockModel model = new WhileBlockModel(false);
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if ("dowhile".equals(genus_name)) {
				WhileBlockModel model = new WhileBlockModel(true);
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if ("if".equals(genus_name) || "ifelse".equals(genus_name)) {
				IfBlockModel model = new IfBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if ("repeat".equals(genus_name)) {
				RepeatBlockModel model = new RepeatBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if ("abstraction".equals(genus_name)) {
				AbstractionBlockModel model = new AbstractionBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("inc")
					&& genus_name.endsWith("number")) {// #matsuzawa 何で特別扱い？
				PostfixExpressionModel model = new PostfixExpressionModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("not")) {// #matsuzawa
														// とりあえずadhocに追加
				NotExpressionModel model = new NotExpressionModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("callActionMethod")
					|| genus_name.startsWith("callGetterMethod")
					/* ! */|| genus_name.startsWith("callBooleanMethod")
					|| genus_name.startsWith("callDoubleMethod")
					|| genus_name.startsWith("callStringMethod")
					|| genus_name.startsWith("callObjectMethod")
					|| genus_name.startsWith("callThisActionMethod")) {
				ReferenceBlockModel model = new ReferenceBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("special")) {// special,
															// special-expression
				// とりあえず，call methodと同じで実装 #matsuzawa 2012.11.07 //
				SpecialBlockModel model = new SpecialBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("caller")) {// method-call (stub)
				CallMethodBlockModel model = new CallMethodBlockModel(true);
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("return")) {// #matsuzawa return
				ReturnBlockModel model = new ReturnBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("break")) {// #matsuzawa
				BreakBlockModel model = new BreakBlockModel("break");
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("continue")) {// #matsuzawa
				BreakBlockModel model = new BreakBlockModel("continue");
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("super")) {
				CallMethodBlockModel model = new CallMethodBlockModel(false);
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else if (genus_name.startsWith("type-object")) {
				TypeBlockModel model = new TypeBlockModel();
				parseBlock(block, model);
				blockNode = blockNode.getNextSibling();
			} else {
				throw new RuntimeException("not supported blockName: "
						+ genus_name);
			}
		}

	}

	private boolean isProjectMethod(String name, Node node) {
		int i = getBlockSocketsNumber(node);
		name = name + "(" + i + ")";

		if (BlockConverter.projectMethods.get(name) != null) {
			return true;
		}
		return false;
	}

	private boolean isDataBlock(String blockName) {
		if (blockName.startsWith("new-object")
				|| blockName.startsWith("new-arrayobject")
				|| blockName.startsWith("new-listobject")) {
			return true;
		}

		if (blockName.startsWith("getter") || blockName.contains("this")) {
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
			} else if (blockInfo.getNodeName() == "BeforeBlockId") {
				model.setBeforeID(Integer.parseInt(blockInfo.getTextContent()));
			} else if (blockInfo.getNodeName() == "AfterBlockId") {
				model.setAfterID(Integer.parseInt(blockInfo.getTextContent()));
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

	private int getBlockSocketsNumber(Node node) {
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

		return num;
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

}
