package a.slab.blockeditor.extent;

import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.LinkRule;

/**
 * 
 * created by sakai lab 2011/11/21
 * 
 * @author yasui
 * 
 *         <code>CallMethodRule</code>
 *         はオブジェクト変数のメソッド参照ブロックのソケットにあるブロックが一つだけであるかをチェックする。
 * 
 */
public class SCallMethodRule implements LinkRule {

	boolean isMandatory = false;

	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {
		if ((block1.isCallGetterMethodBlock() && !block2
				.isCallActionMethodBlock())
				|| (block2.isCallGetterMethodBlock() && !block1
						.isCallActionMethodBlock())) {
			return canLinkForCallGetterMethodBlock(block1, block2);
		} else if (callActionMethodBlockChecker(block1)
				&& callActionMethodBlockChecker(block2)) {
			if (block1.isDataBlock() || block2.isDataBlock()) {
				isMandatory = false;
				return false;
			}
			return canLinkForCallActionMethodBlock(block1, block2, socket1,
					socket2);
		}
		isMandatory = false;
		return false;
	}

	private boolean canLinkForCallActionMethodBlock(Block block1, Block block2,
			BlockConnector socket1, BlockConnector socket2) {

		if (!block1.isMethodBlock() || !block2.isMethodBlock()) {
			isMandatory = true;
			return false;
		}
		//		if (callActionMethodBlockChecker(block1) && block2.getAfterBlockID() != Block.NULL) {
		//			isMandatory = true;
		//			return false;
		//		} else if (callActionMethodBlockChecker(block2) && block1.getAfterBlockID() != Block.NULL) {
		//			isMandatory = true;
		//			return false;
		//		}
		//		if (socketHasBlockChecker(block1, socket1) || socketHasBlockChecker(block2, socket2)) {
		//			isMandatory = true;
		//			return false;
		//		}
		isMandatory = false;
		return true;
	}

	private boolean canLinkForCallGetterMethodBlock(Block block1, Block block2) {
		if (isDataBlocksWithNotCallMethod(block1)
				|| isDataBlocksWithNotCallMethod(block2)) {
			isMandatory = true;
			return false;
		}
		isMandatory = false;
		return true;
	}

	// 後でBlockGenusクラスに移し変える
	private boolean isDataBlocksWithNotCallMethod(Block block) {
		return block.getGenusName().equals("number")
				|| block.getGenusName().equals("random");
	}

	private boolean callActionMethodBlockChecker(Block block) {
		if (block.isCallActionMethodBlock()
				|| (block.getBeforeBlockID() != Block.NULL && Block.getBlock(
						block.getBeforeBlockID()).isCallActionMethodBlock())) {
			return true;
		}
		return false;
	}

	//	private boolean socketHasBlockChecker(Block block, BlockConnector socket) {
	//		if (block.isCallActionMethodBlock() && socket.hasBlock()) {
	//			return true;
	//		} else if (block.getBeforeBlockID() != Block.NULL
	//				&& Block.getBlock(block.getBeforeBlockID())
	//						.isCallActionMethodBlock()) {
	//			BlockConnector beforeBlockSocket = Block.getBlock(
	//					block.getBeforeBlockID()).getSocketAt(0);
	//			if (beforeBlockSocket.hasBlock()) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}

	public boolean isMandatory() {
		return isMandatory;
	}

}
