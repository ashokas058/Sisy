package sisy.support.ashokas.sisy.RecyclerVw;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import mva2.adapter.ItemBinder;
import mva2.adapter.ItemViewHolder;
import sisy.support.ashokas.sisy.DbModel.MDL_DbCash;
import sisy.support.ashokas.sisy.R;

/**
 * Created by DARK-DEVIL on 1/7/2021.
 */

public class RCY_cashBinder extends ItemBinder<MDL_DbCash,RCY_cashBinder.CLS_cashViewHolder> {

    @Override
    public CLS_cashViewHolder createViewHolder(ViewGroup parent) {
        return new CLS_cashViewHolder(inflate(parent, R.layout.lyt_rcy_cashlist));
    }

    @Override
    public void bindViewHolder(CLS_cashViewHolder holder, MDL_DbCash item) {
        String[] dateTime=getDateTimeCall(Long.valueOf(item.getStrTime()));
        holder.obTxtname.setText(item.getStrName());
        holder.obTxtCash.setText(item.getStrCash());
        holder.obTxtdataHeadDay.setText(dateTime[0]);
        holder.obTxtDateMain.setText(dateTime[1]);
        int bgColor = ContextCompat.getColor(holder.obTxtDateMain.getContext(),
                holder.isItemSelected() ? R.color.colorAccent : R.color.gaycustom);
       holder.obcardView.setBackgroundColor(bgColor);


    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof MDL_DbCash;
    }

    static class CLS_cashViewHolder extends ItemViewHolder<MDL_DbCash>{
        TextView obTxtdataHeadDay,obTxtDateMain,obTxtname,obTxtCash;
        CardView obcardView;
        public CLS_cashViewHolder(View itemView) {
            super(itemView);
            obcardView=itemView.findViewById(R.id.lyt_main_card);
            obTxtdataHeadDay=itemView.findViewById(R.id.lyt_rcy_date);
            obTxtDateMain=itemView.findViewById(R.id.lyt_rcy_date_extra);
            obTxtname=itemView.findViewById(R.id.lyt_rcy_name);
            obTxtCash=itemView.findViewById(R.id.lyt_rcy_cash);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleItemSelection();
                }
            });
        }
    }

    public String[] getDateTimeCall(long seconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd hh:mm a");
        String dateString = formatter.format(new Date(seconds));
        return dateString.split(" ", 2);
    }
}
