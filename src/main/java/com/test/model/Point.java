/**
 * @author zxf2015年8月3日
 */
package com.test.model;

import com.test.service.AngularBisector;

/**
 *  
 * 
 * @author zxf2015年8月3日
 */
public class Point {
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public double angleValue = -1;

	public double x;//经度
	public double y;//纬度
	public String name;
	public int flag; //哪个下标
	public Point() {
		
	}
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(double angleValue) {
		this.angleValue = angleValue;
	}

	public Point(double x, double y, String name) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public Point(int flag) {
		this.flag = flag;
	}

	private static void setPoint(Point point, Point point1) {

		point.x = point1.x;
		point.y = point1.y;

	}


	/**
	 *
	 * @param point
	 * @param wareHouse
	 * @param flag   true ->第一象限的L  第四象限的R
	 * @return
	 */
	public boolean FirAndFourPoint(Point point, Point wareHouse, boolean flag) {
		Point firstPoint = new Point(wareHouse.x + Math.abs(wareHouse.x), wareHouse.y);
		double value = AngularBisector.Angle(wareHouse, firstPoint, point);
		if ((value > this.angleValue && flag) || (value < this.angleValue && !flag)) {
			this.angleValue = value;
			setPoint(this, point);
			return true;
		}
		return false;
	}


	/**
	 *
	 * @param point
	 * @param wareHouse
	 * @param flag  true ->第二象限的L  第三象限的R
	 * @return
	 */
	public boolean SecAndTriPoint(Point point, Point wareHouse, boolean flag) {
		Point firstPoint = new Point(wareHouse.x - Math.abs(wareHouse.x), wareHouse.y);
		double value = AngularBisector.Angle(wareHouse, firstPoint, point);
		if ((value > this.angleValue && !flag) || (value < this.angleValue && flag)) {
			this.angleValue = value;
			setPoint(this, point);
			return true;
		}
		return false;
	}





/*
	public boolean FirPoint_R(Point point, Point wareHouse) {
		Point firstPoint = new Point(wareHouse.x + Math.abs(wareHouse.x), wareHouse.y);
		double value = AngularBisector.Angle(wareHouse, firstPoint, point);
		if (value < this.angleValue) {
			this.angleValue = value;
			setPoint(this, point);
			return true;
		}
		return false;
	}

	public void Spoint_L(Point point) {
		if (point.x < this.x && point.y < this.y) {
			this.x = point.x;
			this.y = point.y;
		}
	}

	public void Spoint_R(Point point) {
		if (point.x > this.x && point.y > this.y) {
			this.x = point.x;
			this.y = point.y;
		}
	}


	public void Tpoint_L(Point point) {
		if (point.x > this.x && point.y < this.y) {
			this.x = point.x;
			this.y = point.y;
		}
	}
	public void Tpoint_R(Point point) {
		if (point.x < this.x && point.y > this.y) {
			this.x = point.x;
			this.y = point.y;
		}
	}


	public void FouPoint_L(Point point) {
		if (point.x > this.x ) {
			this.x = point.x;
			this.y = point.y;
		}
	}

	public void FouPoint_R(Point point) {
		if (point.x < this.x && point.y < this.y) {
			this.x = point.x;
			this.y = point.y;
		}
	}
*/


	
}
