package drawingobjects;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import workspace.Workspace;

public class MultiJointArrowObject extends ArrowObject{

	private Point startJointPoint;
	private Point endJointPoint;
	private static final long serialVersionUID = 5134494205703423521L;

	
	public MultiJointArrowObject(Point startPoint, Point endPoint) {
		super(startPoint, endPoint);
		this.startJointPoint = startPoint;
		this.endJointPoint = endPoint;
	}
	
	public void updateJoints(Point startJointPoint, Point endJointPoint){
		this.startJointPoint = startJointPoint;		
		this.endJointPoint = endJointPoint;
	}
	
	public void resetJoiuts(){
		this.startJointPoint = getStartPoint();
		this.endJointPoint = getEndPoint();
	}

	public void drawArrow(Graphics2D graphic) {
		setBounds(0,0,Workspace.getInstance().getBlockCanvas().getCanvas().getHeight(),Workspace.getInstance().getBlockCanvas().getCanvas().getWidth());
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ArrowPointCalcUtil util = new ArrowPointCalcUtil(startJointPoint, endJointPoint);
		BasicStroke stroke = new BasicStroke(3.0f);
		graphic.setStroke(stroke);

		Point2D p2 = util.getPoint2();
		Point2D p3 = util.getPoint3();

		graphic.setColor(getArrowColor());
		// arrowLengthの変更（ベクトルの向きに応じて変更）
		int dx = getMiddleDx();
		int dy = getMiddleDy();
		
		graphic.drawLine(getStartPoint().x, getStartPoint().y, startJointPoint.x, startJointPoint.y);
//		graphic.setColor(Color.RED);
		graphic.drawLine(startJointPoint.x, startJointPoint.y, endJointPoint.x, endJointPoint.y);
//		graphic.setColor(Color.BLUE);
		graphic.drawLine(getEndPoint().x, getEndPoint().y, endJointPoint.x, endJointPoint.y);
	
		graphic.drawLine(endJointPoint.x + dx,  endJointPoint.y + dy, (int) p2.getX() + dx,(int) p2.getY() + dy);
		graphic.drawLine(endJointPoint.x + dx,  getEndPoint().y + dy, (int) p3.getX() + dx,(int) p3.getY() + dy);
		
		Workspace.getInstance().getBlockCanvas().getCanvas().repaint();
	}
	
	public void addStartPoint(int dx, int dy) {
		super.addStartPoint(dx, dy);
		if(!startJointPoint.equals(getStartPoint())){
			this.startJointPoint.x += dx;
			this.startJointPoint.y += dy;
		}
	}

	public void addEndPoint(int dx, int dy) {
		super.addEndPoint(dx, dy);
		if(!endJointPoint.equals(getEndPoint())){
			this.endJointPoint.x += dx;
			this.endJointPoint.y += dy;
		}
	}
	
	public int getMiddleDy(){
		int yLength = Math.abs(endJointPoint.y - startJointPoint.y);
		int dy = 0;
		if(endJointPoint.y > startJointPoint.y){
			dy = -yLength/2;
		}else if(endJointPoint.y < startJointPoint.y){
			dy = yLength/2;
		}
		return dy;
	}
	
	public int getMiddleDx(){
		int xLength = Math.abs(endJointPoint.x - startJointPoint.x);
		int dx = 0;
		if(endJointPoint.x > startJointPoint.x){
			dx = -xLength/2;
		}else if(endJointPoint.x < startJointPoint.x){
			dx = xLength/2;
		}
		
		return dx;
	}
	
}
