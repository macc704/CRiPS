package drawingobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import workspace.Workspace;



public class ArrowObject extends JComponent {

	private static final long serialVersionUID = -1745361279120477995L;
	private Point startPoint;// 起点
	private Point endPoint;// 終点

	
	private static Color thinColor = new Color(255,255,0,30);
	private static Color normalColor = new Color(255,255,0, 255);
	private Color arrowColor = normalColor;
	
	
	public ArrowObject(Point startPoint, Point endPoint) {
		setBounds(Workspace.getInstance().getBlockCanvas().getCanvas().getBounds());
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		setDoubleBuffered(true);
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
	
	public void resetPoint(Point startPoint, Point endPoint){
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}
	
	public Point getStartPoint(){
		return this.startPoint;
	}
	
	public Point getEndPoint(){
		return this.endPoint;
	}
	
	public void addStartPoint(int dx, int dy) {
		this.startPoint.x += dx;
		this.startPoint.y += dy;
	}

	public void addEndPoint(int dx, int dy) {
		this.endPoint.x += dx;
		this.endPoint.y += dy;
	}


	public void setStartPoint(Point p) {
		this.startPoint.x = p.x;
		this.startPoint.y = p.y;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawArrow((Graphics2D) g);
	}

	public void paint(Graphics g) {
		super.paint(g);
		//boundsの再設定
		drawArrow((Graphics2D) g);
	}

	public void update(Graphics g) {
		super.update(g);
		paint(g);
	}

	public void drawArrow(Graphics2D graphic) {
		setBounds(0,0,Workspace.getInstance().getBlockCanvas().getCanvas().getHeight(),Workspace.getInstance().getBlockCanvas().getCanvas().getWidth());
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ArrowPointCalcUtil util = new ArrowPointCalcUtil(getStartPoint(), getEndPoint());
		BasicStroke stroke = new BasicStroke(3.0f);
		graphic.setStroke(stroke);

		Point2D p2 = util.getPoint2();
		Point2D p3 = util.getPoint3();

		graphic.setColor(arrowColor);
		// arrowLengthの変更（ベクトルの向きに応じて変更）
		int dx = getMiddleDx();
		int dy = getMiddleDy();
		
		graphic.drawLine(getStartPoint().x, getStartPoint().y, getEndPoint().x, getEndPoint().y);
		graphic.drawLine(getEndPoint().x + dx,  getEndPoint().y + dy, (int) p2.getX() + dx,(int) p2.getY() + dy);
		graphic.drawLine(getEndPoint().x + dx,  getEndPoint().y + dy, (int) p3.getX() + dx,(int) p3.getY() + dy);
		
		Workspace.getInstance().getBlockCanvas().getCanvas().repaint();
	}
	
	public int getMiddleDy(){
		int yLength = Math.abs(getEndPoint().y - getStartPoint().y);
		int dy = 0;
		if(getEndPoint().y > getStartPoint().y){
			dy = -yLength/2;
		}else if(getEndPoint().y < getStartPoint().y){
			dy = yLength/2;
		}
		return dy;
	}
	
	public int getMiddleDx(){
		int xLength = Math.abs(getEndPoint().x - getStartPoint().x);
		int dx = 0;
		if(getEndPoint().x > getStartPoint().x){
			dx = -xLength/2;
		}else if(getEndPoint().x < getStartPoint().x){
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

}
