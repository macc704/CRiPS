package drawingobjects;

import java.util.ArrayList;
import java.util.List;

import renderable.RenderableBlock;

public class DrawingArrowManager {
	
	private List<RenderableBlock> arrowPossesser = new ArrayList<RenderableBlock>();
	
	public void addPossesser(RenderableBlock possesser){
		arrowPossesser.add(possesser);
	}
	
	public void clearPossessers(){
		for(RenderableBlock block : arrowPossesser){
			block.clearArrows();
		}
		
		arrowPossesser.clear();
	}
	
	public void repaintArrows(){
		for(RenderableBlock rb: arrowPossesser){
			rb.redrawArrows();
		}
	}

}
