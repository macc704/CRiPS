/*
 * SContextMenuProvider.java
 * Created on 2011/11/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package a.slab.blockeditor.extent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import renderable.BlockUtilities;
import renderable.RenderableBlock;
import workspace.Workspace;
import workspace.WorkspaceEvent;
import workspace.WorkspaceWidget;
import codeblocks.Block;
import codeblocks.BlockLink;

/**
 * @author macchan
 */
public class SContextMenuProvider {

	private RenderableBlock rb;

	private JMenuItem blockCopyItem;
	private JMenuItem createValueItem;
	private JMenuItem createWriterItem;
	private JMenuItem createIncrementerItem;
	private JMenuItem createCallActionMethodBlockItem;
	private JMenuItem createCallGetterMethodBlockItem;
	private JMenuItem createCallDoubleMethodBlockItem;
	private JMenuItem createCallBooleanMethodBlockItem;
	private JMenuItem createCallStringMethodBlockItem;
	private JMenuItem createCallerItem;

	public SContextMenuProvider(RenderableBlock rb) {
		this.rb = rb;
	}

	private JMenuItem createBlockCopyMenu() {
		if (blockCopyItem == null) {
			blockCopyItem = new JMenuItem("複製");
			blockCopyItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SBlockCopier(rb).doWork(e);
				}
			});
		}
		return blockCopyItem;
	}

	private JMenuItem createCreateValueMenu() {
		if (createValueItem == null) {
			createValueItem = new JMenuItem("「値ブロック」の作成");
			createValueItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("getter", rb).doWork(e);
				}
			});
		}
		return createValueItem;
	}

	// #ohata added
	private JMenuItem createNewGetterMenu() {
		JMenuItem item = new JMenuItem("ゲッターメソッドの作成");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewGetterMethod("procedure");
			}
		});
		return item;
	}

	// #ohata added
	private JMenuItem createNewSetterMenu() {
		JMenuItem item = new JMenuItem("セッターメソッドの作成");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewSetterMethod("procedure");
			}
		});
		return item;
	}

	private JMenuItem createCreateWriterMenu() {
		if (createWriterItem == null) {
			createWriterItem = new JMenuItem("「書込ブロック」の作成");
			createWriterItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("setter", rb).doWork(e);
				}
			});
		}
		return createWriterItem;
	}

	private JMenuItem createCreateIncrementerMenu() {
		if (createIncrementerItem == null) {
			createIncrementerItem = new JMenuItem("「増やすブロック」の作成");
			createIncrementerItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("inc", rb).doWork(e);
				}
			});
		}
		return createIncrementerItem;
	}

	private JMenuItem createCallActionMethodBlockMenu() {
		if (createCallActionMethodBlockItem == null) {
			createCallActionMethodBlockItem = new JMenuItem("「メソッド実行ブロック」の作成");
			createCallActionMethodBlockItem
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							new SStubCreator("callActionMethod", rb).doWork(e);
						}
					});
		}
		return createCallActionMethodBlockItem;
	}

	private JMenuItem createCallGetterMethodBlockMenu() {
		if (createCallGetterMethodBlockItem == null) {
			createCallGetterMethodBlockItem = new JMenuItem(
					"「メソッド実行ブロック(整数型)」の作成");
			createCallGetterMethodBlockItem
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							new SStubCreator("callGetterMethod", rb).doWork(e);
						}
					});
		}
		return createCallGetterMethodBlockItem;
	}

	private JMenuItem createCallDoubleMethodBlockMenu() {
		if (createCallDoubleMethodBlockItem == null) {
			createCallDoubleMethodBlockItem = new JMenuItem(
					"「メソッド実行ブロック(double型)」の作成");
			createCallDoubleMethodBlockItem
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							new SStubCreator("callDoubleMethod", rb).doWork(e);
						}
					});
		}
		return createCallDoubleMethodBlockItem;
	}

	private JMenuItem createCallBooleanMethodBlockMenu() {
		if (createCallBooleanMethodBlockItem == null) {
			createCallBooleanMethodBlockItem = new JMenuItem(
					"「メソッド実行ブロック(真偽型)」の作成");
			createCallBooleanMethodBlockItem
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							new SStubCreator("callBooleanMethod", rb).doWork(e);
						}
					});
		}
		return createCallBooleanMethodBlockItem;
	}

	private JMenuItem createCallStringMethodBlockMenu() {
		if (createCallStringMethodBlockItem == null) {
			createCallStringMethodBlockItem = new JMenuItem(
					"「メソッド実行ブロック(文字列)」の作成");
			createCallStringMethodBlockItem
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							new SStubCreator("callStringMethod", rb).doWork(e);
						}
					});
		}
		return createCallStringMethodBlockItem;
	}

	private JMenuItem createCallerMenu() {
		if (createCallerItem == null) {
			createCallerItem = new JMenuItem("「メソッド実行ブロック」の作成");
			createCallerItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("caller", rb).doWork(e);
				}
			});
		}
		return createCallerItem;
	}

	public JMenu createClassMethodsCategory() {
		List<Map<String, List<String>>> methods = rb.getMethods();
		JMenu category = new JMenu("パブリックメソッド");
		for (Map<String, List<String>> method : methods) {
			category.add(createCallClassMethodMenu(method));
		}
		return category;
	}

	/**
	 * @return
	 */
	public JPopupMenu getPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		// #ohata added
		if (rb.getBlock().isPrivateVariableBlock()) {
			menu.add(createCreateValueMenu());
			menu.add(createNewGetterMenu());
			menu.add(createCreateWriterMenu());
			menu.add(createNewSetterMenu());
			//menu.add(createCreateGetterMenu());
			menu.addSeparator();
		}

		if (rb.getBlock().isVariableDeclBlock()
		/*&& !rb.getBlock().isObjectTypeVariableDeclBlock()*/) {
			menu.add(createCreateValueMenu());
			menu.add(createCreateWriterMenu());
			//menu.add(createCreateGetterMenu());
			menu.addSeparator();
		}

		if (rb.getBlock().isProcedureParamBlock()) {
			menu.add(createCreateValueMenu());
			menu.add(createCreateWriterMenu());
			menu.addSeparator();
		}

		if (rb.getBlock().isNumberVariableDecBlock()) {
			menu.add(createCreateIncrementerMenu());
			menu.addSeparator();
		}

		if (rb.getBlock().isObjectTypeVariableDeclBlock()) {
			menu.add(createActionBlockMenu());
			menu.add(createGetterBlockMenu());
			//TODO menuにメソッドを追加
			menu.add(createClassMethodsCategory());
			if (rb.getBlock().getHeaderLabel().contains("Scanner")) {
				{
					JMenu category = new JMenu("Scanner");
					category.add(createCallMethodMenu("next", "入力を受け取る(文字列型)"));
					category.add(createCallMethodMenu("nextInt", "入力を受け取る(整数型)"));
					category.add(createCallMethodMenu("nextDouble",
							"入力を受け取る(実数型)"));
					menu.add(category);
				}
			}
			if (rb.getBlock().getHeaderLabel().contains("Turtle")) {
				{
					JMenu category = new JMenu("Turtle基本");
					category.add(createCallMethodMenu("fd", "進む"));
					category.add(createCallMethodMenu("bk", "戻る"));
					category.add(createCallMethodMenu("rt", "回る（右）"));
					category.add(createCallMethodMenu("lt", "回る（左）"));
					category.add(createCallMethodMenu("up", "ペンを上げる"));
					category.add(createCallMethodMenu("down", "ペンを下ろす"));
					category.add(createCallMethodMenu("color", "ペン色を変える"));
					menu.add(category);
				}
				{
					JMenu category = new JMenu("Turtle応用");
					category.add(createCallMethodMenu("warp", "ワープする"));
					category.add(createCallMethodMenu("size", "大きさを変える"));
					category.add(createCallMethodMenu("getX", "X座標"));
					category.add(createCallMethodMenu("getY", "Y座標"));
					category.add(createCallMethodMenu("getWidth", "幅"));
					category.add(createCallMethodMenu("getHeight", "高さ"));
					category.add(createCallMethodMenu("intersects",
							"オブジェクトが重なっているかどうか調べる"));
					category.add(createCallMethodMenu("contains",
							"オブジェクトが指定した座標を含む位置にいるか調べる"));
					category.add(createCallMethodMenu("looks", "見た目を変える"));
					category.add(createCallMethodMenu("show", "表示する"));
					category.add(createCallMethodMenu("hide", "非表示にする"));
					category.add(createCallMethodMenu("setShow", "表示状態を設定する"));
					category.add(createCallMethodMenu("isShow",
							"表示されているかどうか調べる"));
					menu.add(category);
				}
			}
			if (rb.getBlock().getHeaderLabel().contains("ImageTurtle")) {
				JMenu category = new JMenu("ImageTurtle");
				category.add(createCallMethodMenu("image", "画像ファイルを設定する"));
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("TextTurtle")) {
				JMenu category = new JMenu("TextTurtle");
				category.add(createCallMethodMenu("text", "テキストを設定する"));
				category.add(createCallMethodMenu("getText", "テキストを取得する"));
				category.add(category);
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("SoundTurtle")) {
				JMenu category = new JMenu("TextTurtle");
				category.add(createCallMethodMenu("file", "ファイルを設定する"));
				category.add(createCallMethodMenu("play", "再生する"));
				category.add(createCallMethodMenu("loop", "ループ再生する"));
				category.add(createCallMethodMenu("stop", "停止する"));
				category.add(createCallMethodMenu("isPlaying", "再生しているかどうか"));
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("ListTurtle")) {
				JMenu category = new JMenu("ListTurtle");
				category.add(createCallMethodMenu("get", "x番値の要素取得"));
				category.add(createCallMethodMenu("getSize", "要素数"));
				category.add(createCallMethodMenu("add", "追加する"));
				category.add(createCallMethodMenu("addFirst", "最初に追加する"));
				category.add(createCallMethodMenu("addLast", "最後に追加する"));
				category.add(createCallMethodMenu("addAll", "全て追加する"));
				category.add(createCallMethodMenu("moveAllTo", "全て移動する"));
				category.add(createCallMethodMenu("removeFirst", "先頭要素を削除する"));
				category.add(createCallMethodMenu("removeLast", "末尾要素を削除する"));
				category.add(createCallMethodMenu("removeAll", "全ての要素を削除する"));
				category.add(createCallMethodMenu("getCursor", "カーソル位置"));
				category.add(createCallMethodMenu("setCursor", "カーソル位置を設定する"));
				category.add(createCallMethodMenu("moveCursorToNext",
						"カーソルを進める"));
				category.add(createCallMethodMenu("moveCursorToPrevious",
						"カーソルを戻す"));
				category.add(createCallMethodMenu("getObjectAtCursor",
						"カーソル位置の要素取得"));
				category.add(createCallMethodMenu("addToBeforeCursor",
						"カーソルの前に追加する"));
				category.add(createCallMethodMenu("addToAfterCursor",
						"カーソルの後に追加する"));
				category.add(createCallMethodMenu("removeAtCursor",
						"カーソル位置の要素を削除する"));
				category.add(createCallMethodMenu("shuffle", "かき混ぜる"));
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("CardTurtle")) {
				JMenu category = new JMenu("CardTurtle");
				category.add(createCallMethodMenu("getNumber", "番号取得"));
				category.add(createCallMethodMenu("getText", "文字列取得"));
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("ButtonTurtle")) {
				JMenu category = new JMenu("ButtonTurtle");
				category.add(createCallMethodMenu("isClicked", "クリックされたかどうか"));
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("InputTurtle")) {
				JMenu category = new JMenu("InputTurtle");
				category.add(createCallMethodMenu("getText", "文字列を取得する"));
				category.add(createCallMethodMenu("text", "文字列を設定する"));
				category.add(createCallMethodMenu("clearText", "文字列を空にする"));
				category.add(createCallMethodMenu("setActive", "活動状態を設定する"));
				category.add(createCallMethodMenu("isActive", "活動状態を取得する"));
				category.add(createCallMethodMenu("toJapaneseMode", "日本語モードにする"));
				category.add(createCallMethodMenu("toEnglishMode", "英語モードにする"));
				category.add(createCallMethodMenu("fontsize", "フォントサイズを設定する"));
				menu.add(category);
			}
			menu.addSeparator();
		}

		if (rb.getBlock().isProcedureDeclBlock()) {
			menu.add(createCallerMenu());
			menu.addSeparator();
		}

		if (!rb.getBlock().isProcedureDeclBlock()) {
			menu.add(createBlockCopyMenu());
			menu.addSeparator();
		}

		//古いオブジェクト実行ブロックの互換性のために残してあります．
		if (rb.getBlock().isObjectTypeVariableDeclBlock()) {
			menu.add(createCallActionMethodBlockMenu());
			menu.add(createCallGetterMethodBlockMenu());
			menu.add(createCallDoubleMethodBlockMenu());
			menu.add(createCallBooleanMethodBlockMenu());
			menu.add(createCallStringMethodBlockMenu());
			menu.addSeparator();
		}
		return menu;
	}

	private JMenuItem createActionBlockMenu() {
		JMenuItem item = new JMenuItem("「実行」ブロック作成");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createActionGetterBlock(rb, "callActionMethod2");
			}
		});
		return item;
	}

	private JMenuItem createGetterBlockMenu() {
		JMenuItem item = new JMenuItem("「実行値」ブロック作成");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createActionGetterBlock(rb, "callGetterMethod2");
			}
		});
		return item;
	}

	private JMenuItem createCallClassMethodMenu(
			final Map<String, List<String>> method) {

		String param = "(";
		for (int i = 0; i < method.get("parameters").size(); i++) {
			param += method.get("parameters").get(i);
			if (i + 1 != method.get("parameters").size()) {
				param += ", ";
			}
		}

		param += ")";
		JMenuItem item = new JMenuItem(method.get("name").get(0) + param);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createActionGetterBlock(rb, "callActionMethod2");
			}
		});
		return item;
	}

	/**
	 */
	private JMenuItem createCallMethodMenu(final String name, String label) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createCallMethod(name);
			}
		});
		return item;
	}

	private void createCallMethod(String name) {
		//RenderableBlock createRb = BlockUtilities.getBlock("get","hoge");//does not work !!

		RenderableBlock newCommandRBlock = createNewBlock(rb.getParentWidget(),
				name);

		boolean cmd = newCommandRBlock.getBlock().getPlug() == null;
		if (cmd) {
			RenderableBlock newActionRBlock = createActionGetterBlock(rb,
					"callActionMethod2");
			connectByBefore(newActionRBlock, 1, newCommandRBlock);
		} else {
			RenderableBlock newGetterRBlock = createActionGetterBlock(rb,
					"callGetterMethod2");
			connectByPlug(newGetterRBlock, 1, newCommandRBlock);

			boolean returnObject = newCommandRBlock.getBlock().getPlug()
					.getKind().equals("object");
			if (returnObject) {
				RenderableBlock newActionRBlock = createNewBlock(
						rb.getParentWidget(), "callActionMethod2");
				newActionRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // 新しく生成するブロックのポジション
				connectByPlug(newActionRBlock, 0, newGetterRBlock);
			}
		}
	}

	//#ohata
	private void createNewGetterMethod(String name) {

		RenderableBlock newCommandRBlock = createNewBlock(rb.getParentWidget(),
				name);
		RenderableBlock returnBlock = createNewBlock(rb.getParentWidget(),
				"return");
		RenderableBlock getter = SStubCreator.createStub("getter", rb);

		newCommandRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // 新しく生成するブロックのポジション

		returnBlock.setLocation(rb.getX() + 20,
				rb.getY() + newCommandRBlock.getHeight() + 20); //無理やり座標指定

		getter.setLocation(rb.getX() + returnBlock.getBlockWidth() + 10,
				rb.getY() + newCommandRBlock.getHeight() + 20);

		connectByPlug(returnBlock, 0, getter);

		//ラベル張替え
		Block methodBlock = newCommandRBlock.getBlock();
		methodBlock.setBlockLabel("get"
				+ rb.getKeyword().toUpperCase().charAt(0)
				+ rb.getKeyword().substring(1));

		BlockLink link = newCommandRBlock.getNearbyLink();
		if (link != null) {
			link.connect();
		}

		//returnBlock.getNearbyLink().connect();
	}

	private void createNewSetterMethod(String name) {//#ohata
		RenderableBlock newCommandRBlock = createNewBlock(rb.getParentWidget(),
				name);
		newCommandRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // 新しく生成するブロックのポジション
		//ラベル張替え
		Block methodBlock = newCommandRBlock.getBlock();
		methodBlock.setBlockLabel("set"
				+ rb.getKeyword().toUpperCase().charAt(0)
				+ rb.getKeyword().substring(1));

		RenderableBlock setter = SStubCreator.createStub("setter", rb);
		setter.setLocation(rb.getX() + 20, rb.getY() + 40);

		BlockLink link = newCommandRBlock.getNearbyLink();

		link.connect();

		if (rb.getGenus().endsWith("string")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-string");
			connectByPlug(newCommandRBlock, 0, param);
		} else if (rb.getGenus().endsWith("boolean")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-boolean");
			connectByPlug(newCommandRBlock, 0, param);
		} else if (rb.getGenus().endsWith("double-number")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-double-number");
			connectByPlug(newCommandRBlock, 0, param);
		} else if (rb.getGenus().endsWith("number")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-number");
			connectByPlug(newCommandRBlock, 0, param);

		} else if (rb.getGenus().endsWith("TextTurtle")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-TextTurtle");
			connectByPlug(newCommandRBlock, 0, param);

		} else if (rb.getGenus().endsWith("Turtle")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-Tertle");
			connectByPlug(newCommandRBlock, 0, param);
		}

		//		newCommandRBlock.getParentWidget().blockDropped(newCommandRBlock);
		//	newCommandRBlock.getParentWidget().blockDropped(setter);
	}

	private static RenderableBlock createActionGetterBlock(
			RenderableBlock parent, String genusName) {

		RenderableBlock newCallRBlock = createNewBlock(
				parent.getParentWidget(), genusName);

		newCallRBlock.setLocation(parent.getX() + 20, parent.getY() + 20); // 新しく生成するブロックのポジション

		String genusNameLabel = "getter";
		if (parent.getGenus().contains("private")) {
			genusNameLabel += "private";
		}

		RenderableBlock newValueBlock = SStubCreator.createStub(genusNameLabel,
				parent);

		connectByPlug(newCallRBlock, 0, newValueBlock);

		return newCallRBlock;
	}

	public static RenderableBlock createNewBlock(WorkspaceWidget widget,
			String genusName) {
		for (RenderableBlock block : Workspace.getInstance()
				.getFactoryManager().getBlocks()) {
			if (block.getBlock().getGenusName().equals(genusName)) {
				RenderableBlock newBlock = BlockUtilities.cloneBlock(block
						.getBlock());
				newBlock.setParentWidget(widget);
				widget.addBlock(newBlock);
				return newBlock;
			}
		}
		throw new RuntimeException("block not found: " + genusName);
	}

	public static void connectByPlug(RenderableBlock parent, int socketIndex,
			RenderableBlock child) {
		BlockLink link = BlockLink.getBlockLink(parent.getBlock(),
				child.getBlock(), parent.getBlock().getSocketAt(socketIndex),
				child.getBlock().getPlug());
		link.connect();
		//これをやらないと形が変わらない
		Workspace.getInstance().notifyListeners(
				new WorkspaceEvent(parent.getParentWidget(), link,
						WorkspaceEvent.BLOCKS_CONNECTED));
	}

	public static void connectByBefore(RenderableBlock parent, int socketIndex,
			RenderableBlock child) {
		BlockLink link = BlockLink.getBlockLink(parent.getBlock(),
				child.getBlock(), parent.getBlock().getSocketAt(socketIndex),
				child.getBlock().getBeforeConnector());
		link.connect();
		//これをやらないと形が変わらない
		Workspace.getInstance().notifyListeners(
				new WorkspaceEvent(parent.getParentWidget(), link,
						WorkspaceEvent.BLOCKS_CONNECTED));
	}

	public static void connectBySocket(RenderableBlock parent, int socketIndex,
			RenderableBlock child) {
		BlockLink link = BlockLink.getBlockLink(parent.getBlock(),
				child.getBlock(), parent.getBlock().getSocketAt(socketIndex),
				child.getBlock().getSocketAt(socketIndex));
		link.connect();
		//これをやらないと形が変わらない
		Workspace.getInstance().notifyListeners(
				new WorkspaceEvent(parent.getParentWidget(), link,
						WorkspaceEvent.BLOCKS_CONNECTED));
	}
}
