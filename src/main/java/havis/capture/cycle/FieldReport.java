package havis.capture.cycle;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class FieldReport {
	private String name;
	private Object value;
	@XmlJavaTypeAdapter(DateTypeAdapter.class)
	private Date date;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
