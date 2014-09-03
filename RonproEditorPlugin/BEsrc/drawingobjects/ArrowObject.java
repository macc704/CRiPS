package drawingobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import workspace.Workspace;

public class ArrowObject extends JComponent {


	private static final long serialVersionUID = 1L;
	private Point startPoint;// 起点
	private Point endPoint;// 終点

	
	public ArrowObject(Point p1, Point p2, String pageName) {
		setBounds(0, 0, Workspace.getInstance().getBlockCanvas().getWidth(), Workspace.getInstance().getBlockCanvas().getHeight());
		this.startPoint = p1;
		this.endPoint = p2;
		setDoubleBuffered(true);	
	}
	

	public void addStartPoint(int dx, int dy) {
		this.startPoint.x += dx;
		this.startPoint.y += dy;
	}

	public void addEndPoint(int dx, int dy) {
		this.endPoint.x += dx;
		this.endPoint.y += dy;
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		drawArrow((Graphics2D)g);
	}
	
	public void paint(Graphics g){
		super.paint(g);
		//boundsの再設定
		setBounds(0, 0, Workspace.getInstance().getBlockCanvas().getWidth(), Workspace.getInstance().getBlockCanvas().getHeight());
		drawArrow((Graphics2D)g);
	}
	
    public void update(Graphics g) {
    	super.update(g);
    	paint(g);
    }

	public void drawArrow(Graphics2D graphic) {
		ArrowPointCalcUtil util=new ArrowPointCalcUtil(startPoint,endPoint);
		BasicStroke stroke = new BasicStroke(3.0f);
		graphic.setStroke(stroke);

		Point2D p2 = util.getPoint2();
		Point2D p3 = util.getPoint3();
		
		graphic.setColor(Color.RED);
		// arrowLengthの変更（ベクトルの向きに応じて変更）
		graphic.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
		graphic.drawLine(endPoint.x, endPoint.y, (int)p2.getX(),
				(int)p2.getY());
		graphic.drawLine(endPoint.x, endPoint.y, (int)p3.getX(),
				(int)p3.getY());
	}



}
