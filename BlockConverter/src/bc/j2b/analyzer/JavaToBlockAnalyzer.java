package bc.j2b.analyzer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import bc.BlockConverter;
import bc.j2b.model.ClassModel;
import bc.j2b.model.CompilationUnitModel;
import bc.j2b.model.ElementModel;
import bc.j2b.model.ExArrayInstanceCreationModel;
import bc.j2b.model.ExCallActionMethodModel;
import bc.j2b.model.ExCallActionMethodModel2;
import bc.j2b.model.ExCallGetterMethodModel;
import bc.j2b.model.ExCallMethodModel;
import bc.j2b.model.ExCallUserMethodModel;
import bc.j2b.model.ExCastModel;
import bc.j2b.model.ExClassInstanceCreationModel;
import bc.j2b.model.ExInfixModel;
import bc.j2b.model.ExLeteralModel;
import bc.j2b.model.ExNotModel;
import bc.j2b.model.ExPostfixModel;
import bc.j2b.model.ExSpecialExpressionModel;
import bc.j2b.model.ExTypeModel;
import bc.j2b.model.ExVariableGetterModel;
import bc.j2b.model.ExVariableSetterModel;
import bc.j2b.model.ExpressionModel;
import bc.j2b.model.StAbstractionBlockModel;
import bc.j2b.model.StBlockModel;
import bc.j2b.model.StBreakStatementModel;
import bc.j2b.model.StConstructorDeclarationModel;
import bc.j2b.model.StEmptyStatementModel;
import bc.j2b.model.StExpressionModel;
import bc.j2b.model.StIfElseModel;
import bc.j2b.model.StLocalVariableModel;
import bc.j2b.model.StMethodDeclarationModel;
import bc.j2b.model.StPrivateVariableDeclarationModel;
import bc.j2b.model.StReturnModel;
import bc.j2b.model.StSuperConstructorInvocationModel;
import bc.j2b.model.StThisVariableModel;
import bc.j2b.model.StVariableDeclarationModel;
import bc.j2b.model.StWhileModel;
import bc.j2b.model.StatementModel;

public class JavaToBlockAnalyzer extends ASTVisitor {

	// private static final int FIRST_ID_COUNTER = 245;
	private static final int FIRST_ID_COUNTER = 1000;

	private IdCounter idCounter = new IdCounter(FIRST_ID_COUNTER);
	private VariableResolver variableResolver = new VariableResolver();
	private MethodResolver methodResolver = new MethodResolver();

	private CompilationUnitModel currentCompilationUnit;
	private CompilationUnit compilationUnit;
	// private ClassModel currentClass;

	// 抽象化ブロックのコメント
	private HashMap<Integer, String> abstractionComments = new HashMap<Integer, String>();
	private JavaCommentManager commentGetter;

	/** created by sakai lab 2011/11/22 */
	// private AbstractionBlockByTagParser abstParser;
	public JavaToBlockAnalyzer(File file, String enc) {
		this.commentGetter = new JavaCommentManager(file, enc);
		createThisModel();
	}

	public JavaToBlockAnalyzer(File file, String enc,
			Map<String, String> addedMethods) {
		this.commentGetter = new JavaCommentManager(file, enc);
		createThisModel();
		for (String method : addedMethods.keySet()) {
			methodResolver
					.addMethodReturnType(method, addedMethods.get(method));
		}
		// arranged by sakai lab 2011/11/22
		// abstParser = new AbstractionBlockByTagParser(file);
		// StGlobalVariableModel variable = new StGlobalVariableModel();
		// variable.setName("window");
		// variable.setType("TurtleFrame");
		// variableResolver.addGlobalVariable(variable);
	}

	public CompilationUnitModel getCompilationUnit() {
		return currentCompilationUnit;
	}

	@Override
	public boolean visit(CompilationUnit node) {
		compilationUnit = node;
		CompilationUnitModel model = new CompilationUnitModel();
		currentCompilationUnit = model;

		@SuppressWarnings("unchecked")
		List<Comment> comments = node.getCommentList();

		for (Comment comment : comments) {
			if (comment instanceof LineComment) {
				abstractionComments
						.put(comment.getStartPosition(), commentGetter
								.getLineComment(comment.getStartPosition()));
			} else if (comment instanceof BlockComment) {
				abstractionComments.put(comment.getStartPosition(),
						commentGetter.getBlockComment(comment
								.getStartPosition()));
			}
		}
		return true;
	}

	@Override
	public boolean preVisit2(ASTNode node) {
		return true;
	}

	/**
	 * Classの解析
	 * 
	 * @param node
	 *            :TypeDeclarationノード
	 */
	@Override
	public boolean visit(TypeDeclaration node) {

		variableResolver.resetGlobalVariable();
		methodResolver.reset();
		ClassModel model = new ClassModel();
		model.setName(node.getName().toString());
		if (node.getSuperclassType() != null) {
			model.setSuperClass(node.getSuperclassType().toString());
		}
		currentCompilationUnit.addClass(model);
		// this.currentClass = model;

		// 2パス必要 #matsuzawa 2012.11.24

		for (MethodDeclaration method : node.getMethods()) {
			createStub(method);
		}

		// #ohata replaced
		int x = 50;
		int y = 50;
		for (FieldDeclaration fieldValue : node.getFields()) {
			if (addPrivateVariableDeclarationModel(model, fieldValue, x, y)) {
				y += model.getPrivateValues()
						.get(model.getPrivateValues().size() - 1)
						.getBlockHeight();
			}
		}

		for (MethodDeclaration method : node.getMethods()) {
			if (x > 1000) {// とりあえず整数で...
				x = 50;
				y += 200;
			}

			if (method.isConstructor()) {
				if (addConstructorModel(model, method, x, y)) {
					if (model.getConstructors()
							.get(model.getConstructors().size() - 1).getPosX() == x) {
						x += 200;
					}
				}
			} else {
				if (addMethodModel(model, method, x, y)) {
					if (model.getMethods().get(model.getMethods().size() - 1)
							.getPosX() == x) {
						x += 200;
					}
				}
			}
		}
		return true;
	}

	private boolean addPrivateVariableDeclarationModel(ClassModel model,
			FieldDeclaration fieldValue, int x, int y) {

		StPrivateVariableDeclarationModel privateVariableModel = analyzePrivateValue(fieldValue);

		int index = commentGetter.getLineCommentPosition(fieldValue
				.getStartPosition() + fieldValue.getLength());

		if (privateVariableModel != null) {
			String lineComment = commentGetter.getLineComment(index);
			String position = getPositionFromLineComment(lineComment);
			// set position
			privateVariableModel.setPosX(getX(position, x));
			privateVariableModel.setPosY(getY(position, y));
			// コメントの追加
			privateVariableModel.setComment(lineComment.substring(0,
					getCommentEndIndex(lineComment)));
			model.addPrivateVariable(privateVariableModel);
			return true;
		}
		return false;
	}

	// #ohata added
	private int getCommentEndIndex(String lineComment) {
		String position = getPositionFromLineComment(lineComment);
		if (position == null) {
			return lineComment.length();
		} else {
			return lineComment.indexOf(position);
		}
	}

	private boolean getOpenCloseInfoFromLineComment(String lineComment) {
		// #ohata added default:open
		// 新しくクラスを作って移植予定
		if (lineComment.indexOf("[close]") != -1) {
			return true;
		} else if (lineComment.indexOf("[open]") != -1) {
			return false;
		} else {
			return false;// default open
		}
		/*
		 * int state = 0; for (int i = 0; i < lineComment.length(); i++){ switch
		 * (state) { case 0: if(lineComment.charAt(i) == '['){ state = 1; }
		 * break; case 1: if(lineComment.charAt(i) == 'c'){ state = 2; }else {
		 * state = 0; } break; case 2: if(lineComment.charAt(i) == 'l'){ state =
		 * 3; }else { state = 0; } break; case 3: if(lineComment.charAt(i) ==
		 * 'o'){ state = 4; }else { state = 0; } break; case 4:
		 * if(lineComment.charAt(i) == 's'){ state = 5; }else { state = 0; }
		 * break; case 5: if(lineComment.charAt(i) == 'e'){ state = 6; }else {
		 * state = 0; } break; case 6: if(lineComment.charAt(i) == ']'){ return
		 * true; }else{ state = 0; } break; default: break; } } return false;
		 */
	}

