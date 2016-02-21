/* 
*Author: Ameya Kadam and Gopinatha Mishra
*/

package com.askgxm.findmycar.www;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class FileManager {

	public static void saveToFile (String filePath, LatLng parkingSpot, Calendar calPark, String floorNo, String note)
	{   
		//Checks to replace blank fields with the word "undefined".
		if(floorNo.equalsIgnoreCase("")){
			floorNo="undefined";
		}
		if(note.equalsIgnoreCase("")){
			note="undefined";
		}
		
		
		
		String data;
		
		if(Parking.getAddress()!=null){
			data=parkingSpot.latitude+ "|" + parkingSpot.longitude+"|" + calPark.getTimeInMillis() + "|" + floorNo+ "|" +note+                     "|"+Parking.getAddress();
		}
		else{
			data=parkingSpot.latitude+ "|" + parkingSpot.longitude+"|" + calPark.getTimeInMillis() + "|" + floorNo+ "|" +note+                     "|Address not available";
		}
		
		
	        try {

	            File f = new File(filePath);
	            if (f != null)
	                f.delete();

	            FileOutputStream out = new FileOutputStream(filePath, true);
	            out.write(data.getBytes());
	            out.close();

	        }
	        catch ( Exception e)
	        {
                
            }

	}
	
	public static void deleteFile(Context context){
	File dir = context.getFilesDir();
	File file = new File(dir, "parking.txt");
	boolean flag = file.delete();
	System.out.println("==File deletion Status: "+flag);
	}
	
	
	public static boolean retrieve(Context context)
    {
        String statement;
        String FilePath = context.getFilesDir().toString() + "/parking.txt";
        try{
                BufferedReader br = new BufferedReader(new FileReader(FilePath));
            	 try {
            		 
            		 statement=br.readLine();
            		 Parking.setValues(new ArrayList<String>(Arrays.asList(statement.split("\\|"))));
				    } catch (IOException e) {
					           // TODO Auto-generated catch block
					           e.printStackTrace();
					           return false;
				            }
            
            	 try {
					   br.close();
                     } catch (IOException e) {
					       // TODO Auto-generated catch block
					       e.printStackTrace();
					       return false;
                        }
            }catch(FileNotFoundException e){
            	   System.out.println("==File does not exist");
            	   return false;
              }
            
            return true;
    }

}
