package boopathi.app.mahendrahostel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

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
import java.util.Date;
import java.util.Locale;

public class single_student_approve extends AppCompatActivity {

    ImageView photo_img,req_letter_img,img_father_call;
    TextView tv_name,tv_reg_no,tv_type,tv_from_date,tv_from_time,tv_enddate,tv_endtime,tv_father_no,tv_reason;
    EditText wreason_et;
    Button approve_btn,reject_btn;
    LinearLayout ll_img;
    LinearLayout father_layout;

    String id_str="",name_str="",roll_no_str="",fromdate_str="",fromtime_str="",enddate_str="",endtime_str="",req_type_str="",out_reason_str="",father_no_str="",stu_image_str="",req_letter_str="";
    String warden_comment_str="",approve_reject_str="",req_img_letter="";

    LayoutInflater inflater;
    View customView;
    PopupWindow mPopupWindow;

    StringBuffer sb = new StringBuffer();
    String json_url = Url_interface.url+"request_approval_deny_mobile/";
    String json_string="";
    ProgressDialog progressDialog;
    SessionMaintance sessionMaintance;

    boolean isAllFieldsChecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        Drawable background = getResources().getDrawable(R.drawable.gradient_color); //bg_gradient is your gradient.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_color));
        setContentView(R.layout.activity_single_student_approve);
        getSupportActionBar().setTitle("Approve/Reject Forms");
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        photo_img = findViewById(R.id.img_stu_photo);
        req_letter_img = findViewById(R.id.img_req);
        img_father_call = findViewById(R.id.img_father_call);
        tv_name = findViewById(R.id.tv_name);
        tv_reg_no = findViewById(R.id.tv_reg_no);
        tv_type = findViewById(R.id.tv_type);
        tv_from_date = findViewById(R.id.tv_from_date);
        tv_from_time = findViewById(R.id.tv_from_time);
        tv_enddate = findViewById(R.id.tv_enddate);
        tv_endtime = findViewById(R.id.tv_endtime);
        tv_father_no = findViewById(R.id.tv_father_no);
        tv_reason = findViewById(R.id.tv_reason);
        wreason_et = findViewById(R.id.wreason_et);
        approve_btn = findViewById(R.id.approve_btn);
        reject_btn = findViewById(R.id.reject_btn);
        ll_img = findViewById(R.id.ll_img);
        father_layout = findViewById(R.id.father_layout);

        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = inflater.inflate(R.layout.custom_photo_layout,null);

        Intent intent = getIntent();
        id_str = intent.getStringExtra("ID");
        name_str = intent.getStringExtra("NAME");
        roll_no_str = intent.getStringExtra("ROLLNO");
        fromdate_str = intent.getStringExtra("FROMDATE");
        fromtime_str = intent.getStringExtra("FROMTIME");
        enddate_str = intent.getStringExtra("ENDDATE");
        endtime_str = intent.getStringExtra("ENDTIME");
        req_type_str = intent.getStringExtra("REQTYPE");
        out_reason_str = intent.getStringExtra("OUTREASON");
        father_no_str = intent.getStringExtra("FATHERNO");
        stu_image_str = intent.getStringExtra("STUIMAGE");
        req_letter_str = intent.getStringExtra("REQLETTER");



        req_img_letter =req_letter_str.substring(req_letter_str.lastIndexOf("/")+1);
        Log.d("REQQQQ",req_img_letter);

        tv_name.setText(name_str);
        tv_reg_no.setText("("+roll_no_str+")");
        tv_type.setText(req_type_str);
        //tv_from_date.setText(fromdate_str);
        //tv_from_time.setText(fromtime_str);
        //tv_enddate.setText(enddate_str);
        //tv_endtime.setText(endtime_str);
        tv_reason.setText(out_reason_str);
        if(father_no_str.equals("") || father_no_str.equals("null")){
            father_layout.setVisibility(View.GONE);
        }else {
            father_layout.setVisibility(View.VISIBLE);
        }
        tv_father_no.setText(father_no_str);

        //from date
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(fromdate_str);
            str = outputFormat.format(date);
            tv_from_date.setText(str);
            //Log.d("FFFFDATE",str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //from time
        SimpleDateFormat HHmmFormat = new SimpleDateFormat("HH:mm", Locale.US);

