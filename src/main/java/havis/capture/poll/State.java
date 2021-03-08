package havis.capture.poll;

import java.util.Objects;
import java.util.logging.Logger;

class State {

	private final static Logger log = Logger.getLogger(State.class.getName());

	private String device;
	private String field;
	private long period;
	private long nextReadTime;
	private boolean failure;
	private boolean errorState = false;
	private int failureCount;
	private Object value;
	private boolean eventOnlyOnChange;

	/**
	 * 
	 * @param time
	 *            The time at which the read operation is to be aligned.
	 * @param device
	 *            The device ID.
	 * @param field
	 *            The field ID.
	 * @param period
	 *            The poll interval in which the field shall be read.
	 * @param eventOnlyOnChange
	 */
	public State(long time, String device, String field, long period, boolean failure, boolean eventOnlyOnChange) {
		this.device = device;
		this.field = field;
		this.period = period;
		this.failure = failure;
		this.eventOnlyOnChange = eventOnlyOnChange;
		reset(time);
	}

	public String getDevice() {
		return device;
	}

	public String getField() {
		return field;
	}

	public boolean hasPeriod() {
		return period > 0;
	}

	public Object getValue() {
		return value;
	}

	public boolean isEventOnlyOnChange() {
		return eventOnlyOnChange;
	}

	/**
	 * 
	 * @param value
	 * @return True if value has changed
	 */
	public boolean setValue(Object value) {
		boolean equal = Objects.equals(this.value, value);
		this.value = value;
		// update time
		next();
		return !equal;
	}

	public void error() {
		if (failure && failureCount < 100) {
			failureCount++;
		}
	}

	public void success() {
		failureCount = 0;
		errorState = false;
	}

	/**
	 * 
	 * @param time
	 *            The time at which the read operation is to be aligned.
	 */
	public void reset(long time) {
		nextReadTime = time;
		next();
	}

	/**
	 * Calculates the next time the field must be read
	 */
	public void next() {
		long time = System.currentTimeMillis();
		long diff = time - nextReadTime;
		if (!hasPeriod()) {
			return;
		}
		long mod = diff % period;
		if (diff < 0) {
			// time has changed to past
			nextReadTime = time - mod;
		} else {
			nextReadTime = time + period - mod;
		}
		// will set the next read time to a later time.
		if (failure && failureCount > 0) {
			long delay = Math.min(failureCount * failureCount, 3600) * 1000;
			nextReadTime += delay + period - delay % period;
			log.fine(nextReadTime - time + "ms");
		}
	}

	/**
	 * Calculates the remaining time until the field must be read.
	 * 
	 * @return Returns 0 if field must be read or the remaining time.
	 */
	public long remaining() {
		return remaining(System.currentTimeMillis());
	}

	public long remaining(long currentTime) {
		long remaining = Math.max(0, this.nextReadTime - currentTime);
		if (remaining > period) {
			// Time has changed to past
			next();
			remaining = Math.max(0, this.nextReadTime - currentTime);
		}
		return remaining;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((device == null) ? 0 : device.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (device == null) {
			if (other.device != null)
				return false;
		} else if (!device.equals(other.device))
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		return true;
	}

	public boolean isErrorState() {
		return errorState;
	}

	public void setErrorState(boolean errorState) {
		this.errorState = errorState;
	}

}
