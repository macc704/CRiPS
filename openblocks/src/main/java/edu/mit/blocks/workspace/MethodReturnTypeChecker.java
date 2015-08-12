package edu.mit.blocks.workspace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.FactoryRenderableBlock;

public class MethodReturnTypeChecker implements WorkspaceListener {

	private Map<Long, String> methodReturnTypes = new HashMap<>(); //キーがメソッドのid, 値が返り値
	private Map<Long, Long> returnParent = new HashMap<>();//キー returnのid, 値 所属するメソッドブロックのid
	private Map<Long, HashSet<Long>> methodsReturnBlocks = new HashMap<>();//キー メソッドブロックのid, 値 returnのid

	public MethodReturnTypeChecker(){

	}

	public void workspaceEventOccurred(WorkspaceEvent event) {
		Block sourceBlock;
		if(event.getEventType() == WorkspaceEvent.BLOCK_ADDED && !(event.getWorkspace().getEnv().getRenderableBlock(event.getSourceBlockID()) instanceof FactoryRenderableBlock)){
			sourceBlock = event.getWorkspace().getEnv().getBlock(event.getSourceBlockID());
			if("procedure".equals(sourceBlock.getGenusName())){
				methodReturnTypes.put(sourceBlock.getBlockID(), "void");//一時的にvoid型で登録
				methodsReturnBlocks.put(sourceBlock.getBlockID(), new HashSet<>());
			}else if("return".equals(sourceBlock.getGenusName())){
				Block topBlock = Block.getTopBlock(sourceBlock);
				returnParent.put(sourceBlock.getBlockID(), topBlock.getBlockID());
			}
		}else if(event.getEventType() == WorkspaceEvent.BLOCKS_CONNECTED){
			//メソッドのもつreturnBlockを更新する
			for(Long id : returnParent.keySet()){
				Block newParent = Block.getTopBlock(getBlock(event.getWorkspace(), id));
				if(!newParent.getBlockID().equals(returnParent.get(id))){
					returnParent.put(id, newParent.getBlockID());
					if(newParent.getGenusName().equals("procedure")){
						methodsReturnBlocks.get(newParent.getBlockID()).add(id);
					}
				}
			}

			//全メソッドの返り値を再計算する
			updateReturnTypes(event.getWorkspace());

		}else if(event.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED){
			//メソッドの持つreturnblockを更新する
			for(Long id : returnParent.keySet()){
				Block newParent = Block.getTopBlock(getBlock(event.getWorkspace(), id));
				//親が変更したreturnBlockの親を再登録する
				if(!newParent.getBlockID().equals(returnParent.get(id))){
					Block oldParent = getBlock(event.getWorkspace(), returnParent.get(id));
					if(oldParent.getGenusName().equals("procedure")){
						methodsReturnBlocks.get(oldParent.getBlockID()).remove(id);
					}

					returnParent.put(id, newParent.getBlockID());
				}
			}

			updateReturnTypes(event.getWorkspace());

		}else if(event.getEventType() == WorkspaceEvent.BLOCK_REMOVED){
			sourceBlock = event.getWorkspace().getEnv().getBlock(event.getSourceBlockID());
			if("return".equals(sourceBlock.getGenusName())){
				returnParent.remove(sourceBlock.getBlockID());
			}

			if("procedure".equals(sourceBlock.getGenusName())){
				methodReturnTypes.remove(sourceBlock.getBlockID());
				methodsReturnBlocks.remove(sourceBlock.getBlockID());
			}
		}else if(event.getEventType() == WorkspaceEvent.WORKSPACE_FINISHED_LOADING){
			for(Long id : returnParent.keySet()){
				Block topBlock = getBlock(event.getWorkspace(), returnParent.get(id));

				if(methodsReturnBlocks.get(topBlock.getBlockID()) != null){
					methodsReturnBlocks.get(topBlock.getBlockID()).add(id);
				}
			}

			updateReturnTypes(event.getWorkspace());

		}

	}

	public String calcReturnType(HashSet<Long> set, Workspace ws){
		String returnType ="void";

		for(Long id : set){
			Block returnBlock = ws.getEnv().getBlock(id);
			String socketType = getSocketType(ws, returnBlock);
			if(!"void".equals(socketType)){
				returnType = socketType;
			}
		}

		return returnType;
	}

	public void updateReturnTypes(Workspace ws){
		//返り値を更新する
		for(Long id : methodsReturnBlocks.keySet()){
			HashSet<Long> set = methodsReturnBlocks.get(id);
			//返り値再計算
			methodReturnTypes.put(id, calcReturnType(set, ws));
			getBlock(ws, id).setReturnType(methodReturnTypes.get(id));
		}
	}


	private Block getBlock(Workspace ws, Long id){
		return ws.getEnv().getBlock(id);
	}


	public String getSocketType(Workspace ws, Block block){
		String returnType = "void";

		Block socketBlock =ws.getEnv().getBlock(block.getSocketAt(0).getBlockID());

		if(socketBlock != null){
			returnType = socketBlock.getType();
		}

		return returnType;
	}

	public void reset(){
		methodReturnTypes.clear();
		returnParent.clear();
		methodsReturnBlocks.clear();
	}

}
