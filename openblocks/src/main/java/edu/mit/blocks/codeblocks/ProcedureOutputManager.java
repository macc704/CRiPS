package edu.mit.blocks.codeblocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceEvent;
import edu.mit.blocks.workspace.WorkspaceWidget;

public class ProcedureOutputManager {
    private static class OutputInfo
    {

        private final List outputs;
        private String type;
        private int numTyped;

        private OutputInfo()
        {
            outputs = new ArrayList(5);
            type = null;
            numTyped = 0;
        }
    }



    public ProcedureOutputManager(Workspace wworkspace)
    {
        workspace = wworkspace;
    }


    public void procedureUpdateInfo(WorkspaceEvent event)
    {
        Block b = getBlock(event.getWorkspace(), event.getSourceBlockID());
        BlockLink link = event.getSourceLink();
        switch(event.getEventType())
        {
        case WorkspaceEvent.BLOCKS_CONNECTED: // '\005'
            if(link != null)
                blocksConnected(workspace, event.getSourceWidget(), link.getSocketBlockID(), link.getPlugBlockID());
            return;

        case WorkspaceEvent.BLOCKS_DISCONNECTED: // '\006'
            if(link != null)
                blocksDisconnected(workspace,  event.getSourceWidget(), link.getSocketBlockID(), link.getPlugBlockID());
            return;

        case WorkspaceEvent.BLOCK_ADDED: // '\003'
            if(b != null && b.isProcedureDeclBlock())
                myProcInfo.put(b.getBlockID(), new OutputInfo());
            return;

        case WorkspaceEvent.BLOCK_REMOVED: // '\004'
            if(b != null && b.isProcedureDeclBlock())
            {
                myProcInfo.remove(b.getBlockID());
                if(link != null)
                    blocksDisconnected(workspace, event.getSourceWidget(), link.getSocketBlockID(), link.getPlugBlockID());
            }
            return;
        }
    }

    public void finishLoad()
    {
        Iterator i;
        Block p;
        for(i = workspace.getBlocksFromGenus("procedure").iterator(); i.hasNext(); myProcInfo.put(p.getBlockID(), new OutputInfo()))
            p = (Block)i.next();

        i = workspace.getBlocksFromGenus("output").iterator();
        do
        {
            if(!i.hasNext())
                break;
            Block b = (Block)i.next();
            Long top = SLBlockProperties.getTopBlockID(workspace, b.getBlockID());

            if(top != null && workspace.getEnv().getBlock(top).isProcedureDeclBlock())
            {
                OutputInfo info = (OutputInfo)myProcInfo.get(top);
                if(!info.outputs.contains(b))
                {
                    if(info.type == null && !b.getSocketAt(0).getKind().equals("poly"))
                    {
                        info.type = b.getSocketAt(0).getKind();
                        BlockStub.parentPlugChanged(workspace, top, info.type);
                    }
                    if(b.getSocketAt(0).getBlockID().longValue() != -1L)
                        info.numTyped++;
                    info.outputs.add(b.getBlockID());
                }
            }
        } while(true);
    }

    public void reset()
    {
        myProcInfo.clear();
    }

    private void blocksConnected(Workspace ws, WorkspaceWidget w, Long socket, Long plug)
    {
        Long top = SLBlockProperties.getTopBlockID(ws, socket);
        if(top == null || !ws.getEnv().getBlock(top).isProcedureDeclBlock())
            return;
        OutputInfo info = (OutputInfo)myProcInfo.get(top);
        List events = new ArrayList();
        Block b = ws.getEnv().getBlock(socket);
        boolean add = true;
        if(isOutput(b))
            add = false;
        else
            b = ws.getEnv().getBlock(plug);
        if(info.type != null)
        {
            changeType(add, b, info.type, info, w, events);
        } else
        {
            examineType(add, b, info);
            if(info.type != null)
            {
                changeType(ws, info, w, events);
                BlockStub.parentPlugChanged(ws, top, info.type);
            }
        }
//        if(!events.isEmpty())
//            workspace.notifyListeners(events);
    }

