package com.ics.fvfd.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.ics.fvfd.AppUtils.BaseUrl;
import com.ics.fvfd.Fragment.Order_details;
import com.ics.fvfd.Model.Delivery_data;
import com.ics.fvfd.Model.Order_Finish;
import com.ics.fvfd.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Delivery_List_Adapter  extends RecyclerView.Adapter<Delivery_List_Adapter.ViewHolder>
{
    List<Delivery_data> dataList;
    Activity mactivity;

    public Delivery_List_Adapter(Activity mactivity, List<Delivery_data> dataList)
    {
        this.mactivity=mactivity;
        this.dataList=dataList;
    }


    @Override
    public Delivery_List_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_delivery,viewGroup,false);
        return new Delivery_List_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Delivery_List_Adapter.ViewHolder viewHolder,final int i) {

        if (viewHolder != null) {

            final Delivery_data dob = dataList.get(i);

            //viewHolder.name.setText(dob.getUserFullname());
            viewHolder.oid.setText(""+dob.getOrderId());
            viewHolder.name.setText(""+dob.getUserFullname());
            viewHolder.mobile.setText(""+dob.getUserPhone());
            //viewHolder.paymode.setText(""+dob.getPaymentMode()+" -- Rs."+dob.getTotalAmount());
            viewHolder.paymode.setText("Rs."+dob.getTotalAmount());
            viewHolder.date.setText(""+dob.getOrderDate()+"\n"+dob.getDeliveryTimeFrom()+" -- "+dob.getDeliveryTimeTo());
            //viewHolder.amount.setText("Rs. "+dob.getTotalAmount());
            viewHolder.address.setText(""+dob.getDeliveryAddress());
            viewHolder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent in = new Intent(mactivity, Order_details.class);
                    in.putExtra("o_id",dob.getOrderId());
                    mactivity.startActivity(in);

                }
            });

            viewHolder.finishb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        alert_box(i);
                }
            });

            viewHolder.icmap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String latitude = dob.getLatitude();
                    String longitude = dob.getLongitude();
                    Uri.Builder builder = new Uri.Builder();

                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longitude + "&mode=d");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    mactivity.startActivity(mapIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView oid,name, mobile, address, paymode, amount, details, finishb, date;
        ImageView icmap;

        public ViewHolder(View itemview) {
            super(itemview);

            oid = itemview.findViewById(R.id.p_oidd);
            name = itemview.findViewById(R.id.p_nmm);
            mobile = itemview.findViewById(R.id.p_phone);
            address = itemview.findViewById(R.id.p_addr);
            paymode = itemview.findViewById(R.id.p_paymnt);
            date = itemview.findViewById(R.id.p_dte);
            details = itemview.findViewById(R.id.details_buttn);
            finishb = itemview.findViewById(R.id.finish_buttn);
            icmap = itemview.findViewById(R.id.icon_map);
        }
    }

    private void alert_box(final int value)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mactivity);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    CALL_API(value);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dlg = builder.create();
        //dlg.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
        dlg.show();
        dlg.getButton(AlertDialog.BUTTON_NEGATIVE).setPadding(0,0,10,0);
        dlg.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mactivity.getResources().getColor(R.color.grey));
        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mactivity.getResources().getColor(R.color.colorPrimary));
    }


    private void CALL_API(final int val){
        BaseUrl.getAPIService().ORDER_FINISH(dataList.get(val).getOrderId()).enqueue(new Callback<Order_Finish>() {
            @Override
            public void onResponse(Call<Order_Finish> call, Response<Order_Finish> response) {
                if (response.body().getResponce()) {
                    dataList.remove(dataList.get(val));
                    notifyDataSetChanged();
                    Toast.makeText(mactivity, "Order finished / Delivered...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mactivity, "Order not finished...", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Order_Finish> call, Throwable t) {

            }
        });
    }


}
