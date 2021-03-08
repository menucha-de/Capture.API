package havis.capture.cycle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateTypeAdapter extends XmlAdapter<String, Date> {

	private static ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		}
	};

	@Override
	public Date unmarshal(String string) throws Exception {
		if (string == null)
			return null;
		try {
			return format.get().parse(string);
		} catch (ParseException e) {
			return null;
		}
	}

	@Override
	public String marshal(Date date) throws Exception {
		if (date == null)
			return null;
		return format.get().format(date);
	}
}