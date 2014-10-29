/*
 * SContextMenuProvider.java
 * Created on 2011/11/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package edu.inf.shizuoka.blocks.extent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockLink;
import edu.mit.blocks.renderable.BlockUtilities;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceEvent;
import edu.mit.blocks.workspace.WorkspaceWidget;

/**
 * @author macchan
 */
public class SContextMenuProvider {

	private RenderableBlock rb;

	private JMenuItem blockCopyItem;
	private JMenuItem createValueItem;
	private JMenuItem createWriterItem;
	private JMenuItem createIncrementerItem;

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

	private JMenuItem createWriterMenu(final String genusName) {
		JMenuItem item = new JMenuItem(rb.getWorkspace().getEnv()
				.getGenusWithName(genusName).getInitialLabel());
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createCallMethod(genusName);
			}
		});
		return item;
	}

	private void createCallMethod(String name) {
		// RenderableBlock createRb =
		// BlockUtilities.getBlock("get","hoge");//does not work !!

		RenderableBlock newCommandRBlock = createNewBlock(rb.getWorkspace(),
				rb.getParentWidget(), name);

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
				RenderableBlock newActionRBlock = createNewBlock(rb.getWorkspace(),
						rb.getParentWidget(), "callActionMethod2");
				newActionRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // 新しく生成するブロックのポジション
				connectByPlug(newActionRBlock, 0, newGetterRBlock);
			}
		}
	}

	// private JMenuItem createLengthMenu() {
	// if (createIncrementerItem == null) {
	// createIncrementerItem = new JMenuItem("文字列の長さを取得する");
	// createIncrementerItem.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// createCallMethod("length");
	// }
	// });
	// }
	// return createIncrementerItem;
	// }

	// private JMenuItem createCallActionMethodBlockMenu() {
	// if (createCallActionMethodBlockItem == null) {
	// createCallActionMethodBlockItem = new JMenuItem("「メソッド実行ブロック」の作成");
	// createCallActionMethodBlockItem
	// .addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// new SStubCreator("callActionMethod", rb).doWork(e);
	// }
	// });
	// }
	// return createCallActionMethodBlockItem;
	// }
	//
	// private JMenuItem createCallGetterMethodBlockMenu() {
	// if (createCallGetterMethodBlockItem == null) {
	// createCallGetterMethodBlockItem = new JMenuItem(
	// "「メソッド実行ブロック(整数型)」の作成");
	// createCallGetterMethodBlockItem
	// .addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// new SStubCreator("callGetterMethod", rb).doWork(e);
	// }
	// });
	// }
	// return createCallGetterMethodBlockItem;
	// }
	//
	// private JMenuItem createCallDoubleMethodBlockMenu() {
	// if (createCallDoubleMethodBlockItem == null) {
	// createCallDoubleMethodBlockItem = new JMenuItem(
	// "「メソッド実行ブロック(double型)」の作成");
	// createCallDoubleMethodBlockItem
	// .addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// new SStubCreator("callDoubleMethod", rb).doWork(e);
	// }
	// });
	// }
	// return createCallDoubleMethodBlockItem;
	// }
	//
	// private JMenuItem createCallBooleanMethodBlockMenu() {
	// if (createCallBooleanMethodBlockItem == null) {
	// createCallBooleanMethodBlockItem = new JMenuItem(
	// "「メソッド実行ブロック(真偽型)」の作成");
	// createCallBooleanMethodBlockItem
	// .addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// new SStubCreator("callBooleanMethod", rb).doWork(e);
	// }
	// });
	// }
	// return createCallBooleanMethodBlockItem;
	// }
	//
	// private JMenuItem createCallStringMethodBlockMenu() {
	// if (createCallStringMethodBlockItem == null) {
	// createCallStringMethodBlockItem = new JMenuItem(
	// "「メソッド実行ブロック(文字列)」の作成");
	// createCallStringMethodBlockItem
	// .addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// new SStubCreator("callStringMethod", rb).doWork(e);
	// }
	// });
	// }
	// return createCallStringMethodBlockItem;
	// }

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

	// public JMenu createClassMethodsCategory(String className,
	// List<MethodInformation> methods) {
	// JMenu category = new JMenu(className);
	// for (MethodInformation method : methods) {
	// category.add(createCallClassMethodMenu(method));
	// }
	//
	// return category;
	// }

	public JMenu createClassMethodsCategory(String className,
			List<String> methods) {
		JMenu category = new JMenu(className);

		for (String methodName : methods) {
			category.add(createWriterMenu(methodName));
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
			// menu.add(createCreateGetterMenu());
			menu.addSeparator();
		}

		if (rb.getBlock().isVariableDeclBlock()
		/* && !rb.getBlock().isObjectTypeVariableDeclBlock() */) {
			menu.add(createCreateValueMenu());
			menu.add(createCreateWriterMenu());
			// menu.add(createCreateGetterMenu());
			menu.addSeparator();
			//メソッドの呼び出しブロックメニューの追加
			
			JMenu methodCallMenu = new JMenu(rb.getBlockName() + "に命令する");
			
			Map<String, List<String>> methods;
			
			for (String className : rb.getWorkspace().getEnv()
					.getBlock(rb.getBlockID()).getMethodList().keySet()) {
				methods = rb.getWorkspace().getEnv().getBlock(rb.getBlockID())
						.getMethodList();
				methodCallMenu.add(createClassMethodsCategory(className,
						methods.get(className)));
			}
			if(methodCallMenu.getItemCount()>0){
				menu.add(methodCallMenu);
				menu.addSeparator();
			}
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

		// if (rb.getBlock().isStringVariableDecBlock()) {
		// menu.add(createLengthMenu());
		// menu.addSeparator();
		// }

		if (rb.getBlock().getGenusName().contains("arrayobject")) {// 配列
			final String scope = getBlockScope(rb.getBlock().getGenusName());

			final String type = getBlockVariableType(rb.getBlock()
					.getGenusName());

			// 型に応じたゲッター、セッターの追加
			JMenuItem elementGetter = new JMenuItem("「書込ブロック（要素）」の作成");
			// getterの作成
			elementGetter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("setter-arrayelement" + scope + type
							+ "-arrayobject", rb).doWork(e);
				}
			});
			menu.add(elementGetter);

			// setter
			JMenuItem elementSetter = new JMenuItem("「値ブロック（要素）」の作成");
			elementSetter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("getter-arrayelement" + scope + type
							+ "-arrayobject", rb).doWork(e);
				}
			});

			menu.add(elementSetter);
		}
		//
		// if (rb.getBlock().getGenusName().contains("-bcanvas")) {
		// JMenu category = new JMenu("BCanvas");
		// category.add(createCallMethodMenu("drawLine", "線を引きます"));
		// category.add(createCallMethodMenu("drawFillTriangle",
		// "塗りつぶした三角形を書きます"));
		// category.add(createCallMethodMenu("drawText",
		// "文字を書きます"));
		// category.add(createCallMethodMenu(
		// "drawFillArc", "塗りつぶした円を書きます"));
		// category.add(createCallMethodMenu(
		// "drawArc", "円を書きます"));
		// category.add(createCallMethodMenu("drawImage", "画像を書きます"));
		// // category.add(createCallMethodMenu("drawImage",
		// // "指定したサイズにリサイズする画像を書きます"));
		// category.add(createCallMethodMenu("getImageWidth", "画像の幅を取得します"));
		// category.add(createCallMethodMenu("getImageHeight", "画像の高さを取得します"));
		// category.add(createCallMethodMenu("clear", "キャンバスを白く塗りつぶします"));
		// category.add(createCallMethodMenu("update", "画面を更新します"));
		// category.add(createCallMethodMenu("getKeyCode", "押されたキーのコードを取得します"));
		// category.add(createCallMethodMenu("isKeyDown", "キーが押されたかどうか調べます"));
		// category.add(createCallMethodMenu("isKeyPressing",
		// "指定されたキーコードが押されているか調べます"));
		// category.add(createCallMethodMenu("isSingleClick",
		// "シングルクリックかどうか調べます"));
		// category.add(createCallMethodMenu("isDoubleClick",
		// "ダブルクリックかどうか調べます"));
		// category.add(createCallMethodMenu("isDragging", "ドラッグしているかどうか調べます"));
		// category.add(createCallMethodMenu("isRightMouseDown",
		// "右クリックかどうか調べます"));
		// category.add(createCallMethodMenu("isLeftMouseDown",
		// "左クリックかどうか調べます"));
		// category.add(createCallMethodMenu("getMouseX", "マウスのX座標を取得します"));
		// category.add(createCallMethodMenu("getMouseY", "マウスのY座標を取得します"));
		// category.add(createCallMethodMenu("getCanvasWidth",
		// "キャンバスの幅を取得します"));
		// category.add(createCallMethodMenu("getCanvasHeight",
		// "キャンバスの高さを取得します"));
		//
		// menu.add(category);
		// }
		//
		// if (rb.getBlock().getGenusName().contains("-bwindow")) {
		// JMenu category = new JMenu("BWindow");
		// category.add(createCallMethodMenu("setLocation", "位置を設定する"));
		// category.add(createCallMethodMenu("show", "ウインドウを表示する"));
		// category.add(createCallMethodMenu("setSize", "大きさを指定する"));
		// category.add(createCallMethodMenu("getCanvas", "書き込みできるキャンバスを取得する"));
		// menu.add(category);
		// }
		//
		// if (rb.getBlock().getGenusName().contains("-bsound")) {
		// JMenu category = new JMenu("BSound");
		// category.add(createCallMethodMenu("getVolume", "音量を取得する"));
		// category.add(createCallMethodMenu("setVolume", "音量を指定する"));
		// category.add(createCallMethodMenu("getDefaultVolume",
		// "音量のデフォルト値を取得する"));
		// category.add(createCallStaticMethodMenu("play[@string]", "再生する"));
		// category.add(createCallStaticMethodMenu("loadOnMemory[@string]",
		// "メモリに読み込む"));
		// category.add(createCallMethodMenu("loop", "ループ再生する"));
		// category.add(createCallMethodMenu("stop", "停止する"));
		// category.add(createCallMethodMenu("isPlaying", "再生しているかどうか"));
		// menu.add(category);
		// }
		//
		// if (rb.getBlock().isObjectTypeVariableDeclBlock()
		// || rb.getBlock().getGenusName().contains("listobject")) {
		// menu.add(createActionBlockMenu());
		// menu.add(createGetterBlockMenu());
		//

		// for (String key : rb.getBlock().getMethods().keySet()) {
		// menu.add(createClassMethodsCategory(key, rb.getBlock().getMethods()
		// .get(key)));
		// }
		//
		// if (rb.getBlock().getHeaderLabel().contains("Scanner")) {
		// {
		// JMenu category = new JMenu("Scanner");
		// category.add(createCallMethodMenu("next", "入力を受け取る(文字列型)"));
		// category.add(createCallMethodMenu("nextInt", "入力を受け取る(整数型)"));
		// category.add(createCallMethodMenu("hasNextInt",
		// "入力が整数型かどうか調べる"));
		// category.add(createCallMethodMenu("hasNextDouble",
		// "入力が実数型かどうか調べる"));
		// category.add(createCallMethodMenu("nextDouble",
		// "入力を受け取る(実数型)"));
		// menu.add(category);
		// }
		// }
		// if (rb.getBlock().getHeaderLabel().contains("Turtle")) {
		// {
		// JMenu category = new JMenu("Turtle基本");
		// category.add(createCallMethodMenu("fd", "進む"));
		// category.add(createCallMethodMenu("bk", "戻る"));
		// category.add(createCallMethodMenu("rt", "回る（右）"));
		// category.add(createCallMethodMenu("lt", "回る（左）"));
		// category.add(createCallMethodMenu("up", "ペンを上げる"));
		// category.add(createCallMethodMenu("down", "ペンを下ろす"));
		// category.add(createCallMethodMenu("color", "ペン色を変える"));
		// menu.add(category);
		// }
		// {
		// JMenu category = new JMenu("Turtle応用");
		// category.add(createCallMethodMenu("warp", "ワープする"));
		// category.add(createCallMethodMenu("size", "大きさを変える"));
		// category.add(createCallMethodMenu("getX", "X座標"));
		// category.add(createCallMethodMenu("getY", "Y座標"));
		// category.add(createCallMethodMenu("getWidth", "幅"));
		// category.add(createCallMethodMenu("getHeight", "高さ"));
		// category.add(createCallMethodMenu("intersects",
		// "オブジェクトが重なっているかどうか調べる"));
		// category.add(createCallMethodMenu("contains",
		// "オブジェクトが指定した座標を含む位置にいるか調べる"));
		// category.add(createCallMethodMenu("looks", "見た目を変える"));
		// category.add(createCallMethodMenu("show", "表示する"));
		// category.add(createCallMethodMenu("hide", "非表示にする"));
		// category.add(createCallMethodMenu("setShow", "表示状態を設定する"));
		// category.add(createCallMethodMenu("isShow",
		// "表示されているかどうか調べる"));
		// menu.add(category);
		// }
		// }
		// if (rb.getBlock().getHeaderLabel().contains("ImageTurtle")) {
		// JMenu category = new JMenu("ImageTurtle");
		// category.add(createCallMethodMenu("image", "画像ファイルを設定する"));
		// menu.add(category);
		// }
		// if (rb.getBlock().getHeaderLabel().contains("TextTurtle")) {
		// JMenu category = new JMenu("TextTurtle");
		// category.add(createCallMethodMenu("text", "テキストを設定する"));
		// category.add(createCallMethodMenu("getText", "テキストを取得する"));
		// category.add(category);
		// menu.add(category);
		// }
		// if (rb.getBlock().getHeaderLabel().contains("SoundTurtle")) {
		// JMenu category = new JMenu("TextTurtle");
		// category.add(createCallMethodMenu("file", "ファイルを設定する"));
		// category.add(createCallMethodMenu("play", "再生する"));
		// category.add(createCallMethodMenu("loop", "ループ再生する"));
		// category.add(createCallMethodMenu("stop", "停止する"));
		// category.add(createCallMethodMenu("isPlaying", "再生しているかどうか"));
		// menu.add(category);
		// }
		// if (rb.getBlock().getHeaderLabel().contains("ListTurtle")) {
		// JMenu category = new JMenu("ListTurtle");
		// category.add(createCallMethodMenu("get", "x番値の要素取得"));
		// category.add(createCallMethodMenu("getSize", "要素数"));
		// category.add(createCallMethodMenu("add", "追加する"));
		// category.add(createCallMethodMenu("addFirst", "最初に追加する"));
		// category.add(createCallMethodMenu("addLast", "最後に追加する"));
		// category.add(createCallMethodMenu("addAll", "全て追加する"));
		// category.add(createCallMethodMenu("moveAllTo", "全て移動する"));
		// category.add(createCallMethodMenu("removeFirst", "先頭要素を削除する"));
		// category.add(createCallMethodMenu("removeLast", "末尾要素を削除する"));
		// category.add(createCallMethodMenu("removeAll", "全ての要素を削除する"));
		// category.add(createCallMethodMenu("getCursor", "カーソル位置"));
		// category.add(createCallMethodMenu("setCursor", "カーソル位置を設定する"));
		// category.add(createCallMethodMenu("moveCursorToNext",
		// "カーソルを進める"));
		// category.add(createCallMethodMenu("moveCursorToPrevious",
		// "カーソルを戻す"));
		// category.add(createCallMethodMenu("getObjectAtCursor",
		// "カーソル位置の要素取得"));
		// category.add(createCallMethodMenu("addToBeforeCursor",
		// "カーソルの前に追加する"));
		// category.add(createCallMethodMenu("addToAfterCursor",
		// "カーソルの後に追加する"));
		// category.add(createCallMethodMenu("removeAtCursor",
		// "カーソル位置の要素を削除する"));
		// category.add(createCallMethodMenu("shuffle", "かき混ぜる"));
		// category.add(createCallMethodMenu("warpByTopLeft",
		// "（左上の座標指定で）ワープする"));
		// menu.add(category);
		// }
		//
		// if (rb.getBlock().getGenusName().contains("listobject")) {
		// JMenu category = new JMenu("List");
		// category.add(createCallListMethodMenu("get[@number]",
		// "x番値の要素取得"));
		// category.add(createCallListMethodMenu("size", "要素数"));
		// category.add(createCallListMethodMenu("add", "追加する"));
		// category.add(createCallListMethodMenu("clear", "全ての要素を削除する"));
		// category.add(createCallListMethodMenu("contains", "ある要素があるか調べる"));
		// category.add(createCallListMethodMenu("isEmpty", "リストが空か調べる"));
		// category.add(createCallListMethodMenu("remove",
		// "指定した要素を削除する"));
		// menu.add(category);
		// }
		//
		// if (rb.getBlock().getHeaderLabel().contains("CardTurtle")) {
		// JMenu category = new JMenu("CardTurtle");
		// category.add(createCallMethodMenu("getNumber", "番号取得"));
		// category.add(createCallMethodMenu("getText", "文字列取得"));
		// menu.add(category);
		// }
		// if (rb.getBlock().getHeaderLabel().contains("ButtonTurtle")) {
		// JMenu category = new JMenu("ButtonTurtle");
		// category.add(createCallMethodMenu("isClicked", "クリックされたかどうか"));
		// menu.add(category);
		// }
		// if (rb.getBlock().getHeaderLabel().contains("InputTurtle")) {
		// JMenu category = new JMenu("InputTurtle");
		// category.add(createCallMethodMenu("getText", "文字列を取得する"));
		// category.add(createCallMethodMenu("text", "文字列を設定する"));
		// category.add(createCallMethodMenu("clearText", "文字列を空にする"));
		// category.add(createCallMethodMenu("setActive", "活動状態を設定する"));
		// category.add(createCallMethodMenu("isActive", "活動状態を取得する"));
		// category.add(createCallMethodMenu("toJapaneseMode", "日本語モードにする"));
		// category.add(createCallMethodMenu("toEnglishMode", "英語モードにする"));
		// category.add(createCallMethodMenu("fontsize", "フォントサイズを設定する"));
		// menu.add(category);
		// }
		// menu.addSeparator();
		// }

		if (rb.getBlock().isProcedureDeclBlock()) {
			menu.add(createCallerMenu());
			menu.addSeparator();
		}

		if (!rb.getBlock().isProcedureDeclBlock()) {
			menu.add(createBlockCopyMenu());
			menu.addSeparator();
		}

		return menu;
	}

	// private JMenuItem createActionBlockMenu() {
	// JMenuItem item = new JMenuItem("「実行」ブロック作成");
	// item.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// createActionGetterBlock(rb, "callActionMethod2");
	// }
	// });
	// return item;
	// }
	//
	// private JMenuItem createGetterBlockMenu() {
	// JMenuItem item = new JMenuItem("「実行値」ブロック作成");
	// item.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// createActionGetterBlock(rb, "callGetterMethod2");
	// }
	// });
	// return item;
	// }

	/**
	 */
	// private JMenuItem createCallMethodMenu(final String name, String label) {
	// JMenuItem item = new JMenuItem(label);
	// item.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// createCallMethod(name);
	// }
	// });
	// return item;
	// }
	//
	// private JMenuItem createCallStaticMethodMenu(final String name, String
	// label) {
	// JMenuItem item = new JMenuItem(label);
	// item.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// createCallStaticMethod(name);
	// }
	// });
	// return item;
	// }
	//
	// private JMenuItem createCallListMethodMenu(final String name, String
	// label) {
	// JMenuItem item = new JMenuItem(label);
	// item.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// createListMethod(name);
	// }
	// });
	// return item;
	// }
	//
	// private void createConstructor(String name) {
	// RenderableBlock newCommandRBlock = createNewBlock(rb.getWorkspace(),
	// rb.getParentWidget(), name);
	// newCommandRBlock.setLocation(rb.getX() + 20, rb.getY() + 20);
	// }
