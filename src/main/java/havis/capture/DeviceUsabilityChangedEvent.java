package havis.capture;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents the event that should be raised when a device becomes usable or unusable.
 * It provides information about which device and the usable state.
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DeviceUsabilityChangedEvent extends DeviceStateChangedEvent {

	private boolean usable;

	public DeviceUsabilityChangedEvent() {
	}

	public DeviceUsabilityChangedEvent(String device, boolean usable) {
		super(device);
		this.usable = usable;
	}

	public boolean isUsable() {
		return usable;
	}

	public void setUsable(boolean usable) {
		this.usable = usable;
	}
}