package edu.mit.blocks.codeblocks;

import java.util.ArrayList;
import java.util.Iterator;

import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceEvent;
import edu.mit.blocks.workspace.WorkspaceListener;

public class PolyRule implements LinkRule, WorkspaceListener {
	//
	// @Override
	// public boolean canLink(Block block1, Block block2, BlockConnector
	// socket1,
	// BlockConnector socket2) {
	// //既にブロックがくっついている場合は，結合しない
	// if(("poly".equals(socket1.getKind()) ||
	// "poly".equals(socket2.getKind()))){
	// if("data".equals(block1.getKind())){
	// if("command".equals(block2.getKind()) ||
	// "return".equals(block2.getKind())){
	// return true;
	// }
	// } else if("function".equals(block1.getKind())){
	// if("command".equals(block2.getKind()) ||
	// "return".equals(block2.getKind()) ||
	// "function".equals(block2.getKind())){
	// return true;
	// }
	// } else if("command".equals(block1.getKind()) ||
	// "return".equals(block1.getKind())){
	// if("data".equals(block2.getKind()) ||
	// "function".equals(block2.getKind())){
	// return true;
	// }
	// }else if("procedure".equals(block1.getKind())){
	// if("param".equals(block2.getKind())){
	// return true;
	// }
	// }else if("param".equals(block1.getKind())){
	// if("procedure".equals(block2.getKind())){
	// return true;
	// }
	// }
	// }
	// return false;
	// }
	//
	// @Override
	// public boolean isMandatory() {
	// return false;
	// }
	//
	// @Override
	// public void workspaceEventOccurred(WorkspaceEvent event) {
	// //ohata added
	// if(event.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED &&
	// "poly".equals(event.getSourceLink().getSocket().initKind())){
	// BlockLink link = event.getSourceLink();
	// link.getSocket().setKind("poly");
	//
	// Block block =
	// event.getWorkspace().getEnv().getBlock(link.getSocketBlockID());
	// if(block.getGenusName().equals("callGetterMethod2")){
	// block.getPlug().setKind("poly");
	// }
	// }else if(event.getEventType() == WorkspaceEvent.BLOCKS_CONNECTED){
	// BlockLink link = event.getSourceLink();
	// //ソケットの形を変更
	// BlockConnector socket1 = link.getPlug();
	// BlockConnector socket2 = link.getSocket();
	// socket2.setKind("poly");
	// if("poly".equals(socket1.getKind())){
	// socket1.setKind(socket2.getKind());
	//
	// Block block =
	// event.getWorkspace().getEnv().getBlock(socket2.getBlockID());
	// if(block.getGenusName().equals("callGetterMethod2")){
	// block.getPlug().setKind(socket2.getKind());
	// }
	// }else if("poly".equals(socket2.getKind())){
	// socket2.setKind(socket1.getKind());
	//
	// Block block =
	// event.getWorkspace().getEnv().getBlock(socket1.getBlockID());
	// if(block.getGenusName().equals("callGetterMethod2")){
	// block.getPlug().setKind(socket1.getKind());
	// }
	// }
	// }
	// }
	private ProcedureOutputManager procedureOutputManager;

	public PolyRule(Workspace ws) {
		procedureOutputManager = new ProcedureOutputManager(ws);
	}

	private static boolean isInitPoly(BlockConnector socket) {
		return socket.initKind().contains("poly");
	}

	private static boolean isCurrentlyPoly(BlockConnector socket) {
		return socket.getKind().contains("poly");
	}

	private static boolean isProcRelated(Workspace ws, Long blockID) {
		Block block = ws.getEnv().getBlock(blockID);
		return block.isProcedureDeclBlock() || block.getGenusName().equals("callerprocedure");
	}

	public ProcedureOutputManager getProcedureOutputManager() {
		return procedureOutputManager;
	}

	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {
		boolean isPlug1 = block1.hasPlug() && block1.getPlug() == socket1;
		boolean isPlug2 = block2.hasPlug() && block2.getPlug() == socket2;
		if (socket1.hasBlock() || socket2.hasBlock() || !(isPlug1 ^ isPlug2))
			return false;
		boolean isPoly1 = socket1.getKind().contains("poly");
		boolean isPoly2 = socket2.getKind().contains("poly");
		boolean isList1 = socket1.getKind().contains("list");
		boolean isList2 = socket2.getKind().contains("list");
		boolean inListBlock1 = block1.isListRelated();
		boolean inListBlock2 = block2.isListRelated();
		if (isList1 || isList2)
			return canLinkList(isPoly1, isPoly2, isList1, isList2, inListBlock1, inListBlock2);
		else
			return (isPoly1 || isPoly2) && !socket1.getKind().contains("cmd")
					&& !socket2.getKind().contains("cmd");
	}

