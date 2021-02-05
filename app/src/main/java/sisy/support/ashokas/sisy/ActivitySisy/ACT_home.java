package sisy.support.ashokas.sisy.ActivitySisy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

import sisy.support.ashokas.sisy.ApplicationUtils.iMessage;
import sisy.support.ashokas.sisy.DbModel.MDL_DbCash;
import sisy.support.ashokas.sisy.R;

import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.SETTING_ACT_FILTER_ACTION;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.getSharedAdminData;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.obCashRef;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.obDblBalanceCapitalShrd;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.obDblBalanceShrd;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.rupees;

public class ACT_home extends AppCompatActivity implements View.OnClickListener,iMessage {
    TextView obTotal,obLastPaymt,obTxtSearch,obTxtBalence, obTxtTotCapital;
    CardView obCardVw;
    EditText obUserName,obAddrs,obCash;
    FloatingActionButton obFltBtMenu;
    Button obSave;
    RadioGroup obRadioGrpTypTrans;
    RadioButton obRadioErlyCash,obRadioWithDraw ,obRadioCash,obRadioCapital;
    IntentFilter obFilter;
    Date date;
    iMessage message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComp();
        initRadioGrupListeners();
        obFltBtMenu.setOnClickListener(this);
        obTxtSearch.setOnClickListener(this);
        obSave.setOnClickListener(this);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();
        date=new Date();
        message=(iMessage) this;
        obRadioCash.setChecked(true);
        checkAdmin();

        try{
            obCashRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    double income=0;
                    double EarlyCashExp=0;
                    double withdraw=0;
                    double dblBalence=0;
                    double dblTotal=0;
                    double dblTotalCapital=0;
                    double dblCapital=0;
                    double dblCapitalWithdraw=0;
                    for (DataSnapshot snap:dataSnapshot.getChildren()){

                        MDL_DbCash modelCash=snap.getValue(MDL_DbCash.class);

                        if(modelCash.getStrPaymntType().contains("PaidEarly")) {
                            EarlyCashExp += Double.valueOf(modelCash.getStrCash());
                        }

                        else if (modelCash.getStrPaymntType().contains("Cash")) {
                            income += Double.valueOf(modelCash.getStrCash());
                        }
                        else if (modelCash.getStrPaymntType().contains("Capital"))
                        {
                            dblCapital += Double.valueOf(modelCash.getStrCash());
                        }
                        else if (modelCash.getStrPaymntType().contains("zoo"))
                        {
                            dblCapitalWithdraw+=Double.valueOf(modelCash.getStrCash());
                        }

                        else {
                            withdraw += Double.valueOf(modelCash.getStrCash());
                        }



                        String lastperson=modelCash.getStrName();
                        String lastmoney=modelCash.getStrCash();
                        obLastPaymt.setText("last payment:"+lastperson+","+rupees+lastmoney);
                    }
                    dblTotal=income+EarlyCashExp+dblCapital;
                    dblBalence=income-withdraw;
                    dblTotalCapital=dblCapital-dblCapitalWithdraw;

                        obTxtBalence.setText(String.valueOf("Balance:"+rupees+dblBalence));
                        obTxtTotCapital.setText("Capital:"+rupees+dblTotalCapital);
                        obTotal.setText("Total Cash Received:"+rupees+String.valueOf(dblTotal));


                    obDblBalanceShrd =dblBalence;
                    obDblBalanceCapitalShrd=dblTotalCapital;




                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){}



    }

    private  void  initComp(){
        obTotal=findViewById(R.id.lyt_total_rcvd);
        obLastPaymt=findViewById(R.id.lyt_last_paymt);
        obTxtBalence=findViewById(R.id.lyt_balence);
        obTxtTotCapital =findViewById(R.id.lyt_tot_capital);
        obCash=findViewById(R.id.lyt_cash);
        obUserName=findViewById(R.id.lyt_username);
        obAddrs=findViewById(R.id.lyt_addrs);
        obSave=findViewById(R.id.lyt_save);
        obFltBtMenu=findViewById(R.id.lyt_flt_bt_menu);
        obTxtSearch=findViewById(R.id.lyt_search);
        obRadioGrpTypTrans =findViewById(R.id.lyt_radio_grp);
        obRadioErlyCash =findViewById(R.id.lyt_radio_expen);
        obRadioWithDraw=findViewById(R.id.lyt_radio_withdraw);
        obRadioCash=findViewById(R.id.lyt_radio_cash);
        obRadioCapital=findViewById(R.id.lyt_radio_capital);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lyt_save:
                checkInputValid();
                break;
            case R.id.lyt_flt_bt_menu:
                Intent obIntStng=new Intent(getApplicationContext(),ACT_setting.class);
                startActivity(obIntStng);
                this.finish();
                break;
            case  R.id.lyt_search:
                Intent obIntentSearchVw=new Intent(this,ACT_searchView.class);
                startActivity(obIntentSearchVw);
                this.finish();
                break;

        }
    }

    private void saveCashRcvd() {
        double tempblc=Double.valueOf(obCash.getText().toString());
        if (obRadioGrpTypTrans.getCheckedRadioButtonId()==R.id.lyt_radio_withdraw) {
            if(obDblBalanceShrd>=tempblc)
            {
                sendTransact();
                resetRadioBt();;
            }
            else
            {
                Toast.makeText(this, "Insufficient Balence", Toast.LENGTH_SHORT).show();
                resetRadioBt();
            }
        }
        else
        {sendTransact();}
    }

    private void sendTransact() {
        try{
            RadioButton but=findViewById(obRadioGrpTypTrans.getCheckedRadioButtonId());
            String obStrPaymntType=but.getText().toString();
            String unicKey=obCashRef.push().getKey();
            String obstrUserName, obStrAddrss, obStrCash;
            obstrUserName = obUserName.getText().toString();
            obStrAddrss = obAddrs.getText().toString();
            obStrCash = obCash.getText().toString();
            String key=obCashRef.push().getKey();
            HashMap<String,String> cashMap=new HashMap<>();
            HashMap containerMap=new HashMap();
            cashMap.put("strName",obstrUserName);
            cashMap.put("strAddrs",obStrAddrss);
            cashMap.put("strCash",obStrCash);
            cashMap.put("strTime",String.valueOf(date.getTime()));
            cashMap.put("strUID",key);
            cashMap.put("strPaymntType",obStrPaymntType);
            containerMap.put(key,cashMap);
            obCashRef.updateChildren(containerMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError!=null)
                    {
                        message.fail(databaseError.getMessage());
                        resetRadioBt();
                        resetTextBox();



                    }
                    else{
                        message.success("saved");
                        resetRadioBt();
                        resetTextBox();}
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void success(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    private void resetTextBox() {
        obUserName.setText("");
        obAddrs.setText("");
        obCash.setText("");
    }

    @Override
    public void fail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    public class HomeRcvr extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp=intent.getStringExtra("message");
        }
    }
    private void initBroadCastRcvr(){
        try{
            obFilter=new IntentFilter(SETTING_ACT_FILTER_ACTION);
            registerReceiver(new HomeRcvr(),obFilter);
        } catch(Exception e){}

        obFilter=new IntentFilter(SETTING_ACT_FILTER_ACTION);

    }

private  void initRadioGrupListeners(){
        obRadioGrpTypTrans.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                resetTextBox();
                switch (checkedId){

                    case R.id.lyt_radio_withdraw:
                       if (obDblBalanceShrd >0||obDblBalanceCapitalShrd>0)
                           initEnableView("1","withDraw");
                       else {
                           //initEnableView("0","");
                           resetRadioBt();
                           Toast.makeText(ACT_home.this, "insufficient Balance", Toast.LENGTH_SHORT).show();

                       }
                       break;
                    case  R.id.lyt_radio_cash:
                        initEnableView("1","other");
                        break;
                    case  R.id.lyt_radio_expen:
                        initEnableView("1","other");
                        break;
                    case R.id.lyt_radio_capital:
                        initEnableView("1","other");
                        break;

                }
            }
        });
}

    private void resetRadioBt() {
    obRadioCash.setChecked(true);
    }

