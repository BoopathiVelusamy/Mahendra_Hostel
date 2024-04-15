package boopathi.app.mahendrahostel;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;

public class Bottom_Shett_Dialog_File_Chooser extends BottomSheetDialogFragment {

    View v;
    Context contexter;

    ImageView imageView;
    LinearLayout linearlayout_camera,linearlayout_gallery;

    final int REQUEST_IMAGE_FROM_GALLERY = 200;
    final int REQUEST_IMAGE_FROM_CAMERA = 100;

    File selectedImagepath;
    String selectedpath="";

    public Bottom_Shett_Dialog_File_Chooser(Context contexter) {
        this.contexter = contexter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.custom_camera_gallery_layout,
                container, false);

        imageView = v.findViewById(R.id.imageView6);
        linearlayout_camera = v.findViewById(R.id.linearlayout_camera);
        linearlayout_gallery = v.findViewById(R.id.linearlayout_gallery);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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

        return v;
    }

    private void pickImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_FROM_CAMERA);
    }

    private void pickImageFromDeviceGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_IMAGE_FROM_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_FROM_GALLERY && null != data) {
            Uri selectedImage = data.getData();
            selectedpath = FilePath.getPath(contexter,selectedImage);
            selectedImagepath = new File(FilePath.getPath(contexter, selectedImage));
            Log.d("UUURI2222", String.valueOf(selectedImagepath)+"----"+selectedpath);

            //tv_file_text.setText(selectedpath.substring(selectedpath.lastIndexOf("/")+1));
            //tv_file_text.setTextColor(Color.parseColor("#000000"));

        }else if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_FROM_CAMERA && null != data){
            Uri selectedImage = data.getData();
            selectedpath = FilePath.getPath(contexter,selectedImage);
            selectedImagepath = new File(FilePath.getPath(contexter, selectedImage));
            Log.d("UUURI3333", String.valueOf(selectedImagepath)+"----"+selectedpath);

        }
    }
}
