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

			Map<String, List<String>> methods = rb.getWorkspace().getEnv().getGenusWithName(rb.getGenus()).getMethods();

			for (String className : methods.keySet()) {
				methodCallMenu.add(createClassMethodsCategory(className, methods.get(className)));
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

		if (rb.getBlock().isProcedureDeclBlock()) {
			menu.add(createCallerMenu());
			menu.addSeparator();
			menu.add(createBlockCopyMenu());
			menu.addSeparator();

		}

		return menu;
	}

	// #ohata
	private void createNewGetterMethod(String name) {
		RenderableBlock newCommandRBlock = createNewBlock(rb.getWorkspace(), rb.getParentWidget(), name);
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
					rb.getParentWidget(), "proc-param-Turtle");
			connectByPlug(newCommandRBlock, 0, param);
		}
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
		String scope = name.substring(0, rb.getBlock().getGenusName().indexOf("-"));

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
