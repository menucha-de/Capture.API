package havis.capture;

import java.util.Date;

public class Entry {

	private String name;
	private String value;
	private String unit;
	private String device;
	private String field;
	private Date timestamp;

	public Entry() {
	}

	public Entry(String name, String value, String unit, String device, String field, Date timestamp) {
		this.name = name;
		this.value = value;
		this.unit = unit;
		this.device = device;
		this.field = field;
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}