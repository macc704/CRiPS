package workspace;

import java.awt.Color;

import renderable.RenderableBlock;

/**
 * An Immuateble class identifying a subset's properties and blocks
 * @author An Ho
 */
public class Subset {
	private String name;
	private Color color;
	private Iterable<RenderableBlock> blocks;
	public Subset(String name, Color color, Iterable<RenderableBlock> blocks){
		this.name = name;
		this.color=color;
		this.blocks = blocks;
	}
	public String getName() {
		return name;
	}
	public Color getColor() {
		return color;
	}
	public Iterable<RenderableBlock> getBlocks() {
		return blocks;
	}
}
