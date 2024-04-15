package boopathi.app.mahendrahostel;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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

public class Bottomsheet_dialog_warden extends BottomSheetDialogFragment {

    View v;
    Context contexter;

    LayoutInflater inflater1;
    View customView;
    PopupWindow mPopupWindow;

    ImageView imageView,father_call_img,mother_call_img,img_stu_photo;
    TextView tv_name,tv_reg_no,tv_from_date,tv_from_time,tv_enddate,tv_endtime,tv_type,tv_reason,tv_father_no,tv_mother_no,tv_image_view;
    EditText et_warden_comment;
    ConstraintLayout wardenlayout;
    Button btn_approve,btn_reject;
    String id_str="",name_str="",rollno_str="",fromdate_str="",fromtime_str="",endate_str="",endtime_str="",request_str="",reason_str="",father_no_str="",mother_no_str="",image_str="",approve_reject_str="",warden_comment_str="",req_img_list="";

    StringBuffer sb = new StringBuffer();
    String json_url = Url_interface.url+"request_approval_deny_mobile/";
    String json_string="";
    ProgressDialog progressDialog;
    SessionMaintance sessionMaintance;

    AlertDialog dialog;


    public Bottomsheet_dialog_warden(Context contexter, String id, String name, String rollno, String fromdate, String fromtime,
                                     String enddate, String endtime, String requesttype, String reason, String fatherno, String motherno, String image,String reqletterimg) {
        this.contexter = contexter;
        this.id_str=id;
        this.name_str=name;
        this.rollno_str = rollno;
        this.fromdate_str=fromdate;
        this.fromtime_str = fromtime;
        this.endate_str = enddate;
        this.endtime_str = endtime;
        this.request_str = requesttype;
        this.reason_str = reason;
        this.father_no_str = fatherno;
        this.mother_no_str = motherno;
        this.image_str = image;
        this.req_img_list = reqletterimg;

        sessionMaintance = new SessionMaintance(this.contexter);

        progressDialog = new ProgressDialog(contexter);
        progressDialog.setMessage("Please Wait...!!!");
        progressDialog.setCanceledOnTouchOutside(false);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.custom_bottomsheet_warden_layout,
                container, false);

        imageView = v.findViewById(R.id.imageView6);
        tv_name = (TextView) v.findViewById(R.id.tv_name);
        tv_reg_no = (TextView) v.findViewById(R.id.tv_reg_no);
        tv_from_date = (TextView) v.findViewById(R.id.tv_from_date);
        tv_enddate = (TextView) v.findViewById(R.id.tv_enddate);
        tv_from_time= (TextView) v.findViewById(R.id.tv_from_time);
        tv_endtime = (TextView)v.findViewById(R.id.tv_endtime);
        tv_type = (TextView)v.findViewById(R.id.tv_type);
        tv_reason = (TextView)v.findViewById(R.id.tv_reason);
        et_warden_comment = (EditText) v.findViewById(R.id.wreason_et);
        btn_approve = v.findViewById(R.id.approve_btn);
        btn_reject = v.findViewById(R.id.reject_btn);
        tv_father_no = v.findViewById(R.id.tv_father_no);
        tv_mother_no = v.findViewById(R.id.tv_mother_no);
        father_call_img = v.findViewById(R.id.img_father_call);
        mother_call_img = v.findViewById(R.id.img_mother_call);
        img_stu_photo = v.findViewById(R.id.img_stu_photo);
        tv_image_view = v.findViewById(R.id.tv_image_view);
        wardenlayout = v.findViewById(R.id.wardenlayout);

        tv_name.setText(name_str);
        tv_reg_no.setText("("+rollno_str+")");

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


        //tv_from_date.setText(fromdate_str);
        //tv_from_time.setText(fromtime_str);

        Date date1 = null;
        String str1 = null;
        try {
            date1 = inputFormat.parse(endate_str);
            str1 = outputFormat.format(date1);
            tv_enddate.setText(str1);
            //Log.d("FFFFDATE",str);
        } catch (ParseException e) {
            e.printStackTrace();
        }


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

        //tv_enddate.setText(endate_str);
        //tv_endtime.setText(endtime_str);
        tv_type.setText(request_str);
        tv_reason.setText(reason_str);
        tv_father_no.setText(father_no_str);
        tv_mother_no.setText(mother_no_str);

        inflater1 = (LayoutInflater) contexter.getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = inflater.inflate(R.layout.custom_photo_layout,null);



        Log.d("IIIMMMM",image_str+"-"+req_img_list);

        Picasso.get().load(image_str)
                .error(R.mipmap.logo_small)
                .into(img_stu_photo);

        father_call_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String father_str=tv_father_no.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + father_str));
                startActivity(intent);
            }
        });

        mother_call_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mother_str = tv_mother_no.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mother_str));
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                warden_comment_str = et_warden_comment.getText().toString();
                approve_reject_str = "Approved";
                progressDialog.show();
                new backgroundworker().execute();

            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warden_comment_str = et_warden_comment.getText().toString();
                approve_reject_str = "Rejected";
                progressDialog.show();
                new backgroundworker().execute();

            }
        });

        tv_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                BottomSheetDialog_photo bottomSheetDialog_photo = new BottomSheetDialog_photo(contexter,req_img_list);
//                bottomSheetDialog_photo.setCancelable(false);
//                bottomSheetDialog_photo.show(getParentFragmentManager(),"Dialog");


                // Create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(contexter);


                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_photo_layout, null);
                builder.setView(customLayout);

                ImageView close_img = customLayout.findViewById(R.id.imageView6);
                PhotoView photoView = customLayout.findViewById(R.id.img_photo_view);

                Picasso.get().load(req_img_list)
                        .error(R.mipmap.logo_small)
                        .into(photoView);

                dialog = builder.create();
//                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                lp.copyFrom(dialog.getWindow().getAttributes());
//                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                //dialog.getWindow().setAttributes(lp);


                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());

                // setting width to 90% of display
                layoutParams.width = (int) (displayMetrics.widthPixels * 0.9f);

                // setting height to 90% of display
                layoutParams.height = (int) (displayMetrics.heightPixels * 0.9f);
                dialog.getWindow().setAttributes(layoutParams);

//                dialog = builder.create();
//                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
//                dialog.show();

                close_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });


                //dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);


//                mPopupWindow = new PopupWindow(
//                        customView,
//                        ConstraintLayout.LayoutParams.MATCH_PARENT,
//                        ConstraintLayout.LayoutParams.MATCH_PARENT,
//                        true
//                );
//                if(Build.VERSION.SDK_INT>=21){
//                    mPopupWindow.setElevation(5.0f);
//                }
//
//                ImageView closeButton = (ImageView) customView.findViewById(R.id.imageView6);
//                PhotoView photoView = customView.findViewById(R.id.img_photo_view);
//
//                Picasso.get().load(req_img_list)
//                        .error(R.mipmap.logo_small)
//                        .into(photoView);
//
//                closeButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mPopupWindow.dismiss();
//
//                    }
//                });
//
//                mPopupWindow.setOutsideTouchable(true);
//                mPopupWindow.setFocusable(true);
//                mPopupWindow.update();
//                mPopupWindow.setContentView(customView);
//                mPopupWindow.showAtLocation(customView, Gravity.CENTER, 0, 0);


            }
        });

        return v;
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
                    dismiss();
                    Toast.makeText(contexter, approve_reject_str+" Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(contexter,warden.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }catch (Exception e){

            }
        }
    }
}