	private String getPositionFromLineComment(String lineComment) {
		// #ohata added
		// 新しくクラスを作って移植
		int state = 0;
		int start = 0;

		for (int i = 0; i < lineComment.length(); i++) {
			switch (state) {
			case 0:
				if (lineComment.charAt(i) == '@') {
					state = 1;
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			case 1:
				if (lineComment.charAt(i) == '(') {
					state = 2;
				} else if (lineComment.charAt(i) == ' ') {
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			case 2:
				if (lineComment.charAt(i) == ' ') {// 空白文字は素通り
				} else if (Character.isDigit(lineComment.charAt(i))) {
					state = 3;
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			case 3:
				if (Character.isDigit(lineComment.charAt(i))) {
				} else if (lineComment.charAt(i) == ',') {
					state = 4;
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			case 4:
				if (Character.isDigit(lineComment.charAt(i))) {
					state = 5;
				} else if (lineComment.charAt(i) == ' ') {
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			case 5:
				if (Character.isDigit(lineComment.charAt(i))) {
				} else if (lineComment.charAt(i) == ')') {
					int end = i + 1;
					return lineComment.substring(start, end);
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			default:
				break;
			}
		}
		return null;
	}

	private boolean addConstructorModel(ClassModel model,
			MethodDeclaration method, int x, int y) {
		// #ohata added
		StConstructorDeclarationModel constructorModel = analyzeConstructor(method);

		int index = commentGetter.getLineCommentPosition(method
				.getStartPosition() + method.getLength());// ここで-1が帰ってくると、

		if (constructorModel != null) {
			String lineComment = commentGetter.getLineComment(index);
			String position = getPositionFromLineComment(lineComment);
			// 座標の指定
			constructorModel.setPosX(getX(position, x));
			constructorModel.setPosY(getY(position, y));
			// comment set
			constructorModel.setComment(lineComment.substring(0,
					getCommentEndIndex(lineComment)));// こっちでへんな場所をとっちゃう
			// open/close set
			constructorModel
					.setCollapsed(getOpenCloseInfoFromLineComment(lineComment));
			model.addConstructor(constructorModel);
			return true;
		}
		return false;
	}

	private boolean addMethodModel(ClassModel model, MethodDeclaration method,
			int x, int y) {
		// #ohata added
		StMethodDeclarationModel methodModel = analyzeMethod(method);

		int index = commentGetter.getLineCommentPosition(method
				.getStartPosition() + method.getLength());

		if (methodModel != null) {
			String lineComment = commentGetter.getLineComment(index);
			String position = getPositionFromLineComment(lineComment);
			// position set
			methodModel.setPosX(getX(position, x));
			methodModel.setPosY(getY(position, y));
			// comment set
			methodModel.setComment(lineComment.substring(0,
					getCommentEndIndex(lineComment)));
			// open/close set
			methodModel
					.setCollapsed(getOpenCloseInfoFromLineComment(lineComment));
			model.addMethod(methodModel);
			return true;
			// open/close
		}
		return false;
	}

	private int getX(String position, int x) {
		// #ohata added
		int posX = x;
		if (position != null) {
			return getNumber(position);
		}
		return posX;
	}

	private int getNumber(String str) {
		int start = 0;
		int state = 0;
		for (int i = 0; i <= str.length(); i++) {
			switch (state) {
			case 0:
				if (Character.isDigit(str.charAt(i))) {
					state = 1;
				} else {
					start = i + 1;
					state = 0;
					break;
				}
			case 1:
				if (Character.isDigit(str.charAt(i))) {
				} else {
					return Integer.parseInt(str.substring(start, i));
				}
				break;
			default:
				break;
			}
		}
		return -1;
	}

	private int getY(String position, int y) {
		// #ohata added
		int posY = y;
		if (position != null) {
			return getNumber(position.substring(position.indexOf(','),
					position.length()));
		}
		return posY;
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		// this.currentClass = null;
	}

	@SuppressWarnings("unchecked")
	private void createStub(MethodDeclaration method) {
		if (method.isConstructor()) {
			methodResolver.putUserConstructor(method.getName().toString(),
					(List<SingleVariableDeclaration>) method.parameters());
		} else {
			methodResolver.putUserMethod(method.getName().toString(),
					(List<SingleVariableDeclaration>) method.parameters(),
					method.getReturnType2().toString());
		}

	}

	private StPrivateVariableDeclarationModel analyzePrivateValue(
			FieldDeclaration node) {
		StPrivateVariableDeclarationModel model = new StPrivateVariableDeclarationModel();

		int index = node.fragments().get(0).toString().indexOf("=");

		model.setType(node.getType().toString());
		model.setId(idCounter.getNextId());
		model.setName(node.fragments().get(0).toString()
				.substring(0, node.fragments().get(0).toString().length()));
		for (Object modifer : node.modifiers()) {
			if (modifer.toString().equals("final")) {
				model.setModifer("final-");
			}
		}

		// initializeラベルの貼り付け
		if (index != -1) {
			model.setName(node
					.fragments()
					.get(0)
					.toString()
					.substring(0,
							node.fragments().get(0).toString().indexOf("=")));
			VariableDeclaration val = ((VariableDeclaration) node.fragments()
					.get(0));
			model.setInitializer(parseExpression(val.getInitializer()));
		}

		variableResolver.addGlobalVariable(model);

		return model;

	}

	// /**
	// * メソッド定義の解析
	// *
	// * @param node
	// * :MethodDeclarationノード
	// * @return
	// */
	// @Override
	// public boolean visit(MethodDeclaration node) {

	private StConstructorDeclarationModel analyzeConstructor(
			MethodDeclaration node) {
		StConstructorDeclarationModel model = new StConstructorDeclarationModel();

		variableResolver.resetLocalVariable();

		// メソッド引数の処理
		for (Object o : node.parameters()) {
			SingleVariableDeclaration arg = ((SingleVariableDeclaration) o);
			StLocalVariableModel argModel = createLocalVariableModel(arg
					.getType().toString(), arg.getName().toString(),
					arg.getInitializer(), true);
			model.addArgument(argModel);
		}

		// SingleVariableDeclaration
		model.setName(node.getName().toString());
		model.setId(idCounter.getNextId());

		// currentClass.addMethod(model);

		// コンストラクタの中身
		StBlockModel body = parseBlockStatement(node.getBody());
		body.setParent(model);
		model.setBody(body);

		// return true;
		return model;

	}

	private StMethodDeclarationModel analyzeMethod(MethodDeclaration node) {
		if ("main".equals(node.getName().toString())) {
			// return false;
			return null;
		}

		variableResolver.resetLocalVariable();

		StMethodDeclarationModel model = new StMethodDeclarationModel();
		// メソッド引数の処理
		for (Object o : node.parameters()) {
			SingleVariableDeclaration arg = ((SingleVariableDeclaration) o);
			StLocalVariableModel argModel = createLocalVariableModel(arg
					.getType().toString(), arg.getName().toString(),
					arg.getInitializer(), true);
			argModel.setLineNumber(compilationUnit.getLineNumber(arg
					.getStartPosition()));
			model.addArgument(argModel);
		}
		// SingleVariableDeclaration
		model.setName(node.getName().toString());
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// currentClass.addMethod(model);

		for (Object modifer : node.modifiers()) {
			if ("abstract".equals(modifer.toString())) {
				return model;
			}
		}

		// メソッドの中身
		StBlockModel body = parseBlockStatement(node.getBody());
		body.setParent(model);
		model.setBody(body);

		// return true;
		return model;
	}

	/************************************************************
	 * 以下，Statementの解析系
	 ************************************************************/

	/**
	 * 各Statementへの経由関数
	 * 
	 * @param stmt
	 *            ：Statementノード
	 * @return 各Statementの解析結果
	 */
	public StatementModel parseStatement(Statement stmt) {
		try {
			if (stmt instanceof Block && stmt.getParent() instanceof Block) {
				return parseAbstractionBlock((Block) stmt);
			} else if (stmt instanceof Block) {
				return parseBlockStatement((Block) stmt);
			} else if (stmt instanceof IfStatement) {
				return parseIfStatement((IfStatement) stmt);
			} else if (stmt instanceof WhileStatement) {
				return parseWhileStatement((WhileStatement) stmt);
			} else if (stmt instanceof DoStatement) {
				return parseDoStatement((DoStatement) stmt);
			} else if (stmt instanceof ForStatement) {
				return parseForStatement((ForStatement) stmt);
			} else if (stmt instanceof EnhancedForStatement) {
				return parseEnhancedForStatement((EnhancedForStatement) stmt);
			} else if (stmt instanceof ExpressionStatement) {
				return parseExpressionStatement((ExpressionStatement) stmt);
			} else if (stmt instanceof VariableDeclarationStatement) {
				return parseVariableDeclarationStatement((VariableDeclarationStatement) stmt);
			} else if (stmt instanceof ReturnStatement) {
				return analyzeReturnStatement((ReturnStatement) stmt);
			} else if (stmt instanceof BreakStatement) {
				return analyzeBreakStatement((BreakStatement) stmt);
			} else if (stmt instanceof ContinueStatement) {
				return analyzeContinueStatement((ContinueStatement) stmt);
			} else if (stmt instanceof EmptyStatement) {
				StEmptyStatementModel empty = new StEmptyStatementModel();
				empty.setId(idCounter.getNextId());
				empty.setLineNumber(compilationUnit.getLineNumber(stmt
						.getStartPosition()));
				return empty;
			} else if (stmt instanceof SuperConstructorInvocation) {
				return analyzeSuperConstructorInvocation((SuperConstructorInvocation) stmt);
			}
			throw new RuntimeException(
					"The stmt type has not been supported yet stmt: "
							+ stmt.getClass() + ", " + stmt.toString());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
			// Expressionの方で対応 2012.11.23 #matsuzawa 2.10.15
			// // StSpecialBlockModel special = new
			// // StSpecialBlockModel(stmt.toString());
			// // #matsuzawa SpExpressionに統合 2012.11.12
			// ExSpecialExpressionModel special = new ExSpecialExpressionModel(
			// stmt.toString());
			// special.setId(idCounter.getNextId());
			// StExpressionModel stex = new StExpressionModel(special);
			// stex.setId(idCounter.getNextId());
			// return stex;
		}
	}

	private StatementModel parseEnhancedForStatement(EnhancedForStatement node) {
		// Initializer
		StAbstractionBlockModel block = new StAbstractionBlockModel();
		block.setCommnent("for each");
		block.setId(idCounter.getNextId());
		block.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));

		// counter
		StatementModel counter = createLocalVariableModel("int", "counter",
				null, false);
		ExLeteralModel initValue = new ExLeteralModel();
		initValue.setType("number");
		initValue.setValue("0");
		initValue.setId(idCounter.getNextId());
		initValue.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		initValue.setParent(counter);
		((StLocalVariableModel) counter).setInitializer(initValue);
		block.addChild(counter);

		// endIndex initializer
		ExpressionModel testClause = parseExpression(node.getExpression());
		ExCallMethodModel indexEndInitializer = new ExCallMethodModel();
		indexEndInitializer.setType("int");
		indexEndInitializer.setName("size");
		indexEndInitializer.setId(idCounter.getNextId());
		indexEndInitializer.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		ExCallActionMethodModel2 callActionMethodModel = createExCallActionBlock2(
				testClause, indexEndInitializer, node.getStartPosition());

		// endIndex
		StLocalVariableModel endIndex = createLocalVariableModel("int",
				"endIndex", null, false);
		endIndex.setInitializer(callActionMethodModel);
		block.addChild(endIndex);

		{// While 本体
			// 条件式
			ExInfixModel condition = new ExInfixModel();
			condition.setOperator("<");
			condition.setId(idCounter.getNextId());
			condition.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			// 条件式のrightexpression
			ExVariableGetterModel sizeBlock = new ExVariableGetterModel();
			sizeBlock.setVariable(variableResolver.resolve("endIndex"));
			sizeBlock.setId(idCounter.getNextId());
			sizeBlock.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			condition.setRightExpression(sizeBlock);
			sizeBlock.setParent(condition);
			// left
			ExVariableGetterModel counterGetter = new ExVariableGetterModel();
			counterGetter.setVariable(variableResolver.resolve("counter"));
			counterGetter.setId(idCounter.getNextId());
			counterGetter.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));

			condition.setLeftExpression(counterGetter);
			counterGetter.setParent(condition);

			// body
			// receiver
			ExVariableGetterModel listReceiver = new ExVariableGetterModel();
			listReceiver.setId(idCounter.getNextId());
			listReceiver.setVariable(variableResolver.resolve(node
					.getExpression().toString()));
			listReceiver.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));

			// method
			ExCallMethodModel getMethod = new ExCallMethodModel();
			getMethod.setType(node.getParameter().getType().toString());
			getMethod.setName("get");
			getMethod.setId(idCounter.getNextId());
			getMethod.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));

