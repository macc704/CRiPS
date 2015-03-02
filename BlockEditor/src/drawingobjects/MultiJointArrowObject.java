package drawingobjects;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import renderable.RenderableBlock;
import workspace.Workspace;

public class MultiJointArrowObject extends ArrowObject{

	private Point callerJointPoint;
	private Point calleeJointPoint;
	private static final long serialVersionUID = 5134494205703423521L;

	
	public MultiJointArrowObject(RenderableBlock caller, RenderableBlock callee, boolean isShow, boolean isActive) {
		super(caller, callee, isShow, isActive);
		this.callerJointPoint = caller.getLocation();
		this.calleeJointPoint = callee.getLocation();
	}
	
	public void updateJoints(Point startJointPoint, Point endJointPoint){
		this.callerJointPoint = startJointPoint;		
		this.calleeJointPoint = endJointPoint;
	}
	
	public void resetJoiuts(){
		this.callerJointPoint = getStartPoint();
		this.calleeJointPoint = getEndPoint();
	}

	public void drawArrow(Graphics2D graphic) {
		setBounds(0,0,Workspace.getInstance().getBlockCanvas().getCanvas().getHeight(),Workspace.getInstance().getBlockCanvas().getCanvas().getWidth());
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ArrowPointCalcUtil util = new ArrowPointCalcUtil(callerJointPoint, calleeJointPoint);
		BasicStroke stroke = new BasicStroke(3.0f);
		graphic.setStroke(stroke);

		Point2D p2 = util.getPoint2();
		Point2D p3 = util.getPoint3();

		graphic.setColor(getArrowColor());
		// arrowLengthの変更（ベクトルの向きに応じて変更）
		int dx = getMiddleDx();
		int dy = getMiddleDy();
		
		graphic.drawLine(getStartPoint().x, getStartPoint().y, callerJointPoint.x, callerJointPoint.y);
//		graphic.setColor(Color.RED);
		graphic.drawLine(callerJointPoint.x, callerJointPoint.y, calleeJointPoint.x, calleeJointPoint.y);
//		graphic.setColor(Color.BLUE);
		graphic.drawLine(getEndPoint().x, getEndPoint().y, calleeJointPoint.x, calleeJointPoint.y);
	
		graphic.drawLine(calleeJointPoint.x + dx,  calleeJointPoint.y + dy, (int) p2.getX() + dx,(int) p2.getY() + dy);
		graphic.drawLine(calleeJointPoint.x + dx,  getEndPoint().y + dy, (int) p3.getX() + dx,(int) p3.getY() + dy);
		
		Workspace.getInstance().getBlockCanvas().getCanvas().repaint();
	}
	
	public void addStartPoint(int dx, int dy) {
//		super.addStartPoint(dx, dy);
//		if(!callerJointPoint.equals(getStartPoint())){
//			this.callerJointPoint.x += dx;
//			this.callerJointPoint.y += dy;
//		}
	}

	public void addEndPoint(int dx, int dy) {
//		super.addEndPoint(dx, dy);
//		if(!calleeJointPoint.equals(getEndPoint())){
//			this.calleeJointPoint.x += dx;
//			this.calleeJointPoint.y += dy;
//		}
	}
	
	public int getMiddleDy(){
		int yLength = Math.abs(calleeJointPoint.y - callerJointPoint.y);
		int dy = 0;
		if(calleeJointPoint.y > callerJointPoint.y){
			dy = -yLength/2;
		}else if(calleeJointPoint.y < callerJointPoint.y){
			dy = yLength/2;
		}
		return dy;
	}
	
	public int getMiddleDx(){
		int xLength = Math.abs(calleeJointPoint.x - callerJointPoint.x);
		int dx = 0;
		if(calleeJointPoint.x > callerJointPoint.x){
			dx = -xLength/2;
		}else if(calleeJointPoint.x < callerJointPoint.x){
			dx = xLength/2;
		}
		
		return dx;
	}
	
}
