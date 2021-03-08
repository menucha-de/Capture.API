package havis.capture;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({ @JsonSubTypes.Type(value = DeviceUsabilityChangedEvent.class), @JsonSubTypes.Type(value = FieldStateChangedEvent.class) })
public class DeviceStateChangedEvent extends StateChangedEvent {

	private String device;

	public DeviceStateChangedEvent() {
	}

	public DeviceStateChangedEvent(String device) {
		this.device = device;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}
}