        SimpleDateFormat hhmmampmFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String outputDateStr = "";
        String ftime = fromtime_str;
        if(ftime.equals("null")){

        }else {
            String fromtime[]=ftime.split(":");
            if(fromtime.length>=2){
                outputDateStr = parseDate(fromtime_str, HHmmFormat, hhmmampmFormat);
                Log.d("otime",fromtime_str);
                tv_from_time.setText(outputDateStr);
                //Log.i("output_string", outputDateStr);
            }else {
                ftime=ftime+":00";
                outputDateStr = parseDate(ftime, HHmmFormat, hhmmampmFormat);
                tv_from_time.setText(outputDateStr);

            }
        }

        //end date

        Date date1 = null;
        String str1 = null;
        try {
            date1 = inputFormat.parse(enddate_str);
            str1 = outputFormat.format(date1);
            tv_enddate.setText(str1);
            //Log.d("FFFFDATE",str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //end time

        String etime = endtime_str;
        if(etime.equals("null")){

        }else {
            String endtime[] = etime.split(":");
            if (endtime.length >= 2) {
                outputDateStr = parseDate(endtime_str, HHmmFormat, hhmmampmFormat);
                Log.d("etime", endtime_str);
                tv_endtime.setText(outputDateStr);
                //Log.i("output_string", outputDateStr);
            } else {
                etime = etime + ":00";
                outputDateStr = parseDate(etime, HHmmFormat, hhmmampmFormat);
                tv_endtime.setText(outputDateStr);

            }
        }



        Picasso.get().load(stu_image_str)
                .error(R.mipmap.logo_small)
                .into(photo_img);

        if(req_img_letter.equals("")){
            ll_img.setVisibility(View.GONE);
            req_letter_img.setVisibility(View.GONE);
        }else {
            ll_img.setVisibility(View.VISIBLE);
            req_letter_img.setVisibility(View.VISIBLE);
            Picasso.get().load(req_letter_str)
                    .error(R.mipmap.logo_small)
                    .into(req_letter_img);
        }


        img_father_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String father_str=tv_father_no.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + father_str));
                startActivity(intent);
            }
        });

        req_letter_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPopupWindow = new PopupWindow(
                        customView,
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        true
                );
                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }

                ImageView closeButton = (ImageView) customView.findViewById(R.id.imageView6);
                PhotoView photoView = customView.findViewById(R.id.img_photo_view);

                Picasso.get().load(req_letter_str)
                        .error(R.mipmap.logo_small)
                        .into(photoView);

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();

                    }
                });

                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.update();
                mPopupWindow.setContentView(customView);
                mPopupWindow.showAtLocation(customView, Gravity.CENTER, 0, 0);


            }
        });

        approve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllFieldsChecked = CheckAllFields();
                if(isAllFieldsChecked) {
                    warden_comment_str = wreason_et.getText().toString();
                    approve_reject_str = "Approved";
                    progressDialog = new ProgressDialog(single_student_approve.this);
                    progressDialog.setMessage("Please Wait...!!!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    new backgroundworker().execute();

                }
            }
        });

        reject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllFieldsChecked = CheckAllFields();
                if(isAllFieldsChecked) {
                    warden_comment_str = wreason_et.getText().toString();
                    approve_reject_str = "Rejected";
                    progressDialog = new ProgressDialog(single_student_approve.this);
                    progressDialog.setMessage("Please Wait...!!!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    new backgroundworker().execute();
                }
            }
        });


    }

    private boolean CheckAllFields(){

        if(wreason_et.getText().toString().equals("")){
            wreason_et.setError("Reason is required");
            return false;
        }
        return true;
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
//                Intent intent = new Intent(single_student_approve.this,warden.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                this.finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
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
                sessionMaintance = new SessionMaintance(single_student_approve.this);
                httpURLConnection.setRequestProperty("Authorization","token "+sessionMaintance.get_user_token());
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", Integer.parseInt(id_str));
                jsonObject.put("approve_reject_status", approve_reject_str);
                jsonObject.put("comments", warden_comment_str);


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
            Log.d("addresult",json_string);
            try {
                JSONObject jsonObject = new JSONObject(json_string);
                if(jsonObject.getString("status").equals("Success")){
                    Toast.makeText(single_student_approve.this, approve_reject_str+" Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(single_student_approve.this,warden.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }catch (Exception e){
                e.printStackTrace();

            }
        }
    }
}