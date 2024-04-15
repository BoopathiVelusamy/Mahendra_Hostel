package boopathi.app.mahendrahostel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    EasyLocationProvider easyLocationProvider;
    String add="";
    FusedLocationProviderClient fusedLocationProviderClient;

    ProgressDialog progressDialog;

    SessionMaintance sessionMaintance;

    StringBuffer sb3 = new StringBuffer();
    String json_url3 = Url_interface.url+"daily_attendance/";
    String json_string3="";

    String slatitude="",slongitude="";

    TextView tv_name,tv_date,tv_time;


    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        Drawable background = getResources().getDrawable(R.drawable.gradient_color); //bg_gradient is your gradient.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_color));
        setContentView(R.layout.activity_notification);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        intialise();
        getLoc();
        button = findViewById(R.id.button);
        progressDialog.show();
        button.setVisibility(View.INVISIBLE);

        tv_name = findViewById(R.id.tv_name);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);


        tv_name.setText(sessionMaintance.get_user_unique());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tv_date.setText(currentDate);
        tv_time.setText(currentTime);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                new backgroundworker3().execute();
            }
        });
    }

    public void intialise(){

        progressDialog = new ProgressDialog(NotificationActivity.this);
        progressDialog.setMessage("Please Wait...!!!");
        progressDialog.setCanceledOnTouchOutside(false);

        sessionMaintance = new SessionMaintance(NotificationActivity.this);
    }

    public void getLoc(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){

                                try {
                                    Geocoder geocoder = new Geocoder(NotificationActivity.this,Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    slatitude = String.valueOf(addresses.get(0).getLatitude());
                                    slongitude = String.valueOf(addresses.get(0).getLongitude());
                                    add = addresses.get(0).getAddressLine(0);
                                    Log.d("ADDDRRRREEE", String.valueOf(" Lati :"+addresses.get(0).getLatitude()+" Longi :")+addresses.get(0).getLongitude()+"Addr :"+addresses.get(0).getAddressLine(0)+
                                            " city : "+addresses.get(0).getLocality()+" country : "+addresses.get(0).getCountryName());
                                    progressDialog.dismiss();
                                    button.setVisibility(View.VISIBLE);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
        }else{
            askpermission();
        }

    }

    private void askpermission(){
        ActivityCompat.requestPermissions(NotificationActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 123){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLoc();
            }else {
                Toast.makeText(NotificationActivity.this,"Required Permission",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    public void getLoc(){
//        easyLocationProvider = new EasyLocationProvider.Builder(NotificationActivity.this)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setListener(new EasyLocationProvider.EasyLocationCallback() {
//                    @Override
//                    public void onGoogleAPIClient(GoogleApiClient googleApiClient, String message) {
//                        Log.e("EasyLocationProvider","onGoogleAPIClient: "+message);
//                    }
//                    @Override
//                    public void onLocationUpdated(double latitude, double longitude) {
//                        Log.e("EasyLocationProvider","onLocationUpdated:: "+ "Latitude: "+latitude+" Longitude: "+longitude);
//                        add = getCompleteAddressString(latitude,longitude);
//                        //Toast.makeText(NotificationActivity.this, ""+slatitude+"-"+slongitude+"="+add, Toast.LENGTH_SHORT).show();
//                        easyLocationProvider.removeUpdates();
//                        getLifecycle().removeObserver(easyLocationProvider);
//                        slatitude = String.valueOf(latitude);
//                        slongitude = String.valueOf(longitude);
//                        progressDialog.dismiss();
//                        button.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onLocationUpdateRemoved() {
//                        Log.e("EasyLocationProvider","onLocationUpdateRemoved");
//                    }
//                }).build();
//
//        getLifecycle().addObserver(easyLocationProvider);
//    }

 //   @Override
//    protected void onDestroy() {
//        easyLocationProvider.removeUpdates();
//        getLifecycle().removeObserver(easyLocationProvider);
//        super.onDestroy();
//    }

//    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
//        String strAdd = "";
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
//            if (addresses != null) {
//                Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("");
//
//                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
//                }
//                strAdd = strReturnedAddress.toString();
//
//            } else {
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return strAdd;
//    }

    public class backgroundworker3 extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            URL url= null;
            try {
                url = new URL(json_url3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sb3=new StringBuffer();
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                httpURLConnection.setRequestProperty("Accept","application/json");
                httpURLConnection.setRequestProperty("Authorization","token "+sessionMaintance.get_user_token());
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("profile_id", sessionMaintance.get_user_id());
                jsonObject.put("latitude", slatitude);
                jsonObject.put("longitude", slongitude);
                jsonObject.put("address", add);
                jsonObject.put("attendance", "P");
                Log.i("JSON", jsonObject.toString());
                DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
                os.writeBytes(jsonObject.toString());
                os.flush();
                os.close();
                Log.i("STATUS", String.valueOf(httpURLConnection.getResponseCode()));
                Log.i("MSG" , String.valueOf(httpURLConnection.getDoOutput()));
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream));
                while((json_string3=bufferedReader.readLine())!=null)
                {
                    sb3.append(json_string3+"\n");
                    Log.d("json_string1",""+json_string3);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.d("GGG1",""+sb3.toString());
                return sb3.toString().trim();

            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            json_string3 = result;
            progressDialog.dismiss();
            int count=0;
            Log.d("addresult1",json_string3);
            try {
                JSONObject jsonObject = new JSONObject(json_string3);
                if(jsonObject.getString("status").equals("Success")){
                    Toast.makeText(NotificationActivity.this, "Attendnace Inserted", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){

            }
        }
    }


}