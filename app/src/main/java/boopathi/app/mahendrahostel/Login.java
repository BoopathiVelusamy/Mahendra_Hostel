package boopathi.app.mahendrahostel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {

    EditText et_username,et_password;
    Button bt_login;
    ConstraintLayout mainlayout;
    StringBuffer sb = new StringBuffer();
    String json_url = Url_interface.url+"login_mobile/";
    String json_string="";
    ProgressDialog progressDialog;
    String susername="",spassword="";
    SessionMaintance sessionMaintance;

    String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.FOREGROUND_SERVICE, android.Manifest.permission.FOREGROUND_SERVICE_LOCATION, Manifest.permission.POST_NOTIFICATIONS,
    Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.CAMERA,Manifest.permission.SCHEDULE_EXACT_ALARM,
            Manifest.permission.CAMERA,Manifest.permission.SET_ALARM,Manifest.permission.USE_EXACT_ALARM};
    int permission_All = 1;

    View customview;
    LayoutInflater inflater;
    PopupWindow popupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        intialise();
        mainlayout = findViewById(R.id.mainlayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) mainlayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        AnimationDrawable animationDrawable1 = (AnimationDrawable) bt_login.getBackground();
        animationDrawable1.setEnterFadeDuration(2500);
        animationDrawable1.setExitFadeDuration(5000);
        animationDrawable1.start();

        if(!haspermission(this,permissions)) {
            ActivityCompat.requestPermissions(this, permissions, permission_All);
        }

        inflater = (LayoutInflater)Login.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        customview = inflater.inflate(R.layout.custom_layout_data_connection, null);




        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                susername = et_username.getText().toString();
                spassword = et_password.getText().toString();
                if(susername.length()>0&&spassword.length()>0) {

                    ConnectivityManager connManager = (ConnectivityManager) Login.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if ((connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connManager
                            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected())
                            || (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connManager
                            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                            .isConnected())) {
                        progressDialog.show();
                        new backgroundworker().execute();

                    }else {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                show1();
                            }
                        },2000);

                    }

                }else{
                    Toast.makeText(Login.this,"Username or Password is Empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                sb = new StringBuffer();
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", susername);
                jsonObject.put("password", spassword);

                Log.i("JSON", jsonObject.toString());

                DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
                os.writeBytes(jsonObject.toString());
                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(httpURLConnection.getResponseCode()));
                Log.i("MSG", String.valueOf(httpURLConnection.getDoOutput()));

                InputStream inputStream=null;
                BufferedReader bufferedReader = null;

                int responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    inputStream = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


                    while ((json_string = bufferedReader.readLine()) != null) {
                        sb.append(json_string + "\n");
                        Log.d("json_string1", "" + json_string);
                    }
                }else{
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
                Log.d("GGG1", "" + sb.toString());
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
            Log.d("loginresult",json_string);
            try {
                JSONObject jsonObject = new JSONObject(json_string);
                if(jsonObject.getString("status").equals("Success")){

                    if(jsonObject.getString("role").equalsIgnoreCase("STUDENT")){
                        SessionMaintance sessionMaintance1 = new SessionMaintance(Login.this);
                        sessionMaintance1.set_user_token(jsonObject.getString("token"));
                        sessionMaintance1.set_user_unique(jsonObject.getString("username"));
                        sessionMaintance1.set_user_name(jsonObject.getString("name"));
                        //sessionMaintance1.set_user_id(jsonObject.getString("rollno"));
                        sessionMaintance1.set_user_role(jsonObject.getString("role"));
                        sessionMaintance1.set_user_id(jsonObject.getString("profile"));
                        sessionMaintance1.set_user_gender(jsonObject.getString("gender"));

                        Log.d("TOKEN1",sessionMaintance1.get_user_token());

                        startActivity(new Intent(Login.this,MainActivity.class));

                    }else if(jsonObject.getString("role").equalsIgnoreCase("WARDEN")){
                        SessionMaintance sessionMaintance2 = new SessionMaintance(Login.this);
                        sessionMaintance2.set_user_token(jsonObject.getString("token"));
                        sessionMaintance2.set_user_name(jsonObject.getString("name"));
                        sessionMaintance2.set_user_unique(jsonObject.getString("username"));
                        sessionMaintance2.set_user_id(jsonObject.getString("profile"));
                        sessionMaintance2.set_user_role(jsonObject.getString("role"));

                        Log.d("TOKEN2",sessionMaintance2.get_user_token());

                        startActivity(new Intent(Login.this,warden.class));
                    }

                    //overridePendingTransition(R.anim.slide_in_right,
                    //R.anim.slide_out_left);
                }else {
                    Toast.makeText(Login.this,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                }



            }catch (Exception e){
                e.printStackTrace();

            }
        }
    }

    public void intialise(){

        bt_login = findViewById(R.id.login);
        et_username = findViewById(R.id.username);
        et_password = findViewById(R.id.password);

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Please Wait...!!!");
        progressDialog.setCanceledOnTouchOutside(false);

        sessionMaintance = new SessionMaintance(Login.this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}