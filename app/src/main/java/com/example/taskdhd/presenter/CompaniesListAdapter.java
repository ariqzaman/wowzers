package com.example.taskdhd.presenter;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskdhd.R;
import com.example.taskdhd.view.CompanyActivity;
import com.example.taskdhd.view.NotificationActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CompaniesListAdapter
        extends RecyclerView.Adapter<CompaniesListAdapter.companyListsViewHolder>
        implements Filterable {

    private ArrayList<ArrayList<String>> companyList;
    private companyFilter filter;
    private Context context;
    private Activity activity;
    private ArrayList<ArrayList<String>> allNotificationRecords;
    private NotificationAlarmUtils alarmUtils;

    public class companyListsViewHolder extends RecyclerView.ViewHolder{

        public TextView companyName;
        public TextView companyCode;
        public TextView companyIndustry;
        public TextView notificationCompany;

        public companyListsViewHolder(View view){
            super(view);
            companyName = view.findViewById(R.id.companyName);
            companyCode = view.findViewById(R.id.companyCode);
            companyIndustry = view.findViewById(R.id.companyIndustry);
            if(activity.getClass().getSimpleName().equals("NotificationActivity")){
                notificationCompany = view.findViewById(R.id.notificationCompany);
            }
        }

    }

    public CompaniesListAdapter(Activity activity, Context context, ArrayList<ArrayList<String>> companyList){
        this.context = context;
        this.companyList = companyList;
        this.activity = activity;
        this.filter = new companyFilter(CompaniesListAdapter.this);
        this.alarmUtils = new NotificationAlarmUtils(context);
        this.allNotificationRecords = alarmUtils.getAllNotification();
        //this.companyNotification; //get from notification Manager
    }

    @NonNull
    @Override
    public CompaniesListAdapter.companyListsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.companies_list_layout, viewGroup, false);

        return new companyListsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull companyListsViewHolder holder, int position) {
        //System.out.println(companyList.get(position).get(0));
        holder.companyName.setText(companyList.get(position).get(0));
        holder.companyCode.setText(companyList.get(position).get(1));
        holder.companyIndustry.setText(companyList.get(position).get(2));

        if(activity.getClass().getSimpleName().equals("NotificationActivity")){
            try {
                for(int i=0; i<allNotificationRecords.size(); i++){
                    if(allNotificationRecords.get(i).get(0).equals(companyList.get(position).get(1))){
                        holder.notificationCompany.setText(allNotificationRecords.get(i).get(1)+" minutes");
                        break;
                    }
                }
            }catch (IndexOutOfBoundsException ex){
                holder.notificationCompany.setText("Bugger");
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.getClass().getSimpleName().equals("NotificationActivity")){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                    alertDialog.setTitle("Please choose the minute/s you want to receive notification");

                    final NumberPicker numberPicker = new NumberPicker(activity);
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(5);

                    alertDialog.setView(numberPicker);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Notification notification = alarmUtils.addNotification(
                            companyList.get(position).get(0), String.valueOf(position));
                            alarmUtils.addAlarmNotification(notification, position, numberPicker.getValue(), companyList.get(position).get(1));
                            ArrayList<String> tempNotification =  new ArrayList<>();
                            tempNotification.add(companyList.get(position).get(1));
                            tempNotification.add(String.valueOf(numberPicker.getValue()));
                            allNotificationRecords.set(position, tempNotification);

                            //alarmUtils.cancelAlarmNotification(notification, position);
                            notifyItemChanged(position);
                            notifyDataSetChanged();
                            Toast.makeText(activity, "Notification for "+companyList.get(position).get(1)+" succesfully added",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.setNegativeButton("Delete Notification", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Notification notification = alarmUtils.addNotification(
                                    companyList.get(position).get(0), String.valueOf(position));
                            System.out.println("Delete from Prefrence");
                            alarmUtils.cancelAlarmNotification(notification, position, companyList.get(position).get(1));
                            Toast.makeText(activity, "Notification for "+companyList.get(position).get(1)+" deleted",
                                    Toast.LENGTH_SHORT).show();
                            ArrayList<String> removeNotification =  new ArrayList<>();
                            removeNotification.add(companyList.get(position).get(1));
                            removeNotification.add(String.valueOf(0));
                            allNotificationRecords.set(position, removeNotification);
                            notifyItemChanged(position);
                            notifyDataSetChanged();
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();

                }else{
                    Intent intent = new Intent(v.getContext(), CompanyActivity.class);
                    intent.putExtra("companyName", companyList.get(position).get(0));
                    intent.putExtra("companyCode", companyList.get(position).get(1));
                    intent.putExtra("companyIndustry", companyList.get(position).get(2));
                    context.startActivity(intent);
                }

            }
        });

        // Not supported due to conflict in notification
//        if(activity.getClass().getSimpleName().equals("FavouriteActivity")){
//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
//                    alertDialog.setTitle("Are you sure you want to delete this company?");
//                    alertDialog.setPositiveButton("Delete",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    FavouriteListManager favouriteListManager = new FavouriteListManager(context, v);
//                                    favouriteListManager.deleteFromFavourite(
//                                            companyList.get(position).get(0),
//                                            companyList.get(position).get(1),
//                                            companyList.get(position).get(2));
//                                    Toast toastMessage = Toast.makeText(context, companyList.get(position).get(0)+" succesfully delete from favourite list",
//                                            Toast.LENGTH_SHORT);
//                                    toastMessage.show();
//                                    ArrayList<String> arrayLists = new ArrayList<>();
//                                    arrayLists.add(companyList.get(position).get(0));
//                                    arrayLists.add(companyList.get(position).get(1));
//                                    arrayLists.add(companyList.get(position).get(2));
//                                    companyList.remove(arrayLists);
//                                    notifyDataSetChanged();
//                                }
//                            });
//                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) { }
//                    });
//                    alertDialog.show();
//                    return false;
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class companyFilter extends Filter{
        public CompaniesListAdapter filteredAdapter;

        public companyFilter(CompaniesListAdapter filteredAdapter){
            super();
            this.filteredAdapter = filteredAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            companyList.clear();
            RetrieveCompaniesList retrieveCompaniesList = new RetrieveCompaniesList();
            ArrayList<ArrayList<String>> filteredCompanies;
            filteredCompanies = retrieveCompaniesList.retrieveCompanies();

            final FilterResults results = new FilterResults();
            if(constraint.length() == 0){
                companyList.addAll(filteredCompanies);
            }else{
                final String filterPattern =constraint.toString().toLowerCase().trim();
                for(ArrayList<String> company : filteredCompanies){
                    if(constraint.length() > 3){
                        if(company.get(0).toLowerCase().startsWith(filterPattern)
                                || company.get(2).toLowerCase().startsWith(filterPattern)){
                            companyList.add(company);
                        }
                    }else{
                        if(company.get(1).toLowerCase().startsWith(filterPattern)){
                            companyList.add(company);
                        }
                    }
                }
            }
            results.values = companyList;
            results.count = companyList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            this.filteredAdapter.notifyDataSetChanged();
        }
    }
}
