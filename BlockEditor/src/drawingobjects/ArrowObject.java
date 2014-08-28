package drawingobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;

public class ArrowObject extends JComponent {
	
	private static int ARROW_LENGTH = 5;
	
	private static final long serialVersionUID = 1L;
	private Point startPoint;//起点
	private Point endPoint;//終点
	private Graphics2D graphic;
	private int arrowLength = ARROW_LENGTH;

	public ArrowObject(Point p1, Point p2){
		this.startPoint = p1;
		this.endPoint = p2;
	}
	
	public void addStartPoint(int dx, int dy){
		this.startPoint.x += dx;
		this.startPoint.y += dy;
	}
	
	public void addEndPoint(int dx, int dy){
		this.endPoint.x += dx;
		this.endPoint.y += dy;
	}
	
	public void paintComponent(Graphics g){
		this.graphic = (Graphics2D)g;	 
		BasicStroke stroke = new BasicStroke(3.0f);
		graphic.setStroke(stroke);
		
		drawArrow();
	}
	
	public void drawArrow(){
		graphic.setColor(Color.RED);
		//arrowLengthの変更（ベクトルの向きに応じて変更）
		changeArrowLength();
		
		graphic.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
		graphic.drawLine(endPoint.x, endPoint.y,endPoint.x  + arrowLength, endPoint.y + arrowLength);
		graphic.drawLine(endPoint.x, endPoint.y,endPoint.x + arrowLength, endPoint.y  - arrowLength);
	}
	
	public void changeArrowLength(){
		if(startPoint.x < endPoint.x){
			arrowLength = -ARROW_LENGTH;
		}else{
			arrowLength = ARROW_LENGTH;
		}
	}
	
	public void repaint(){
		if(graphic != null){
			drawArrow();
		}
	}
	
	public void repaint(int x,int y, int width, int height){
		repaint();
	}
	
	public void reset(){
		this.graphic = null;
		repaint();
	}
	

	
}
