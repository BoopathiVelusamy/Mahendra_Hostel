package boopathi.app.mahendrahostel;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;


public class BottomSheetDialog_photo extends BottomSheetDialogFragment{

    View v;
    Context contexter;
    String req_img;

    ImageView close_img;
    PhotoView photoView;


    public BottomSheetDialog_photo(Context contexterr,String reqimg) {
        contexter = contexterr;
        this.req_img = reqimg;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.custom_photo_layout,
                container, false);


        close_img = (ImageView) v.findViewById(R.id.imageView6);
        photoView = v.findViewById(R.id.img_photo_view);

        Picasso.get().load(req_img)
                        .error(R.mipmap.logo_small)
                        .into(photoView);

        close_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



        return v;
    }
}

