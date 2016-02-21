/*This is the class that describes the model for the Parking instance in terms of its attributes.
 * NetID: ask140730
 * Purpose: Assignment 6 (UI Design) CS6301.001
 * Date Created: 15 April 2015
 * Date last modified: 24th April 2015
 * Author: Ameya Kadam and Gopinatha Mishra
 */

package com.askgxm.findmycar.www;

import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;

public class Parking {

	// Seven attributes defined for a paking instance  
	
	private static boolean isParked = false;

	private static LatLng parkingSpot;

	private static Calendar calPark;

	private static String floorNo;
	
	private static String note;
	
	private static String address;
	
	private static int unit=0;
	
	
	//Standard getters and seetters for the attributes
	public static int getUnit() {
		return unit;
	}
	
    public static void setUnit(int unit) {
		Parking.unit = unit;
	}
	
    public static String getAddress() {
		return address;
	}

    public static void setAddress(String address) {
		Parking.address = address;
	}

	
	// to reset fields of parking
	public static void resetFields(){
		parkingSpot=null;
		calPark=null;
		floorNo=null;
		note=null;
		address=null;
	}

    public static String getNote() {
		return note;
	}
	
    public static void setNote(String note) {
		Parking.note = note;
	}
	
    public static String getFloorNo() {
		return floorNo;
	}

    public static void setFloorNo(String floorNo) {
		Parking.floorNo = floorNo;
	}

    public static boolean isParked() {
		return isParked;
	}
	
	public static void setParked(boolean isParked) {
			Parking.isParked = isParked;
	}
	
	public static LatLng getParkingSpot() {
		return parkingSpot;
	}
	
	public static void setParkingSpot(LatLng parkingSpot) {
		Parking.parkingSpot = parkingSpot;
	}
	
	public static Calendar getCalPark() {
		return calPark;
	}

	//Overloaded versions of setCalPark one of which takes no input and the other takes the milliseconds in long format
	public static void setCalPark() {
		calPark = Calendar.getInstance();
	}
	
	public static void setCalPark(long parseLong) {
		// TODO Auto-generated method stub
		calPark = Calendar.getInstance();
		calPark.setTimeInMillis(parseLong);
	}
	
	//Method to set values from an arrayList
	public static void setValues(ArrayList<String> values) {

	    setParkingSpot(new LatLng(Double.parseDouble(values.get(0)),Double.parseDouble(values.get(1))));
	    setCalPark(Long.parseLong(values.get(2)));
	    if(values.get(3).equalsIgnoreCase("undefined")){
	    	setFloorNo("0");
	    }else{
	    	setFloorNo(values.get(3));
	    }
	    if(values.get(4).equalsIgnoreCase("undefined")){
	    	setNote("");
	    }else{
	    	setNote(values.get(4));
	    }
	    if(values.get(5).equalsIgnoreCase("undefined")){
	    	setAddress("");
	    }else{
	    	setAddress(values.get(5));
	    }
	}
}
