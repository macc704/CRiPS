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
		if (isCompareBlock(cmpBlock)
				&& !cmpBlock.getGenusName().contains("private")) {

			if (isIndependentBlock(beforeBlock)) {//離れ小島のブロックの場合はtrueを返す
				return true;
			}

			if (compareBlock.getGenusName().equals("callActionMethod2")
					|| compareBlock.getGenusName().equals("callGetterMethod2")) {//callActionブロックの場合は、参照ブロックはソケットにくっついているので、そちらを参照ブロックに設定する
				compareBlock = Block.getBlock(compareBlock.getSocketAt(0)
						.getBlockID());
				if (compareBlock.getGenusName().contains("private")) {//参照ブロックがprivateだった場合はスコープ問わず
					return true;
				}
			}
			//参照ブロックの名前
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
				RenderableBlock.getRenderableBlock(cmpBlock.getBlockID())
						.setBlockHighlightColor(Color.RED);
				return false;
			}

			originBlock = Block.getBlock(originID);//参照元ブロック

			//自分の所属場所を確認する
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

	private long searchCompareBlockOrigin(Block originBlock,
			String compareBlockName) {
		while (!originBlock.getGenusName().equals("procedure")) {
			if (originBlock.getBlockLabel().equals(compareBlockName)) {//ラベルを確認して、一致するか　一致したらbreak;
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

			if (block.getGenusName().equals("abstraction")) {
				if (confirmCompareBlockIsBelongableAtAbstruction(block,
						compareBlockParent)) {
					return true;
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

	private boolean confirmCompareBlockIsBelongableAtAbstruction(
			Block originBlock, Block compareBlockParent) {
		for (Block block = Block.getBlock(originBlock.getSocketAt(0)
				.getBlockID()); block != null; block = Block.getBlock(block
				.getAfterBlockID())) {
			if (compareBlockParent.getBlockID() == block.getBlockID()) {//
				return true;
			}
			if (block.getGenusName().equals("abstraction")) {
				if (confirmCompareBlockIsBelongableAtAbstruction(block,
						compareBlockParent)) {
					return true;
				}
			}
		}//abstructionブロック内のすべてのブロックを探索し終えた
		return false;
	}

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
			if (prevBlock.getGenusName().equals("procedure")) {
				return prevBlock.getBlockID();
			}
			return -1;
		}

		while (!(checkRBlock.getGenus().equals("procedure"))) {//procedureまで探索
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

		return checkRBlock.getBlockID();
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
