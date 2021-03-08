package havis.capture;

import java.util.HashMap;
import java.util.Map;

import havis.capture.Device;
import havis.capture.Field;

public class DeviceSpec extends Device {

	private boolean enabled;

	private String specId;

	private Long period;

	public DeviceSpec() {

	}

	public DeviceSpec(Device d) {
		this.setId(d.getId());
		this.setLabel(d.getLabel());
		this.setName(d.getName());
		this.setCustomized(d.isCustomized());
		this.setUsable(d.isUsable());
		this.setProperties(new HashMap<String, String>(d.getProperties()));
		if (d.getFields() != null) {
			this.setFields(new HashMap<String, Field>());
			for (Map.Entry<String, Field> entry : d.getFields().entrySet()) {
				Field f = new Field();
				f.setId(entry.getValue().getId());
				f.setLabel(entry.getValue().getLabel());
				f.setName(entry.getValue().getName());
				if (entry.getValue().getProperties() != null) {
					f.setProperties(new HashMap<String, String>(entry.getValue().getProperties()));
				}
				this.getFields().put(entry.getKey(), f);
			}
		}

		if (d instanceof DeviceSpec) {
			DeviceSpec ds = ((DeviceSpec) d);
			this.enabled = ds.enabled;
			this.specId = ds.specId;
			this.period = ds.period;
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getSpecId() {
		return specId;
	}

	public void setSpecId(String specId) {
		this.specId = specId;
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}
}