			// counter
			ExVariableGetterModel index = new ExVariableGetterModel();
			index.setVariable(variableResolver.resolve("counter"));
			index.setId(idCounter.getNextId());
			getMethod.addArgument(index);

			// size
			ExCallActionMethodModel2 callMethodModel = createExCallActionBlock2(
					listReceiver, getMethod, node.getStartPosition());
			// element = list.get(counter)
			SingleVariableDeclaration param = node.getParameter();
			StatementModel model = createLocalVariableModel(param.getType()
					.toString(), param.getName().toString(), null, false);

			((StLocalVariableModel) model).setInitializer(callMethodModel);

			Statement bodyClause = node.getBody();
			StWhileModel whileModel = createWhileStatement(
					node.getExpression(), bodyClause, model);
			whileModel.setTestClause(condition);

			// updater
			ExVariableSetterModel increment = createExVariableSetterModel("counter");
			ExInfixModel incrementLeft = createExInfixModel(
					createExVariableGetterModel("counter"),
					createExLeteralModel("1"), "+");
			increment.setRightExpression(incrementLeft);

			StExpressionModel incrementModel = new StExpressionModel(increment);

			whileModel.getBodyClause().addElement(incrementModel);

			block.addChild(whileModel);
		}

		return block;

	}

	private ExInfixModel createExInfixModel(ExpressionModel left,
			ExpressionModel right, String operator) {
		ExInfixModel model = new ExInfixModel();
		model.setOperator(operator);
		model.setId(idCounter.getNextId());
		model.setRightExpression(right);
		model.setLeftExpression(left);
		return model;
	}

	private ExLeteralModel createExLeteralModel(String value) {
		ExLeteralModel model = new ExLeteralModel();
		model.setId(idCounter.getNextId());
		model.setValue("1");
		return model;
	}

	private ExVariableGetterModel createExVariableGetterModel(String name) {
		ExVariableGetterModel model = new ExVariableGetterModel();
		model.setId(idCounter.getNextId());
		model.setVariable(variableResolver.resolve(name));
		return model;
	}

	private ExVariableSetterModel createExVariableSetterModel(String name) {
		ExVariableSetterModel model = new ExVariableSetterModel();
		model.setId(idCounter.getNextId());
		model.setVariable(variableResolver.resolve(name));
		return model;
	}

	private ExCallActionMethodModel2 createExCallActionBlock2(
			ExpressionModel receiver, ExpressionModel callMethod,
			int startPosition) {
		ExCallActionMethodModel2 model = new ExCallActionMethodModel2();
		model.setId(idCounter.getNextId());
		model.setLineNumber(startPosition);

		model.setReceiver(receiver);
		model.setCallMethod(callMethod);

		return model;
	}

	private StatementModel analyzeSuperConstructorInvocation(
			SuperConstructorInvocation stmt) {
		StSuperConstructorInvocationModel model = new StSuperConstructorInvocationModel(
				"super");

		for (Object parameter : stmt.arguments()) {
			ExpressionModel param = parseExpression((Expression) parameter);
			param.setId(idCounter.getNextId());
			param.setLineNumber(compilationUnit.getLineNumber(stmt
					.getStartPosition()));
			param.setParent(model);
			model.addParameter(param);
		}

		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(stmt
				.getStartPosition()));
		return model;
	}

	private StatementModel analyzeReturnStatement(ReturnStatement stmt) {
		StReturnModel model = new StReturnModel();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(stmt
				.getStartPosition()));
		if (stmt.getExpression() != null) {
			model.setReturnValue(parseExpression(stmt.getExpression()));
		}
		return model;
	}

	private StatementModel analyzeBreakStatement(BreakStatement stmt) {
		StBreakStatementModel model = new StBreakStatementModel("break");
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(stmt
				.getStartPosition()));
		return model;
	}

	private StatementModel analyzeContinueStatement(ContinueStatement stmt) {
		StBreakStatementModel model = new StBreakStatementModel("continue");
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(stmt
				.getStartPosition()));
		return model;
	}

	/**
	 * ブロックの中身の解析（"{" と "}"で囲まれたStatement）
	 * 
	 * @param block
	 *            ：Blockノード
	 * @return Blockの解析結果
	 */
	private StBlockModel parseBlockStatement(Block block) {
		StBlockModel model = new StBlockModel();

		List<?> statements = block.statements();
		// すべてのメソッド内のステートメントに対して解析し、ステートメントブロックモデルへ加える
		for (Object each : statements) {
			Statement child = (Statement) each;
			model.addElement(parseStatement(child));
		}
		return model;
	}

	/**
	 * 抽象化ブロックの解析
	 * 
	 * @param block
	 * @return
	 */
	private StAbstractionBlockModel parseAbstractionBlock(Block block) {
		StAbstractionBlockModel model = new StAbstractionBlockModel();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(block
				.getStartPosition()));
		for (int i = 1; i <= 2; i++) {
			if (abstractionComments.get(block.getStartPosition() + i) != null) {
				String aComments = abstractionComments.get(block
						.getStartPosition() + i);
				if (aComments.startsWith(BlockConverter.COLLAPSED_BLOCK_LABEL)) {
					aComments = aComments
							.substring(BlockConverter.COLLAPSED_BLOCK_LABEL
									.length());
					model.setCollapsed(true);
				}
				model.setCommnent(aComments);
			}
		}
		List<?> statements = block.statements();
		for (Object each : statements) {
			Statement child = (Statement) each;
			model.addChild(parseStatement(child));
		}
		if (statements.size() > 0) {
			model.getChild(0).setParent(model);
		}
		return model;
	}

	/**
	 * If文の解析
	 * 
	 * @param node
	 *            ：IfStatementノード
	 * @return IfStatementの解析結果
	 */
	public StIfElseModel parseIfStatement(IfStatement node) {
		StIfElseModel model = new StIfElseModel();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		Expression testClause = node.getExpression();
		if (testClause != null) {
			model.setTestClause(parseExpression(testClause));
		}
		Statement thenClause = node.getThenStatement();
		if (thenClause != null) {
			// #matsuzawa 2012.10.23 複文，単文に対応
			model.setThenClause(getStBlock(parseStatement(thenClause)));
		}
		Statement elseClause = node.getElseStatement();
		if (elseClause != null) {
			// model.setElseClause((StBlockModel) parseStatement(elseClause));
			// 2012.09.25 #matsuzawa else if文に対応
			model.setElseClause(getStBlock(parseStatement(elseClause)));
		}

		return model;
	}

	public StWhileModel parseWhileStatement(WhileStatement node) {
		Expression testClause = node.getExpression();
		Statement bodyClause = node.getBody();
		return createWhileStatement(testClause, bodyClause, false);
	}

	public StWhileModel parseDoStatement(DoStatement node) {
		Expression testClause = node.getExpression();
		Statement bodyClause = node.getBody();
		return createWhileStatement(testClause, bodyClause, true);
	}

	private StWhileModel createWhileStatement(Expression testClause,
			Statement bodyClause, boolean isDo) {
		StWhileModel model = new StWhileModel(isDo);
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(testClause
				.getStartPosition()));
		if (testClause != null) {
			model.setTestClause(parseExpression(testClause));
		}

		if (bodyClause != null) {
			model.setBodyClause(getStBlock(parseStatement(bodyClause)));
		}
		return model;
	}

	private StWhileModel createWhileStatement(Expression testClause,
			Statement bodyClause, StatementModel initializer) {
		StWhileModel model = new StWhileModel(false);
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(testClause
				.getStartPosition()));

		StBlockModel body = new StBlockModel();
		body.addElement(initializer);
		if (testClause != null) {
			model.setTestClause(parseExpression(testClause));
		}

		if (bodyClause != null) {
			Block bodyBlock = (Block) bodyClause;
			List<?> statements = bodyBlock.statements();

			for (Object statement : statements) {
				Statement child = (Statement) statement;
				body.addElement(parseStatement(child));
			}

			model.setBodyClause(body);
		}

		return model;
	}

	// 単文も複文も複文に変換する
	private StBlockModel getStBlock(StatementModel model) {
		if (model instanceof StBlockModel) {
			return (StBlockModel) model;
		} else {
			StBlockModel stblock = new StBlockModel();
			stblock.addElement(model);
			return stblock;
		}
	}

	/**
	 * For文の解析
	 * 
	 * @param stmt
	 *            :ForStatement
	 * @return ForStatementの解析結果
	 */
	// private StBlockModel parseForStatement(ForStatement node) {
	private StatementModel parseForStatement(ForStatement node) {
		// StBlockModel block = new StBlockModel();
		// block.setId(idCounter.getNextId());
		StAbstractionBlockModel block = new StAbstractionBlockModel();
		block.setCommnent("for");
		block.setId(idCounter.getNextId());
		block.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// Initializer
		for (Object o : node.initializers()) {
			Expression exp = (Expression) o;
			if (exp instanceof Assignment) {
				StExpressionModel initializer = new StExpressionModel(
						parseExpression(exp));
				block.addChild(initializer);
			} else {
				StVariableDeclarationModel vmodel = parseVariableDeclarationExpression((VariableDeclarationExpression) exp);
				block.addChild(vmodel);
			}
			// if (exp instanceof VariableDeclarationExpression) {
			// StVariableDeclarationModel vmodel =
			// parseVariableDeclarationExpression((VariableDeclarationExpression)
			// exp);
			// block.addChild(vmodel);
			// // } else if (exp instanceof Expression) {
			// Expression model
			// ExpressionModel initializer = parseExpression(exp);
			// block.addChild(initializer);
			// }

		}

		{
			// While 本体
			Expression testClause = node.getExpression();
			Statement bodyClause = node.getBody();
			StWhileModel whileModel = createWhileStatement(testClause,
					bodyClause, false);

			// // Updater
			for (Object o : node.updaters()) {
				Expression exp = (Expression) o;
				ExpressionModel model = parseExpression(exp);
				whileModel.getBodyClause().addElement(model);
			}

			block.addChild(whileModel);
		}

		return block;

		// StLocalVariableModel model = new StLocalVariableModel();
		// model.setId(idCounter.getNextId());
		//
		// model.setName("hoge");
		// model.setType("int");
		// model.setId(idCounter.getNextId());
		// resolver.addLocalVariable(model);
		//
		// return model;

		// //Initialize
		// List<?> fragments = node.initializers();
		// for(Object initialize : fragments){
		// currentBlockStatement.addElement(parseExpression((Expression)
		// initialize));
		// }
		//
		// WhileStatementModel j2b.model = new WhileStatementModel();
		// j2b.model.setId(idCounter.getIdCounter());
		// currentStatement.setNext(j2b.model);
		// j2b.model.setPrevious(currentStatement);
		// currentStatement = j2b.model;
		// //Expression
		// Expression testClause = node.getExpression();
		// if (testClause != null) {
		// j2b.model.setTestClause(parseExpression(testClause));
		// }
		//
		// //Body
		// Statement bodyClause = node.getBody();
		// if (bodyClause != null) {
		// j2b.model.setThenClause(parseStatement(bodyClause));
		//
		// if(j2b.model.getBodyClause() instanceof BlockStatementModel){
		// currentStatement =
		// (((BlockStatementModel)j2b.model.getBodyClause()).getLastChild());
		// }
		//
		// //Update
		// List<?> updaters = node.updaters();
		// for(Object update : updaters){
		// if(update instanceof PostfixExpressionModel){
		// ElementModel updateEx = parseExpression((Expression)update);
		// currentBlockStatement.addElement(updateEx);
		// if(currentStatement instanceof ExpressionStatementModel){
		// ((ExpressionStatementModel)
		// currentStatement).getModel().setNext(updateEx);
		// }
		// currentStatement.setNext(updateEx);
		// }else if(update instanceof Assignment){
		// currentBlockStatement.addElement(
		// new ExpressionStatementModel(
		// parseExpression((Expression)update)));
		// }
		// }
		// }
		// return j2b.model;
	}

	/**
	 * 式（文として）の解析
	 * 
	 * @param stmt
	 *            ：Expressionノード
	 * @return ExpressionStatementの解析結果
	 */
	private StExpressionModel parseExpressionStatement(ExpressionStatement stmt) {
		return new StExpressionModel(parseExpression(stmt.getExpression()));
	}

	/**
	 * 変数宣言の解析
	 * 
	 * @param node
	 *            ：VariableDeclarationStatementノード
	 * @return j2b.model:VariableDeclarationStatementの解析結果
	 */
	public StatementModel parseVariableDeclarationStatement(
			VariableDeclarationStatement node) {
		if (node.fragments().size() > 1) {
			throw new RuntimeException(
					"Two or more do not make a variable declaration simultaneously. ");
		}
		// // int i,j,k; のような書き方をパースする
		// BlockStatementModel block = new BlockStatementModel();
		// List<?> fragments = node.fragments();
		// for (int i = 0; i < fragments.size(); i++) {
		String typeString = typeString(node.getType());
		if (node.getType().isArrayType()) {// Type i[]
			// 配列を作る　とりあえず、通常の変数と同様に作成する
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) node
					.fragments().get(0);
			StatementModel model = createLocalVariableModel(typeString,
					fragment.getName().toString(), fragment.getInitializer(),
					false);
			model.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			return model;

		} else if (node.getType().isParameterizedType()) {// Type< Type{, TYpe}>
			ParameterizedType type = ((ParameterizedType) node.getType());
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) node
					.fragments().get(0);
			StatementModel model = createLocalVariableModel(type.getType()
					.toString(), fragment.getName().toString(),
					fragment.getInitializer(), false);
			model.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			return model;
			// ((ParameterizedType)node.getType()).typeArguments()
		} else if (node.getType().isPrimitiveType()
				|| node.getType().isSimpleType()) {
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) node
					.fragments().get(0);
			StatementModel model = createLocalVariableModel(typeString,
					fragment.getName().toString(), fragment.getInitializer(),
					false);
			model.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			return model;
		} else {
			throw new IllegalArgumentException("まだ未対応の AST node type を解析しました.");
		}
	}

	private StLocalVariableModel parseVariableDeclarationExpression(
			VariableDeclarationExpression node) {
		if (node.fragments().size() > 1) {
			throw new RuntimeException(
					"Two or more do not make a variable declaration simultaneously. ");
		}

		String typeString = typeString(node.getType());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) node
				.fragments().get(0);
		StLocalVariableModel model = createLocalVariableModel(typeString,
				fragment.getName().toString(), fragment.getInitializer(), false);
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		return model;
	}

	private String typeString(Type type) {
		String typeString = type.toString();
		if (type instanceof ParameterizedType) {
			typeString = typeString.replaceAll("<", "＜");
			typeString = typeString.replaceAll(">", "＞");
			// ひとまず，一段のみ
			// ParameterizedType pType = ((ParameterizedType) type);
			// Type baseType = pType.getType();
			// typeString = baseType.toString();
		}
		return typeString;
	}

	private StLocalVariableModel createLocalVariableModel(String type,
			String name, Expression initializer, boolean argument) {
		// create local variable
		StLocalVariableModel model = new StLocalVariableModel(argument);
		model.setId(idCounter.getNextId());
		// String name = fragment.getName().toString();
		model.setName(name);
		model.setType(type);
		variableResolver.addLocalVariable(model);

		// int x = 3;のように，initializerがついている場合
		if (initializer != null) {
			model.setInitializer(parseExpression(initializer));
		}

		return model;
	}

	private void createThisModel() {
		// create local variable
		StThisVariableModel model = new StThisVariableModel();
		model.setType("object");
		model.setName("自分");

		model.setId(idCounter.getNextId());
		variableResolver.setThisValue(model);
	}

	/************************************************************
	 * 以下，Expressionの解析系
	 ************************************************************/
	/**
	 * 各Expressionへの経由関数
	 * 
	 * @param node
	 *            ：Expressionノード
	 * @return 各Expressionの解析結果
	 */
	public ExpressionModel parseExpression(Expression node) {
		try {
			if (node instanceof InfixExpression) {
				return (ExpressionModel) parseInfixExpression((InfixExpression) node);
			} else if (node instanceof NumberLiteral
					|| node instanceof BooleanLiteral
					|| node instanceof StringLiteral) {
				return (ExpressionModel) parseLeteralExpression(node);
			} else if (node instanceof SimpleName) {
				ExpressionModel model = (ExpressionModel) parseVariableGetterExpression(((SimpleName) node)
						.toString());
				model.setLineNumber(compilationUnit.getLineNumber(node
						.getStartPosition()));
				return model;
			} else if (node instanceof Assignment) {
				return parseAssignementExpression((Assignment) node);
			} else if (node instanceof MethodInvocation) {
				return parseMethodInvocationExpression((MethodInvocation) node);
			} else if (node instanceof PostfixExpression) {
				return (ExpressionModel) parsePostfixExpression((PostfixExpression) node);
			} else if (node instanceof PrefixExpression) {
				return (ExpressionModel) parsePrefixExpression((PrefixExpression) node);
			} else if (node instanceof QualifiedName) {
				return (ExpressionModel) parseQualifiedName((QualifiedName) node);
			} else if (node instanceof ParenthesizedExpression) {
				return (ExpressionModel) parseParenthesizedExpression((ParenthesizedExpression) node);
			} else if (node instanceof ClassInstanceCreation) {
				return (ExpressionModel) parseClassInstanceCreation((ClassInstanceCreation) node);
			} else if (node instanceof CastExpression) {
				return (ExpressionModel) parseCastExpression((CastExpression) node);
			} else if (node instanceof ArrayCreation) {
				return (ExpressionModel) parseArrayInstanceCreation((ArrayCreation) node);
			} else if (node instanceof FieldAccess) {
				return (ExpressionModel) parseFieldAccess(((FieldAccess) node));
			} else if (node instanceof ArrayAccess) {
				return (ExpressionModel) parseArrayAccess((ArrayAccess) node);
			}
			throw new RuntimeException(
					"The node type has not been supported yet node: "
							+ node.getClass() + ", " + node.toString());
		} catch (Exception ex) {
			// ex.printStackTrace();
			ExSpecialExpressionModel special = new ExSpecialExpressionModel(
					node.toString());
			special.setId(idCounter.getNextId());
			special.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			return special;
		}
	}

	/**
	 * 代入式の解析
	 * 
	 * @param node
	 *            :Assignmentノード
	 * @return Assignmentの解析結果
	 */
	private ExpressionModel parseAssignementExpression(Assignment node) {

		ExVariableSetterModel model = new ExVariableSetterModel();
		ExpressionModel rightExpression = parseExpression(node
				.getRightHandSide());
		// operatorチェック
		if ("+=".equals(node.getOperator().toString())) {
			rightExpression = setOperator("+", node, rightExpression);
		} else if ("-=".equals(node.getOperator().toString())) {
			rightExpression = setOperator("-", node, rightExpression);
		} else if ("*=".equals(node.getOperator().toString())) {
			rightExpression = setOperator("*", node, rightExpression);
		} else if ("/=".equals(node.getOperator().toString())) {
			rightExpression = setOperator("/", node, rightExpression);
		}

		model.setRightExpression(rightExpression);
		if (node.getRightHandSide() instanceof Assignment) {
			throw new RuntimeException("not supported two or more substitution");
		}

		// +=なら、左辺の値ブロックと、+ブロック
		// -=
		// *=
		// /=
		Expression leftExpression = node.getLeftHandSide();
		// 左辺のexpressionも解析する
		ExpressionModel setterModel = parseLeftExpression(leftExpression, model);
		return setterModel;

		// if (name.contains("this.")) {
		// ExCallActionMethodModel2 thisSetterModel = new
		// ExCallActionMethodModel2();
		// thisSetterModel.setId(idCounter.getNextId());
		// thisSetterModel.setLineNumber(compilationUnit.getLineNumber(node
		// .getStartPosition()));
		// // ExCallMethodModel callMethod = new ExCallMethodModel();
		// ExpressionModel thisModel = (ExpressionModel)
		// parseVariableGetterExpression("this");
		// thisModel.setType("object");
		//
		// name = name.substring(name.indexOf(".") + 1, name.length());
		// model.setVariable(variableResolver.resolve(name));
		// model.setId(idCounter.getNextId());
		// model.setLineNumber(compilationUnit.getLineNumber(node
		// .getStartPosition()));
		//
		// thisSetterModel.setReceiver(thisModel);
		// thisSetterModel.setCallMethod(model);
		// return thisSetterModel;
		// } else if (name.contains("[")) {
		// // 配列の処理
		// ExCallMethodModel arraySetter = new ExCallMethodModel();
		// // とりあえず無理やり変数を取る
		// Expression left = node.getLeftHandSide();
		// if (left instanceof ) {
		// System.out.println("hoge");
		// }
		//
		// String index = name.substring(name.indexOf("[", name.indexOf("]")));
		//
		// arraySetter.addArgument(rightExpression);
		// // 書き込みブロックを作成
		//
		// }

	}

	private ExpressionModel setOperator(String operator, Assignment node,
			ExpressionModel model) {
		ExInfixModel operatorModel = new ExInfixModel();
		operatorModel.setOperator(operator);
		operatorModel.setId(idCounter.getNextId());
		operatorModel.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		ExpressionModel left = parseExpression(node.getLeftHandSide());

		operatorModel.setType("number");
		operatorModel.setLeftExpression(left);
		operatorModel.setRightExpression(model);
		return operatorModel;
	}

	private ExpressionModel parseLeftExpression(Expression node,
			ExVariableSetterModel model) {
		try {
			if (node instanceof SimpleName) {
				model.setVariable(variableResolver.resolve(node.toString()));
				model.setId(idCounter.getNextId());
				model.setLineNumber(compilationUnit.getLineNumber(node
						.getStartPosition()));
				return model;
			} else if (node instanceof ArrayAccess) {
				ArrayAccess arrayNode = (ArrayAccess) node;
				// メソッド呼び出しモデル作成
				ExCallMethodModel arraySetter = new ExCallMethodModel();
				arraySetter.setId(idCounter.getNextId());
				// インデックスの解析
				ExpressionModel variable = parseExpression(arrayNode.getIndex());
				variable.setId(idCounter.getNextId());

				String scope = variableResolver.resolve(
						arrayNode.getArray().toString()).getGenusName();

				if (scope.startsWith("local")) {
					scope = "local";
				} else {
					scope = "private";
				}

				if (variableResolver.resolve(arrayNode.getArray().toString())
						.getType().contains("int")
						|| variableResolver
								.resolve(arrayNode.getArray().toString())
								.getType().contains("number")) {
					arraySetter.setName("setter-" + scope
							+ "-numberarrayelement");
				} else if (variableResolver
						.resolve(arrayNode.getArray().toString()).getType()
						.contains("String")) {
					arraySetter.setName("setter-" + scope
							+ "-stringarrayelement");
				} else if (variableResolver
						.resolve(arrayNode.getArray().toString()).getType()
						.contains("double")) {
					arraySetter.setName("setter-" + scope
							+ "doublearrayelement");
				} else {
					System.out.println("not supported array setter:"
							+ ((ArrayAccess) node).toString());
				}

				arraySetter
						.setLabel(((ArrayAccess) node).getArray().toString());
				arraySetter.addArgument(variable);
				arraySetter.addArgument(model.getRightExpression());
				return arraySetter;
			} else if (node instanceof FieldAccess) {
				ExCallActionMethodModel2 thisSetterModel = new ExCallActionMethodModel2();
				thisSetterModel.setId(idCounter.getNextId());
				thisSetterModel.setLineNumber(compilationUnit
						.getLineNumber(node.getStartPosition()));

				ExpressionModel thisModel = (ExpressionModel) parseVariableGetterExpression("this");
				thisModel.setType("object");

				String name = node.toString();
				name = name.substring(name.indexOf(".") + 1, name.length());
				model.setVariable(variableResolver.resolve(name));
				model.setId(idCounter.getNextId());
				model.setLineNumber(compilationUnit.getLineNumber(node
						.getStartPosition()));

				thisSetterModel.setReceiver(thisModel);
				thisSetterModel.setCallMethod(model);
				return thisSetterModel;
			}
			throw new RuntimeException(
					"The node type has not been supported yet node: "
							+ node.getClass() + ", " + node.toString());
		} catch (Exception ex) {
			// ex.printStackTrace();
			ExSpecialExpressionModel special = new ExSpecialExpressionModel(
					node.toString());
			special.setId(idCounter.getNextId());
			special.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			return special;
		}
	}

	/**
	 * インクリメント、デクリメントを解析
	 * 
	 * @param node
	 *            :PostfixExpressionノード
	 * @return PostfixExpressionの解析結果
	 */
	private ElementModel parsePostfixExpression(PostfixExpression node) {
		ExPostfixModel model = new ExPostfixModel();
		String name = node.getOperand().toString();
		model.setVariable(variableResolver.resolve(name));
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		ExLeteralModel leteral = new ExLeteralModel();
		leteral.setType("number");
		if (node.getOperator().toString().equals("++")) {
			leteral.setValue("1");
		} else if (node.getOperator().toString().equals("--")) {
			leteral.setValue("-1");
		}
		leteral.setId(idCounter.getNextId());
		leteral.setParent(model);
		leteral.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		model.setPostfix(leteral);

		return model;
	}

	/**
	 * マイナスの数値の解析
	 * 
	 * @param node
	 * @return
	 */
	private ExpressionModel parsePrefixExpression(PrefixExpression node) {
		if (node.getOperator().toString().equals("!")) {
			ExNotModel model = new ExNotModel();
			model.setId(idCounter.getNextId());
			model.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			model.setExpression(parseExpression(node.getOperand()));
			return model;
		}
		if (node.getOperator().toString().equals("-")) {// -expression など
			ExpressionModel model = parseExpression((Expression) node
					.getOperand());
			model.setId(idCounter.getNextId());
			model.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			model.setType(model.getType());

			if (model instanceof ExLeteralModel) {
				((ExLeteralModel) model).setValue("-1"
						+ ((ExLeteralModel) model).getValue());
			} else {
				ExInfixModel minusModel = new ExInfixModel();
				minusModel.setOperator("*");
				minusModel.setId(idCounter.getNextId());
				minusModel.setLineNumber(compilationUnit.getLineNumber(node
						.getStartPosition()));
				ExLeteralModel minus = new ExLeteralModel();
				minus.setValue("-1");
				minus.setType("number");
				minus.setId(idCounter.getNextId());
				minus.setLineNumber(compilationUnit.getLineNumber(node
						.getStartPosition()));
				minusModel.setType(model.getType());
				minusModel.setLeftExpression(minus);
				minusModel.setRightExpression(model);
				return minusModel;
			}

			return model;
		}

		throw new RuntimeException("not supported: " + node.toString());
	}

	public ExpressionModel parseMethodInvocationExpression(MethodInvocation node) {
		// ExpressionModel object = parseExpression(node.getExpression());
		// <Type{,Type}>
		// node.typeArguments()
		// identifer
		ExpressionModel identifer = parseMethodInvocationIdentifer(node);

		// if (object != null) {
		// // // excallブロック作成
		// ExCallActionMethodModel2 model = new ExCallActionMethodModel2();
		// model.setId(idCounter.getNextId());
		// model.setLineNumber(compilationUnit.getLineNumber(node
		// .getStartPosition()));
		// model.setReceiver(object);
		// model.setCallMethod(identifer);
		//
		// return model;
		// } else {
		return identifer;
		// }
	}

	public ExpressionModel parseMethodInvocationIdentifer(MethodInvocation node) {
		String fullName = node.toString();
		// String siName = node.getName().getIdentifier();
		if (fullName.startsWith("System.out.print(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setName("cui-print");
			return callMethod;
		} else if (fullName.startsWith("System.out.println(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setName("cui-println");
			return callMethod;
		} else if (fullName.startsWith("Math.random(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("cui-random");
			return callMethod;
		} else if (fullName.startsWith("Math.sqrt(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("sqrt");
			return callMethod;
		} else if (fullName.startsWith("Math.sin(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("sin");
			return callMethod;
		} else if (fullName.startsWith("Math.cos(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("cos");
			return callMethod;
		} else if (fullName.startsWith("Math.tan(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("tan");
			return callMethod;
		} else if (fullName.startsWith("Math.log(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("log");
			return callMethod;
		} else if (fullName.startsWith("Math.toRadians(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("toRadians");
			return callMethod;
		} else if (fullName.startsWith("Math.ceil(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("ceil");
			return callMethod;
		} else if (fullName.startsWith("Math.max(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("max");
			return callMethod;
		} else if (fullName.startsWith("Math.min(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("min");
			return callMethod;
		} else if (fullName.startsWith("Math.floor(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("floor");
			return callMethod;
		} else if (fullName.startsWith("Integer.parseInt(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("int");
			callMethod.setName("toIntFromString");
			return callMethod;
		} else if (fullName.startsWith("Integer.toString(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("string");
			callMethod.setName("toStringFromInt");
			return callMethod;
		} else if (fullName.startsWith("Double.parseDouble(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("toDoubleFromString");
			return callMethod;
		} else if (fullName.startsWith("Double.toString(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("string");
			callMethod.setName("toStringFromDouble");
			return callMethod;
		} else if (fullName.endsWith("hashCode()")) {
			Expression receiver = node.getExpression();
			if (receiver != null) {
				ExpressionModel expModel = parseExpression(receiver);
				ExCallMethodModel callMethod = parseMethodCallExpression(node);
				callMethod.addArgument(expModel);
				callMethod.setType("int");
				callMethod.setName("hashCode");
				return callMethod;
			}
		} else if (fullName.indexOf(".equals(") != -1
				&& node.arguments().size() == 1) {// ひとまず，string型だと思うことにする．オブジェクト型は後回し（根本的な解決が必要）．#matsuzawa
													// 2012.11.23
			Expression receiver = node.getExpression();
			Expression arg = (Expression) node.arguments().get(0);
			ExInfixModel model = new ExInfixModel();
			model.setId(idCounter.getNextId());
			model.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			model.setOperator("equals");
			model.setLeftExpression(parseExpression(receiver));
			model.setRightExpression(parseExpression(arg));
			return model;
		}

		if (methodResolver.isRegistered(node)) {
			// System.out.println("methodinvoke: " + node.toString());
			Expression receiver = node.getExpression();
			if (receiver == null) {
				return parseMethodCallExpression(node);
			}

			// ココも応急処置
			if (receiver instanceof MethodInvocation) {
				ExpressionModel receiverModel = parseMethodInvocationExpression((MethodInvocation) receiver);
				return parseCallActionMethodExpression2(node, receiverModel);
			}
			if (receiver instanceof ThisExpression) {
				// Ex2作る
				ExCallActionMethodModel2 model = new ExCallActionMethodModel2();
				model.setId(idCounter.getNextId());
				model.setLineNumber(compilationUnit.getLineNumber(node
						.getStartPosition()));
				// ExCallMethodModel callMethod = new ExCallMethodModel();
				// thisキーワードブロック作成
				ExpressionModel thisModel = (ExpressionModel) parseVariableGetterExpression("this");
				thisModel.setType("object");

				// model.setReceiver(thisModel);
				return parseCallActionMethodExpression2(node, thisModel);
				// methodResolver.getReturnType(node);
			} else {
				// Listのgetなどは、listの型に合わせて変形する必要性
				ExVariableGetterModel receiverModel = null;
				StVariableDeclarationModel variable = variableResolver
						.resolve(receiver.toString());

				if (variable != null) {
					receiverModel = parseVariableGetterExpression(variable
							.getName());
					receiverModel.setLineNumber(compilationUnit
							.getLineNumber(receiver.getStartPosition()));
					ExCallActionMethodModel2 model = parseCallActionMethodExpression2(
							node, receiverModel);

					return model;
				}

				return parseMethodCallExpression(node);
			}

			// if (methodResolver.getReturnType(node) == ExpressionModel.VOID) {
			// return parseCallActionMethodExpression(node);
			// } else {
			// return parseCallGetterMethodExpression(node);
			// }
		}

		// This method is not registered.
		String expression = node.getExpression().toString();
		if (expression != null) {
			expression += ".";
		}
		String typeArguments = "";
		if (node.typeArguments().size() > 0) {
			typeArguments = "&lt;" + node.typeArguments().toString() + "&gt;";
		}

		ExSpecialExpressionModel sp = new ExSpecialExpressionModel(expression
				+ typeArguments + node.getName());
		sp.setId(idCounter.getNextId());
		sp.setLineNumber(compilationUnit.getLineNumber(node.getStartPosition()));

		for (Object param : node.arguments()) {
			if (param instanceof ExSpecialExpressionModel) {

			}
			ExpressionModel paramModel = parseExpression((Expression) param);
			paramModel.setParent(sp);
			sp.addParameter(paramModel);
		}

		return sp;
	}

	private ExCallMethodModel transformBlock(ExCallMethodModel callerModel,
			ExpressionModel receiverModel) {
		// メソッド名確認　transformの必要性のあるメソッドは変形する
		if (receiverModel.getType().equals("List")
				&& callerModel.getName().equals("get")) {
			StVariableDeclarationModel parentVariableModel = variableResolver
					.resolve(receiverModel.getLabel());
			ExClassInstanceCreationModel classInstanceCreator = (ExClassInstanceCreationModel) parentVariableModel
					.getInitializer();
			callerModel.setType(ElementModel
					.convertJavaTypeToBlockType(classInstanceCreator
							.getAruguments().get(0).getType()));
		}
		return callerModel;
	}

	// private String getType(Expression exp) {
	// System.out.println("resolveTypeBinding(): " + exp.resolveTypeBinding());
	// // System.out.println(exp.resolveTypeBinding().getElementType());
	// return exp.resolveTypeBinding().getElementType().getName();
	// }

	/**
	 * 
	 * @param node
	 *            :MethodInvocationノード
	 * @return MethodInvocationの解析結果
	 */
	public ExCallMethodModel parseMethodCallExpression(MethodInvocation node) {
		ExCallMethodModel model;
		if (methodResolver.isRegisteredAsUserMethod(node)) {
			model = new ExCallUserMethodModel();
			model.setArgumentLabels(methodResolver.getArgumentLabels(node));
		} else {
			model = new ExCallMethodModel();
		}

		String name;

		name = node.getName().toString();

		model.setName(name);
		model.setType(ElementModel.convertJavaTypeToBlockType(methodResolver
				.getReturnType(node)));
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// 引数
		for (int i = 0; i < node.arguments().size(); i++) {
			ExpressionModel arg = parseExpression((Expression) node.arguments()
					.get(i));
			// if ("print".equals(model.getName()) &&
			// numberRelationChecker(arg)) {
			// arg = typeChangeModelCreater(arg);
			// }
			model.addArgument(arg);
		}
		return model;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public ExCallActionMethodModel parseCallActionMethodExpression(
			MethodInvocation node) {
		ExCallActionMethodModel model = new ExCallActionMethodModel();
		String receiver = node.getExpression().toString();
		StVariableDeclarationModel variable = variableResolver
				.resolve(receiver);
		if (variable == null) {
			variable = new StVariableDeclarationModel();// dummy
			variable.setName(receiver.toString());
			variable.setType("void");
		}

		model.setVariable(variable);
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// #matsuzawa 2012.11.13 ここ根本的な治療が必要！
		StBlockModel block = new StBlockModel();
		block.setId(model.getId());
		block.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));

		// if (node.getExpression() instanceof QualifiedName) {
		// throw new RuntimeException("not supported two or more substitution");
		// }
		block.addElement(parseMethodCallExpression(node));
		model.setCallMethod(block);

		// // 以下，松澤が改変
		// if (node.getExpression() instanceof QualifiedName) {
		// throw new RuntimeException("not supported two or more substitution");
		// }
		//
		// CallMethodModel method = parseMethodCallExpression(node);
		// method.setId(model.getId());
		// model.setCallMethod(method);
		// // 以上，松澤が改変
		return model;
	}

	public ExCallActionMethodModel2 parseCallActionMethodExpression2(
			MethodInvocation node, ExpressionModel receiverModel) {
		ExCallActionMethodModel2 model = new ExCallActionMethodModel2();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		ExCallMethodModel callMethod = parseMethodCallExpression(node);
		callMethod = transformBlock(callMethod, receiverModel);

		model.setReceiver(receiverModel);
		model.setCallMethod(callMethod);
		return model;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public ExCallGetterMethodModel parseCallGetterMethodExpression(
			MethodInvocation node) {
		ExCallGetterMethodModel model = new ExCallGetterMethodModel();
		String name = node.getExpression().toString();
		model.setVariable(variableResolver.resolve(name));
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		if (node.getExpression() instanceof QualifiedName) {
			throw new RuntimeException("not supported two or more substitution");
		}
		model.setArgument(parseMethodCallExpression(node));
		return model;
	}

	// /**
	// *
	// * @param MethodInvocationノード
	// * @return MethodInvocationノードの解析結果
	// */
	// public ExAssignmentCallMethodModel parseAssignmentCallExpression(
	// MethodInvocation node) {
	// ExAssignmentCallMethodModel model = new ExAssignmentCallMethodModel();
	// model.setName(node.getName().toString());
	// model.setId(idCounter.getNextId());
	// // 引数
	// for (int i = 0; i < node.arguments().size(); i++) {
	// ExpressionModel arg = parseExpression((Expression) node.arguments()
	// .get(i));
	// model.addArgument(arg);
	// }
	// return model;
	// }

	/**
	 * 定数の解析
	 * 
	 * @param node
	 *            :Expressionノード
	 * @return Expressionの解析結果
	 */
	public ExLeteralModel parseLeteralExpression(Expression node) {
		ExLeteralModel model = new ExLeteralModel();
		model.setType(getLeteralType(node));
		model.setValue(node.toString());
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		return model;
	}

	/**
	 * 定数の型の解析
	 * 
	 * @param leteral
	 *            :Experssionノード
	 * @return 解析した定数の型
	 */
	private String getLeteralType(Expression leteral) {
		if (leteral instanceof NumberLiteral) {
			if (leteral.toString().contains(".")
					|| leteral.toString().endsWith("d")) {
				return "double-number";
			} else {
				return "number";
			}
		} else if (leteral instanceof BooleanLiteral) {
			return "boolean";
		} else if (leteral instanceof StringLiteral) {
			return "string";
		}

		throw new RuntimeException("not supported");
	}

	/**
	 * 計算式の解析
	 * 
	 * @param node
	 *            :InfixExpressionノード
	 * @return InfixExpressionの解析結果
	 */
	public ExInfixModel parseInfixExpression(InfixExpression node) {
		String oparator = node.getOperator().toString();
		// String type = infixTypeChecker(node);

		ExInfixModel model = new ExInfixModel();
		model.setOperator(oparator);
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// model.setLeftExpression(numberToStringConverter(type,parseExpression(node.getLeftOperand())));
		// model.setRightExpression(numberToStringConverter(type,parseExpression(node.getRightOperand())));
		model.setLeftExpression(parseExpression(node.getLeftOperand()));
		model.setRightExpression(parseExpression(node.getRightOperand()));

		// 多項式　追加分
		for (Object o : node.extendedOperands()) {
			Expression exp = (Expression) o;
			ExInfixModel newmodel = new ExInfixModel();
			newmodel.setId(idCounter.getNextId());
			newmodel.setLineNumber(compilationUnit.getLineNumber(exp
					.getStartPosition()));
			newmodel.setOperator(oparator);
			newmodel.setLeftExpression(model);
			// newmodel.setRightExpression(numberToStringConverter(type,
			// parseExpression(exp)));
			newmodel.setRightExpression(parseExpression(exp));
			model = newmodel;
		}

		// string の == 比較チェック
		if (model.getSocketType().equals("string")
				&& (model.getOperator().equals("==") || model.getOperator()
						.equals("!="))) {
			throw new RuntimeException("string type is compared by == or !=");
		}

		// ExInfixModel model = new ExInfixModel();
		// model.setOperator(node.getOperator().toString());
		// model.setId(idCounter.getNextId());
		// model.setLeftExpression(numberToStringConverter(infixTypeChecker(node),
		// parseExpression(node.getLeftOperand())));
		// model.setRightExpression(numberToStringConverter(
		// infixTypeChecker(node), parseExpression(node.getRightOperand())));
		return model;
	}

	// private String infixTypeChecker(InfixExpression node) {
	// if (checkOperandStringType(node.getLeftOperand())
	// || checkOperandStringType(node.getRightOperand())) {
	// return "string";
	// }
	// return "int";
	// }

	// private boolean checkOperandStringType(Expression operand) {
	// if (operand instanceof StringLiteral) {
	// return true;
	// }
	// if (operand instanceof InfixExpression) {
	// return checkOperandStringType(((InfixExpression) operand)
	// .getLeftOperand())
	// || checkOperandStringType(((InfixExpression) operand)
	// .getRightOperand());
	// }
	// return false;
	// }

	// 以下，ここでやる仕事じゃないので削除 #matsuzawa 2012.11.14
	// private ExpressionModel numberToStringConverter(String type,
	// ExpressionModel initializer) {
	// if (("String".equals(type) || "string".equals(type))
	// && numberRelationChecker(initializer)) {
	// return typeChangeModelCreater(initializer);
	// }
	//
	// return initializer;
	// }

	// private boolean numberRelationChecker(ExpressionModel initializer) {
	// if (initializer instanceof ExInfixModel) {
	// String type = ((ExInfixModel) initializer).getType();
	// if ("number".equals(type)) {
	// return true;
	// }
	// } /*
	// * else if (initializer instanceof ExAssignmentCallMethodModel) { String
	// * name = ((ExAssignmentCallMethodModel) initializer).getName(); if
	// * ("random".equals(name)) { return true; } }
	// */
	// else if (initializer instanceof ExCallMethodModel) {
	// String name = ((ExCallMethodModel) initializer).getName();
	// if ("random".equals(name)) {
	// return true;
	// }
	// } else if (initializer instanceof ExLeteralModel) {
	// String type = ((ExLeteralModel) initializer).getType();
	// if ("number".equals(type)) {
	// return true;
	// }
	// } else if (initializer instanceof ExVariableGetterModel) {
	// String type = ((ExVariableGetterModel) initializer).getType();
	// if ("int".equals(type) || "double".equals(type)) {
	// type = "number";
	// }
	// if ("number".equals(type)) {
	// return true;
	// }
	// }
	// return false;
	// }

	// private ExpressionModel typeChangeModelCreater(ExpressionModel argument)
	// {
	// ExCastModel model = new ExCastModel();
	// model.setType("toString");
	// model.setId(idCounter.getNextId());
	// // 引数
	// model.addArgument(argument);
	// return model;
	// }

	/**
	 * 丸括弧の解析
	 * 
	 * @param node
	 * @return
	 */
	private ExpressionModel parseParenthesizedExpression(
			ParenthesizedExpression node) {
		return parseExpression(node.getExpression());
	}

	/**
	 * 変数呼び出しの解析
	 * 
	 * @param node
	 *            :SimpleNameノード
	 * @return SimpleNameノードの解析
	 */
	public ExVariableGetterModel parseVariableGetterExpression(String name) {
		ExVariableGetterModel model = new ExVariableGetterModel();
		model.setVariable(variableResolver.resolve(name));
		model.setId(idCounter.getNextId());
		return model;
	}

	public ExVariableGetterModel parseThisVariableGetterExpression(String name) {
		ExVariableGetterModel model = new ExVariableGetterModel();
		model.setVariable(variableResolver.resolveThisGetter(name));
		model.setId(idCounter.getNextId());
		return model;
	}

	/**
	 * @param node
	 * @return
	 */
	private ExpressionModel parseQualifiedName(QualifiedName node) {
		ExLeteralModel model = new ExLeteralModel();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// #matsuzawa 2012.11.24
		if (isColorBlock(node)) {
			model.setType("color");
			model.setValue(node.getName().toString());
			return model;
		}
		// #matsuzawa 2012.11.12
		// model.setType("number");
		// model.setValue(node.getName().toString());
		model.setType("poly");
		model.setValue(node.getFullyQualifiedName().toString());

		return model;
	}

	private boolean isColorBlock(QualifiedName node) {
		String fullName = node.getFullyQualifiedName();
		return fullName.startsWith("java.awt.Color.");
		// for (String colorName : BlockConverter.COLOR_NAMES) {
		// if (value.endsWith(colorName)) {
		// return true;
		// }
		// }
		// return false;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private ExpressionModel parseClassInstanceCreation(
			ClassInstanceCreation node) {

		if (node.getType().isParameterizedType()) {
			ExClassInstanceCreationModel model = new ExClassInstanceCreationModel();
			ParameterizedType type = (ParameterizedType) node.getType();
			model.setValue(type.getType().toString());

			model.setId(idCounter.getNextId());
			model.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			for (Object argument : type.typeArguments()) {
				// 型ブロックを作成する
				ExTypeModel typeModel = new ExTypeModel();
				typeModel.setId(idCounter.getNextId());
				typeModel.setLineNumber(compilationUnit.getLineNumber(node
						.getStartPosition()));
				typeModel.setType(argument.toString());
				typeModel.setParent(model);
				model.addArgument(typeModel);
			}

			return model;
		}

		ExClassInstanceCreationModel model = new ExClassInstanceCreationModel();
		model.setValue(typeString(node.getType()));
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// 引数
		for (int i = 0; i < node.arguments().size(); i++) {
			ExpressionModel arg = parseExpression((Expression) node.arguments()
					.get(i));
			model.addArgument(arg);
		}
		return model;
	}

	// ohata
	private ExpressionModel parseArrayInstanceCreation(ArrayCreation node) {
		ExArrayInstanceCreationModel model = new ExArrayInstanceCreationModel();
		model.setValue(typeString(node.getType()));
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// 引数
		for (int i = 0; i < node.dimensions().size(); i++) {
			ExpressionModel arg = parseExpression((Expression) node
					.dimensions().get(i));
			model.addArgument(arg);
		}
		return model;

	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private ExpressionModel parseCastExpression(CastExpression node) {
		ExCastModel model = new ExCastModel();
		model.setType(node.getType().toString());
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// 引数
		model.addArgument(parseExpression(node.getExpression()));
		return model;
	}

	private ExpressionModel parseArrayAccess(ArrayAccess node) {
		// callmethodモデルを作成
		ExCallMethodModel arrayGetter = new ExCallMethodModel();
		arrayGetter.setId(idCounter.getNextId());
		arrayGetter.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// 配列の要素番号を変換するためのleteralモデルを作成
		ExpressionModel index = parseExpression(node.getIndex());
		index.setId(idCounter.getNextId());
		arrayGetter.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));

		// arraygetterは、変数の型によってname,typeを変更する
		String scope = variableResolver.resolve(node.getArray().toString())
				.getGenusName();
		if (scope.startsWith("local")) {
			scope = "local";
		} else {
			scope = "private";
		}

		if (variableResolver.resolve(node.getArray().toString()).getType()
				.contains("int")) {
			arrayGetter.setName("getter" + scope + "numberarrayelement");
			arrayGetter.setType("number");
		} else if (variableResolver.resolve(node.getArray().toString())
				.getType().contains("String")) {
			arrayGetter.setName("getter" + scope + "stringarrayelement");
			arrayGetter.setType("string");
		} else if (variableResolver.resolve(node.getArray().toString())
				.getType().contains("double")) {
			arrayGetter.setName("getter" + scope + "doublearrayelement");
			arrayGetter.setType("double-number");
		} else {
			System.out.println("not supported arraygetter block:"
					+ node.getArray().toString());
		}
		arrayGetter.setLabel(node.getArray().toString());
		arrayGetter.addArgument(index);

		return arrayGetter;
	}

	private ExpressionModel parseFieldAccess(FieldAccess node) {
		// 右辺のthisキーワード解析
		// ExcallActionブロックを作成
		ExCallActionMethodModel2 model = new ExCallActionMethodModel2();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// ExCallMethodModel callMethod = new ExCallMethodModel();
		// thisキーワードブロック作成
		ExpressionModel thisModel = (ExpressionModel) parseVariableGetterExpression("this");
		thisModel.setType("object");

		// 変数名を解析したモデルとthisをExCallActionブロックにセット
		model.setReceiver(thisModel);
		if (variableResolver.resolve(node.getName().toString()) == null) {
		} else {
			model.setCallMethod(parseThisVariableGetterExpression(node
					.getName().toString()));
			model.setType(parseVariableGetterExpression(
					node.getName().toString()).getType());
		}

		// model.setCallMethod();

		// ExCallGetterMethodModel model = new ExCallGetterMethodModel();
		// String name = node.getExpression().toString();
		// model.setVariable(variableResolver.resolve(name));
		// model.setId(idCounter.getNextId());
		// model.setLineNumber(compilationUnit.getLineNumber(node
		// .getStartPosition()));
		// if (node.getExpression() instanceof QualifiedName) {
		// throw new RuntimeException("not supported two or more substitution");
		// }
		//
		// model.setArgument((ExpressionModel)
		// parseVariableGetterExpression(node
		// .toString().substring(node.toString().indexOf("this."),
		// node.toString().length())));
		return model;
	}
}