	public boolean canLinkReturnBlock(Workspace ws, WorkspaceWidget w, Long block, Long socket){
		Long top = SLBlockProperties.getTopBlockID(ws, socket);

		if(top == null || !ws.getEnv().getBlock(top).isProcedureDeclBlock()){
			return true;
		}

        OutputInfo info = (OutputInfo)myProcInfo.get(top);
        Block b = ws.getEnv().getBlock(block);

        return canLink(b, info.type, info, w);
	}

	private boolean canLink(Block b, String type, OutputInfo info, WorkspaceWidget w){
		boolean canLink = true;
        //引数で渡されたブロックが結合可能かチェック
		if(isOutput(b)){
        	if(b.getSocketAt(0).getKind().equals("poly") || b.getSocketAt(0).getKind().equals(type) || type == null){
        		return true;
        	}else{
        		return false;
        	}
        }

        //ソケットのブロックが結合可能かチェック
        Iterator<BlockConnector> i = b.getSockets().iterator();
        do{
            if(!i.hasNext())
                break;
            BlockConnector conn = (BlockConnector)i.next();
            Block b2 = getBlock(b.getWorkspace(), conn.getBlockID());
            if(b2 != null){
            	canLink  &= canLink(b2, type, info, w);
            }
        } while(true);

        //次のブロックが結合可能かチェック
        Block b2 = getBlock(b.getWorkspace(), b.getAfterBlockID());
        if(b2 != null)
            canLink &= canLink(b2, type, info, w);

		return canLink;
	}

    private void blocksDisconnected(Workspace ws, WorkspaceWidget w, Long socket, Long plug)
    {
        Long top = SLBlockProperties.getTopBlockID(ws, socket);
        if(top == null || !ws.getEnv().getBlock(top).isProcedureDeclBlock())
            return;
        OutputInfo info = (OutputInfo)myProcInfo.get(top);
        if(isOutput(ws.getEnv().getBlock(socket)) && info.type != null)
        {
            info.numTyped--;
            ws.getEnv().getBlock(socket).getSocketAt(0).setKind(info.type);
            ws.getEnv().getBlock(socket).notifyRenderable();
        } else
        {
            revertType(ws.getEnv().getBlock(plug), info, true);
        }
        if(info.numTyped == 0)
        {
            info.type = null;
            revertType(ws.getEnv().getBlock(top), info, false);
            BlockStub.parentPlugChanged(ws, top, null);
        }
        System.out.println();
    }

    private static void examineType(boolean add, Block b, OutputInfo info)
    {
        if(isOutput(b))
        {
            if(add)
                info.outputs.add(b.getBlockID());
            BlockConnector socket = b.getSocketAt(0);
            Block b2 = getBlock(b.getWorkspace(), socket.getBlockID());
            if(b2 != null)
            {
                info.numTyped++;
                if(info.type == null)
                    info.type = socket.getKind();
            } else
            if(!socket.getKind().equals(socket.initKind()))
            {
                socket.setKind(socket.initKind());
                b.notifyRenderable();
            }
            return;
        }
        Iterator i = b.getSockets().iterator();
        do
        {
            if(!i.hasNext())
                break;
            BlockConnector conn = (BlockConnector)i.next();
            Block b2 = getBlock(b.getWorkspace(), conn.getBlockID());
            if(b2 != null)
                examineType(true, b2, info);
        } while(true);
        Block b2 = getBlock(b.getWorkspace(), b.getAfterBlockID());
        if(b2 != null)
            examineType(true, b2, info);
    }

