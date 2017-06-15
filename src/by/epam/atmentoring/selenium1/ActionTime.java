package by.epam.atmentoring.selenium1;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActionTime {

	static String getTime() {
		
		return (new SimpleDateFormat("hh:mm a").format(new Date()).toLowerCase()).replaceFirst("^0+(?!$)", "");
		
	}
}