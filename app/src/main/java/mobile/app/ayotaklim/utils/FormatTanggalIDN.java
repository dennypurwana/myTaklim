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

         SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MMM-yyyy");
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


}