    private static void changeType(Workspace ws, OutputInfo info, WorkspaceWidget w, List<WorkspaceEvent> e)
    {
        String type = info.type;
        Iterator i = info.outputs.iterator();
        do
        {
            if(!i.hasNext())
                break;
            Long id = (Long)i.next();
            Block b = ws.getEnv().getBlock(id);
            BlockConnector socket = b.getSocketAt(0);
            Block b2 = getBlock(ws, socket.getBlockID());
            if(b2 == null && !socket.getKind().equals(type))
            {
                socket.setKind(type);
                b.notifyRenderable();
            } else
            if(!socket.getKind().endsWith(type))
            {
                BlockLink link = BlockLink.getBlockLink(ws, b, b2, socket, b2.getPlug());
                link.disconnect();
                ws.getEnv().getRenderableBlock(id).blockDisconnected(socket);
                e.add(new WorkspaceEvent(ws, w, link, WorkspaceEvent.BLOCKS_DISCONNECTED));
            }
        } while(true);
    }

    private static void changeType(boolean add, Block b, String type, OutputInfo info, WorkspaceWidget w, List<WorkspaceEvent> e)
    {
        if(isOutput(b))
        {
            if(add)
                info.outputs.add(b.getBlockID());
            BlockConnector socket = b.getSocketAt(0);
            Block b2 = getBlock(b.getWorkspace(), socket.getBlockID());
            if(b2 == null && !socket.getKind().equals(type))
            {
                socket.setKind(type);
                b.notifyRenderable();
            } else
            if(!socket.getKind().endsWith(type))
            {
                info.numTyped++;
                BlockLink link = BlockLink.getBlockLink(b.getWorkspace(), b, b2, socket, b2.getPlug());
                link.disconnect();
                b.getWorkspace().getEnv().getRenderableBlock(b.getBlockID()).blockDisconnected(socket);
                e.add(new WorkspaceEvent(b.getWorkspace(), w, link, WorkspaceEvent.BLOCKS_DISCONNECTED));
            } else
            {
                info.numTyped++;
            }
            return;
        }
        Iterator i = b.getSockets().iterator();
        do
        {
            if(!i.hasNext())
                break;
            BlockConnector conn = (BlockConnector)i.next();
            Block b2 = getBlock(b.getWorkspace(), conn.getBlockID());
            if(b2 != null)
                changeType(true, b2, type, info, w, e);
        } while(true);
        Block b2 = getBlock(b.getWorkspace(), b.getAfterBlockID());
        if(b2 != null)
            changeType(true, b2, type, info, w, e);
    }

    private static void revertType(Block b, OutputInfo info, boolean remove)
    {
        if(isOutput(b))
        {
            if(remove)
                info.outputs.remove(b.getBlockID());
            BlockConnector socket = b.getSocketAt(0);
            Block b2 = getBlock(b.getWorkspace(), socket.getBlockID());
            if(b2 == null && !socket.getKind().equals(socket.initKind()))
            {
                socket.setKind(socket.initKind());
                b.notifyRenderable();
            } else
            if(b2 != null && remove)
                info.numTyped--;
            return;
        }
        Iterator i = b.getSockets().iterator();
        do
        {
            if(!i.hasNext())
                break;
            BlockConnector conn = (BlockConnector)i.next();
            Block b2 = getBlock(b.getWorkspace(), conn.getBlockID());
            if(b2 != null)
                revertType(b2, info, remove);
        } while(true);
        Block b2 = getBlock(b.getWorkspace(), b.getAfterBlockID());
        if(b2 != null)
            revertType(b2, info, remove);
    }

    private static Block getBlock(Workspace ws, Long id)
    {
        if(id == null || id.equals(Block.NULL))
            return null;
        else
            return ws.getEnv().getBlock(id);
    }

    private static boolean isOutput(Block b)
    {
        return SLBlockProperties.isCmd("output", b);
    }

    private  Workspace workspace;
    private static final int DEFAULT_SIZE = 5;
    private  final Map<Long, OutputInfo> myProcInfo = new HashMap<Long, OutputInfo>();

}