	private boolean canLinkList(boolean isPoly1, boolean isPoly2, boolean isList1, boolean isList2,
			boolean inListBlock1, boolean inListBlock2) {
		return isList1 && isList2 && isPoly1 ^ isPoly2 || !isList1 && isPoly1 && !inListBlock1
				|| !isList2 && isPoly2 && !inListBlock2;
	}

	public boolean isMandatory() {
		return false;
	}

	public void workspaceEventOccurred(WorkspaceEvent e) {
		BlockLink link = e.getSourceLink();
		if (e.getEventType() == WorkspaceEvent.BLOCKS_CONNECTED) {
			boolean isPolyPlug = link.getPlug().getKind().contains("poly");
			boolean isPolySocket = link.getSocket().getKind().contains("poly");
			boolean isListPlug = link.getPlug().getKind().contains("list");
			boolean isListSocket = link.getSocket().getKind().contains("list");
			if (isPolyPlug && !isListPlug && (!isPolySocket || isListSocket))
				connectPoly(e.getWorkspace(), link.getSocketBlockID(), link.getSocket());
			else if (isPolySocket && !isListSocket)
				connectPoly(e.getWorkspace(), link.getPlugBlockID(), link.getPlug());
			else if (isPolyPlug && isListPlug && (!isPolySocket || !isListSocket))
				connectPoly(e.getWorkspace(), link.getSocketBlockID(), link.getSocket());
			else if (isPolySocket && isListSocket)
				connectPoly(e.getWorkspace(), link.getPlugBlockID(), link.getPlug());
			else if (link.getSocket().isExpandable())
				setPolyConnectors(e.getWorkspace(), link.getPlug());
		} else if (e.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED) {
			revertPoly(e.getWorkspace().getEnv().getBlock(link.getPlugBlockID()), link.getPlug());
			revertPoly(e.getWorkspace().getEnv().getBlock(link.getSocketBlockID()),
					link.getSocket());
		}
		procedureOutputManager.procedureUpdateInfo(e);
	}

	private static Iterable<BlockConnector> getPolyConnectors(Block b) {
		ArrayList<BlockConnector> polySockets = new ArrayList<BlockConnector>();
		if (b.hasPlug() && isInitPoly(b.getPlug()))
			polySockets.add(b.getPlug());
		Iterator<BlockConnector> i$ = b.getSockets().iterator();
		do {
			if (!i$.hasNext())
				break;
			BlockConnector socket = (BlockConnector) i$.next();
			if (isInitPoly(socket))
				polySockets.add(socket);
		} while (true);
		return polySockets;
	}

	private static void connectPoly(Workspace ws, Long setterBlockID, BlockConnector setterConn) {
		if (setterConn.hasBlock())
			setPolyConnectors(ws, setterConn, setterBlockID);
	}

	private static void setPolyConnectors(Workspace ws, BlockConnector setterConn) {
		if (setterConn.hasBlock()) {
			Block otherBlock = ws.getEnv().getBlock(setterConn.getBlockID());
			Iterator<BlockConnector> i$ = getPolyConnectors(otherBlock).iterator();
			do {
				if (!i$.hasNext())
					break;
				BlockConnector polyConn = (BlockConnector) i$.next();
				if (isCurrentlyPoly(polyConn) && !otherBlock.isProcedureDeclBlock())
					setPolyKind(otherBlock, polyConn, setterConn.getKind());
			} while (true);
			otherBlock.notifyRenderable();
		}
	}

	private static void setPolyConnectors(Workspace ws, BlockConnector setterConn,
			Long setterBlockID) {
		Block otherBlock = ws.getEnv().getBlock(setterConn.getBlockID());
		if (isProcRelated(otherBlock.getWorkspace(), otherBlock.getBlockID())) {
			BlockConnector polyConn = otherBlock.getConnectorTo(setterBlockID);
			setPolyKind(otherBlock, polyConn, setterConn.getKind());
			otherBlock.notifyRenderable();
			if (otherBlock.hasStubs())
				BlockStub.parentConnectorsChanged(ws, otherBlock.getBlockID());
		} else {
			Iterator<BlockConnector> i$ = getPolyConnectors(otherBlock).iterator();
			do {
				if (!i$.hasNext())
					break;
				BlockConnector polyConn = (BlockConnector) i$.next();
				if (isCurrentlyPoly(polyConn)) {
					setPolyKind(otherBlock, polyConn, setterConn.getKind());
					if (polyConn.hasBlock() && !polyConn.getBlockID().equals(setterBlockID))
						setPolyConnectors(ws, polyConn, otherBlock.getBlockID());
				}
			} while (true);
			otherBlock.notifyRenderable();
		}
	}

