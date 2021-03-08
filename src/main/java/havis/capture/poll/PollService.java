package havis.capture.poll;

import havis.capture.Adapter;
import havis.capture.AdapterHandler;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PollService {

	private final static Logger log = Logger.getLogger(PollService.class.getName());

	private List<State> fields = new CopyOnWriteArrayList<State>();
	private long time;
	private boolean eventOnlyOnChange;
	private boolean enableFailure;

	private boolean running;
	private ExecutorService service;
	private Object executorWaitNotify = new Object();

	private Adapter adapter;
	private AdapterHandler handler;
	private PollListener listener;

	private Runnable runnable = new Runnable() {

		/**
		 * Waiting until the next field must be read.
		 * 
		 * @throws InterruptedException
		 */
		private void pause() throws InterruptedException {
			// if there is no field subscription the delay will be max
			// value.
			long delay = Long.MAX_VALUE;

			// Calculate delay.
			for (State state : fields) {
				if (state.hasPeriod()) {
					delay = Math.min(delay, state.remaining());
				}
			}

			if (delay > 0) {
				// Waiting until the next field must be read.
				synchronized (executorWaitNotify) {
					// Will be notified if delay must be
					// updated because new subscriptions are
					// added.
					executorWaitNotify.wait(delay);
				}
			}
		}

		@Override
		public void run() {
			long currentTime;
			while (running) {
				try {
					pause();
				} catch (InterruptedException e) {
					break;
				}

				// Reading all fields where delay = 0
				currentTime = System.currentTimeMillis();
				for (State state : fields) {
					if (state.hasPeriod() && state.remaining(currentTime) == 0) {
						try {
							// check if service is still enabled
							if (!running || Thread.currentThread().isInterrupted()) {
								return;
							}

							// read value
							Object value = null;
							if (adapter != null) {
								value = adapter.getValue(state.getDevice(), state.getField());
							} else {
								value = handler.getValue(state.getDevice(), state.getField());
							}

							// reset failure counter
							state.success();
							// inform listener
							boolean changed = state.setValue(value);
							if (listener != null) {
								try {
									if (!state.isEventOnlyOnChange() || changed)
										listener.onSuccess(state.getDevice(), state.getField(), value);
								} catch (Exception e) {
									log.log(Level.WARNING, e.getMessage(), e);
								}
							}

						} catch (Exception e) {
							if (!state.isErrorState()) {
								log.log(Level.WARNING, "Failed to read Device " + state.getDevice() + " " + state.getField() + ":" + e.getMessage(), e);
								state.setErrorState(true);
							}
							if (listener != null) {
								try {
									listener.onFailure(state.getDevice(), state.getField(), e);
								} catch (Exception ex) {
									log.log(Level.WARNING, ex.getMessage(), ex);
								}
							}
							state.error();
							state.next();
						}
					}
				}
			}
		}
	};

	public PollService() {
		time = System.currentTimeMillis();
	}

	/**
	 * 
	 * @param eventOnlyOnChange
	 *            If true events will only be send if the value has changed.
	 * @param enableFailure
	 *            If true the next time to read a field will be increased if an
	 *            error occurred.
	 */
	public PollService(boolean eventOnlyOnChange, boolean enableFailure) {
		time = System.currentTimeMillis();
		this.eventOnlyOnChange = eventOnlyOnChange;
		this.enableFailure = enableFailure;
	}

	/**
	 * 
	 * @return The state of the service.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Will add the field to the list. The previous definition will be removed.
	 * 
	 * @param device
	 *            The device ID.
	 * @param field
	 *            The field ID.
	 * @param period
	 *            The time interval in which the field is to be read.
	 * @throws NullPointerException
	 *             If device or field are null.
	 */
	public synchronized void setField(String device, String field, long period) throws NullPointerException {
		setField(device, field, period, eventOnlyOnChange);
	}

	public synchronized void setField(String device, String field, long period, boolean eventOnlyOnChange) throws NullPointerException {
		Objects.requireNonNull(device, "Device must not be null.");
		Objects.requireNonNull(field, "Field must not be null.");
		State state = new State(time, device, field, period, enableFailure, eventOnlyOnChange);
		int index = fields.indexOf(state);
		if (period > 0) {
			if (index < 0) {
				fields.add(state);
			} else {
				State old = fields.get(index);
				state.setValue(old.getValue());
				fields.set(index, state);
			}
			// notify to recalculate delay
			synchronized (executorWaitNotify) {
				executorWaitNotify.notify();
			}
		} else if (index > -1) {
			fields.remove(index);
		}
	}

	/**
	 * 
	 * @param device
	 *            The device ID.
	 * @param field
	 *            The field ID.
	 */
	public synchronized void removeField(String device, String field) {
		if (device == null || field == null)
			return;
		fields.remove(new State(time, device, field, 0, enableFailure, eventOnlyOnChange));
	}

	/**
	 * Removes all fields which are part of the given device.
	 * 
	 * @param device
	 */
	public synchronized void removeFields(String device) {
		if (device == null)
			return;
		for (State state : fields) {
			if (Objects.equals(state.getDevice(), device)) {
				fields.remove(state);
			}
		}
	}

	/**
	 * Removes all fields.
	 */
	public void clear() {
		fields.clear();
	}

	public synchronized void resetFailure(String device, String field) {
		for (State state : fields) {
			if (Objects.equals(state.getDevice(), device) && Objects.equals(state.getField(), field)) {
				state.success();
				state.reset(time);
			}
		}
		// notify to recalculate delay
		synchronized (executorWaitNotify) {
			executorWaitNotify.notify();
		}
	}

	/**
	 * Starts the thread which reads the fields.
	 * 
	 * @param adapter
	 * @param listener
	 * @throws IllegalStateException
	 *             If the service is already running.
	 */
	public synchronized void start(Adapter adapter, PollListener listener) throws IllegalStateException {
		if (running) {
			throw new IllegalStateException("Service is already running.");
		}
		running = true;
		this.adapter = adapter;
		this.listener = listener;
		service = Executors.newSingleThreadExecutor();
		service.execute(runnable);
	}

	/**
	 * Starts the thread which reads the fields.
	 * 
	 * @param handler
	 * @param listener
	 * @throws IllegalStateException
	 *             If the service is already running.
	 */
	public synchronized void start(AdapterHandler handler, PollListener listener) throws IllegalStateException {
		if (running) {
			throw new IllegalStateException("The service is already running.");
		}
		running = true;
		this.handler = handler;
		this.listener = listener;
		service = Executors.newSingleThreadExecutor();
		service.execute(runnable);
	}

	/**
	 * Stops the thread which reads the fields.
	 */
	public synchronized void stop() {
		running = false;
		if (service != null) {
			service.shutdownNow();
			try {
				service.awaitTermination(3000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				log.log(Level.WARNING, e.getMessage());
			}
			service = null;
		}
		listener = null;
	}
}
