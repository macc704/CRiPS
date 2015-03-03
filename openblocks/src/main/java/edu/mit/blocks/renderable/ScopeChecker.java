package edu.mit.blocks.renderable;

import java.awt.Color;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;



public class ScopeChecker {

	public static boolean checkScope(Block beforeBlock, Block cmpBlock) {
		//cmpblock:チェックする変数のブロック
		//before　結合先のブロック

		String compareBlockName;
		Block compareBlock = cmpBlock;
		//参照ブロック、private変数ブロックでない場合はスコープを確認
		if (isCompareBlock(cmpBlock)
				&& !cmpBlock.getGenusName().contains("private")) {
			//直前のブロックがプラグを持っている場合、そちらが直前のブロックになる
			if ((beforeBlock.getPlugBlockID()) != null) {
				beforeBlock = purcePlugBlock(beforeBlock);
			}
			//離れ小島のブロックの場合はtrueを返す
			if (isIndependentBlock(beforeBlock)) {
				return true;
			}

			//callActionブロックの場合は、参照ブロックはソケットにくっついているので、そちらを参照ブロックに設定する
			if (compareBlock.getGenusName().equals("callActionMethod2")
					|| compareBlock.getGenusName().equals("callGetterMethod2")) {
				compareBlock = compareBlock.getWorkspace().getEnv().getBlock(compareBlock.getSocketAt(0)
						.getBlockID());
				//参照ブロックがなかった場合、チェックする参照ブロックがないためtrue privateもtrue
				if (compareBlock == null
						|| compareBlock.getGenusName().contains("private")) {
					return true;//とりあえずくっつける
				}
			}

			//参照ブロックの名前を取得
			compareBlockName = compareBlock.getBlockLabel().substring(
					0,
					compareBlock.getBlockLabel().indexOf(
							compareBlock.getLabelSuffix()));

			//所属できる場所かどうか確認する
			if (confirmCompareBlockIsBelongable(cmpBlock, beforeBlock,
					compareBlockName)) {
				return true;
			}
		
			cmpBlock.getWorkspace().getEnv().getRenderableBlock(cmpBlock.getBlockID())
					.setBlockHighlightColor(Color.RED);

			return false;
		} else {//参照ブロックでなかった場合、private変数の参照ブロックで会った場合は結合許可
			return true;
		}
	}

	private static Block purcePlugBlock(Block block) {

		while (block.getWorkspace().getEnv().getBlock(block.getPlugBlockID()) != null) {
			block = block.getWorkspace().getEnv().getBlock(block.getPlugBlockID());
		}
		return block;
	}

	//参照ブロックが所属できるかどうか確認する. 参照ブロックから上に順番にたどっていって、スコープがあっているか確認する
	//引数　スコープを確認するブロック:cmpblock 結合先の一番前のブロック:beforelock
	private static boolean confirmCompareBlockIsBelongable(Block cmpBlock,
			Block beforeBlock, String originBlockName) {

		Block checkBlock = cmpBlock;

		if (cmpBlock.getPlugBlockID() != -1) {
			while (cmpBlock.getPlugBlockID() != -1) {
				cmpBlock = cmpBlock.getWorkspace().getEnv().getBlock(cmpBlock.getPlugBlockID());
			}
		}
		
		
		Block lastBlock = null;
		//持ってるブロックから前のブロックをすべてチェックする
		while (cmpBlock != null) {
			if (cmpBlock.getBlockLabel().equals(originBlockName)) {
				return true;
			}
			//procedureだった場合
			if (cmpBlock.getWorkspace().getEnv().getRenderableBlock(cmpBlock.getBlockID())
					.getGenus().equals("procedure")) {
				for (BlockConnector socket : cmpBlock.getSockets()) {
					if (socket.getBlockID() != -1) {
						if (cmpBlock.getWorkspace().getEnv().getBlock(socket.getBlockID()).getBlockLabel()
								.equals(originBlockName)) {
							return true;
						}
					}
				}
			}
			lastBlock = cmpBlock;
			cmpBlock = cmpBlock.getWorkspace().getEnv().getBlock(cmpBlock.getBeforeBlockID());
		}
		
		//結合先のブロック郡をチェック
		cmpBlock = beforeBlock;
		while (cmpBlock != null) {
			if (cmpBlock.getBlockLabel().equals(originBlockName)) {
				return true;
			}
			//procedureだった場合ソケットをすべて確認して
			if (cmpBlock.getWorkspace().getEnv().getRenderableBlock(cmpBlock.getBlockID())
					.getGenus().equals("procedure")) {
				for (BlockConnector socket : cmpBlock.getSockets()) {
					if (socket.getBlockID() != -1) {
						if (cmpBlock.getWorkspace().getEnv().getBlock(socket.getBlockID()).getBlockLabel()
								.equals(originBlockName)) {
							return true;
						}
					}
				}
			}
			
			lastBlock = cmpBlock;
			cmpBlock = cmpBlock.getWorkspace().getEnv().getBlock(cmpBlock.getBeforeBlockID());
		}
		
		
		return false;
	}


	public static boolean isCompareBlock(Block block) {
		if (block.getGenusName().equals("gettersuper")
				|| block.getGenusName().equals("getterthis")) {
			return false;
		}
		if (block.getGenusName().startsWith("getter")
				|| block.getGenusName().startsWith("setter")
				|| block.getGenusName().startsWith("inc")) {
			return true;
		}
		return false;
	}

	public static boolean isIndependentBlock(Block block) {
		while (!block.getGenusName().equals("procedure")) {
			//次のブロックが存在シない場合は、離れ小島のブロックのためtrue
			if (block.getWorkspace().getEnv().getBlock(block.getBeforeBlockID()) == null) {
				return true;
			}
			block = block.getWorkspace().getEnv().getBlock(block.getBeforeBlockID());
		}
		return false;
	}

	public static boolean isAloneBlock(Block block) {
		if (isIndependentBlock(block)
				&& block.getWorkspace().getEnv().getBlock(block.getAfterBlockID()) == null) {
			return true;
		}
		return false;
	}
}
