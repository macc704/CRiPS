package renderable;

import java.awt.Color;

import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.BlockLinkChecker;

public class ScopeChecker {

	public boolean checkScope(Block beforeBlock, Block cmpBlock) {
		//callActionの場合は、参照元をソケットが持ってるので、そのための処理が必要
		Block originBlock = beforeBlock;//結合する場所の直前のブロック

		String compareBlockName;
		Block compareBlock = cmpBlock;
		//参照ブロック、private変数ブロックでない場合はスコープを確認
		if (isCompareBlock(cmpBlock)
				&& !cmpBlock.getGenusName().contains("private")) {
			//直前のブロックがプラグを持っている場合、そちらが直前のブロックになる
			if (Block.getBlock(beforeBlock.getPlugBlockID()) != null) {
				beforeBlock = purcePlugBlock(beforeBlock);
				originBlock = beforeBlock;
			}

			//離れ小島のブロックの場合はtrueを返す
			if (isIndependentBlock(beforeBlock)) {
				return true;
			}

			//callActionブロックの場合は、参照ブロックはソケットにくっついているので、そちらを参照ブロックに設定する
			if (compareBlock.getGenusName().equals("callActionMethod2")
					|| compareBlock.getGenusName().equals("callGetterMethod2")) {
				compareBlock = Block.getBlock(compareBlock.getSocketAt(0)
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

			if (originBlock == null) {//探索対象のブロックが存在しない
				RenderableBlock.getRenderableBlock(cmpBlock.getBlockID())
						.setBlockHighlightColor(Color.RED);
				return false;
			}

			//参照元を探索する
			long originID = searchCompareBlockOrigin(originBlock,
					compareBlockName);

			if (originID == -1) {//参照元の探索に失敗
				originBlock = cmpBlock;
				originBlock = purcePlugBlock(originBlock);
				while (Block.getBlock(originBlock.getAfterBlockID()) != null) {
					originBlock = Block.getBlock(originBlock.getAfterBlockID());
				}
				//自分の持ってるブロックの中を探索
				originID = searchCompareBlockOrigin(originBlock,
						compareBlockName);
				//失敗
				if (originID == -1) {
					RenderableBlock.getRenderableBlock(cmpBlock.getBlockID())
							.setBlockHighlightColor(Color.RED);
					return false;
				}
				beforeBlock = originBlock;
			}

			originBlock = Block.getBlock(originID);//参照元ブロック
			//スコープチェックするブロックの所属する親を探す
			Block compareBlockParent = Block
					.getBlock(searchParentBlockID(RenderableBlock
							.getRenderableBlock(beforeBlock.getBlockID())));

			if (compareBlockParent == null) {//探索失敗
				RenderableBlock.getRenderableBlock(cmpBlock.getBlockID())
						.setBlockHighlightColor(Color.RED);
				return false;
			}

			//所属できる場所かどうか確認する
			if (confirmCompareBlockIsBelongable(originBlock, compareBlockParent)) {
				return true;
			}

			RenderableBlock.getRenderableBlock(cmpBlock.getBlockID())
					.setBlockHighlightColor(Color.RED);

			return false;
		} else {//参照ブロックでなかった場合、private変数の参照ブロックで会った場合は結合許可
			return true;
		}
	}

	private Block purcePlugBlock(Block block) {

		while (Block.getBlock(block.getPlugBlockID()) != null) {
			block = Block.getBlock(block.getPlugBlockID());
		}
		return block;
	}

	private long searchCompareBlockOrigin(Block originBlock,
			String compareBlockName) {
		while (!originBlock.getGenusName().equals("procedure")) {
			if (originBlock.getBlockLabel().equals(compareBlockName)) {//ラベルを確認して、一致するか　
				return originBlock.getBlockID();
			}

			if (originBlock.getGenusName().equals("abstraction")) {
				long result = searchCompareOriginBlockInAbstructionBlock(
						Block.getBlock(originBlock.getSocketAt(0).getBlockID()),
						compareBlockName);
				if (result != -1) {
					return result;
				}
			}

			originBlock = Block.getBlock(originBlock.getBeforeBlockID());//ブロック更新

			if (originBlock == null) {//終了条件
				return -1;
			}
		}
		//procedureまで辿り着いたら、引数ブロックをチェック　
		for (BlockConnector socket : BlockLinkChecker
				.getSocketEquivalents(originBlock)) {
			if (Block.getBlock(socket.getBlockID()) == null) {
				break;
			}

			if (Block.getBlock(socket.getBlockID()).getBlockLabel()
					.equals(compareBlockName)) {//ラベルを確認して、一致するか　一致したらbreak;
				return originBlock.getBlockID();//便宜上proceureブロックを返す
			}
		}
		return -1;//procedureまで辿り着いたら、参照元の探索に失敗
	}

	private long searchCompareOriginBlockInAbstructionBlock(Block start,
			String compareBlockName) {
		if (start == null) {
			return -1;
		}
		while (!start.getBlockLabel().equals(compareBlockName)) {
			start = Block.getBlock(start.getAfterBlockID());
			if (start == null) {
				return -1;
			}
		}
		return start.getBlockID();
	}

	//参照ブロックが所属できるかどうか確認する. 　参照元のブロックの所属する親から下をたどっていって、スコープが会っているかどうか確認する
	private boolean confirmCompareBlockIsBelongable(Block originBlock,
			Block compareBlockParent) {
		//親から下をたどっていく
		if (originBlock == null) {
			return false;
		}
		for (Block block = Block.getBlock(searchParentBlockID(RenderableBlock
				.getRenderableBlock(originBlock.getBlockID()))); block != null; block = Block
				.getBlock(block.getAfterBlockID())) {
			if (compareBlockParent.getBlockID() == block.getBlockID()) {
				return true;
			}
			if (block.hasStubs()) {
				if (searchStubs(block, compareBlockParent.getBlockID())) {
					return true;
				}
			}
			//ブロックがソケットを持っていた場合は、そのソケット内を確認する。
			if (block.getSockets() != null) {//ソケットにブロックを持っている
				for (BlockConnector socket : block.getSockets()) {
					if (confirmCompareBlockIsBelongable(
							Block.getBlock(socket.getBlockID()),
							compareBlockParent)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean searchStubs(Block block, long compareBlockParentID) {
		for (BlockConnector socket : block.getSockets()) {
			if (socket.getBlockID() == compareBlockParentID) {
				return true;
			}
		}
		return false;
	}

	//ブロックが所属できるかどうか、ソケット内を探索する。
	/*	private boolean confirmCompareBlockIsBelongableAtSockets(Block originBlock,
				Block compareBlockParent) {
			for (BlockConnector socket : BlockLinkChecker
					.getSocketEquivalents(originBlock)) {
				for (Block block = Block.getBlock(socket.getBlockID()); block != null; block = Block
						.getBlock(block.getAfterBlockID())) {
					if (compareBlockParent.getBlockID() == block.getBlockID()) {//
						return true;
					}
					if (block.getSockets() != null) {//ソケットにブロックを持っている
						if (confirmCompareBlockIsBelongableAtSockets(block,
								compareBlockParent)) {
							return true;
						}
					}
				}//abstructionブロック内のすべてのブロックを探索し終えた			
			}
			return false;
		}
	*/
	//親のブロックを探索する
	private long searchParentBlockID(RenderableBlock prevRBlock) {
		//最寄りのprocedure,abstructionブロックを探索
		if (prevRBlock == null
				|| prevRBlock.getBlock().getAfterBlockID() == null) {
			return -1;
		}

		RenderableBlock checkRBlock = RenderableBlock
				.getRenderableBlock(prevRBlock.getBlock().getBeforeBlockID());
		Block prevBlock = Block.getBlock(prevRBlock.getBlockID());//直前のブロックをとっとく

		if (checkRBlock == null) {
			//if (prevBlock.getGenusName().equals("procedure")) {
			return prevBlock.getBlockID();
			//}
			//return -1;
		}

		while (Block.getBlock(checkRBlock.getBlock().getBeforeBlockID()) != null) {//直前ブロックがnullになるまで
			//abstructionの場合、親かもしれないのでチェック
			if (checkRBlock.getGenus().equals("abstraction")) {
				if (Block.getBlock(checkRBlock.getBlockID()).getSocketAt(0)
						.getBlockID().equals(prevBlock.getBlockID())) {//ソケットが直前のブロックと一致した場合、
					return prevRBlock.getBlockID();
				}
			}
			//prevBlockの更新
			prevBlock = checkRBlock.getBlock();
			//checkRBlockの更新
			checkRBlock = RenderableBlock.getRenderableBlock(Block.getBlock(
					checkRBlock.getBlockID()).getBeforeBlockID());
		}
		return prevRBlock.getBlockID();
	}

	public static boolean isCompareBlock(Block block) {
		if (block.getGenusName().startsWith("getter")
				|| block.getGenusName().startsWith("setter")
				|| block.getGenusName().startsWith("inc")
				|| block.getGenusName().equals("callActionMethod2")
				|| block.getGenusName().equals("callGetterMethod2")) {
			return true;
		}
		return false;
	}

	public static boolean isIndependentBlock(Block block) {
		while (!block.getGenusName().equals("procedure")) {
			//次のブロックが存在シない場合は、離れ小島のブロックのためtrue
			if (Block.getBlock(block.getBeforeBlockID()) == null) {
				return true;
			}
			block = Block.getBlock(block.getBeforeBlockID());
		}
		return false;
	}

	public static boolean isAloneBlock(Block block) {
		if (isIndependentBlock(block)
				&& Block.getBlock(block.getAfterBlockID()) == null) {
			return true;
		}
		return false;
	}
}
