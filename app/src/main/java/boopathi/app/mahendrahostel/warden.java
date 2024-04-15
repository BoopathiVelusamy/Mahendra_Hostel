package boopathi.app.mahendrahostel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class warden extends AppCompatActivity {

    SessionMaintance sessionMaintance;
    ListView listview;
    List<String> id_list = new ArrayList<>();
    List<String> name_list = new ArrayList<>();
    List<String> rollno_list = new ArrayList<>();
    List<String> fromdate_list = new ArrayList<>();
    List<String> fromtime_list = new ArrayList<>();
    List<String> enddate_list = new ArrayList<>();
    List<String> endtime_list = new ArrayList<>();
    List<String> request_type_list = new ArrayList<>();
    List<String> outing_reason_list = new ArrayList<>();
    List<String> father_no_list = new ArrayList<>();
    List<String> mother_no_list = new ArrayList<>();
    List<String> image_list = new ArrayList<>();
    List<String> req_img_list = new ArrayList<>();

    StringBuffer sb = new StringBuffer();
    String json_url = Url_interface.url+"logout_mobile/";
    String json_string="";
    ProgressDialog progressDialog;

    StringBuffer sb2 = new StringBuffer();
    String json_url2 = Url_interface.url+"request_approval_deny_mobile/";
    String json_string2="";

    TextView tv_nrf;

    CustomAdapter customAdapter;

    String[] permissions = {Manifest.permission.CALL_PHONE};
    int permission_All = 1;

    View customview;
    LayoutInflater inflater;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        Drawable background = getResources().getDrawable(R.drawable.gradient_color); //bg_gradient is your gradient.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_color));
        setContentView(R.layout.activity_warden);
        sessionMaintance = new SessionMaintance(warden.this);
        getSupportActionBar().setTitle(sessionMaintance.get_user_unique());



        listview = findViewById(R.id.listview);
        listview.setDivider(null);
        tv_nrf = findViewById(R.id.txtview_nrf);

        inflater = (LayoutInflater)warden.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        customview = inflater.inflate(R.layout.custom_layout_data_connection, null);

        ConnectivityManager connManager = (ConnectivityManager) warden.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected())
                || (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnected())) {

            progressDialog = new ProgressDialog(warden.this);
            progressDialog.setMessage("Please Wait...!!!");
            progressDialog.setCanceledOnTouchOutside(false);
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




        customAdapter = new CustomAdapter();

        if(!haspermission(this,permissions)) {
            ActivityCompat.requestPermissions(this, permissions, permission_All);
        }
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


        @Override
        public int getCount() {

            return name_list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view1, ViewGroup viewGroup) {


            view1 = getLayoutInflater().inflate(R.layout.custom_list_rowitem_warden, null);


            TextView tv_name = (TextView) view1.findViewById(R.id.tv_name);
            TextView tv_reg_no = (TextView) view1.findViewById(R.id.tv_reg_no);
            TextView tv_from_date = (TextView) view1.findViewById(R.id.tv_from_date);
            TextView tv_enddate = (TextView) view1.findViewById(R.id.tv_enddate);
            TextView tv_from_time= (TextView) view1.findViewById(R.id.tv_from_time);
            TextView tv_endtime = (TextView)view1.findViewById(R.id.tv_endtime);
            TextView tv_type = (TextView)view1.findViewById(R.id.tv_type);
            TextView tv_reason = (TextView)view1.findViewById(R.id.tv_reason);
            ImageView img_view = (ImageView) view1.findViewById(R.id.img_approve);
            TextView tv_father_no = view1.findViewById(R.id.tv_father_no);
            TextView tv_mother_no = view1.findViewById(R.id.tv_mother_no);
            ImageView father_call_img = view1.findViewById(R.id.img_father_call);
            ImageView mother_call_img = view1.findViewById(R.id.img_mother_call);
            LinearLayout father_layout = view1.findViewById(R.id.father_layout);

            tv_name.setText(name_list.get(i));
            tv_reg_no.setText("("+rollno_list.get(i)+")");

            String inputPattern = "yyyy-MM-dd";
            String outputPattern = "dd-MM-yyyy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            Date date = null;
            String str = null;
            try {
                date = inputFormat.parse(fromdate_list.get(i));
                str = outputFormat.format(date);
                tv_from_date.setText(str);
                //Log.d("FFFFDATE",str);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat HHmmFormat = new SimpleDateFormat("HH:mm", Locale.US);

            SimpleDateFormat hhmmampmFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            String outputDateStr = "";
            String ftime = fromtime_list.get(i);
            if(ftime.equals("null")){

            }else {
                String fromtime[]=ftime.split(":");
                if(fromtime.length>=2){
                    outputDateStr = parseDate(fromtime_list.get(i), HHmmFormat, hhmmampmFormat);
                    Log.d("otime",fromtime_list.get(i));
                    tv_from_time.setText(outputDateStr);
                    //Log.i("output_string", outputDateStr);
                }else {
                    ftime=ftime+":00";
                    outputDateStr = parseDate(ftime, HHmmFormat, hhmmampmFormat);
                    tv_from_time.setText(outputDateStr);

                }


            }




           // tv_from_time.setText(fromtime_list.get(i));

            Date date1 = null;
            String str1 = null;
            try {
                date1 = inputFormat.parse(enddate_list.get(i));
                str1 = outputFormat.format(date1);
                tv_enddate.setText(str1);
                //Log.d("FFFFDATE",str);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            String etime = endtime_list.get(i);
            if(etime.equals("null")){

            }else {
                String endtime[] = etime.split(":");
                if (endtime.length >= 2) {
                    outputDateStr = parseDate(endtime_list.get(i), HHmmFormat, hhmmampmFormat);
                    Log.d("etime", endtime_list.get(i));
                    tv_endtime.setText(outputDateStr);
                    //Log.i("output_string", outputDateStr);
                } else {
                    etime = etime + ":00";
                    outputDateStr = parseDate(etime, HHmmFormat, hhmmampmFormat);
                    tv_endtime.setText(outputDateStr);

                }
            }

            //tv_endtime.setText(endtime_list.get(i));
            tv_type.setText(request_type_list.get(i));
            tv_reason.setText(outing_reason_list.get(i));
            if(father_no_list.get(i).equals("") || father_no_list.get(i).equals("null")){
                father_layout.setVisibility(View.GONE);
            }else {
                father_layout.setVisibility(View.VISIBLE);
            }
            tv_father_no.setText(father_no_list.get(i));
            tv_mother_no.setText(mother_no_list.get(i));

            img_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Bottomsheet_dialog_warden bottomSheetDialog = new Bottomsheet_dialog_warden(warden.this,
//                            id_list.get(i),name_list.get(i),rollno_list.get(i),fromdate_list.get(i),fromtime_list.get(i),
//                            enddate_list.get(i),endtime_list.get(i),request_type_list.get(i),outing_reason_list.get(i),
//                            father_no_list.get(i),mother_no_list.get(i),image_list.get(i),req_img_list.get(i));
//                    bottomSheetDialog.setCancelable(false);
//                    bottomSheetDialog.show(getSupportFragmentManager(),"Dialog");

                    Intent intent = new Intent(warden.this,single_student_approve.class);
                    intent.putExtra("ID",id_list.get(i));
                    intent.putExtra("NAME",name_list.get(i));
                    intent.putExtra("ROLLNO",rollno_list.get(i));
                    intent.putExtra("FROMDATE",fromdate_list.get(i));
                    intent.putExtra("FROMTIME",fromtime_list.get(i));
                    intent.putExtra("ENDDATE",enddate_list.get(i));
                    intent.putExtra("ENDTIME",endtime_list.get(i));
                    intent.putExtra("REQTYPE",request_type_list.get(i));
                    intent.putExtra("OUTREASON",outing_reason_list.get(i));
                    intent.putExtra("FATHERNO",father_no_list.get(i));
                    intent.putExtra("STUIMAGE",image_list.get(i));
                    intent.putExtra("REQLETTER",req_img_list.get(i));
                    startActivity(intent);

                }
            });

            father_call_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String father_str=tv_father_no.getText().toString();
                    if(father_str.length()==10){
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + father_str));
                        startActivity(intent);
                    }else {

                    }

                }
            });

            mother_call_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mother_str = tv_mother_no.getText().toString();
                    if(mother_str.length()==10){
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mother_str));
                        startActivity(intent);
                    }else {

                    }

                }
            });

            return view1;

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
                    //startActivity(new Intent(warden.this,Login.class));
                    Intent intent = new Intent(warden.this,Login.class);
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
                //Log.d("TTTTTTT",sessionMaintance.get_user_token());
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.setDoInput(true);
//                JSONObject jsonObject = new JSONObject();
//                Log.i("JSON", jsonObject.toString());
//                DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
//                os.writeBytes(jsonObject.toString());
//                os.flush();
//                os.close();
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
           Log.d("addresult2",json_string2);
            try {
                JSONObject jsonObject = new JSONObject(json_string2);
                if(jsonObject.getString("status").equals("Success")){
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
                    while(count<jsonArray.length()){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(count);

                        if(jsonObject1.getString("from_date").equals("null") || jsonObject1.getString("from_time").equals("null")){

                        }else {
                            id_list.add(jsonObject1.getString("id"));
                            name_list.add(jsonObject1.getString("firstname"));
                            rollno_list.add(jsonObject1.getString("rollno"));
                            request_type_list.add(jsonObject1.getString("request_type"));
                            fromdate_list.add(jsonObject1.getString("from_date"));
                            enddate_list.add(jsonObject1.getString("to_date"));
                            fromtime_list.add(jsonObject1.getString("from_time"));
                            endtime_list.add(jsonObject1.getString("to_time"));
                            outing_reason_list.add(jsonObject1.getString("outing_reason"));
                            image_list.add("http://13.232.51.73:8080/"+(jsonObject1.getString("image")));
                            father_no_list.add(jsonObject1.getString("parentContact"));
                            mother_no_list.add("9876543210");
                            req_img_list.add("http://13.232.51.73:8080/media/request_letter/"+(jsonObject1.getString("request_letter")));
                        }

                        count++;
                    }

                    Log.d("REQLET",image_list.toString());
                    if(id_list.size()>0){
                        listview.setAdapter(customAdapter);
                        listview.setVisibility(View.VISIBLE);
                        tv_nrf.setVisibility(View.GONE);
                    }else{
                        tv_nrf.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.GONE);
                    }
                }else {
                    if(id_list.size()>0){
                        listview.setAdapter(customAdapter);
                        listview.setVisibility(View.VISIBLE);
                        tv_nrf.setVisibility(View.GONE);
                    }else{
                        tv_nrf.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.GONE);
                    }

                }
            }catch (Exception e){

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                progressDialog.show();
                new backgroundworker().execute();
                return true;

            case R.id.loc:
                Intent intent = new Intent(warden.this,MapsActivity.class);
                startActivity(intent);
                return true;

            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}