/*
 * SBlockCopier.java
 * Created on 2011/11/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package a.slab.blockeditor.extent;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import renderable.BlockUtilities;
import renderable.RenderableBlock;
import workspace.Workspace;
import workspace.WorkspaceEvent;
import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.BlockLink;

/**
 * @author macchan
 */
public class SBlockCopier {

	private RenderableBlock rb;

	public SBlockCopier(RenderableBlock rb) {
		this.rb = rb;
	}

	public void doWork(ActionEvent e) {
		// System.out.println("block copy");
		copy(rb);
	}

	private static RenderableBlock copy(RenderableBlock srcRBlock) {

		// copy
		Block srcBlock = srcRBlock.getBlock();
		RenderableBlock newRBlock = BlockUtilities.cloneBlock(srcBlock);
		Block newBlock = newRBlock.getBlock();
		newRBlock.setLocation(srcRBlock.getX() + 10, srcRBlock.getY() + 10);
		newRBlock.setParentWidget(srcRBlock.getParentWidget());
		srcRBlock.getParentWidget().addBlock(newRBlock);

		// ブロック作成時に作られてしまったデフォルト値があれば削除
		for (BlockConnector con : newBlock.getSockets()) {
			if (con.hasBlock()) {
				deleteRecursively(Block.getBlock(con.getBlockID()));
			}
		}

		// 値を作成して連結
		int numSockets = srcBlock.getNumSockets();
		for (int i = 0; i < numSockets; i++) {
			BlockConnector srcCon = srcBlock.getSocketAt(i);
			BlockConnector newCon = newBlock.getSocketAt(i);
			copyValueAndConnect(srcCon, newBlock, newCon);
		}

		// Viewを乗せる
		if (srcRBlock.isCollapsed()) {
			newRBlock.updateCollapse();
		}

		// 下に連結している値を連結
		if (srcBlock.hasAfterConnector()) {
			BlockConnector srcCon = srcBlock.getAfterConnector();
			BlockConnector newCon = newBlock.getAfterConnector();
			copyValueAndConnect(srcCon, newBlock, newCon);
		}
		return newRBlock;
	}

	private static void copyValueAndConnect(BlockConnector srcCon,
			Block newBlock, BlockConnector newCon) {
		if (srcCon.hasBlock()) {
			RenderableBlock newValueRBlock = copy(RenderableBlock
					.getRenderableBlock(srcCon.getBlockID()));
			Block newValueBlock = newValueRBlock.getBlock();
			BlockConnector newValueCon = getLeftConnection(newValueBlock);// 最後のつなぐ箇所　注意
			BlockLink link = BlockLink.getBlockLink(newBlock, newValueBlock,
					newCon, newValueCon);
			link.connect();
			//これをやらないと形が変わらない
			Workspace.getInstance().notifyListeners(
					new WorkspaceEvent(newValueRBlock.getParentWidget(), link,
							WorkspaceEvent.BLOCKS_CONNECTED));
		}
	}

	private static void deleteRecursively(Block block) {
		// delete sub blocks
		for (BlockConnector con : block.getSockets()) {
			if (con.hasBlock()) {
				Block subBlock = Block.getBlock(con.getBlockID());
				deleteRecursively(subBlock);
			}
		}

		// if I am owned then disconnect
		BlockConnector meToOwnerCon = getLeftConnection(block);
		if (meToOwnerCon.hasBlock()) {
			Block ownerBlock = Block.getBlock(meToOwnerCon.getBlockID());
			BlockConnector ownerToMeCon = getRightConnection(ownerBlock, block);
			BlockLink.getBlockLink(ownerBlock, block, ownerToMeCon,
					meToOwnerCon).disconnect();
		}

		// delete me
		BlockUtilities.deleteBlock(RenderableBlock.getRenderableBlock(block
				.getBlockID()));
	}

	private static BlockConnector getRightConnection(Block ownerBlock,
			Block ownedBlock) {
		for (BlockConnector con : ownerBlock.getSockets()) {
			if (con.getBlockID().equals(ownedBlock.getBlockID())) {
				return con;
			}
		}
		throw new RuntimeException("RightConnection not found.");
	}

	private static BlockConnector getLeftConnection(Block block) {
		if (block.hasBeforeConnector()) {
			return block.getBeforeConnector();
		} else if (block.hasPlug()) {
			return block.getPlug();
		} else {
			throw new RuntimeException("LeftConnection not found.");
		}
	}

	// -------------------------------------------------------------------------
	// 手による配置のエミュレートにより内部ブロックを複製します（以下，現在使われていません）
	// -------------------------------------------------------------------------

	public static void copyRecursivelyByEmuration(RenderableBlock rb,
			ActionEvent e) {
		if (rb == null) {
			return;
		}
		RenderableBlock newRb = BlockUtilities.cloneBlock(rb.getBlock());
		newRb.setLocation(rb.getX() + 200, rb.getY()); // 新しく生成するブロックのポジション
		newRb.setParentWidget(rb.getParentWidget());
		rb.getParent().add(newRb, 0);
		MouseEvent me = new MouseEvent(newRb, e.getID(), e.getWhen(),
				InputEvent.BUTTON1_MASK, 1, 1, 1, false, MouseEvent.BUTTON1);
		newRb.mousePressed(me);
		newRb.mouseDragged(me);
		newRb.mouseReleased(me);
		for (BlockConnector con : newRb.getBlock().getSockets()) {
			deleteRecursivelyByHandEmuration(
					RenderableBlock.getRenderableBlock(con.getBlockID()), e);
		}
		for (BlockConnector con : rb.getBlock().getSockets()) {
			copyRecursivelyByEmuration(
					RenderableBlock.getRenderableBlock(con.getBlockID()), e);
		}
		if (rb.isCollapsed()) {
			newRb.updateCollapse();
		}

		// 対象となるブロックにコネクトしているブロック（AfterBlock）の生成
		long next = rb.getBlock().getAfterBlockID();
		if (next == Block.NULL) {
			return;
		}
		rb = RenderableBlock.getRenderableBlock(next);
		copyRecursivelyByEmuration(rb, e);

		return;
	}

	private static void deleteRecursivelyByHandEmuration(RenderableBlock rb,
			ActionEvent e) {
		if (rb == null) {
			return;
		}
		for (BlockConnector con : rb.getBlock().getSockets()) {
			deleteRecursivelyByHandEmuration(
					RenderableBlock.getRenderableBlock(con.getBlockID()), e);
		}
		int x = rb.getX();
		int y = rb.getY();
		MouseEvent e1 = new MouseEvent(rb, e.getID(), e.getWhen(),
				InputEvent.BUTTON1_MASK, 0, 0, 1, false, MouseEvent.BUTTON1);
		MouseEvent e2 = new MouseEvent(rb, e.getID(), e.getWhen(),
				InputEvent.BUTTON1_MASK, -x, -y, 1, false, MouseEvent.BUTTON1);
		rb.mousePressed(e1);
		rb.mouseDragged(e2);
		rb.mouseReleased(e1);
		BlockUtilities.deleteBlock(rb);
	}
}
