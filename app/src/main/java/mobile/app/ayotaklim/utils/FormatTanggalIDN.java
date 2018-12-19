package mobile.app.ayotaklim.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}
