package havis.capture.cycle;

import havis.capture.AdapterException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CycleSpec {

	/**
	 * ID of the spec
	 */
	private String id;

	/**
	 * Identifies the application
	 */
	private String applicationId;

	/**
	 * Name of the spec
	 */
	private String name;

	/**
	 * Specifies if the cycle should be running or should be on standby
	 */
	private boolean enabled;

	/**
	 * The cycle duration, e.g. time span data is received from the data source
	 */
	private long duration = -1;

	/**
	 * The cycle repeat period, e.g. time before the cycle repeats, disabled by
	 * default
	 */
	private long repeatPeriod = -1;

	/**
	 * The duration which has to pass without any data changes before a report
	 * is generated, disabled by default
	 */
	private long interval = -1;

	/**
	 * Whether to report data when it is available, disabled by default
	 */
	private boolean whenDataAvailable = false;

	/**
	 * The delay before a report will be generated and sent if
	 * {@link CycleSpec#whenDataAvailable} is true and new data was received, 0
	 * by default
	 */
	private int whenDataAvailableDelay = 0;

	/**
	 * Whether the report should be send if not data is available
	 */
	private boolean reportIfEmpty = false;

	/**
	 * The field subscriptions. Map&lt;Device ID, List&lt;Field ID&gt;&gt;
	 */
	private Map<String, Set<String>> fieldSubscriptions;

	public CycleSpec() {

	}

	public CycleSpec(CycleSpec spec) throws AdapterException {
		if (spec == null) {
			throw new AdapterException("Spec can not be null");
		}
		this.applicationId = spec.getApplicationId();
		this.duration = spec.getDuration();
		this.enabled = spec.isEnabled();
		this.id = spec.getId();
		this.interval = spec.getInterval();
		this.name = spec.getName();
		this.repeatPeriod = spec.getRepeatPeriod();
		this.reportIfEmpty = spec.isReportIfEmpty();
		this.whenDataAvailable = spec.isWhenDataAvailable();
		this.whenDataAvailableDelay = spec.getWhenDataAvailableDelay();

		for (Entry<String, Set<String>> fieldSubscription : spec.getFieldSubscriptions().entrySet()) {
			this.getFieldSubscriptions().put(fieldSubscription.getKey(), new HashSet<String>(fieldSubscription.getValue()));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getRepeatPeriod() {
		return repeatPeriod;
	}

	public void setRepeatPeriod(long repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public boolean isWhenDataAvailable() {
		return whenDataAvailable;
	}

	public void setWhenDataAvailable(boolean whenDataAvailable) {
		this.whenDataAvailable = whenDataAvailable;
	}

	public int getWhenDataAvailableDelay() {
		return whenDataAvailableDelay;
	}

	public void setWhenDataAvailableDelay(int whenDataAvailableDelay) {
		this.whenDataAvailableDelay = whenDataAvailableDelay;
	}

	public boolean isReportIfEmpty() {
		return reportIfEmpty;
	}

	public void setReportIfEmpty(boolean reportIfEmpty) {
		this.reportIfEmpty = reportIfEmpty;
	}

	public Map<String, Set<String>> getFieldSubscriptions() {
		if (fieldSubscriptions == null) {
			fieldSubscriptions = new HashMap<String, Set<String>>();
		}
		return fieldSubscriptions;
	}

	public void setFieldSubscriptions(Map<String, Set<String>> fieldSubscriptions) {
		this.fieldSubscriptions = fieldSubscriptions;
	}

	public void addFieldSubscription(String device, String field) {
		Set<String> fields = getFieldSubscriptions().get(device);
		if (fields == null) {
			fields = new HashSet<String>();
			getFieldSubscriptions().put(device, fields);
		}
		fields.add(field);
	}

	public void removeFieldSubscription(String device, String field) {
		Set<String> fields = getFieldSubscriptions().get(device);
		if (fields != null) {
			fields.remove(field);
		}
	}
}
