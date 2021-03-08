package havis.capture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class AdapterManager {

	private class Key {

		private String device;
		private String field;

		private Key(String device, String field) {
			this.device = device;
			this.field = field;
		}

		private boolean equals(Key key) {
			return Objects.equals(device, key.device) && Objects.equals(field, key.field);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Key && equals((Key) obj);
		}

		@Override
		public int hashCode() {
			return Objects.hash(device, field);
		}
	}

	private final static Logger log = Logger.getLogger(AdapterManager.class.getName());

	private Lock lock = new ReentrantLock();
	private Adapter adapter;
	private Map<Key, List<AdapterListener>> listeners = new ConcurrentHashMap<>();
	private List<AdapterListener> capsules = new CopyOnWriteArrayList<>();
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private AdapterListener listener = new AdapterListener() {
		@Override
		public void usabilityChanged(final Adapter source, final DeviceUsabilityChangedEvent event) {
			if (event != null) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						for (AdapterListener capsule : capsules)
							capsule.usabilityChanged(source, event);
					}
				});
			}
		}

		@Override
		public void usabilityChanged(final Adapter source, final FieldUsabilityChangedEvent event) {
			if (event != null) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						for (AdapterListener capsule : capsules)
							capsule.usabilityChanged(source, event);
					}
				});
			}
		}

		@Override
		public void valueChanged(final Adapter source, final FieldValueChangedEvent event) {
			if (event != null) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						Key key = new Key(event.getDevice(), event.getField());
						List<AdapterListener> listeners = AdapterManager.this.listeners.get(key);
						if (listeners != null) {
							for (AdapterListener listener : listeners)
								listener.valueChanged(source, event);
						}
					}
				});
			}
		}
	};

	private static void log(Level level, String msg, Throwable e, Object... parameters) {
		LogRecord record = new LogRecord(level, msg);
		record.setParameters(parameters);
		record.setThrown(e);
		log.log(record);
	}

	public AdapterManager(Adapter adapter) {
		this.adapter = adapter;
	}

	private void open() throws AdapterException {
		adapter.open(listener);
	}

	private void close() throws AdapterException {
		adapter.close();
	}

	private Map<String, Device> getDevices() throws AdapterException {
		lock.lock();
		try {
			return adapter.getDevices();
		} finally {
			lock.unlock();
		}
	}

	private String getLabel(String device) throws AdapterException {
		lock.lock();
		try {
			return adapter.getLabel(device);
		} finally {
			lock.unlock();
		}
	}

	private String getLabel(String device, String field) throws AdapterException {
		lock.lock();
		try {
			return adapter.getLabel(device, field);
		} finally {
			lock.unlock();
		}
	}

	private void setLabel(String device, String value) throws AdapterException {
		lock.lock();
		try {
			adapter.setLabel(device, value);
		} finally {
			lock.unlock();
		}
	}

	private void setLabel(String device, String field, String value) throws AdapterException {
		lock.lock();
		try {
			adapter.setLabel(device, field, value);
		} finally {
			lock.unlock();
		}
	}

	private void setProperty(String device, String name, String value) throws AdapterException {
		lock.lock();
		try {
			adapter.setProperty(device, name, value);
		} finally {
			lock.unlock();
		}
	}

	private void setProperty(String device, String field, String name, String value) throws AdapterException {
		lock.lock();
		try {
			adapter.setProperty(device, field, name, value);
		} finally {
			lock.unlock();
		}
	}

	private Object getValue(String device, String field) throws AdapterException {
		lock.lock();
		try {
			return adapter.getValue(device, field);
		} finally {
			lock.unlock();
		}
	}

	private void setValue(String device, String field, Object value) throws AdapterException {
		lock.lock();
		try {
			adapter.setValue(device, field, value);
		} finally {
			lock.unlock();
		}
	}

	private Key subscribe(String device, String field, AdapterListener listener) throws AdapterException {
		lock.lock();
		try {
			Key key = new Key(device, field);
			List<AdapterListener> listeners = this.listeners.get(key);
			if (listeners == null) {
				this.listeners.put(key, listeners = new CopyOnWriteArrayList<>());
				adapter.subscribe(device, field);
			}
			listeners.add(listener);
			return key;
		} finally {
			lock.unlock();
		}
	}

	private Key unsubscribe(String device, String field, AdapterListener listener) throws AdapterException {
		lock.lock();
		try {
			Key key = new Key(device, field);
			List<AdapterListener> listeners = this.listeners.get(key);
			if (listeners != null) {
				if (listeners.remove(listener)) {
					if (listeners.isEmpty()) {
						this.listeners.remove(key);
						adapter.unsubscribe(device, field);

					}
				}
			}
			return key;
		} finally {
			lock.unlock();
		}
	}

	private String add(Device device) throws AdapterException {
		lock.lock();
		try {
			return adapter.add(device);
		} finally {
			lock.unlock();
		}
	}

	private void remove(String device) throws AdapterException {
		lock.lock();
		try {
			adapter.remove(device);
		} finally {
			lock.unlock();
		}
	}

	private void add(AdapterListener capsule) {
		if (capsules.add(capsule) && capsules.size() == 1) {
			try {
				open();
			} catch (AdapterException e) {
				log(Level.FINE, "Failed to open adapter ''{0}''", e, adapter.getClass().getName());
			}
		}
	}

	private void remove(AdapterListener capsule) {
		if (capsules.remove(capsule) && capsules.size() == 0) {
			try {
				close();
			} catch (AdapterException e) {
				log(Level.FINE, "Failed to close adapter ''{0}''", e, adapter.getClass().getName());
			}
		}
	}

	public AdapterHandler createInstance() {
		return new AdapterHandler() {

			private List<Key> keys = new ArrayList<>();
			private Lock lock = new ReentrantLock();
			private AdapterListener listener;

			private AdapterListener capsule = new AdapterListener() {
				@Override
				public void valueChanged(Adapter source, FieldValueChangedEvent event) {
					lock.lock();
					try {
						if (listener != null)
							listener.valueChanged(source, event);
					} finally {
						lock.unlock();
					}
				}

				@Override
				public void usabilityChanged(Adapter source, FieldUsabilityChangedEvent event) {
					lock.lock();
					try {
						if (listener != null)
							listener.usabilityChanged(source, event);
					} finally {
						lock.unlock();
					}
				}

				@Override
				public void usabilityChanged(Adapter source, DeviceUsabilityChangedEvent event) {
					lock.lock();
					try {
						if (listener != null)
							listener.usabilityChanged(source, event);
					} finally {
						lock.unlock();
					}
				}
			};

			{
				AdapterManager.this.add(capsule);
			}

			@Override
			public void setListener(AdapterListener listener) throws AdapterException {
				lock.lock();
				try {
					this.listener = listener;
				} finally {
					lock.unlock();
				}
			}

			@Override
			public Map<String, Device> getDevices() throws AdapterException {
				return AdapterManager.this.getDevices();
			}

			@Override
			public String getLabel(String device) throws AdapterException {
				return AdapterManager.this.getLabel(device);
			}

			@Override
			public String getLabel(String device, String field) throws AdapterException {
				return AdapterManager.this.getLabel(device, field);
			}

			@Override
			public void setLabel(String device, String value) throws AdapterException {
				AdapterManager.this.setLabel(device, value);
			}

			@Override
			public void setLabel(String device, String field, String value) throws AdapterException {
				AdapterManager.this.setLabel(device, field, value);
			}

			@Override
			public Object getValue(String device, String field) throws AdapterException {
				return AdapterManager.this.getValue(device, field);
			}

			@Override
			public void setValue(String device, String field, Object value) throws AdapterException {
				AdapterManager.this.setValue(device, field, value);
			}

			@Override
			public void subscribe(String device, String field) throws AdapterException {
				keys.add(AdapterManager.this.subscribe(device, field, capsule));
			}

			@Override
			public String add(Device device) throws AdapterException {
				return AdapterManager.this.add(device);
			}

			@Override
			public void remove(String device) throws AdapterException {
				AdapterManager.this.remove(device);
			}

			@Override
			public void unsubscribe(String device, String field) throws AdapterException {
				keys.remove(AdapterManager.this.unsubscribe(device, field, capsule));
			}

			@Override
			public void close() throws Exception {
				for (Key key : keys) {
					try {
						AdapterManager.this.unsubscribe(key.device, key.field, listener);
					} catch (AdapterException e) {
						log(Level.FINE, "Failed to unsubscribe from device ''{0}'' field ''{1}''", e, key.device, key.field);
					}
				}
				keys.clear();
				AdapterManager.this.remove(capsule);
			}

			@Override
			public void setProperty(String device, String name, String value) throws AdapterException {
				AdapterManager.this.setProperty(device, name, value);
			}

			@Override
			public void setProperty(String device, String field, String name, String value) throws AdapterException {
				AdapterManager.this.setProperty(device, field, name, value);
			}
		};
	}
}