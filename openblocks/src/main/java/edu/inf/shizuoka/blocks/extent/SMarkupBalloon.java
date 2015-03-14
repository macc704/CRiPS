package edu.inf.shizuoka.blocks.extent;

import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Page;



public class SMarkupBalloon extends JLabel implements MouseListener{

	/**
	 *
	 */
	private static final long serialVersionUID = 1846866621402166198L;

	private RenderableBlock parent;
	private ImageIcon icon = new ImageIcon("../src/main/resources/edu/mit/blocks/codeblocks/balloon.png");
	private static int balloonWidth = 60;
	private static int balloonHeight = 45;

	public SMarkupBalloon(RenderableBlock rb){
		super();
		Image img = icon.getImage().getScaledInstance(balloonWidth, balloonHeight, Image.SCALE_SMOOTH);
		setIcon(new ImageIcon(img));
		parent = rb;
		setBounds(rb.getX(), rb.getY(), balloonWidth, balloonHeight);

		setVisible(false);

		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		//コンテキストメニュー表示
		parent.addPopupMenu(e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
		myHide(getX() + e.getX(), getY() + e.getY());
	}

	public void show(int x, int y){
		if(!isVisible()){
			setBounds(x, y, balloonWidth, balloonHeight);
			setVisible(true);
			if(parent.getParentWidget() instanceof Page){
				Page page = (Page)parent.getParentWidget();
				page.addArrow(this);
			}

			repaint();
		}
	}

	public void myHide(int x, int y){
		if(isVisible() && !contains(SwingUtilities.convertPoint(getParent(), new Point(x, y), this))){
			setVisible(false);
			parent.getParentWidget().getJComponent().remove(this);
			repaint();
		}
	}

}
