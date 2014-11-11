package edu.inf.shizuoka.drawingobjects;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import edu.mit.blocks.controller.WorkspaceController;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;



public class DrawingArrowManager {
	
	private static List<RenderableBlock> arrowPossesser = new ArrayList<RenderableBlock>();
	
	
	public static void addPossesser(RenderableBlock possesser){
		arrowPossesser.add(possesser);
	}
	
	public static void clearPossessers(){
		for(RenderableBlock block : arrowPossesser){
			block.clearArrows();
		}
		arrowPossesser.clear();
	}
	
	public static void clearPosesser(RenderableBlock posesser){
		posesser.clearArrows();
		arrowPossesser.remove(posesser);
	}
	
	
	public static void setVisible(boolean active, Workspace workspace){
		if(active){
			
			WorkspaceController.showAllTraceLine(workspace);
		}else{
			WorkspaceController.disposeTraceLine(workspace);
		}
	}
	
	public static void resetArrowsPosition(){
		for(RenderableBlock rb: arrowPossesser){
			rb.resetArrowPosition();
		}
	}
	
	public static void thinArrows(RenderableBlock rBlock, int concentration){
		Point p = new Point(rBlock.getLocation());
		
		p.x += rBlock.getWidth();
		p.y += rBlock.getHeight() / 2;
		
		for (ArrowObject endArrow : rBlock.getEndArrows()) {
			endArrow.setStartPoint(p);
			endArrow.setColor(new Color(255, 0, 0, concentration));
		}
	}
	
	
	public static Point calcCallerBlockPoint(RenderableBlock callerblock){
		Point p1 = new Point(callerblock.getLocation());
		p1.x += callerblock.getWidth();
		p1.y += callerblock.getHeight()/2;
		
		return p1;
		
	}
	
	public static Point calcDefinisionBlockPoint(RenderableBlock parentBlock){
		Point p2 = new Point(parentBlock.getLocation());
		p2.y +=  parentBlock.getHeight()/2;
		
		return p2;
	}
}