private  void  initEnableView(String enableVw,String  typeTran){
    if(enableVw=="1"){
        if (typeTran=="withDraw"){
            obUserName.setHint("For What?");
            obUserName.setEnabled(true);
            obAddrs.setText("self");
            obAddrs.setEnabled(false);
            obCash.setEnabled(true);
        }
        else{

            obUserName.setHint("Name");
            obUserName.setEnabled(true);
            obAddrs.setText("");
            obAddrs.setEnabled(true);
            obCash.setEnabled(true);
            obCash.setText("");
            obSave.setEnabled(true);

        }
    }
    else{
            obCash.setEnabled(false);
            obSave.setEnabled(false);
            obUserName.setEnabled(false);
            obAddrs.setEnabled(false);


    }





}
private  void checkAdmin(){
    if (getSharedAdminData()){
        obRadioCapital.setEnabled(true);
        obRadioErlyCash.setEnabled(true);
        obRadioWithDraw.setEnabled(true);
    }
    else{
        obRadioCapital.setEnabled(false);
        obRadioErlyCash.setEnabled(false);
        obRadioWithDraw.setEnabled(false);
    }
}

private void checkInputValid(){
    if (obUserName.getText().toString().length()==0)
        obUserName.setError("empty field");
    else if (obAddrs.getText().toString().length()==0)
        obAddrs.setError("empty field");
    else if (obCash.getText().toString().length()==0)
        obCash.setError("empty field");
    else
       saveCashRcvd();
}
}
