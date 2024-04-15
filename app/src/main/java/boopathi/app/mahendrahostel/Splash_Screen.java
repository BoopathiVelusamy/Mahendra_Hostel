package boopathi.app.mahendrahostel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash_Screen extends AppCompatActivity {

    ImageView img_logo;
    TextView text,text0,text1,text2,text3,text4,text5,text6;
    TextView text_hostel;
    FrameLayout framelayout;

    //Logo Animation
    ScaleAnimation scaleInAnimation,scaleOutAnimation;
    TranslateAnimation translatelogo;
    AnimationSet setlogo;

    //Text Animation
    AnimationSet animSetText,animSetText0,animSetText1,animSetText2,animSetText3,animSetText4,animSetText5,animSetText6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.parseColor("#2a66af"));
        getWindow().setNavigationBarColor(Color.parseColor("#2a66af"));
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        img_logo = findViewById(R.id.img_logo);
        text = findViewById(R.id.text);
        text0 = findViewById(R.id.text0);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        text5 = findViewById(R.id.text5);
        text6 = findViewById(R.id.text6);
        text_hostel = findViewById(R.id.text_hostel);
        framelayout = findViewById(R.id.framelayout);

        //Logo Animation

        scaleInAnimation = new ScaleAnimation(1,2.0f,1,2.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleInAnimation.setDuration(500);
        scaleInAnimation.setFillAfter(true);

        scaleOutAnimation = new ScaleAnimation(2.0f,1,2.0f,1, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleOutAnimation.setDuration(500);

        translatelogo = new TranslateAnimation(0,0,0,0);
        translatelogo.setDuration(500);

        setlogo = new AnimationSet(true);
        setlogo.addAnimation(scaleOutAnimation);
        setlogo.addAnimation(translatelogo);
        setlogo.setFillAfter(true);

        //Text Animation

        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(500);

        TranslateAnimation translateAnimation1 = new TranslateAnimation(0,70,0,0);
        translateAnimation1.setDuration(500);

        TranslateAnimation translateAnimation2 = new TranslateAnimation(0,130,0,0);
        translateAnimation2.setDuration(500);

        TranslateAnimation translateAnimation3 = new TranslateAnimation(0,180,0,0);
        translateAnimation3.setDuration(500);

        TranslateAnimation translateAnimation4 = new TranslateAnimation(0,230,0,0);
        translateAnimation4.setDuration(500);

        TranslateAnimation translateAnimation5 = new TranslateAnimation(0,280,0,0);
        translateAnimation5.setDuration(500);

        TranslateAnimation translateAnimation6 = new TranslateAnimation(0,330,0,0);
        translateAnimation6.setDuration(500);

        TranslateAnimation translateAnimation7 = new TranslateAnimation(0,380,0,0);
        translateAnimation6.setDuration(500);

        TranslateAnimation translateAnimation8 = new TranslateAnimation(0,430,0,0);
        translateAnimation6.setDuration(500);

        animSetText = new AnimationSet(true);
        animSetText.addAnimation(alphaAnimation);
        animSetText.addAnimation(translateAnimation1);
        animSetText.setFillAfter(true);

        animSetText0 = new AnimationSet(true);
        animSetText0.addAnimation(alphaAnimation);
        animSetText0.addAnimation(translateAnimation2);
        animSetText0.setFillAfter(true);

        animSetText1 = new AnimationSet(true);
        animSetText1.addAnimation(alphaAnimation);
        animSetText1.addAnimation(translateAnimation3);
        animSetText1.setFillAfter(true);

        animSetText2 = new AnimationSet(true);
        animSetText2.addAnimation(alphaAnimation);
        animSetText2.addAnimation(translateAnimation4);
        animSetText2.setFillAfter(true);

        animSetText3 = new AnimationSet(true);
        animSetText3.addAnimation(alphaAnimation);
        animSetText3.addAnimation(translateAnimation5);
        animSetText3.setFillAfter(true);

        animSetText4 = new AnimationSet(true);
        animSetText4.addAnimation(alphaAnimation);
        animSetText4.addAnimation(translateAnimation6);
        animSetText4.setFillAfter(true);

        animSetText5 = new AnimationSet(true);
        animSetText5.addAnimation(alphaAnimation);
        animSetText5.addAnimation(translateAnimation7);
        animSetText5.setFillAfter(true);

        animSetText6 = new AnimationSet(true);
        animSetText6.addAnimation(alphaAnimation);
        animSetText6.addAnimation(translateAnimation8);
        animSetText6.setFillAfter(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                img_logo.startAnimation(scaleInAnimation);
            }
        },2000);

        scaleInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                img_logo.startAnimation(setlogo);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        setlogo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                text.startAnimation(animSetText);
                text0.startAnimation(animSetText0);
                text1.startAnimation(animSetText1);
                text2.startAnimation(animSetText2);
                text3.startAnimation(animSetText3);
                text4.startAnimation(animSetText4);
                text5.startAnimation(animSetText5);
                text6.startAnimation(animSetText6);

                text.setVisibility(View.VISIBLE);
                text0.setVisibility(View.VISIBLE);
                text1.setVisibility(View.VISIBLE);
                text2.setVisibility(View.VISIBLE);
                text3.setVisibility(View.VISIBLE);
                text4.setVisibility(View.VISIBLE);
                text5.setVisibility(View.VISIBLE);
                text6.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        text_hostel.setVisibility(View.VISIBLE);
                    }
                },500);



            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    SessionMaintance sessionMaintance = new SessionMaintance(Splash_Screen.this);
                    if(sessionMaintance.get_user_token().equals("") && sessionMaintance.get_user_role().equals("")) {
                        startActivity(new Intent(Splash_Screen.this, Login.class));
                    }else if(sessionMaintance.get_user_role().equals("STUDENT")){
                        startActivity(new Intent(Splash_Screen.this, MainActivity.class));
                    }else if(sessionMaintance.get_user_role().equals("FEMALE")){
                        startActivity(new Intent(Splash_Screen.this, MainActivity.class));
                    }else {
                        startActivity(new Intent(Splash_Screen.this,warden.class));
                    }
            }
        },4500);

    }
}