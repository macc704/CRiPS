package ronproeditor.helpers;

import java.util.Calendar;

public class TimeUtil {

	public static String getNowString(){
		Calendar c = Calendar.getInstance();
		String time = ""+
			c.get( Calendar.YEAR ) + 
			c.get( Calendar.MONTH ) +
			c.get( Calendar.DAY_OF_MONTH ) +
			c.get( Calendar.HOUR_OF_DAY ) + 
			c.get( Calendar.MINUTE ) +
			c.get( Calendar.SECOND );
		return time;
	}
	
	public static String getDateString(){
		Calendar c = Calendar.getInstance();
		String time = ""+
			c.get( Calendar.YEAR ) + 
			c.get( Calendar.MONTH ) +
			c.get( Calendar.DAY_OF_MONTH );
		return time;
	}
}
