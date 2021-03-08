package havis.capture.cycle;

import java.util.Date;

public class AdapterNotify {
	private Date date;
	private String device;
	private String field;
	private Object value;

	public AdapterNotify(String device, String field, Object value) {
		this.date = new Date();
		this.device = device;
		this.field = field;
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public String getDevice() {
		return device;
	}

	public String getField() {
		return field;
	}

	public Object getValue() {
		return value;
	}
}
