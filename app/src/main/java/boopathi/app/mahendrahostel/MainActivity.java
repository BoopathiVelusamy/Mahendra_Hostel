package boopathi.app.mahendrahostel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    SessionMaintance sessionMaintance;
    FloatingActionButton fab;
    ListView listview;
    List<String> name_list = new ArrayList<>();
    List<String> rollno_list = new ArrayList<>();
    List<String> fromdate_list = new ArrayList<>();
    List<String> fromtime_list = new ArrayList<>();
    List<String> enddate_list = new ArrayList<>();
    List<String> endtime_list = new ArrayList<>();
    List<String> status_list = new ArrayList<>();
    List<String> request_type_list = new ArrayList<>();
    List<String> outing_reason_list = new ArrayList<>();
    List<String> warden_comment_list = new ArrayList<>();
    CustomAdapter customAdapter;
    StringBuffer sb = new StringBuffer();
    String json_url = Url_interface.url + "logout_mobile/";
    String json_string = "";
    ProgressDialog progressDialog;

    StringBuffer sb2 = new StringBuffer();
    String json_url2 = Url_interface.url + "request_mobile/";
    String json_string2 = "";

    StringBuffer sb3 = new StringBuffer();
    String json_url3 = Url_interface.url + "parcel_data/";
    String json_string3 = "";

    private String TAG = "MainActivity";

    String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.FOREGROUND_SERVICE_LOCATION, Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.CAMERA,
            Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_NUMBERS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.SEND_SMS,Manifest.permission.RECEIVE_SMS};
    int permission_All = 1;

    TextView txtview_nrf;

    ArrayList<student_model> arrayList_m = new ArrayList<student_model>();

    View customview;
    LayoutInflater inflater;
    PopupWindow popupWindow;


    FusedLocationProviderClient fusedLocationProviderClient;
    String lati_str="",longi_str="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        Drawable background = getResources().getDrawable(R.drawable.gradient_color); //bg_gradient is your gradient.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_color));
        setContentView(R.layout.activity_main);
        intialise();
        getSupportActionBar().setTitle("MAHENDRA HMS");

        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        inflater = (LayoutInflater)MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        customview = inflater.inflate(R.layout.custom_layout_data_connection, null);

        if (!haspermission(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, permission_All);
        }

        ConnectivityManager connManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected())
                || (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnected())) {
            startServiceViaWorker();

            progressDialog.show();
            new backgroundworker2().execute();

        }else {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show1();
                }
            },2000);

        }





        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.show(getSupportFragmentManager(),"Dialog");

            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {

                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_MOVE:
                                view.setX(event.getRawX() - 120);
                                view.setY(event.getRawY() - 425);
                                break;

                            case MotionEvent.ACTION_UP:
                                view.setOnTouchListener(null);
                                break;

                            default:
                                break;
                        }
                        return true;
                    }

                });
                return true;
            }
        });

        myAlarm();
    }

    private void show1() {
        popupWindow = new PopupWindow(customview,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,true);
        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow.setElevation(5.0f);
        }

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.update();

        Button agree = customview.findViewById(R.id.button12);

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                show5();
            }
        });
        popupWindow.showAtLocation(customview, Gravity.CENTER, 0, 0);
    }

    private void show5() {
        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        finish();
    }


    public void myAlarm() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 14);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }else{
            //Toast.makeText(this, "Me", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                //Toast.makeText(this, "Alarm Set 23", Toast.LENGTH_SHORT).show();
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                //Toast.makeText(this, "Alarm Set 19", Toast.LENGTH_SHORT).show();
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
               // Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }


    }



    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, MyReceiver.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    public static boolean haspermission(Context context, String... permissions) {
        if((Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)&&(context!=null)&&(permissions!=null))
        {
            for(String temp : permissions)
                if(ActivityCompat.checkSelfPermission(context,temp)!= PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
        }
        return true;
    }


    public class CustomAdapter extends BaseAdapter {

        //variables
        Context mContext;
        LayoutInflater inflater;
        List<student_model> modellist;
        ArrayList<student_model> arrayList;

        public CustomAdapter(Context context, List<student_model> modellist) {
            this.mContext = context;
            this.modellist = modellist;
            inflater = LayoutInflater.from(mContext);
            this.arrayList = new ArrayList<student_model>();
            this.arrayList.addAll(modellist);
        }

        @Override
    public int getCount() {

        return modellist.size();
    }


    @Override
    public Object getItem(int i) {
        return modellist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view1, ViewGroup viewGroup) {


        view1 = getLayoutInflater().inflate(R.layout.custom_list_rowitem, null);


        TextView tv_name = (TextView) view1.findViewById(R.id.tv_name);
        TextView tv_reg_no = (TextView) view1.findViewById(R.id.tv_reg_no);
        TextView tv_from_date = (TextView) view1.findViewById(R.id.tv_from_date);
        TextView tv_enddate = (TextView) view1.findViewById(R.id.tv_enddate);
        TextView tv_approve = (TextView) view1.findViewById(R.id.tv_approve);
        TextView tv_reject = (TextView) view1.findViewById(R.id.tv_reject);
        TextView tv_waiting = (TextView) view1.findViewById(R.id.tv_waiting);
        TextView tv_from_time= (TextView) view1.findViewById(R.id.tv_from_time);
        TextView tv_endtime = (TextView)view1.findViewById(R.id.tv_endtime);
        TextView tv_request_type = view1.findViewById(R.id.tv_type);
        TextView tv_outing_reason = view1.findViewById(R.id.tv_reason);
        TextView tv_warden_comment = view1.findViewById(R.id.tv_warden_reason);
        LinearLayout warden_layout = view1.findViewById(R.id.warden_layout);
        CardView cardview_row = view1.findViewById(R.id.cardview_row);
        LinearLayout linearlayout_row = view1.findViewById(R.id.linearlayout_row);

        tv_name.setText(name_list.get(i));
        tv_reg_no.setText("("+rollno_list.get(i)+")");

        tv_from_date.setText(modellist.get(i).getFromdate());
        tv_from_time.setText(modellist.get(i).getFromtime());
        tv_enddate.setText(modellist.get(i).getEnddate());
        tv_endtime.setText(modellist.get(i).getEndtime());

//        String inputPattern = "yyyy-MM-dd";
//        String outputPattern = "dd-MM-yyyy";
//        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
//        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
//        Date date = null;
//        String str = null;
//        try {
//            date = inputFormat.parse(modellist.get(i).getFromdate());
//            str = outputFormat.format(date);
//            tv_from_date.setText(str);
//            //Log.d("FFFFDATE",str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        //tv_from_date.setText(fromdate_list.get(i));
//        SimpleDateFormat HHmmFormat = new SimpleDateFormat("HH:mm", Locale.US);
//
//        SimpleDateFormat hhmmampmFormat = new SimpleDateFormat("hh:mm a", Locale.US);
//        String outputDateStr = "";
//        String ftime = modellist.get(i).getFromtime();
//        if(ftime.equals("null")){
//
//        }else {
//            String fromtime[]=ftime.split(":");
//            if(fromtime.length>=2){
//                outputDateStr = parseDate(modellist.get(i).getFromtime(), HHmmFormat, hhmmampmFormat);
//                Log.d("otime",modellist.get(i).getFromtime());
//                tv_from_time.setText(outputDateStr);
//                //Log.i("output_string", outputDateStr);
//            }else {
//                ftime=ftime+":00";
//                outputDateStr = parseDate(ftime, HHmmFormat, hhmmampmFormat);
//                tv_from_time.setText(outputDateStr);
//
//            }
//
//
//        }
//
//        //tv_from_time.setText(fromtime_list.get(i));
//
//        Date date1 = null;
//        String str1 = null;
//        try {
//            date1 = inputFormat.parse(modellist.get(i).getEnddate());
//            str1 = outputFormat.format(date1);
//            tv_enddate.setText(str1);
//            //Log.d("FFFFDATE",str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//
//        String etime = modellist.get(i).getEndtime();
//        if(etime.equals("null")){
//
//        }else {
//            String endtime[] = etime.split(":");
//            if (endtime.length >= 2) {
//                outputDateStr = parseDate(modellist.get(i).getEndtime(), HHmmFormat, hhmmampmFormat);
//                Log.d("etime", modellist.get(i).getEndtime());
//                tv_endtime.setText(outputDateStr);
//                //Log.i("output_string", outputDateStr);
//            } else {
//                etime = etime + ":00";
//                outputDateStr = parseDate(etime, HHmmFormat, hhmmampmFormat);
//                tv_endtime.setText(outputDateStr);
//
//            }
//        }



        //tv_enddate.setText(enddate_list.get(i));
        //tv_endtime.setText(endtime_list.get(i));
        tv_request_type.setText(modellist.get(i).getRequesttype());
        tv_outing_reason.setText(modellist.get(i).getOutingreason());

        if(modellist.get(i).getWardenreason().equals("")||modellist.get(i).getWardenreason().equals("null")){
            warden_layout.setVisibility(View.GONE);
        }else {
            warden_layout.setVisibility(View.VISIBLE);
            tv_warden_comment.setText(modellist.get(i).getWardenreason());
        }

        if(modellist.get(i).getStatuslist().equals("Approved")){
            tv_approve.setVisibility(View.VISIBLE);
            tv_reject.setVisibility(View.GONE);
            tv_waiting.setVisibility(View.GONE);
            //cardview_row.setCardBackgroundColor(Color.parseColor("#B4FDE3"));
            linearlayout_row.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bgaccept_new));
        }else if(modellist.get(i).getStatuslist().equals("Rejected")){
            tv_approve.setVisibility(View.GONE);
            tv_reject.setVisibility(View.VISIBLE);
            tv_waiting.setVisibility(View.GONE);
            //cardview_row.setCardBackgroundColor(Color.parseColor("#ffefef"));
            linearlayout_row.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bgreject_new));
        }else {
            tv_approve.setVisibility(View.GONE);
            tv_reject.setVisibility(View.GONE);
            tv_waiting.setVisibility(View.VISIBLE);
            //cardview_row.setCardBackgroundColor(Color.parseColor("#FFF6CC"));
            linearlayout_row.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bgwaiting_new));
        }



        return view1;

    }

        //filter
        public void filter(String charText){
            charText = charText.toLowerCase(Locale.getDefault());
            modellist.clear();
            if (charText.length()==0){
                customAdapter.notifyDataSetChanged();
                modellist.addAll(arrayList);
            }
            else {
                for (student_model model : arrayList){
                    if (model.getFromtime().toLowerCase(Locale.getDefault())
                            .contains(charText) || model.getFromdate().toLowerCase(Locale.getDefault())
                            .contains(charText)||model.getEnddate().toLowerCase(Locale.getDefault())
                            .contains(charText) || model.getEndtime().toLowerCase(Locale.getDefault())
                            .contains(charText) || model.getRequesttype().toLowerCase(Locale.getDefault())
                            .contains(charText) || model.getOutingreason().toLowerCase(Locale.getDefault())
                            .contains(charText) || model.getWardenreason().toLowerCase(Locale.getDefault())
                            .contains(charText) || model.getStatuslist().toLowerCase(Locale.getDefault())
                            .contains(charText)){
                        modellist.add(model);
                    }
                }
            }
            notifyDataSetChanged();
        }
}

    public static String parseDate(String inputDateString, SimpleDateFormat inputDateFormat, SimpleDateFormat outputDateFormat) {
        Date date = null;
        String outputDateString = null;
        try {
            date = inputDateFormat.parse(inputDateString);
            outputDateString = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateString;
    }

    public void intialise(){

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait...!!!");
        progressDialog.setCanceledOnTouchOutside(false);

        sessionMaintance = new SessionMaintance(MainActivity.this);

        fab = findViewById(R.id.add_fab);
        listview = findViewById(R.id.listview);
        listview.setDivider(null);
        txtview_nrf = findViewById(R.id.txtview_nrf);

    }

    public class backgroundworker extends AsyncTask<Void,Void,String> {

    @Override
    protected String doInBackground(Void... voids) {
        URL url= null;
        try {
            url = new URL(json_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sb=new StringBuffer();
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept","application/json");
            httpURLConnection.setRequestProperty("Authorization","token "+sessionMaintance.get_user_token());
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
            while((json_string=bufferedReader.readLine())!=null)
            {
                sb.append(json_string+"\n");
                Log.d("json_string1",""+json_string);
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            Log.d("GGG1",""+sb.toString());
            return sb.toString().trim();

        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String result) {
        json_string = result;
        progressDialog.dismiss();
        Log.d("addresult1",json_string);
        try {
            JSONObject jsonObject = new JSONObject(json_string);
            if(jsonObject.getString("status").equals("Success")){
                sessionMaintance.set_user_token("");
                sessionMaintance.set_user_unique("");
                sessionMaintance.set_user_role("");
                sessionMaintance.set_user_id("");
                sessionMaintance.set_user_gender("");
                sessionMaintance.set_user_name("");
                //startActivity(new Intent(MainActivity.this,Login.class));
                Intent intent = new Intent(MainActivity.this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }catch (Exception e){

        }
    }
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
            sb2=new StringBuffer();
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept","application/json");
            httpURLConnection.setRequestProperty("Authorization","token "+sessionMaintance.get_user_token());
            Log.d("TTTTTT","token "+sessionMaintance.get_user_token());
//            httpURLConnection.setDoOutput(true);
//            httpURLConnection.setDoInput(true);
//            JSONObject jsonObject = new JSONObject();
//            Log.i("JSON", jsonObject.toString());
//            DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
//            os.writeBytes(jsonObject.toString());
//            os.flush();
//            os.close();
            Log.e("STATUS", String.valueOf(httpURLConnection.getResponseCode()));
            Log.e("MSG" , String.valueOf(httpURLConnection.getDoOutput()));

            InputStream inputStream=null;
            BufferedReader bufferedReader = null;

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                inputStream=httpURLConnection.getInputStream();
                bufferedReader =new BufferedReader(new InputStreamReader(inputStream));
                while((json_string2=bufferedReader.readLine())!=null)
                {
                    sb2.append(json_string2+"\n");
                    Log.d("json_string2",""+json_string2);
                }

            }else {
                InputStream errorStream = httpURLConnection.getErrorStream();
                if (errorStream != null) {
                    bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    return errorResponse.toString();
                }
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
        Log.d("addresult1",json_string2);
        try {
            JSONObject jsonObject = new JSONObject(json_string2);
            if(jsonObject.getString("status").equals("Success")){
                JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
                while(count<jsonArray.length()){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(count);

                    if(jsonObject1.getString("from_date").equals("null")){

                    }else {
                        name_list.add(sessionMaintance.get_user_unique());
                        rollno_list.add(sessionMaintance.get_user_id());
                        fromdate_list.add(jsonObject1.getString("from_date"));
                        enddate_list.add(jsonObject1.getString("to_date"));
                        fromtime_list.add(jsonObject1.getString("from_time"));
                        endtime_list.add(jsonObject1.getString("to_time"));
                        status_list.add(jsonObject1.getString("outing_status"));
                        request_type_list.add(jsonObject1.getString("request_type"));
                        outing_reason_list.add(jsonObject1.getString("outing_reason"));
                        warden_comment_list.add(jsonObject1.getString("warden_comments"));

                        //123ucs001

                        //120uag002
                    }

                    count++;
                }
                if(count>0){

                    String str = "",str1="";

                    for (int i =0; i<fromdate_list.size(); i++){

                        String inputPattern = "yyyy-MM-dd";
                        String outputPattern = "dd-MM-yyyy";
                        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                        Date date = null;

                        try {
                            date = inputFormat.parse(fromdate_list.get(i));
                            str = outputFormat.format(date);
                            //tv_from_date.setText(str);
                            Log.d("FFFFDATE",str);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //tv_from_date.setText(fromdate_list.get(i));
                        SimpleDateFormat HHmmFormat = new SimpleDateFormat("HH:mm", Locale.US);

                        SimpleDateFormat hhmmampmFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                        String outputDateStr = "";
                        String outputDateStr1 = "";
                        String ftime = fromtime_list.get(i);
                        if(ftime.equals("null")){

                        }else {
                            String fromtime[]=ftime.split(":");
                            if(fromtime.length>=2){
                                outputDateStr = parseDate(fromtime_list.get(i), HHmmFormat, hhmmampmFormat);
                                Log.d("otime",fromtime_list.get(i));
                                //tv_from_time.setText(outputDateStr);
                                //Log.i("output_string", outputDateStr);
                            }else {
                                ftime=ftime+":00";
                                outputDateStr = parseDate(ftime, HHmmFormat, hhmmampmFormat);
                                //tv_from_time.setText(outputDateStr);

                            }


                        }

                        //tv_from_time.setText(fromtime_list.get(i));

                        Date date1 = null;
                        try {
                            date1 = inputFormat.parse(enddate_list.get(i));
                            str1 = outputFormat.format(date1);
                            //tv_enddate.setText(str1);
                            Log.d("FFFFDATE1",str1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        String etime = endtime_list.get(i);
                        if(etime.equals("null")){

                        }else {
                            String endtime[] = etime.split(":");
                            if (endtime.length >= 2) {
                                outputDateStr1 = parseDate(endtime_list.get(i), HHmmFormat, hhmmampmFormat);
                                Log.d("etime", endtime_list.get(i));
                                //tv_endtime.setText(outputDateStr);
                                //Log.i("output_string", outputDateStr);
                            } else {
                                etime = etime + ":00";
                                outputDateStr1 = parseDate(etime, HHmmFormat, hhmmampmFormat);
                                //tv_endtime.setText(outputDateStr);

                            }
                        }


                        student_model model = new student_model(str, outputDateStr, str1,
                                outputDateStr1,request_type_list.get(i),outing_reason_list.get(i),warden_comment_list.get(i),status_list.get(i));
                        //bind all strings in an array
                        arrayList_m.add(model);
                        Log.d("SSSSIXZ",arrayList_m.toString());
                        customAdapter = new CustomAdapter(MainActivity.this,arrayList_m);
                        listview.setAdapter(customAdapter);

                    }
                }else {
                    if(fromdate_list.size()>0){
                        listview.setAdapter(customAdapter);
                        txtview_nrf.setVisibility(View.GONE);
                        listview.setVisibility(View.VISIBLE);
                    }else {
                        txtview_nrf.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.GONE);
                    }
                }
            }
        }catch (Exception e){

        }
    }
}

    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                progressDialog.show();
                new backgroundworker3().execute();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

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
                Toast.makeText(MainActivity.this, "Parcel Notification Inserted", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
    }
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        if(sessionMaintance.get_user_gender().equals("Male")){
            MenuItem menuItem = menu.findItem(R.id.sos);
            menuItem.setVisible(false);
        }
        MenuItem myActionMenuItem = menu.findItem( R.id.search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(TextUtils.isEmpty(s)){
                    customAdapter.filter("");
                    listview.clearTextFilter();
                    customAdapter.notifyDataSetChanged();
                }else {
                    customAdapter.filter(s);
                    customAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                progressDialog.show();
                new backgroundworker().execute();
                return true;

            case R.id.scan:
                initQRCodeScanner();
                return true;

            case R.id.sos:

                getLoc();

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "9843014251"));
                startActivity(intent);



                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    public void getLoc(){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null){

                                    try {
                                        Geocoder geocoder = new Geocoder(MainActivity.this,Locale.getDefault());
                                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                        lati_str = String.valueOf(addresses.get(0).getLatitude());
                                        longi_str = String.valueOf(addresses.get(0).getLongitude());
                                        Log.d("ADDDRRRREEE", String.valueOf(" Lati :"+addresses.get(0).getLatitude()+" Longi :")+addresses.get(0).getLongitude()+"Addr :"+addresses.get(0).getAddressLine(0)+
                                                " city : "+addresses.get(0).getLocality()+" country : "+addresses.get(0).getCountryName());
                                        SessionMaintance sessionMaintance = new SessionMaintance(MainActivity.this);
                                        SmsManager smsManager = SmsManager.getDefault();
                                        StringBuffer smsBody = new StringBuffer();
                                        smsBody.append("Hey I am, " + sessionMaintance.get_user_name()+"("+sessionMaintance.get_user_unique()+"). I am in DANGER. Here are my coordinates.\n ");
                                        smsBody.append("http://maps.google.com?q=");
                                        smsBody.append(lati_str);
                                        smsBody.append(",");
                                        smsBody.append(longi_str);
                                        Log.d("SSSSMMMM",smsBody.toString());
                                        smsManager.sendTextMessage("9843014251", null, smsBody.toString(), null, null);
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
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 123){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLoc();
            }else {
                Toast.makeText(MainActivity.this,"Required Permission",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void startServiceViaWorker() {
        Log.d(TAG, "startServiceViaWorker called");
        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(this);

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        MyWorker.class,
                        16,
                        TimeUnit.MINUTES)
                        .build();

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);

    }


}