	private static void setPolyKind(Block polyBlock, BlockConnector polyConn, String newType) {
		String prefix = newType;
		int index = prefix.indexOf("-");
		if (!prefix.contains("inv") && index >= 0)
			prefix = prefix.substring(0, index);
		if (polyConn.getKind().equals("poly")) {
			if (!polyBlock.isListRelated())
				polyConn.setKind(newType);
			else
				polyConn.setKind(prefix);
		} else if (polyConn.getKind().startsWith("poly-")) {
			prefix = (new StringBuilder()).append(prefix).append(polyConn.getKind().substring(4))
					.toString();
			polyConn.setKind(prefix);
		}
	}

	private static boolean canRevertPolyConnectors(Workspace ws, Block b, Long prevBlock) {
		if (isProcRelated(ws, b.getBlockID()))
			return true;
		for (Iterator<BlockConnector> i$ = getPolyConnectors(b).iterator(); i$.hasNext();) {
			BlockConnector polyConn = (BlockConnector) i$.next();
			if (polyConn.hasBlock() && polyConn.getBlockID() != prevBlock) {
				Block otherBlock = ws.getEnv().getBlock(polyConn.getBlockID());
				if (!isInitPoly(otherBlock.getConnectorTo(b.getBlockID()))
						|| !canRevertPolyConnectors(ws, otherBlock, b.getBlockID()))
					return false;
			}
		}

		return true;
	}

	private static void revertPoly(Block polyBlock, BlockConnector polyConn) {
		if (!isInitPoly(polyConn)
				|| !canRevertPolyConnectors(polyBlock.getWorkspace(), polyBlock, Block.NULL))
			return;
		polyConn.setKind(polyConn.initKind());
		if (isProcRelated(polyBlock.getWorkspace(), polyBlock.getBlockID())) {
			polyBlock.notifyRenderable();
			if (polyBlock.hasStubs())
				BlockStub.parentConnectorsChanged(polyBlock.getWorkspace(), polyBlock.getBlockID());
		} else {
			Iterator<BlockConnector> i$ = getPolyConnectors(polyBlock).iterator();
			do {
				if (!i$.hasNext())
					break;
				BlockConnector nextConn = (BlockConnector) i$.next();
				revertPolyKind(nextConn, polyConn);
				if (nextConn.hasBlock())
					revertPolyConnectors(polyBlock.getWorkspace(), nextConn, polyBlock.getBlockID());
			} while (true);
			polyBlock.notifyRenderable();
		}
	}

	private static void revertPolyConnectors(Workspace ws, BlockConnector setterConn,
			Long setterBlockID) {
		Block otherBlock = ws.getEnv().getBlock(setterConn.getBlockID());
		if (isProcRelated(ws, otherBlock.getBlockID())) {
			BlockConnector polyConn = otherBlock.getConnectorTo(setterBlockID);
			revertPolyKind(polyConn, setterConn);
			otherBlock.notifyRenderable();
			if (otherBlock.hasStubs())
				BlockStub.parentConnectorsChanged(ws, otherBlock.getBlockID());
		} else {
			Iterator<BlockConnector> i$ = getPolyConnectors(otherBlock).iterator();
			do {
				if (!i$.hasNext())
					break;
				BlockConnector polyConn = (BlockConnector) i$.next();
				revertPolyKind(polyConn, setterConn);
				if (polyConn.hasBlock() && !polyConn.getBlockID().equals(setterBlockID))
					revertPolyConnectors(ws, polyConn, otherBlock.getBlockID());
			} while (true);
		}
		otherBlock.notifyRenderable();
	}

	private static void revertPolyKind(BlockConnector polyConn, BlockConnector setterConn) {
		if (!isInitPoly(polyConn))
			return;
		if (polyConn.initKind().equals(setterConn.initKind()))
			polyConn.setKind(setterConn.getKind());
		else if (polyConn.getKind().contains("list"))
			polyConn.setKind("poly-list");
		else
			polyConn.setKind("poly");
	}

}
