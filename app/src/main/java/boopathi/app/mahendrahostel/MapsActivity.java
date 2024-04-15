package boopathi.app.mahendrahostel;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import boopathi.app.mahendrahostel.databinding.ActivityMapsBinding;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap gmap;
    MarkerOptions[] markerOptions;
    Map<String,String> hmap = new HashMap<>();
    AutoCompleteTextView autocomplete;
    String latlong;
    StringBuffer sb2 = new StringBuffer();
    String json_url2 = Url_interface.url+"get_user_details/";
    ProgressDialog progressDialog;
    String json_string2="";
    List<String> Lrollno = new ArrayList<>();
    List<String> Lname = new ArrayList<>();
    List<String> Linfo = new ArrayList<>();
    List<String> Lpermission_status = new ArrayList<>();
    List<String> Llatitude = new ArrayList<>();
    List<String> Llongitude = new ArrayList<>();
    String latitude="11.4986415",longtitude="78.0299874";
    Handler handler;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Please Wait...!!!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new backgroundworker2().execute();

        markerOptions = new MarkerOptions[100000000];

        autocomplete = (AutoCompleteTextView) findViewById(R.id.editTextTextPersonName);
        findViewById(R.id.imageView25).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latlong = hmap.get(autocomplete.getText().toString());
                Log.d("ASDERF",""+latlong);
                if(!latlong.equals(",")){
                    update();
                }else {
                    Toast.makeText(MapsActivity.this,"No Location data Found", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                counter++;
//                if(counter>1) {
//                    latitude = "11.47524";
//                    longtitude = "78.0394";
//                    new backgroundworker2().execute();
//                }
//                handler.postDelayed(this, 5000);
//
//            }
//        },5000);
    }

    public class backgroundworker2 extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            URL url= null;
            try {
                url = new URL(json_url2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Lname.clear();Lpermission_status.clear();Lrollno.clear();Llatitude.clear();Llongitude.clear();
                sb2=new StringBuffer();
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                httpURLConnection.setRequestProperty("Accept","application/json");
                SessionMaintance sessionMaintance = new SessionMaintance(MapsActivity.this);
                String token = sessionMaintance.get_user_token();
                httpURLConnection.setRequestProperty("Authorization","token "+token);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                JSONObject jsonObject = new JSONObject();
                Log.i("JSON", jsonObject.toString());
                DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
                os.writeBytes(jsonObject.toString());
                os.flush();
                os.close();
                Log.i("STATUS", String.valueOf(httpURLConnection.getResponseCode()));
                Log.i("MSG" , String.valueOf(httpURLConnection.getDoOutput()));
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream));
                while((json_string2=bufferedReader.readLine())!=null)
                {
                    sb2.append(json_string2+"\n");
                    Log.d("json_string1",""+json_string2);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.d("GGG1",""+sb2.toString());
                return sb2.toString().trim();

            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            json_string2 = result;
            progressDialog.dismiss();
            int count=0;

            try {
                Log.d("addresult1",json_string2);
                JSONObject jsonObject = new JSONObject(json_string2);
                if(jsonObject.getString("status").equals("Success")){
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
                    while(count<jsonArray.length()){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(count);
                        Lname.add(jsonObject1.getString("firstname"));
                        Lrollno.add(jsonObject1.getString("rollno"));
                        Linfo.add(jsonObject1.getString("rollno")+"-"+jsonObject1.getString("firstname"));
                        Lpermission_status.add(jsonObject1.getString("permission_status"));
                        Llatitude.add(jsonObject1.getString("latitude"));
                        Llongitude.add(jsonObject1.getString("longitude"));
                        hmap.put(jsonObject1.getString("rollno")+"-"+jsonObject1.getString("firstname"),jsonObject1.getString("latitude")+","+
                                jsonObject1.getString("longitude"));

                        count++;
                    }
                }

                Log.d("LATLAT", Linfo.toString());

                if(count>0){
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (MapsActivity.this,android.R.layout.select_dialog_item, Linfo);
                    autocomplete.setThreshold(1);
                    autocomplete.setAdapter(adapter);
                    SupportMapFragment supportMapFragment = (SupportMapFragment)
                            getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }catch (Exception e){

            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gmap.getUiSettings().setZoomControlsEnabled(true);
        gmap.getUiSettings().setZoomGesturesEnabled(true);
        gmap.getUiSettings().setCompassEnabled(true);

        for(int i=0;i<Llongitude.size();i++){
            markerOptions[i] = new MarkerOptions();
            try {
                LatLng loc = new LatLng(Double.valueOf(Llatitude.get(i)), Double.valueOf(Llongitude.get(i)));
                markerOptions[i].position(loc);
                markerOptions[i].title(Linfo.get(i));
                if(Lpermission_status.get(i).equals("Yes")){
                    markerOptions[i].anchor((float) 0.5,(float) 0.5);
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(loc);
                    circleOptions.strokeWidth(8);
                    circleOptions.strokeColor(Color.argb(255,255,0,0));
                    circleOptions.fillColor(Color.argb(32,255,0,0));
                    circleOptions.radius(10);
                    gmap.addCircle(circleOptions);
                }else{
                    markerOptions[i].anchor((float) 0.5,(float) 0.5);
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(loc);
                    circleOptions.strokeWidth(8);
                    circleOptions.strokeColor(Color.argb(145,192,0,0));
                    circleOptions.fillColor(Color.argb(32,122,200,0));
                    circleOptions.radius(10);
                    gmap.addCircle(circleOptions);
                }
                gmap.addMarker(markerOptions[i]);
            }catch (Exception e){

            }
        }
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(11.6340984,78.1504799),10));
    }

    public void update(){

        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Float.valueOf(latlong.split(",")[0]),
                Float.valueOf(latlong.split(",")[1]));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);

    }

}