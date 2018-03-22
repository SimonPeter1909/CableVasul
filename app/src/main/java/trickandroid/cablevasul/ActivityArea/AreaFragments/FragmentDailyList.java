package trickandroid.cablevasul.ActivityArea.AreaFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import trickandroid.cablevasul.ActivityDailyList.DailyListActivity;
import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.DateSetter;

/**
 * Created by Simon on 2/3/2018.
 */

public class FragmentDailyList extends Fragment{

    //utils
    private DateSetter dateSetter = new DateSetter();

    //widgets
    private MaterialCalendarView materialCalendarView;
    private SweetAlertDialog sweetAlertDialog;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private List<String> areaList = new ArrayList<>();

    //progressBar
    private MaterialDialog progressBar;

    //firebase
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();

    //log tag
    private static final String TAG = "FragmentDailyList";

    public FragmentDailyList() {
    }

    public static FragmentDailyList newInstance(){
        return new FragmentDailyList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_daily_list,container,false);

        materialCalendarView = view.findViewById(R.id.calenderView);
        materialCalendarView.setPagingEnabled(false);

        sweetAlertDialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.show();

        nodes.getNodeDailyList().child("dateList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                setDatesSelected(dataSnapshot,materialCalendarView);

                sweetAlertDialog.dismissWithAnimation();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull final MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected){
                    setProgressBar();
                    final String dates = String.format("%02d,%02d,%04d",date.getDay(),date.getMonth()+1,date.getYear());
                    final String dateSlash = String.format("%02d/%02d/%04d",date.getDay(),date.getMonth()+1,date.getYear());
                    nodes.getNodeDailyList().child("dateList").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(dates)){
                                nodes.getNodeAreaList().addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()){
                                            String area = areaSnapshot.getValue(String.class);
                                            areaList.add(area);
                                        }
                                        openSelectAreaDialog(dateSlash,areaList);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                setDatesSelected(dataSnapshot,materialCalendarView);
                            } else {
                                setDatesSelected(dataSnapshot,materialCalendarView);
                                progressBar.dismiss();
                                Toast.makeText(getContext(),"No Connection on " + dateSlash,Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

        return view;

    }

    /**
     * displays progressDialog till the Firebase values are Loaded
     */
    public void setProgressBar(){
        MaterialDialog.Builder progressBuilder = new MaterialDialog.Builder(getContext())
                .title("Loading")
                .content("Please Wait...")
                .progress(true,0)
                .progressIndeterminateStyle(true);

        progressBar = progressBuilder.build();
        progressBar.setCancelable(false);
        progressBar.show();
    }

    /**
     * opens a dialog with the list of area name
     * @param date
     */
    private void openSelectAreaDialog(final String date, final List<String> areaList){
        progressBar.dismiss();
        new MaterialDialog.Builder(getContext())
                .title("Select an Area")
                .items(areaList)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        openDailyListActivity(getContext(),date,String.valueOf(text));
                        areaList.clear();
                    }
                })
                .show();
        areaList.clear();
    }

    /**
     * Intent to opens DailyListActivity
     * @param context
     * @param date
     * @param areaName
     */
    private void openDailyListActivity(Context context, String date, String areaName){
        Intent i = new Intent(context, DailyListActivity.class);
        i.putExtra("date", date);
        i.putExtra("areaName",areaName);
        startActivity(i);
    }

    /**
     * to set the dates highlight even when the date is selected
     * @param dataSnapshot
     * @param materialCalendarView
     */
    private void setDatesSelected(DataSnapshot dataSnapshot, MaterialCalendarView materialCalendarView){
        for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
            String date = snapshot.getValue(String.class).replace(",","/");
            materialCalendarView.setDateSelected(parseDate(date),true);
            materialCalendarView.setSelectionColor(getResources().getColor(R.color.Green));
        }
    }


    /**
     * to parse the string value of date to Date object
     * @param date
     * @return
     */
    private Date parseDate(String date){

        String dateArray[] = date.split("/");

        int day = Integer.parseInt(dateArray[0]);
        int month = Integer.parseInt(dateArray[1]);
        int year = Integer.parseInt(dateArray[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);


        long time = calendar.getTimeInMillis();

        return new Date(time);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
