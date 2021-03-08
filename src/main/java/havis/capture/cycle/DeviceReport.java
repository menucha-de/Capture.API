package havis.capture.cycle;

import java.util.ArrayList;
import java.util.List;

public class DeviceReport {
	private String name;
	private List<FieldReport> fields;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FieldReport> getFields() {
		if (fields == null) {
			fields = new ArrayList<FieldReport>();
		}
		return fields;
	}

	public void setFields(List<FieldReport> fields) {
		this.fields = fields;
	}
}
