/* Project Description:
 * (The following abstract has been borrowed from the project description in Assignment 5) In the given project, an android application has been designed that aids people in locating their parked cars. Once the car’s parking location has been marked, car’s location is highlighted in red on the map view. User can now see the distance of the parking spot from his current location along with details of the car’s location such as its address and the time that the car is parked at that location. User may also center the map with respect to his location. Moreover, the user has the option to get directions to his/her car by hitting the navigate button.  
 */


/*This is the class that creates and displays the first Android screen to the user. The first screen hosts the map view that displays the markers for the user and the car along with various fields like the address, duration since the car has been parked and the distance between user's current location and the car parking spot. This is accompanied by an action bar that provides options for parking the car that opens up the next screen, changing the units for distance, navigating to the car and centering the map with respect to the user's location.
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

import android.app.Activity;
import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

import com.askgxm.findmycar.www.R.menu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.PendingIntent.OnFinished;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//This class implements LocationListener to get appropriate callbacks regarding the states of the location sensors
public class FindMyCar extends Activity implements LocationListener{
	

	private LocationManager locationManager;
	private String provider;
	private Context context=FindMyCar.this;
	private double userLat;
	private double userLon;
	private TextView address_txt;
	private TextView time_txt;
	private TextView distance_txt;
	private Intent intent;
	private Location location;
	private static MenuItem itemNavigate;
	private static MenuItem itemPark;
	private boolean initialFlag=true;
	private double cval;
	private ConnectivityManager manager;
	private LinearLayout instruction_layout;
	private LinearLayout information_layout;
	private ProgressDialog progressDialog; 

	GoogleMap map;

	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Setting the basic layout and initializing the components available in .xml file 
		setContentView(R.layout.activity_find_my_car);
		address_txt=(TextView) findViewById(R.id.address_txt);
		time_txt=(TextView) findViewById(R.id.time_txt);
		distance_txt=(TextView) findViewById(R.id.distance_txt);
		instruction_layout=(LinearLayout) findViewById(R.id.instruction_layout);
		information_layout=(LinearLayout) findViewById(R.id.information_layout);
		
		//In order to preserve the state between application runs, we write the car parking details to a file. Here we check if the             file exists. If it does, then the car has been parked already, if it does not exist the car is yet to be parked.
		Parking.setParked(FileManager.retrieve(FindMyCar.this));
		//we hide or show certain layouts based on the isParked variable
		manipulateBaseLayout();
			
	    //Initialize the progress dialog to be shown while the location is being fetched from the location sensor
		progressDialog=new ProgressDialog(FindMyCar.this);
		progressDialog.setTitle("Accessing location");
		progressDialog.setCancelable(false);
	   
		//Initializing the map fragment
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
	}
	
	//Method to hide or show certain layouts based on the isParked variable
	
	private void manipulateBaseLayout() {
		// TODO Auto-generated method stub
		if(Parking.isParked()){
			instruction_layout.setVisibility(View.GONE);
			information_layout.setVisibility(View.VISIBLE);
		}else{
			instruction_layout.setVisibility(View.VISIBLE);
			information_layout.setVisibility(View.GONE);
		}
	}

	@Override
	
	protected void onPause() {
		// TODO Auto-generated method stub
		//Remove location updates as we do not want to hog on resources 
		locationManager.removeUpdates(this);
		super.onPause();
	}
	
	@Override
	
	protected void onResume() {
		// TODO Auto-generated method stub
		//we hide or show certain layouts based on the isParked variable
		manipulateBaseLayout();
	
        //Initializing the ConnectivityManager to know if we are connected to the internet
		manager =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo validNetwork = manager.getActiveNetworkInfo();
		boolean isConnected =  validNetwork != null && validNetwork.isConnectedOrConnecting();
		
        if(!isConnected){
			//Displaying an alert dialog if we are not connected to the internet
			new AlertDialog.Builder(this)
        	.setTitle("No Internet Connection")
        	.setMessage("Enable connection by going to settings")
        	.setIcon(R.drawable.yellowcar)
        	.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int what) {
        	    	//If the user clicks on yes, we clear the parking location.
        	    	//startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        	    	startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        	        }
                }).setNegativeButton(android.R.string.no, new OnClickListener() {
				
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
					   // TODO Auto-generated method stub
					   dialog.cancel();
					   finish();
				        }
			     }).show();
		  }
		
		
		//Flag that is used to centre the map with respect to the user only once, when this screen is displayed
		initialFlag=true;
		//show the dialog untill we have the location
        progressDialog.show();
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// Use criteria for selection of location sensor with fine accuracy 
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = locationManager.getBestProvider(criteria, true);

        try{
			//Request location updates as often as possible
			locationManager.requestLocationUpdates(provider, 0, 0, this);
			location = locationManager.getLastKnownLocation(provider);
		  }
		  catch(Exception e){
			Toast.makeText(context, "Check if GPS is switched on", Toast.LENGTH_LONG).show();
		  }
		
		if (location != null) {
			//if we have a valid location, dismiss the progressDialog and draw the map with updated information
			if(progressDialog.isShowing()){
				drawMap();
				progressDialog.dismiss();
             }
			onLocationChanged(location);
		} else {
			     //If we do not have a valid location, present a message that informs the user
                 Toast.makeText(FindMyCar.this, "Location details not available",
                 Toast.LENGTH_LONG).show();
            }  
		
		
        //We start a count down timer that gives a callback every 1000 milliseconds that helps us to update fields on the display
		new CountDownTimer(Long.MAX_VALUE, 1000) {
		
			public void onTick(long millisUntilFinished) {
			
                location = locationManager.getLastKnownLocation(provider);
			     if (location != null) {
				//if location is available, dismiss the progressDialog, draw  and draw the map view with updated information 
				    
                     if(progressDialog.isShowing()){
					
					   drawMap();
					   progressDialog.dismiss();
				    }
				
				//store values in the below variables that would be used to add marker on the map
				userLat = location.getLatitude();
				userLon = location.getLongitude();
				 
            	       if(Parking.isParked()){
            	           //If the car is in parked state, calculate the time since it has been parked	
            		      time_txt.setText("Parked for "+calculateDifference(Parking.getCalPark()));
                        }
				 
				    } 
			}

            public void onFinish() {
                 //Once the countdown reaches zero, this is executed. Left blank as we do not have any specific actions to be performed
			}
		}.start();
		
		super.onResume();
	}
	
	//Method to calculate the time since the car has been parked
	private String calculateDifference(Calendar calPark) {
		// TODO Auto-generated method stub
		 Calendar calNow = Calendar.getInstance();
		 long sub = calNow.getTimeInMillis() - calPark.getTimeInMillis();
		 //Converts the difference into hours : minutes: seconds
		 return (String.format("%02d:%02d:%02d", 
		 TimeUnit.MILLISECONDS.toHours(sub),
		 TimeUnit.MILLISECONDS.toMinutes(sub) -  
		 TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(sub)), 
		 TimeUnit.MILLISECONDS.toSeconds(sub) - 
		 TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sub)))+" hours");   
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.find_my_car, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	itemNavigate=menu.findItem(R.id.action_navigate);
        itemPark=menu.findItem(R.id.action_park);
        
        //we hide or show the navigate button on the action bar based on whether the car is in parked state or not 
        //we change the color of the park button on the action bar based on whether the car is in parked state or not 
        if(!Parking.isParked()){
			itemPark.setIcon(R.drawable.pblack);
			itemNavigate.setVisible(false);
		}else{
			itemPark.setIcon(R.drawable.pblue);
			itemNavigate.setVisible(true);
		}
    	return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_park) {
        	
        	if(!Parking.isParked()){
        		//If car is not parked, open the second screen to capture the details regarding the parking spot
        		intent=new Intent(FindMyCar.this,FormActivity.class);
            	intent.putExtra("userLat", userLat+"");
            	intent.putExtra("userLon", userLon+"");
                startActivityForResult(intent,007);
            }
        	else{
        	
        		//If the car is already parked, we clear the parking instance
        		//Before we delete parking we ask for a confirmation.
            	new AlertDialog.Builder(this)
            	.setTitle("Clear Parking Confirmation")
            	.setMessage("Do you really want to clear the parking location?")
            	.setIcon(R.drawable.yellowcar)
            	.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    
                 public void onClick(DialogInterface dialog, int what) {
            	    	//If the user clicks on yes, we clear the parking location.
            	    	Parking.setParked(false);
            	    	manipulateBaseLayout();
            	 
                        //Reset all the fields in parking class
                		Parking.resetFields();
                		//Delete the parking.txt file in the internal storage of the application
                		FileManager.deleteFile(FindMyCar.this);
                		//reset the color of parking icon and make the navigate icon invisible
                		itemPark.setIcon(R.drawable.pblack);
                        itemNavigate.setVisible(false);

                		distance_txt.setText("");
                		address_txt.setText("");
                		time_txt.setText("");
                		//Redraw the map without the car marker
                		drawMap();
            	        
            	    }})
            	 .setNegativeButton(android.R.string.no, null).show();
        	}
            return true;
        }
        else if (id == R.id.action_navigate) {
        	//Bring upon the google maps to navigate between locations
        	Uri uri = Uri.parse("google.navigation:q="+Parking.getParkingSpot().latitude+","+Parking.getParkingSpot().longitude+"+&mode=w");
        	Intent mIntent = new Intent(Intent.ACTION_VIEW, uri);
        	mIntent.setPackage("com.google.android.apps.maps");
        	startActivity(mIntent);
        	
            return true;
        }
        else if (id == R.id.action_centre) {
    
            //centre the map with respect to the user's location
        	map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat,userLon), 16));
            return true;
        }
        
  else if (id == R.id.action_units) {
	  
	  //display an alertDialog that hosts a list containing different units of distance
      AlertDialog.Builder builder = new AlertDialog.Builder(FindMyCar.this);
      
      builder.setTitle("Pick unit for distance").setItems(R.array.unit_array, new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			 switch(which){
			 //Switch case to manipulate the units based on the item selected in the list
		        case(0): Parking.setUnit(0);
		                 calculateDistance();
		                 break;
		        case(1): Parking.setUnit(1);
		        calculateDistance();
		        break;
		        case(2): Parking.setUnit(2);
		        calculateDistance();
		        break;
		        case(3): Parking.setUnit(3);
		        calculateDistance();
		        break;
		        case(4): Parking.setUnit(4);
		        calculateDistance();
		        break;
		        default:
		        	break;
			     }
		      }
	       });
	 
            AlertDialog dialog = builder.create();
            dialog.show();
	       return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Callback when there is a change in location
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		location = locationManager.getLastKnownLocation(provider);
		if(location!=null){
			
			//Update following variables with new location and draw the map with updated coordinates
			userLat = location.getLatitude();
			userLon = location.getLongitude();
			drawMap();
		}
	}

	//This method draws the map based on the location coordinates 
	
	private void drawMap() {
		// TODO Auto-generated method stub
		//Get last known location from the sensor
		location = locationManager.getLastKnownLocation(provider);
		
	if (location != null) {
	   //If location is available update the latitude and longitude variables and dismiss the progressDialog 
	   //	if(progressDialog.isShowing()){
			progressDialog.dismiss();
	//	}
		userLat = location.getLatitude();
		userLon = location.getLongitude();
		
		//Clear the outdated map view
		 map.clear();
    	 
    	 if(Parking.isParked()){
    		 //If the car is in parked state, recalculate the distance between the parking spot and the user
    		 calculateDistance();
    		 //add marker for the car
    		 map.addMarker(new MarkerOptions().position(Parking.getParkingSpot()).snippet("Parked on floor "+Parking.getFloorNo()+".   "+Parking.getNote()).title("Car").icon(BitmapDescriptorFactory.fromResource(R.drawable.carbig)));
    		 
    		 //If address is available, update the address to the textview else show a default text
    		 
    		 if(Parking.getAddress()!=null){
    			 address_txt.setText(Parking.getAddress()); 
    		 }
    		 else{
    			 address_txt.setText("Address not available"); 
    		 }
    	 }
    	 
    	 //add a marker for the user
		 map.addMarker(new MarkerOptions().position(new LatLng(userLat, userLon))
					.title("You"));
		//Map is centered with respect to the user only for the first time the screen is displayed
		 if(initialFlag){
			 map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat,userLon), 16));
			 initialFlag=false;
		  }
		}else{
			//System.out.println("==Location null");
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
        //If the location sensor is not available, show an alertDialog
			new AlertDialog.Builder(this)
        	.setTitle("GPS disabled")
        	.setMessage("Enable GPS by going to settings")
        	.setIcon(R.drawable.yellowcar)
        	.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int what) {
        	    	//Open the settings screen for the user to switch on the GPS
        	    	startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        	    }})
        	 .setNegativeButton(android.R.string.no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
					finish();
				}
			}).show();
		}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		//Set criteria to have a sensor with fine accuracy
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = locationManager.getBestProvider(criteria, true);
		try{
			//Request location updates as often as possible 
			locationManager.requestLocationUpdates(provider, 0, 0, this);
			location = locationManager.getLastKnownLocation(provider);
		}
		catch(Exception e){
			Toast.makeText(context, "Check if GPS is switched on", Toast.LENGTH_LONG).show();
		}
	
        if (location != null) {
			
			 if(progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			onLocationChanged(location);
		} else {
                    Toast.makeText(FindMyCar.this, "Location details not available",
					Toast.LENGTH_LONG).show();
		  }  
		// TODO Auto-generated method stub
	}

	@Override
	
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
	
	//Method to calculate the distance between the user and the car's location
	public void calculateDistance() {
		int r = 6371;
		double latA = Parking.getParkingSpot().latitude;
		double latB = userLat;
		double lonA = Parking.getParkingSpot().longitude;
		double lonB = userLon;
		double dLat = Math.toRadians(latB - latA);
		double dLon = Math.toRadians(lonB - lonA);
		double num = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(latA))
				* Math.cos(Math.toRadians(latB)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double cNum = 2 * Math.asin(Math.sqrt(num));
		double valueResult = r * cNum;
		double mResult = valueResult * 1000;
	
        Log.i("Radius Value", "" + valueResult
				+ /* "   KM  "+kmInDec+ */" Meter   " + mResult);
		//Manipulating the distance value based on the unit chosen
		switch(Parking.getUnit())
        {    
		case(0): cval=mResult*1; 
			    distance_txt.setText(String.format( " %.2f", cval )+" mts");
			break;
            case (1):
            {
                cval = 3.28084;
                cval = mResult*cval;
                distance_txt.setText(String.format( " %.2f", cval )+" fts");
                break;
            }
            case (2):
            {
                cval = 1.09361;
                cval = mResult*cval;
                distance_txt.setText(String.format( " %.2f", cval )+" yards");
                break;
           }
            case (3):
            {
                cval = 0.001;
                cval = mResult*cval;
                distance_txt.setText(String.format( " %.2f", cval )+" kms");
                break;
            }
            case (4):
            {
                cval = 0.000621371;
                cval = mResult*cval;
                distance_txt.setText(String.format( " %.2f", cval )+" miles");
                break;
            }
            default:
        }
	}

	//Callback to show the navigate button and to change the color of park button
	 @Override
	    public void onActivityResult(int requestCode,int resultCode, Intent data)
	    {
	           super.onActivityResult(requestCode,resultCode,data);
                
                switch(requestCode)
	            {
	                case (007): {
	                    if (resultCode == Activity.RESULT_OK) {
	                        itemNavigate.setVisible(true);
	                        itemPark.setIcon(R.drawable.pblue);
	                    }
	                    break;

	                }
	            }
	    }
}

			

		

	

	