//
//	private void createCallMethod(String name, RenderableBlock newCommandRBlock) {
//
//		boolean cmd = newCommandRBlock.getBlock().getPlug() == null;
//		if (cmd) {
//			RenderableBlock newActionRBlock = createActionGetterBlock(rb,
//					"callActionMethod2");
//			connectByBefore(newActionRBlock, 1, newCommandRBlock);
//		} else {
//			RenderableBlock newGetterRBlock = createActionGetterBlock(rb,
//					"callGetterMethod2");
//			connectByPlug(newGetterRBlock, 1, newCommandRBlock);
//
//			boolean returnObject = newCommandRBlock.getBlock().getPlug()
//					.getKind().equals("object");
//			if (returnObject) {
//				RenderableBlock newActionRBlock = createNewBlock(
//						rb.getWorkspace(), rb.getParentWidget(),
//						"callActionMethod2");
//				newActionRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // 新しく生成するブロックのポジション
//				connectByPlug(newActionRBlock, 0, newGetterRBlock);
//			}
//		}
//	}

	// private void createCallStaticMethod(String name) {
	// RenderableBlock newCommandRBlock = createNewBlock(rb.getWorkspace(),
	// rb.getParentWidget(), name);
	// newCommandRBlock.setLocation(rb.getX() + 20, rb.getY() + 20);
	// }
	//
	// private void createListMethod(String name) {
	// // RenderableBlock newCommandRBlock =
	// // createNewBlock(rb.getParentWidget(),
	// // name);
	// //
	// // //Listの型を確認する
	// // Block newBlock = Block.getBlock(rb.getBlock().getSocketAt(0)
	// // .getBlockID());
	// // Block typeBlock =
	// // Block.getBlock(newBlock.getSocketAt(0).getBlockID());
	// // //listの型
	// // String type =
	// // ElementModel.getConnectorType(typeBlock.getBlockLabel());
	// // if (newCommandRBlock.getBlock().getGenusName().equals("add"))
	// // {//要素を追加するメソッドの場合は、プラグの形を変える
	// // newCommandRBlock.getBlock().getSocketAt(0).setKind(type);
	// // }
	// // boolean cmd = newCommandRBlock.getBlock().getPlug() == null;
	// // if (cmd) {
	// // RenderableBlock newActionRBlock = createActionGetterBlock(rb,
	// // "callActionMethod2");
	// // connectByBefore(newActionRBlock, 1, newCommandRBlock);
	// //
	// // } else {
	// // RenderableBlock newGetterRBlock = createActionGetterBlock(rb,
	// // "callGetterMethod2");
	// // connectByPlug(newGetterRBlock, 1, newCommandRBlock);
	// //
	// // boolean returnObject = newCommandRBlock.getBlock().getPlug()
	// // .getKind().equals("object");
	// // if (returnObject) {
	// //
	// // newGetterRBlock.getBlock().setPlugKind(type);
	// //
	// // //要素がobject型なら、callActionMethod2ブロックと結合する
	// // if (type.equals("object")) {
	// // RenderableBlock newActionRBlock = createNewBlock(
	// // rb.getParentWidget(), "callActionMethod2");
	// // newActionRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); //
	// // 新しく生成するブロックのポジション
	// // connectByPlug(newActionRBlock, 0, newGetterRBlock);
	// // }
	// // }
	// // }
	//
	// }

	// #ohata
	private void createNewGetterMethod(String name) {

		RenderableBlock newCommandRBlock = createNewBlock(rb.getWorkspace(),
				rb.getParentWidget(), name);
		// procedureのブロック名を変える
		Block methodBlock = newCommandRBlock.getBlock();
		methodBlock.setBlockLabel("get"
				+ rb.getKeyword().toUpperCase().charAt(0)
				+ rb.getKeyword().substring(1));

		RenderableBlock returnBlock = createNewBlock(rb.getWorkspace(),
				rb.getParentWidget(), "return");
		RenderableBlock getter = SStubCreator.createStub("getter", rb);

		newCommandRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // 新しく生成するブロックのポジション

		returnBlock.setLocation(rb.getX() + 20,
				rb.getY() + newCommandRBlock.getHeight() + 20); // 無理やり座標指定

		getter.setLocation(rb.getX() + returnBlock.getBlockWidth() + 10,
				rb.getY() + newCommandRBlock.getHeight() + 20);
		// returnと値を結合
		connectByPlug(returnBlock, 0, getter);

		BlockLink link = newCommandRBlock.getNearbyLink();

		if (link != null) {
			link.connect();
		}
	}

	private void createNewSetterMethod(String name) {// #ohata
		RenderableBlock newCommandRBlock = createNewBlock(rb.getWorkspace(),
				rb.getParentWidget(), name);
		newCommandRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // 新しく生成するブロックのポジション
		// ラベル張替え
		Block methodBlock = newCommandRBlock.getBlock();
		methodBlock.setBlockLabel("set"
				+ rb.getKeyword().toUpperCase().charAt(0)
				+ rb.getKeyword().substring(1));

		RenderableBlock setter = SStubCreator.createStub("setter", rb);
		setter.setLocation(rb.getX() + 20, rb.getY() + 40);

		BlockLink link = newCommandRBlock.getNearbyLink();

		link.connect();

		if (rb.getGenus().endsWith("string")) {
			RenderableBlock param = createNewBlock(rb.getWorkspace(),
					rb.getParentWidget(), "proc-param-string");
			connectByPlug(newCommandRBlock, 0, param);
		} else if (rb.getGenus().endsWith("boolean")) {
			RenderableBlock param = createNewBlock(rb.getWorkspace(),
					rb.getParentWidget(), "proc-param-boolean");
			connectByPlug(newCommandRBlock, 0, param);
		} else if (rb.getGenus().endsWith("double-number")) {
			RenderableBlock param = createNewBlock(rb.getWorkspace(),
					rb.getParentWidget(), "proc-param-double-number");
			connectByPlug(newCommandRBlock, 0, param);
		} else if (rb.getGenus().endsWith("number")) {
			RenderableBlock param = createNewBlock(rb.getWorkspace(),
					rb.getParentWidget(), "proc-param-number");
			connectByPlug(newCommandRBlock, 0, param);

		} else if (rb.getGenus().endsWith("TextTurtle")) {
			RenderableBlock param = createNewBlock(rb.getWorkspace(),
					rb.getParentWidget(), "proc-param-TextTurtle");
			connectByPlug(newCommandRBlock, 0, param);

		} else if (rb.getGenus().endsWith("Turtle")) {
			RenderableBlock param = createNewBlock(rb.getWorkspace(),
					rb.getParentWidget(), "proc-param-Tertle");
			connectByPlug(newCommandRBlock, 0, param);
		}

		// newCommandRBlock.getParentWidget().blockDropped(newCommandRBlock);
		// newCommandRBlock.getParentWidget().blockDropped(setter);
	}

	private static RenderableBlock createActionGetterBlock(
			RenderableBlock parent, String genusName) {

		RenderableBlock newCallRBlock = createNewBlock(parent.getWorkspace(),
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

	public static RenderableBlock createNewBlock(Workspace workspace,
			WorkspaceWidget widget, String genusName) {
		for (RenderableBlock block : workspace.getFactoryManager().getBlocks()) {
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
		BlockLink link = BlockLink.getBlockLink(parent.getWorkspace(), parent
				.getBlock(), child.getBlock(),
				parent.getBlock().getSocketAt(socketIndex), child.getBlock()
						.getPlug());
		link.connect();

		parent.getWorkspace().notifyListeners(
				new WorkspaceEvent(parent.getWorkspace(), parent
						.getParentWidget(), link,
						WorkspaceEvent.BLOCKS_CONNECTED));

	}

	public static void connectByBefore(RenderableBlock parent, int socketIndex,
			RenderableBlock child) {
		BlockLink link = BlockLink.getBlockLink(parent.getWorkspace(), parent
				.getBlock(), child.getBlock(),
				parent.getBlock().getSocketAt(socketIndex), child.getBlock()
						.getBeforeConnector());
		link.connect();
		parent.getWorkspace().notifyListeners(
				new WorkspaceEvent(parent.getWorkspace(), parent
						.getParentWidget(), link,
						WorkspaceEvent.BLOCKS_CONNECTED));

	}

	public static void connectBySocket(RenderableBlock parent, int socketIndex,
			RenderableBlock child) {
		BlockLink link = BlockLink.getBlockLink(parent.getWorkspace(), parent
				.getBlock(), child.getBlock(),
				parent.getBlock().getSocketAt(socketIndex), child.getBlock()
						.getSocketAt(socketIndex));
		link.connect();

		parent.getWorkspace().notifyListeners(
				new WorkspaceEvent(parent.getWorkspace(), parent
						.getParentWidget(), link,
						WorkspaceEvent.BLOCKS_CONNECTED));
	}

	private String getBlockScope(String name) {

		String scope = name.substring(0,
				rb.getBlock().getGenusName().indexOf("-"));

		if ("proc".equals(scope)) {
			scope += "-param-";
		} else {
			scope += "-var-";
		}

		return scope;

	}

	private String getBlockVariableType(String name) {
		if (name.contains("number") || name.contains("int")) {
			return "int-number";
		}

		if (name.contains("String") || name.contains("string")) {
			return "string";
		}

		if (name.contains("double")) {
			return "double";
		}

		return "object";
	}

}
