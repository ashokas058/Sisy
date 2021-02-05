package sisy.support.ashokas.sisy.ActivitySisy;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mva2.adapter.ListSection;
import mva2.adapter.MultiViewAdapter;
import mva2.adapter.util.OnSelectionChangedListener;
import sisy.support.ashokas.sisy.DbModel.MDL_DbCash;
import sisy.support.ashokas.sisy.R;
import sisy.support.ashokas.sisy.RecyclerVw.RCY_cashBinder;

import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.getSharedAdminData;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.obCashRef;
import static sisy.support.ashokas.sisy.ApplicationUtils.CLS_utils.rupees;

public class ACT_searchView extends AppCompatActivity  implements View.OnClickListener {
RecyclerView obRcyCashed;
MultiViewAdapter obMultiAdapter;
ArrayList<MDL_DbCash> obMdlCashList;
ListSection<MDL_DbCash> obListSec;
ArrayList<MDL_DbCash> obSelectionList,obTempArrayList;
SearchView obSrchVwCasher;
FloatingActionButton obFltDeleteSec;
TextView obTxtSrcTotVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_search_view);
        initViewComponents();
        if (getSupportActionBar()!=null)
            getSupportActionBar().hide();
        checkAdmin();
        initViewComponents();
        initVariable();
        initComponentsListener();
        initRcylerView();
        initFirebaseListeners();
        initSelectionListListeners();
        initSearchVwQueryListeners();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                obTxtSrcTotVal.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void checkAdmin() {
        if (getSharedAdminData())
            obFltDeleteSec.setEnabled(true);
        else
            obFltDeleteSec.setEnabled(false);
    }

    private void initComponentsListener() {
        obFltDeleteSec.setOnClickListener(this);
    }

    private void initRcylerView() {
        try{
            LinearLayoutManager obLinearManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            obLinearManager.setReverseLayout(true);
            obLinearManager.setStackFromEnd(true);
            obRcyCashed.setLayoutManager(obLinearManager);
            obMultiAdapter=new MultiViewAdapter();
            obMultiAdapter.registerItemBinders(new RCY_cashBinder());
            obListSec=new ListSection<>();
            obMdlCashList=new ArrayList<>();
            obListSec.addAll(obMdlCashList);
            obMultiAdapter.addSection(obListSec);
            obRcyCashed.setAdapter(obMultiAdapter);

        }
        catch (Exception e){
            Log.d("RcyclerView_init",e.getMessage());
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent obIntentHome=new Intent(this,ACT_home.class);
        startActivity(obIntentHome);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lyt_flt_delete:
                deleteSelection();
                break;
        }

    }

    private void deleteSelection() {
        try{
            for (final MDL_DbCash mdl_dbCash:obSelectionList){
                obCashRef.child(mdl_dbCash.getStrUID()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(ACT_searchView.this, mdl_dbCash.getStrName()+" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        catch (Exception e){}

    }

    private void initViewComponents(){
        obRcyCashed=findViewById(R.id.lyt_rcy_cashed_view);
        obSrchVwCasher=findViewById(R.id.lyt_search_view);
        obFltDeleteSec=findViewById(R.id.lyt_flt_delete);
        obTxtSrcTotVal=findViewById(R.id.lyt_srchvw_totcash);
    }
    private void initVariable(){
        obSelectionList = new ArrayList<>();

    }

    private void initFirebaseListeners(){
        try{
            obCashRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    obMdlCashList.clear();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        MDL_DbCash obMDL = snap.getValue(MDL_DbCash.class);
                        obMdlCashList.add(obMDL);
                    }
                    obListSec.clear();
                    obListSec.addAll(obMdlCashList);
                    obMultiAdapter.notifyDataSetChanged();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception e){
            Log.d("firebaseRcy",e.getMessage());
        }
    }
    private void initSelectionListListeners(){
        try{
            OnSelectionChangedListener changedListener=new OnSelectionChangedListener() {
                @Override
                public void onSelectionChanged(Object item, boolean isSelected, List selectedItems) {
                    obSelectionList= (ArrayList<MDL_DbCash>) selectedItems;
                    setSlectionTotValue();

                }

            };
            obListSec.setOnSelectionChangedListener(changedListener);
        }
        catch (Exception e){}

    }

    private void setSlectionTotValue() {
        double value=0;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                obTxtSrcTotVal.setVisibility(View.VISIBLE);
            }
        });
        for (MDL_DbCash dbSec:obSelectionList){
            value+=Double.valueOf(dbSec.getStrCash());
        }
        obTxtSrcTotVal.setText("Total:"+rupees+String.valueOf(value));
    }

    private void initSearchVwQueryListeners(){
        try{
            obSrchVwCasher.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if(!s.isEmpty()) {

                        obTempArrayList=new ArrayList<>();
                        for (MDL_DbCash mdl_dbCash : obMdlCashList) {
                            if (mdl_dbCash.getStrName().toLowerCase().contains(s.toLowerCase()) || mdl_dbCash.getStrAddrs().toLowerCase()
                                    .contains(s.toLowerCase()) || mdl_dbCash.getStrCash().toLowerCase().contains(s.toLowerCase())
                                    ||mdl_dbCash.getStrPaymntType().toLowerCase().contains(s.toLowerCase()))
                                obTempArrayList.add(mdl_dbCash);


                        }
                        obListSec.clear();
                        obListSec.addAll(obTempArrayList);
                        obMultiAdapter.notifyDataSetChanged();
                        setSrchTotVal();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                obTxtSrcTotVal.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else {
                        obListSec.clear();
                        obListSec.addAll(obMdlCashList);
                        obMultiAdapter.notifyDataSetChanged();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                obTxtSrcTotVal.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    return true;
                }
            });
        }catch (Exception e){}

    }
    private  double getListedItemTotValue(){
        double obTotVal = 0;
        try{
            for (MDL_DbCash dbList:obTempArrayList){
                obTotVal+=Double.valueOf(dbList.getStrCash());
            }
        }
        catch (Exception e){}
        finally {
            return obTotVal;
        }



    }
    private  void setSrchTotVal(){
        obTxtSrcTotVal.setText("Total:"+rupees+String.valueOf(getListedItemTotValue()));
    }

}
