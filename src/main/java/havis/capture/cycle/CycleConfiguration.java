package havis.capture.cycle;

import java.util.LinkedHashMap;
import java.util.Map;

import havis.capture.DeviceSpec;
import havis.transport.Subscriber;
import havis.transport.Subscriptor;

public class CycleConfiguration {

	private Map<String, DeviceSpec> devices;
	private Map<String, CycleSpec> cycles;
	private Map<String, Map<String, Subscriber>> subscribers;
	private Map<String, Map<String, Subscriptor>> subscriptors;

	public CycleConfiguration() {
	}

	public Map<String, DeviceSpec> getDevices() {
		if (devices == null) {
			devices = new LinkedHashMap<String, DeviceSpec>();
		}
		return devices;
	}

	public void setDevices(Map<String, DeviceSpec> devices) {
		this.devices = devices;
	}

	public Map<String, CycleSpec> getCycles() {
		if (cycles == null) {
			cycles = new LinkedHashMap<String, CycleSpec>();
		}
		return cycles;
	}

	public void setCycles(Map<String, CycleSpec> cycles) {
		this.cycles = cycles;
	}

	public Map<String, Map<String, Subscriber>> getSubscribers() {
		if (subscribers == null) {
			subscribers = new LinkedHashMap<String, Map<String, Subscriber>>();
		}
		return subscribers;
	}

	public void setSubscribers(Map<String, Map<String, Subscriber>> subscribers) {
		this.subscribers = subscribers;
	}
	public Map<String, Map<String, Subscriptor>> getSubscriptors() {
		if (subscriptors == null) {
			subscriptors = new LinkedHashMap<String, Map<String, Subscriptor>>();
		}
		return subscriptors;
	}

	public void setSubscriptors(Map<String, Map<String, Subscriptor>> subscriptors) {
		this.subscriptors = subscriptors;
	}
}
