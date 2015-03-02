package edu.inf.shizuoka.drawingobjects;

import java.awt.Point;
import java.awt.geom.Point2D;


public class ArrowPointCalcUtil {

	private Point2D p0;
	private Point2D p1;
	private int POINTER_LENGTH = 10;
	
	public ArrowPointCalcUtil(Point p0,Point p1){
		this(new Point2D.Float(p0.x,p0.y),new Point2D.Float(p1.x,p1.y));
	}
	public ArrowPointCalcUtil(Point2D p0,Point2D p1){
		this.p0=p0;
		this.p1=p1;

		//p0-p1の長さを計算
		double diffX=this.p0.getX()-this.p1.getX();
		double diffY=this.p0.getY()-this.p1.getY();


		setLength(POINTER_LENGTH);
	}

	private int angle=45;
	public void setAngle(int angle){
		this.angle=angle;
	}
	public int getAngle(){
		return this.angle;
	}

	private int length;
	public void setLength(int length){
		this.length=length;
	}
	public int getLength(){
		return this.length;
	}


	public Point2D.Double getPoint2(){
		return getPoint2or3(getAngle());
	}

	public Point2D.Double getPoint3(){
		return getPoint2or3(getAngle()*(-1));
	}

	private Point2D.Double getPoint2or3(int hosei){
		Point p = new Point();
		if( p0.getX()<p1.getX() ){
			double radian=calcRadian(p0,p1);
			double kakudo=radian/(Math.PI/180);
			kakudo=kakudo+180;
			return getArrowTerminalPoint(p1,kakudo+hosei,getLength());
		}
		if( p0.getX()>p1.getX() ){
			double radian=calcRadian(p0,p1);
			double kakudo=radian/(Math.PI/180);
			return getArrowTerminalPoint(p1,kakudo+hosei,getLength());
		}
		if( p0.getX()==p1.getX() ){
			double radian=0;
			if( p0.getY()>p1.getY() ){
				radian=90*Math.PI/180;
			}
			if( p0.getY()<p1.getY() ){
				radian=270*Math.PI/180;
			}
			double kakudo=radian/(Math.PI/180);

			return getArrowTerminalPoint(p1,kakudo+hosei,getLength());
		}

		return null;
	}
    
	static private Point2D.Double getArrowTerminalPoint(Point2D topPoint,double kakudo,int length){
		double radian=kakudo*Math.PI/180;
	
		double x=Math.cos( radian )*length;
		double y=Math.sin( radian )*length;
		
		return new Point2D.Double((float)(x+topPoint.getX()),(float)(y+topPoint.getY()));
	}

	/**
	 * 2点を通る直線と水平線との角度を求める（ただし、返すのはラジアン）
	 */
	private static double calcRadian(Point2D startPoint ,Point2D endPoint){
	
		Point2D.Double p0=new Point2D.Double(startPoint.getX(),startPoint.getY());
		Point2D.Double p1=new Point2D.Double(endPoint.getX(),endPoint.getY());
		
		p1.setLocation(p1.getX()-p0.getX(),p1.getY()-p0.getY());
	
		if( p1.getX()==0 ){
			double radian=90*Math.PI/180;
			return radian;
		}
	
		double v=p1.getY()/p1.getX();
		double radian=Math.atan( v );
		
		return radian;
	}
}
