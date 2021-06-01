/**
 * @author zxf2015年8月3日
 */
package com.test.model;


import com.alibaba.fastjson.JSON;

/**
 *  
 * 
 * @author zxf2015年8月3日
 */
public class UserAllocation implements Cloneable {
	//用户地点
	private Point userLocation;
	/*
	 * 内置所属区域编号，用于查询距离
	 * example：u1，u2之间的距离为：dis[u1.getAreaInnerId][u2.getAreaInnerId];
	 * dis为所属区域内部距离数组
	 */
	private int areaInnerId = -1;
	//点到纺锤体中线的直线距离
	private double distanceToMidline = 0;

	private int addOrder = 0; //加入顺序

	public int getAddOrder() {
		return addOrder;
	}

	public void setAddOrder(int addOrder) {
		this.addOrder = addOrder;
	}

	public double getDistanceToMidline() {
		return distanceToMidline;
	}

	public void setDistanceToMidline(double distanceToMidline) {
		this.distanceToMidline = distanceToMidline;
	}

	private double dis2WareHouse = 0;
	//用户货物体积
	private double  clientVolume = 10;

	public double getClientVolume() {
		return clientVolume;
	}

	public void setClientVolume(double clientVolume) {
		this.clientVolume = clientVolume;
	}

	public double getDis2WareHouse() {
		return dis2WareHouse;
	}

	public void setDis2WareHouse(double dis2WareHouse) {
		this.dis2WareHouse = dis2WareHouse;
	}

	public UserAllocation(){

	}

	public UserAllocation(Point location){

			this.userLocation = location;

	}

	public UserAllocation(Point userLocation, int areaInnerId) {
		this.areaInnerId = areaInnerId;
		this.userLocation = userLocation;
	}

	public Point getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(Point userLocation) {
		this.userLocation = userLocation;
	}

	public int getAreaInnerId() {
		return areaInnerId;
	}

	public void setAreaInnerId(int areaInnerId) {
		this.areaInnerId = areaInnerId;
	}


	@Override
	public UserAllocation clone(){
		String json = JSON.toJSONString(this);
		return JSON.parseObject(json, UserAllocation.class);
	}



}
