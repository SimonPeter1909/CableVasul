package trickandroid.cablevasul.Utils;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by Simon Peter on 01-Nov-17.
 */

public class DateSetter {

    private static final String TAG = "DateSetter";

    private Calendar calendar = Calendar.getInstance();
    public int intdate = calendar.get(Calendar.DATE);
    public int intmonth = calendar.get(Calendar.MONTH);
    public int intyear = calendar.get(Calendar.YEAR);
    private int intday = calendar.get(Calendar.DAY_OF_WEEK);

    public String date(){
        Log.d(TAG, "date: " + String.valueOf(intdate));
        return String.valueOf(intdate);
    }

    public String monthAndYear(){
        return wordMonth()+","+year();
    }

    public String wordMonth(){
        String wordMonth = "";
        if (intmonth == 0){
            wordMonth = "January";
        } else if (intmonth == 1){
            wordMonth = "February";
        } else if (intmonth == 2){
            wordMonth = "March";
        } else if (intmonth == 3){
            wordMonth = "April";
        } else if (intmonth == 4){
            wordMonth = "May";
        } else if (intmonth == 5){
            wordMonth = "June";
        } else if (intmonth == 6){
            wordMonth = "July";
        } else if (intmonth == 7){
            wordMonth = "August";
        } else if (intmonth == 8){
            wordMonth = "September";
        } else if (intmonth == 9){
            wordMonth = "October";
        } else if (intmonth == 10){
            wordMonth = "November";
        } else if (intmonth == 11){
            wordMonth = "December";
        }

        return wordMonth;
    }

    public String numberMonth(){
        String numberMonth = "";
        if (intmonth == 0){
            numberMonth = "01";
        } else if (intmonth == 1){
            numberMonth = "02";
        } else if (intmonth == 2){
            numberMonth = "03";
        } else if (intmonth == 3){
            numberMonth = "04";
        } else if (intmonth == 4){
            numberMonth = "05";
        } else if (intmonth == 5){
            numberMonth = "06";
        } else if (intmonth == 6){
            numberMonth = "07";
        } else if (intmonth == 7){
            numberMonth = "08";
        } else if (intmonth == 8){
            numberMonth = "09";
        } else if (intmonth == 9){
            numberMonth = "10";
        } else if (intmonth == 10){
            numberMonth = "11";
        } else if (intmonth == 11){
            numberMonth = "12";
        }
        return numberMonth;
    }

    public String month(){
        return String.valueOf(intmonth + 1);
    }

    public String finalDate(){
        Log.d(TAG, "finalDate: = " + day()+",  "+wordMonth()+" "+date()+",  "+year());
        return day()+",  "+wordMonth()+" "+date()+",  "+year();
    }

    public String ddmmyyyyday(){
        return date()+"/"+numberMonth()+"/"+year()+", "+day();
    }

    public String ddmmyyyy(){
        return date()+"/"+numberMonth()+"/"+year();
    }

    public String year(){
        return String.valueOf(intyear);
    }

    public String day(){
        String day = "";
        if (intday == 2){
            day = "Monday";
        } else if (intday == 3){
            day = "Tuesday";
        } else if (intday == 4){
            day = "Wednesday";
        } else if (intday == 5){
            day = "Thursday";
        } else if (intday == 6){
            day = "Friday";
        } else if (intday == 7){
            day = "Saturday";
        } else if (intday == 1){
            day = "Sunday";
        }

        return day;
    }
}
