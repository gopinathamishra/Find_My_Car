/*
 * Author: Ameya Kadam and Gopinatha Mishra
 */

package com.askgxm.findmycar.www;
import java.io.File;
import java.io.FileOutputStream;
import com.google.android.gms.maps.model.LatLng;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class FormActivity extends Activity {
	
Spinner floor_spin;
EditText note_edt;
Button proceed_btn;
Intent intent;
public static String  PARKING_LOCATION_ADDED = "false";
private String dbContent = "null";
private AddressTask addressTask;
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_form);
	
	addressTask=new AddressTask(FormActivity.this);
	
	intent=getIntent();
	
	floor_spin=(Spinner) findViewById(R.id.floor_spin);
	note_edt=(EditText) findViewById(R.id.note_edt);
	proceed_btn=(Button) findViewById(R.id.proceed_btn);
	
	floor_spin.setOnItemSelectedListener(new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	});
	
	proceed_btn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			markLocation();
            Intent parent=new Intent();
            parent.putExtra(PARKING_LOCATION_ADDED,"true");
            setResult(Activity.RESULT_OK, parent);
			finish();
		}

		private void markLocation() {
			// TODO Auto-generated method stub
			Parking.setFloorNo(floor_spin.getSelectedItem().toString());
			Parking.setCalPark();
			Parking.setParked(true);
			Parking.setParkingSpot(new LatLng(   Double.parseDouble(intent.getStringExtra("userLat"))      , Double.parseDouble(intent.getStringExtra("userLon"))));
			Parking.setNote(note_edt.getText().toString());
			addressTask.execute((Void)null);
			
			//saving contents to a file
            //dbContent = " Notes : "+ Parking.getNote() + " \n"+" Floor Number: " + Parking.getFloorNo();

           String filePath=getFilesDir().toString() + "/parking.txt";
           FileManager.saveToFile(filePath, Parking.getParkingSpot(), Parking.getCalPark(), Parking.getFloorNo(), Parking.getNote());
		}
	});
}	

}
