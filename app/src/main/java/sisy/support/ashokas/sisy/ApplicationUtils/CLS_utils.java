package sisy.support.ashokas.sisy.ApplicationUtils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by DARK-DEVIL on 1/3/2021.
 */

public class CLS_utils extends Application {
    public  static DatabaseReference obCashRef;
    public static SharedPreferences obPref;
    public static  double obDblBalanceShrd;
    public static  double obDblBalanceCapitalShrd;
    public static  String rupees;
    public static SharedPreferences.Editor obEditor;
    private String firebaseDBAccess=" copy paste your db url here";
    public static String SETTING_ACT_FILTER_ACTION="settingActFilter";
    @Override
    public void onCreate() {
        super.onCreate();
        obDblBalanceShrd =0;
        obDblBalanceCapitalShrd=0;
        rupees="\u20B9";
       obPref =getSharedPreferences("userAccess", Context.MODE_PRIVATE);
       obEditor = obPref.edit();
      FirebaseDatabase.getInstance(firebaseDBAccess).setPersistenceEnabled(true);
      initFirebaseDB();


    }

   public static void initFirebaseDB(){
        try{
            if(!getSharedUserData().contains("")|| getSharedUserData()!=null){
                obCashRef = FirebaseDatabase.getInstance("https://sisy-201f0-default-rtdb.firebaseio.com/").getReference(getSharedUserData()).child("cashData");
                obCashRef.keepSynced(true);
            }
        }
        catch(Exception e){}

   }

    public  static String getSharedUserData(){
        String temp = null;
       try{
           temp=obPref.getString("userAccessData","");
       }
       catch(Exception e){}
       finally {
           return  temp;
       }
    }

    public  static boolean getSharedAdminData(){
        boolean temp = false;
        try{
            temp=obPref.getBoolean("adminAccessData",false);
        }
        catch(Exception e){}
        finally {
            return  temp;
        }
    }

}
