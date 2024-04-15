package boopathi.app.mahendrahostel;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionMaintance {
    Context ctx;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    SessionMaintance(Context ctx)
    {
        this.ctx=ctx;
        prefs=ctx.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        editor=prefs.edit();
    }

    public void set_user_token(String user_token){
        editor.putString("user_token",user_token);
        editor.commit();
    }

    public String get_user_token(){ return prefs.getString("user_token","");}

    public void set_user_unique(String user_unique){
        editor.putString("user_unique",user_unique);
        editor.commit();
    }

    public String get_user_unique(){ return prefs.getString("user_unique","");}

    public void set_user_id(String user_id){
        editor.putString("user_id",user_id);
        editor.commit();
    }

    public String get_user_id(){ return prefs.getString("user_id","");}

    public void set_user_name(String user_name){
        editor.putString("user_name",user_name);
        editor.commit();
    }

    public String get_user_name(){ return prefs.getString("user_name","");}

    public void set_user_role(String user_role){
        editor.putString("user_role",user_role);
        editor.commit();
    }

    public String get_user_role(){ return prefs.getString("user_role","");}

    public void set_user_gender(String user_gender){
        editor.putString("user_gender",user_gender);
        editor.commit();
    }

    public String get_user_gender(){ return prefs.getString("user_gender","");}



}
