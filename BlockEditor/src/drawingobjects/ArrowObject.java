package drawingobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import renderable.RenderableBlock;
import workspace.Workspace;



public class ArrowObject extends JComponent {

	private static final long serialVersionUID = -1745361279120477995L;
	private RenderableBlock caller;
	private RenderableBlock callee;
	
	private static Color thinColor = new Color(255,255,0,30);
	private static Color normalColor = new Color(255,255,0, 255);
	private Color arrowColor = normalColor;
	
	private boolean collapsed = false;
	
	private Component oldParent;
	
	public ArrowObject(RenderableBlock caller, RenderableBlock callee, boolean isShow) {
		setBounds(Workspace.getInstance().getBlockCanvas().getCanvas().getBounds());
		this.caller = caller;
		this.callee = callee;
		this.collapsed = !isShow;		
		setDoubleBuffered(true);
		if(DrawingArrowManager.isActive()){
			setVisible(isShow);	
		}else{
			setVisible(false);
		}
		
		oldParent = caller.getParent();
	}

	public Point getStartPoint(){
		return caller.getLocation();
	}
	
	public Point getEndPoint(){
		return caller.getLocation();
	}
	
	
	public Color getArrowColor(){
		return this.arrowColor;
	}
	
	public void chengeColor(boolean isThin){
		if(isThin){
			arrowColor = thinColor;
		}else{
			arrowColor = normalColor;
		}
		
	}

	public void paint(Graphics g) {
		super.paint(g);
		if(isVisible()){
			drawArrow((Graphics2D) g);			
		}
	}


	public void drawArrow(Graphics2D graphic) {
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		BasicStroke stroke = new BasicStroke(3.0f);
		graphic.setStroke(stroke);


		graphic.setColor(arrowColor);
		// arrowLengthの変更（ベクトルの向きに応じて変更）
		
		if(oldParent != callee.getParent()){
			Point p = SwingUtilities.convertPoint(callee.getParent(), getCalleePoint(), oldParent);

			ArrowPointCalcUtil util = new ArrowPointCalcUtil(getCallerPoint(), p);
			Point2D p2 = util.getPoint2();
			Point2D p3 = util.getPoint3();
			
			int dx = getMiddleDx(getCallerPoint(), p);
			int dy = getMiddleDy(getCallerPoint(), p);

			graphic.drawLine(p.x, p.y, getCallerPoint().x, getCallerPoint().y);
			graphic.drawLine(p.x + dx,  p.y + dy , (int) p2.getX() + dx,(int) p2.getY() + dy);
			graphic.drawLine(p.x + dx,  p.y + dy , (int) p3.getX() + dx,(int) p3.getY() + dy);
		}else if(oldParent != caller.getParent()){
			Point p = SwingUtilities.convertPoint(caller.getParent(), getCallerPoint(), oldParent);

			ArrowPointCalcUtil util = new ArrowPointCalcUtil(p, getCalleePoint());
			Point2D p2 = util.getPoint2();
			Point2D p3 = util.getPoint3();
			
			int dx = getMiddleDx(p, getCalleePoint());
			int dy = getMiddleDy(p, getCalleePoint());

			graphic.drawLine(getCalleePoint().x, getCalleePoint().y, p.x, p.y);
			graphic.drawLine(getCalleePoint().x + dx,  getCalleePoint().y + dy , (int) p2.getX() + dx,(int) p2.getY() + dy);
			graphic.drawLine(getCalleePoint().x + dx,  getCalleePoint().y + dy , (int) p3.getX() + dx,(int) p3.getY() + dy);
		}else{
			ArrowPointCalcUtil util = new ArrowPointCalcUtil(getCallerPoint(), getCalleePoint());
			Point2D p2 = util.getPoint2();
			Point2D p3 = util.getPoint3();
			int dx = getMiddleDx(getCallerPoint(), getCalleePoint());
			int dy = getMiddleDy(getCallerPoint(), getCalleePoint());
			
			graphic.drawLine(getCalleePoint().x, getCalleePoint().y, getCallerPoint().x, getCallerPoint().y);
			graphic.drawLine(getCalleePoint().x + dx,  getCalleePoint().y + dy , (int) p2.getX() + dx,(int) p2.getY() + dy);
			graphic.drawLine(getCalleePoint().x + dx,  getCalleePoint().y + dy , (int) p3.getX() + dx,(int) p3.getY() + dy);
		}
		
		
		Workspace.getInstance().getBlockCanvas().getCanvas().repaint();
	}
	
	public Point getCallerPoint(){ 
		Point p = caller.getLocation();
		p.x+=caller.getBlockWidth();
		p.y+=caller.getBlockHeight()/2;
		return p;
	}
	
	public Point getCalleePoint(){ 
		Point p = callee.getLocation();
		p.y+=callee.getBlockHeight()/2;
		return p;
	}
	
	public int getMiddleDy(Point callerPoint, Point calleePoint){
		int yLength = Math.abs(callerPoint.y - calleePoint.y);
		int dy = 0;
		if(callerPoint.y < calleePoint.y){
			dy = -yLength/2;
		}else if(callerPoint.y > calleePoint.y){
			dy = yLength/2;
		}
		return dy;
	}
	
	public int getMiddleDx(Point callerPoint, Point calleePoint){
		int xLength = Math.abs(callerPoint.x - calleePoint.x);
		int dx = 0;
		if(callerPoint.x < calleePoint.x){
			dx = -xLength/2;
		}else if(callerPoint.x> calleePoint.x){
			dx = xLength/2;
		}
		
		return dx;
	}
	
	public static Container getOrigin(JComponent cmp){
		Container component = cmp.getParent();
		
		if(component == null){
			return cmp;
		}
		
		while(component.getParent() != null){
			component = component.getParent();
		}
		
		return component;
	}
	
	public void setVisible(boolean visible){
		if(!collapsed){
			super.setVisible(visible);
		}
	}
	
	public void toggleVisible(){
		if(DrawingArrowManager.isActive()){
			System.out.println(isVisible());
			super.setVisible(!isVisible());			
		}
	}
	
	public void toggleCollapsed(){
		this.collapsed = !this.collapsed;
	}

}
