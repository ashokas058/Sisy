package sisy.support.ashokas.sisy.ActivitySisy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.nio.channels.Channels;
import java.util.Date;
import java.util.HashMap;

import sisy.support.ashokas.sisy.R;

import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.getSharedAdminData;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.initFirebaseDB;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.obCashRef;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.obDblBalanceCapitalShrd;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.obEditor;

public class ACT_setting extends AppCompatActivity implements View.OnClickListener {
SharedPreferences pref;
EditText obEdtUserAccess;
String obStrUserAccess;
TextView obTxtVwAdmin,obTxtVwPdf,obTxtWDrawCap;
AlertDialog.Builder obAlrtBuilder;
AlertDialog  obAlrtDialog;
Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initTextWatchListener();
        initTouchListeners();
        CheckAdmin();
        checkPayment();
        date=new Date();

    }

    private void initTouchListeners() {
        obTxtVwPdf.setOnClickListener(this);
        obTxtVwAdmin.setOnClickListener(this);
        obTxtWDrawCap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lyt_admin_enable:
                enableAdmin();
                break;

            case R.id.lyt_stng_withdraw_capital:
                    obAlrtDialog=getCapWithDrawAlrtBuilder().create();
                    obAlrtDialog.show();
                break;


        }
    }

    private void requestTransact() {

    }

    private void enableAdmin() {

       try{
           Log.d("SharedData",String.valueOf(getSharedAdminData()));
           if (!getSharedAdminData()) {
               obEditor.putBoolean("adminAccessData", true);
               obEditor.commit();

           }
           else{
               obEditor.putBoolean("adminAccessData", false);
               obEditor.commit();
           }
           CheckAdmin();

       }
       catch (Exception e){}

    }

    private void CheckAdmin() {
        try{
            if (getSharedAdminData()) {
                //Toast.makeText(this, String.valueOf(getSharedAdminData()), Toast.LENGTH_SHORT).show();
                obTxtVwAdmin.setText("Enabled Admin");
                obTxtVwAdmin.setTextColor(Color.RED);
            }
            else{
                obTxtVwAdmin.setText("Enable Admin");
                obTxtVwAdmin.setTextColor(Color.BLACK);
            }



        }
        catch (Exception e){}

    }

    private void saveAcessDb() {
        try{
            obStrUserAccess= obEdtUserAccess.getText().toString();
            if(!obStrUserAccess.contains("")||obStrUserAccess!=null) {
                obEditor.putString("userAccessData", obStrUserAccess);
                obEditor.commit();
                initFirebaseDB();
            }
        }
        catch (Exception E){}


    }

    private void initView(){
        obEdtUserAccess =findViewById(R.id.lyt_user_access);
        obTxtVwAdmin=findViewById(R.id.lyt_admin_enable);
        obTxtVwPdf=findViewById(R.id.lyt_gen_pdf);
        obTxtWDrawCap=findViewById(R.id.lyt_stng_withdraw_capital);
    }
    private  void initTextWatchListener(){
        try{
            obEdtUserAccess.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    saveAcessDb();
                }
            });
        }
        catch (Exception ee){}


    }

    private AlertDialog.Builder  getCapWithDrawAlrtBuilder(){
        AlertDialog.Builder obTempBuilder=null;

        try{
            ViewGroup group=findViewById(android.R.id.content);
            View view= LayoutInflater.from(this).inflate(R.layout.lyt_alert_builder_capital,group,false);
            final EditText obEdtAlrtAmount=view.findViewById(R.id.lyt_stng_alrt_cash_amount);
            final EditText obEdtAlrtPurpose=view.findViewById(R.id.lyt_stng_alrt_purpose);
            obAlrtBuilder=new AlertDialog.Builder(this);
            obAlrtBuilder.setView(view);
            obAlrtBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (obEdtAlrtAmount.getText().toString().length()==0)
                        obEdtAlrtAmount.setError("empty field");
                    else if (obEdtAlrtPurpose.getText().toString().length()==0)
                        obEdtAlrtPurpose.setError("empty field");
                    else {
                        double temp = Double.valueOf(obEdtAlrtAmount.getText().toString());
                        if (obDblBalanceCapitalShrd >= temp) {
                            sendTransact(obEdtAlrtAmount.getText().toString(), obEdtAlrtPurpose.getText().toString());
                        } else {
                            Toast.makeText(ACT_setting.this, "Insuffient found", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            obTempBuilder=obAlrtBuilder;
        }
        catch (Exception e){}
        finally {
            return obTempBuilder;

        }
    }

    private void sendTransact(String cash,String purpose) {
        String key = obCashRef.push().getKey();
        HashMap<String, String> cashMap = new HashMap<>();
        HashMap containerMap = new HashMap();
        cashMap.put("strName",purpose);
        cashMap.put("strAddrs", "cap.self");
        cashMap.put("strCash", cash);
        cashMap.put("strTime", String.valueOf(date.getTime()));
        cashMap.put("strUID", key);
        cashMap.put("strPaymntType", "zoo");
        containerMap.put(key, cashMap);
        obCashRef.updateChildren(containerMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(ACT_setting.this, "Failed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ACT_setting.this, "Saved", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent obHomeIntent=new Intent(this,ACT_home.class);
        startActivity(obHomeIntent);
        this.finish();
    }

    private void  checkPayment(){

        if (obDblBalanceCapitalShrd>0)
            obTxtWDrawCap.setEnabled(true);
        else
            obTxtWDrawCap.setEnabled(false);


    }

}
