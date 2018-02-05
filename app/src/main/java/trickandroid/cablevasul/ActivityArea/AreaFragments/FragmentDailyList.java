package trickandroid.cablevasul.ActivityArea.AreaFragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import trickandroid.cablevasul.FirebasePackage.InitialiseFirebaseNodes;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.DateSetter;

/**
 * Created by Simon on 2/3/2018.
 */

public class FragmentDailyList extends Fragment {

    //utils
    private DateSetter dateSetter = new DateSetter();

    //widgets
    private CalendarView calendarView;
    private List<EventDay> eventDays = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();
    private SweetAlertDialog sweetAlertDialog;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    //firebase
    private InitialiseFirebaseNodes nodes = new InitialiseFirebaseNodes();

    //log tag
    private static final String TAG = "FragmentDailyList";

    public static FragmentDailyList newInstance(){
        return new FragmentDailyList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_daily_list,container,false);

        calendarView = view.findViewById(R.id.calenderView);

        sweetAlertDialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.show();

        nodes.getNodeDailyList().child("dateList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String date = dataSnapshot1.getValue(String.class).replace(",","/");
//                    date += String.format(" %02d:%02d:%02d",dateSetter.intHour,dateSetter.intMinute,dateSetter.intSeconds);
                    String[] dateArray = date.split("/");
                    Log.d(TAG, "onDataChange: dateArray = " + dateArray);
                    calendar.set(Integer.parseInt(dateArray[2]),Integer.parseInt(dateArray[1]),Integer.parseInt(dateArray[0]));
                    Log.d(TAG, "onDataChange: stringDate = " + date);
//                        calendar.setTime(sdf.parse(date));
                        eventDays.add(new EventDay(calendar,R.drawable.ic_add));
                    calendarView.setEvents(eventDays);
                }
                sweetAlertDialog.dismissWithAnimation();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        onClickDate(calendarView);

        return view;

    }

    private void onClickDate(CalendarView calendarView) {

    }
}
