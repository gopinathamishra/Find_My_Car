/*This is the class that asynchronously (not on the UI thread) fetches the address details based on the latitude and longitude. As getting values from Google server may take a while, running this code on UI thread is not a good idea.  
 * NetID: ask140730
 * Purpose: Assignment 6 (UI Design) CS6301.001
 * Date Created: 15 April 2015
 * Date last modified: 24th April 2015
 * Author: Ameya Kadam and Gopinatha Mishra
 * 
 * 
 * 
 * */
package com.askgxm.findmycar.www;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

public class AddressTask extends AsyncTask<Void, Void, Void>{

	Context context;
	Geocoder geocoder;
	List<Address> addressList;
	
	//Parameterized constructor to initialize the context from the application
	AddressTask(Context context){
		
		this.context=context;
		geocoder =new Geocoder(context); 
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		 
		 try {
			 
			//Here we get the actual address based on latitude and longitude 
			 addressList = geocoder.getFromLocation(Parking.getParkingSpot().latitude, Parking.getParkingSpot().longitude, 1);
			 String add1 = addressList.get(0).getAddressLine(0);
             String add2 = addressList.get(0).getAddressLine(1);
             String add3 = addressList.get(0).getAddressLine(2);
   		 
             //setting the fetched value in the address attribute
             Parking.setAddress("Parked at "+add1+", "+add2+", "+add3);
   		   } catch (IOException e) {
			 
             // TODO Auto-generated catch block
			 e.printStackTrace();
		  }
		  return null;
    }
    
	@Override
	protected void onPostExecute(Void result) {
		
        // TODO Auto-generated method stub
		super.onPostExecute(result);
		System.out.println("==Address received"+Parking.getAddress());
		if(Parking.getAddress()==null){
			Parking.setAddress("Address not available");
        }
		else{
            FileManager.saveToFile(context.getFilesDir().toString() + "/parking.txt", Parking.getParkingSpot(), Parking.getCalPark(),               Parking.getFloorNo(), Parking.getNote());
		}
		
	}

}
