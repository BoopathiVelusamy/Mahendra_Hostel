package boopathi.app.mahendrahostel;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BottomSheetDialog  extends BottomSheetDialogFragment{

    View v;
    Context contexter;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    ImageView imageView;
    EditText et_fromdate,et_fromtime,et_enddate,et_endtime,et_reason;
    Button submit;
    Spinner spinner;
    ImageView img_fromdate,img_todate;
    TextView tv_choose_file,tv_file_text;

    List<String> type_list = new ArrayList<>();

    String fromdate_str="",fromtime_str="",enddate_str="",endtime_str="",reason_str="",request_type="",selectedpath="";
    int myday, myMonth, myYear, myHour, myMinute,hour, minute;
    File selectedImagepath;

    AlertDialog dialog;

    boolean isAllFieldsChecked = false;

    StringBuffer sb = new StringBuffer();
    String json_url = Url_interface.url+"request_mobile/";
    String json_string="";
    ProgressDialog progressDialog;
    SessionMaintance sessionMaintance;
    final int REQUEST_IMAGE_FROM_GALLERY = 200;
    final  int REQUEST_IMAGE_FROM_CAMERA=100;

    public BottomSheetDialog(Context contexterr) {
        contexter = contexterr;
        sessionMaintance = new SessionMaintance(this.contexter);

        progressDialog = new ProgressDialog(contexter);
        progressDialog.setMessage("Please Wait...!!!");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.custom_bottomsheet_layout,
                container, false);

        imageView = v.findViewById(R.id.imageView6);
        et_fromdate = v.findViewById(R.id.fromtask_date);
        et_fromtime = v.findViewById(R.id.fromtask_time);
        et_enddate = v.findViewById(R.id.endtask_date);
        et_endtime = v.findViewById(R.id.endtask_time);
        et_reason = v.findViewById(R.id.reason_et);
        submit = v.findViewById(R.id.submit);
        spinner = v.findViewById(R.id.spinner_type);
        img_fromdate = v.findViewById(R.id.img_fromdate);
        img_todate = v.findViewById(R.id.img_todate);
        tv_choose_file = v.findViewById(R.id.tv_choose_file);
        tv_file_text = v.findViewById(R.id.tv_file_text);

        type_list.add("Select");
        type_list.add("Outing");
        type_list.add("Holiday");


        tv_choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pickImageFromDeviceGallery();

//                Bottom_Shett_Dialog_File_Chooser bottomShettDialogFileChooser = new Bottom_Shett_Dialog_File_Chooser(contexter);
//                bottomShettDialogFileChooser.setCancelable(false);
//                bottomShettDialogFileChooser.show(getActivity().getSupportFragmentManager(),"Dialog");

                // Create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(contexter);


                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_camera_gallery_layout, null);
                builder.setView(customLayout);
                ImageView close_img = customLayout.findViewById(R.id.imageView6);
                LinearLayout linearlayout_camera = customLayout.findViewById(R.id.linearlayout_camera);
                LinearLayout linearlayout_gallery = customLayout.findViewById(R.id.linearlayout_gallery);


                dialog = builder.create();
                dialog.setContentView(R.layout.custom_camera_gallery_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
//                Window window = dialog.getWindow();
//                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                close_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                linearlayout_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickImageFromCamera();
                    }
                });

                linearlayout_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickImageFromDeviceGallery();

                    }
                });
                
            }
        });


        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(contexter, R.layout.custom_spinner_layout, type_list);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter3);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        img_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });

        img_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker1();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isAllFieldsChecked = CheckAllFields();

                if(isAllFieldsChecked) {


                    reason_str = et_reason.getText().toString();
                    request_type = spinner.getSelectedItem().toString();

                    UploadTask uploadTask = new UploadTask(selectedImagepath, request_type, fromdate_str, enddate_str, fromtime_str, endtime_str, reason_str);
                    uploadTask.execute();


                }


            }
        });

        return v;
    }

    private boolean CheckAllFields(){

        if(spinner.getSelectedItemPosition()<1){
            TextView textView = (TextView)spinner.getSelectedView();
            textView.setError("");
            textView.setTextColor(Color.RED);
            textView.setText("This field is required");
            return false;
        }
        if(et_fromdate.getText().toString().equals("")){
            //et_fromdate.setError("From date is required");
            Toast.makeText(contexter,"From Date & Time is required",Toast.LENGTH_LONG).show();
            return false;
        }
        if(et_fromtime.getText().toString().equals("")){
            //et_fromtime.setError("From time is required");
            return false;
        }
        if(et_enddate.getText().toString().equals("")){
            //et_enddate.setError("To date is required");
            Toast.makeText(contexter,"To Date & Time is required",Toast.LENGTH_LONG).show();
            return false;
        }
        if(et_endtime.getText().toString().equals("")){
            //et_endtime.setError("To time is required");
            return false;
        }
        if(et_reason.getText().toString().equals("")){
            et_reason.setError("Reason is required");
            return false;
        }

        return true;
    }

    private void pickImageFromDeviceGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_IMAGE_FROM_GALLERY);
    }

    private void pickImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_FROM_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_FROM_GALLERY && null != data) {
            Uri selectedImage = data.getData();
            selectedpath = FilePath.getPath(contexter,selectedImage);
            selectedImagepath = new File(FilePath.getPath(contexter, selectedImage));
            Log.d("UUURI", String.valueOf(selectedImagepath));

            tv_file_text.setText(selectedpath.substring(selectedpath.lastIndexOf("/")+1));
            tv_file_text.setTextColor(Color.parseColor("#000000"));
            dialog.cancel();

        }else if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_FROM_CAMERA && null != data){
            Bitmap bitmap = (Bitmap)(data.getExtras().get("data"));
            Uri selectedImage = getImageUri(contexter,bitmap);
            selectedpath = FilePath.getPath(contexter,selectedImage);
            selectedImagepath = new File(FilePath.getPath(contexter, selectedImage));
            Log.d("UUURI3333", String.valueOf(selectedImagepath)+"----"+selectedpath);

            tv_file_text.setText(selectedpath.substring(selectedpath.lastIndexOf("/")+1));
            tv_file_text.setTextColor(Color.parseColor("#000000"));

            dialog.cancel();

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Request", null);
        return Uri.parse(path);
    }


    public class UploadTask extends AsyncTask<Void, Void, String> {

        private static final String POST_URL = Url_interface.url+"request_mobile/";
        private File imageFile;
        private String requesttype,fromdatestr,enddatestr,fromtimestr,endtimestr,reasonstr;

        public UploadTask(File imageFile, String req_type,String f_date_str,String e_date_str,String f_time_str,String e_time_str,String rea_str) {
            this.imageFile = imageFile;
            this.requesttype = req_type;
            this.fromdatestr = f_date_str;
            this.enddatestr = e_date_str;
            this.fromtimestr = f_time_str;
            this.endtimestr = e_time_str;
            this.reasonstr = rea_str;

        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = null;
            // Add image file if provided
            if (imageFile != null) {

            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("req_letter", imageFile.getName(),
                            RequestBody.create(MediaType.parse("image/*"), imageFile))
                    .addFormDataPart("request_type", requesttype)
                    .addFormDataPart("from_date",fromdatestr)
                    .addFormDataPart("to_date",enddatestr)
                    .addFormDataPart("from_time",fromtimestr)
                    .addFormDataPart("to_time",endtimestr)
                    .addFormDataPart("reason",reasonstr)
                    .build();
            
            }else {

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("request_type", requesttype)
                        .addFormDataPart("from_date",fromdatestr)
                        .addFormDataPart("to_date",enddatestr)
                        .addFormDataPart("from_time",fromtimestr)
                        .addFormDataPart("to_time",endtimestr)
                        .addFormDataPart("reason",reasonstr)
                        .build();
                
            }



            Request request = new Request.Builder()
                    .url(POST_URL)
                    .header("Authorization", "token "+sessionMaintance.get_user_token())
                    //.header("Content-Type", "application/json;charset=UTF-8")
                    //.header("Accept","application/json")
                    .post(requestBody)
                    .build();

            Response response = null;

            try {
                response = client.newCall(request).execute();

                    Log.d("RRRRREEESS",response.toString());
                    return response.body().string();


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }finally {
                response.close();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the response here

            Log.d("RRRRES",result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if(status.equals("Success")){

                    Toast.makeText(contexter, "Request Inserted Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(contexter,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    dismiss();

                }else {
                    Toast.makeText(contexter, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();

        //past date disable
        Calendar fivedaysago = (Calendar) c.clone();
        fivedaysago.add(Calendar.DATE,-2);

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(contexter, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                et_fromdate.setText(dayOfMonth + "-"
                        + (monthOfYear + 1) + "-" + year);
                fromdate_str = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;

                tiemPicker();


            }
        },mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(fivedaysago.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000l);
        datePickerDialog.show();
    }

    private void datePicker1(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();

        //past date disable
        Calendar fivedaysago = (Calendar) c.clone();
        fivedaysago.add(Calendar.DATE,-2);

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(contexter, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                et_enddate.setText(dayOfMonth + "-"
                        + (monthOfYear + 1) + "-" + year);
                enddate_str = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;

                tiemPicker1();


            }
        },mYear, mMonth, mDay);
        //datePickerDialog.getDatePicker().setMinDate(fivedaysago.getTimeInMillis());
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000l);
        datePickerDialog.show();
    }

    private void tiemPicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    myHour = hourOfDay;
                    myMinute = minute;
                    fromtime_str=myHour + ":" + myMinute;
                    et_fromtime.setText(myHour + ":" + myMinute);


            }
        };
//        timePickerDialog = new TimePickerDialog(contexter, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                myHour = hourOfDay;
//                myMinute = minute;
//                fromtime_str=myHour + ":" + myMinute;
//                et_fromtime.setText(myHour + ":" + myMinute);
//
//            }
//        },hour,minute,true);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Choose hour:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        timePickerDialog.show();
    }

    private void tiemPicker1(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    myHour = hourOfDay;
                    myMinute = minute;
                    endtime_str=myHour + ":" + myMinute;
                    et_endtime.setText(myHour + ":" + myMinute);


            }
        };
//        timePickerDialog = new TimePickerDialog(contexter, 2,new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                myHour = hourOfDay;
//                myMinute = minute;
//                endtime_str=myHour + ":" + myMinute;
//                et_endtime.setText(myHour + ":" + myMinute);
//
//            }
//        },hour,minute,true);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Choose hour:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }
}

