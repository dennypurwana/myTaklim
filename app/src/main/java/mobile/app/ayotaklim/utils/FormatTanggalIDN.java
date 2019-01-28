package mobile.app.ayotaklim.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FormatTanggalIDN {

    public String formatDate(String dateString){
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = fmt.parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }

         SimpleDateFormat fmtOut = new SimpleDateFormat("dd/MMM/yyyy");
         return fmtOut.format(date);
    }

    public  Date convertStringToDate(String currentDate,
                                               String pattern, Locale locale) {
        Date tanggalDate = null;
        SimpleDateFormat formatter;
        if (locale == null) {
            formatter = new SimpleDateFormat(pattern);
        } else {
            formatter = new SimpleDateFormat(pattern, locale);
        }
        try {
            tanggalDate = formatter.parse(currentDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return tanggalDate;
    }



    public long dateDiff(String eventDateParam){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("MM/dd/yyyy ");
        String currentDate = mdformat.format(calendar.getTime());
        String date  = eventDateParam.substring(8,10);
        String month = eventDateParam.substring(5,7);
        String year  = eventDateParam.substring(0,4);
        String eDate = month+"/"+date+"/"+year;
        Date d1 = new Date(currentDate);
        Date d2 = new Date(eDate);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d2);
        if(d1.equals(d2)){
            return 0;
        }else{
            return daysBetween(d1,d2);
        }


    }
    public static long daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        long daysBetween = -1;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public static Calendar getDatePart(Date date){
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

}
