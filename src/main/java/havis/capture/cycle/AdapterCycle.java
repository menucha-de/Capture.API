package havis.capture.cycle;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import havis.capture.Adapter;
import havis.capture.AdapterException;
import havis.capture.AdapterHandler;
import havis.capture.AdapterListener;
import havis.capture.Device;
import havis.capture.DeviceUsabilityChangedEvent;
import havis.capture.Field;
import havis.capture.FieldUsabilityChangedEvent;
import havis.capture.FieldValueChangedEvent;
import havis.capture.poll.PollListener;
import havis.capture.poll.PollService;
import havis.transport.Subscriber;
import havis.transport.SubscriberListener;
import havis.transport.SubscriberManager;
import havis.transport.Subscriptor;
import havis.transport.SubscriptorManager;
import havis.transport.TransportException;
import havis.util.cycle.ImplementationException;
import havis.util.cycle.ReportFactory;
import havis.util.cycle.ValidationException;
import havis.util.cycle.common.CommonCycle;
import havis.util.cycle.common.ListCycleData;

public class AdapterCycle extends CommonCycle<CycleSpec, AdapterNotify, ListCycleData<AdapterNotify>, Subscriber, SubscriberListener> {

	private final static Logger log = Logger.getLogger(AdapterCycle.class.getName());

	/**
	 * Specifies the property name for the polling interval
	 */
	public final static String PERIOD = "havis.capture.period";

	private AdapterHandler adapterHandler;
	private SubscriberManager subscriberManager;
	private SubscriptorManager subscriptorManager;

	private PollService pollService;
	private ReentrantLock lock = new ReentrantLock();

	private boolean enabled;

	public AdapterCycle(AdapterHandler adapterHandler, CycleSpec spec, SubscriberManager subscriberManager)
			throws ImplementationException, ValidationException {
		this(adapterHandler, spec, subscriberManager, new AdapterReportFactory(adapterHandler, spec));
	}

	public AdapterCycle(AdapterHandler adapterHandler, CycleSpec spec, SubscriberManager subscriberManager, SubscriptorManager subscriptorManager) 
			throws ImplementationException, ValidationException {
		this(adapterHandler, spec, subscriberManager, new AdapterReportFactory(adapterHandler, spec), subscriptorManager);
	}

	public AdapterCycle(AdapterHandler adapterHandler, CycleSpec spec, SubscriberManager subscriberManager,
			ReportFactory<AdapterNotify, ListCycleData<AdapterNotify>> reportFactory) throws ImplementationException, ValidationException {
		this(adapterHandler, spec, subscriberManager, reportFactory, null);
	}

	public AdapterCycle(AdapterHandler adapterHandler, CycleSpec spec, SubscriberManager subscriberManager,
			ReportFactory<AdapterNotify, ListCycleData<AdapterNotify>> reportFactory, SubscriptorManager subscriptorManager) 
			throws ImplementationException, ValidationException {
		super(spec.getName(), spec, new ListCycleData<AdapterNotify>(), reportFactory);
		this.adapterHandler = adapterHandler;
		this.subscriberManager = subscriberManager;
		this.subscriptorManager = subscriptorManager;
	}

	@Override
	protected void applyConfiguration(CycleSpec spec) throws ImplementationException, ValidationException {
		this.duration = spec.getDuration();
		this.repeatPeriod = spec.getRepeatPeriod();
		this.interval = spec.getInterval();
		this.whenDataAvailable = spec.isWhenDataAvailable();
		this.whenDataAvailableDelay = spec.getWhenDataAvailableDelay();
	}

	@Override
	protected String addSubscriber(Subscriber subscriber) throws ImplementationException, ValidationException {
		try {
			return subscriberManager.add(subscriber);
		} catch (havis.transport.ValidationException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	public String add(Subscriptor s) throws ImplementationException, ValidationException {
		return addSubscriptor(s);
	}

	protected String addSubscriptor(Subscriptor subscriptor) throws ImplementationException, ValidationException {
		try {
			String id = subscriptorManager.add(subscriptor);
			evaluateCycleState();
			return id;

		} catch (Throwable e) {
			throw new ValidationException(e.getMessage());
		}
	}

	@Override
	protected void disposeCycle() {
		try {
			disable();
			adapterHandler.close();
		} catch (Exception e) {
		}
	}

	@Override
	protected void disposeSubscribers() {
		if (this.subscriptorManager != null) {
			subscriptorManager.dispose();
		} else {
			subscriberManager.dispose(false);
		}
	}

	@Override
	protected boolean hasEnabledSubscribers() {
		if (this.subscriptorManager != null) {
			return subscriptorManager.hasEnabledSubscriptors();
		} else {
			return subscriberManager.hasEnabledSubscribers();
		}
	}

	@Override
	protected void removeSubscriber(Subscriber subscriber) throws ImplementationException, ValidationException {
		try {
			subscriberManager.remove(subscriber);
		} catch (havis.transport.ValidationException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	protected void removeSubscriptor(Subscriptor subscriptor) throws ImplementationException, ValidationException {
		try {
			subscriptorManager.remove(subscriptor);
		} catch (Throwable e) {
			throw new ValidationException(e.getMessage());
		}
	}

	@Override
	protected void sendReport(Object report, boolean listenersOnly) {
		if (report != null) {
			if (this.subscriptorManager != null) {
				this.subscriptorManager.send(report);
			} else {
				this.subscriberManager.send(report, listenersOnly);
			}
		}
	}

	@Override
	protected void updateSubscriber(Subscriber subscriber) throws ImplementationException, ValidationException {
		try {
			subscriberManager.update(subscriber);
		} catch (havis.transport.ValidationException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	public void update(Subscriptor subscriptor) throws ImplementationException, ValidationException {
		updateSubscriptor(subscriptor);
	}

	protected void updateSubscriptor(Subscriptor subscriptor) throws ImplementationException, ValidationException {
		try {
			subscriptorManager.update(subscriptor);
		} catch (havis.transport.ValidationException | TransportException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	@Override
	protected boolean hasListeners() {
		return subscriberManager.hasListeners();
	}

	@Override
	protected void addSubscriberListener(SubscriberListener listener) throws ImplementationException, ValidationException {
		try {
			subscriberManager.add(listener);
		} catch (havis.transport.ValidationException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	private void setListener() throws ImplementationException {
		try {
			adapterHandler.setListener(new AdapterListener() {

				@Override
				public void usabilityChanged(Adapter source, DeviceUsabilityChangedEvent event) {
					// ignore
				}

				@Override
				public void usabilityChanged(Adapter source, FieldUsabilityChangedEvent event) {
					// ignore
				}

				@Override
				public void valueChanged(Adapter source, FieldValueChangedEvent event) {
					if (event.getDevice() == null || event.getField() == null)
						return;
					// notify value
					AdapterNotify adapterNotify = new AdapterNotify(event.getDevice(), event.getField(), event.getValue());
					AdapterCycle.this.notify(source != null ? source.getClass().getName() : null, adapterNotify);
				}
			});
		} catch (AdapterException e) {
			throw new ImplementationException("Failed to set listener", e);
		}
	}

	@Override
	protected void enable() throws ImplementationException {
		log.log(Level.FINE, "Cycle " + configuration.getName() + " will be enabled.");
		// set listener
		setListener();
		try {
			lock.lock();
			enabled = true;

			pollService = new PollService();

			// subscribe to fields
			for (Map.Entry<String, Set<String>> entry : configuration.getFieldSubscriptions().entrySet()) {
				for (String field : entry.getValue()) {
					try {
						addFieldSubscription(entry.getKey(), field);
					} catch (Exception e) {
						log.log(Level.WARNING, "Failed to subscribe " + entry.getKey() + " " + field + ":", e);
						// remove subscriptions
						for (Map.Entry<String, Set<String>> ent : configuration.getFieldSubscriptions().entrySet()) {
							for (String f : ent.getValue()) {
								try {
									adapterHandler.unsubscribe(ent.getKey(), f);
								} catch (AdapterException ex) {
									log.log(Level.WARNING, "Failed to unsubscribe from field.", ex);
								}
							}
						}
					}
				}
			}

			pollService.start(adapterHandler, new PollListener() {

				@Override
				public void onSuccess(String device, String field, Object value) {
					if (enabled) {
						// notify value
						AdapterNotify adapterNotify = new AdapterNotify(device, field, value);
						AdapterCycle.this.notify(AdapterCycle.class.getName(), adapterNotify);
					}
				}

				@Override
				public void onFailure(String device, String field, Exception e) {
					log.log(Level.WARNING, "Executor: Unable to read field '" + field + "'.", e);
				}
			});
			log.log(Level.FINE, "Cycle " + configuration.getName() + " has been enabled.");
		} finally {
			lock.unlock();
		}
	}

	@Override
	protected void disable() throws ImplementationException {
		try {
			lock.lock();
			if (!enabled)
				return;
		} finally {
			lock.unlock();
		}
		log.log(Level.FINE, "Cycle " + configuration.getName() + " will be disabled.");
		try {
			lock.lock();
			enabled = false;
			if (adapterHandler != null) {
				adapterHandler.setListener(null);
				for (Map.Entry<String, Set<String>> entry : configuration.getFieldSubscriptions().entrySet()) {
					for (String field : entry.getValue()) {
						try {
							adapterHandler.unsubscribe(entry.getKey(), field);
						} catch (AdapterException e) {
							log.log(Level.WARNING, "Failed to unsubscribe from field.", e);
						}
					}
				}
			}
			pollService.stop();
			pollService.clear();
			log.log(Level.FINE, "Cycle " + configuration.getName() + " has been disabled.");
		} catch (AdapterException e) {
			throw new ImplementationException(e);
		} finally {
			lock.unlock();
		}
	}

	public void addFieldSubscription(String device, String field) throws AdapterException {
		if (device == null || field == null)
			return;
		configuration.addFieldSubscription(device, field);
		if (enabled) {
			Device d = adapterHandler.getDevices().get(device);
			if (d != null && d.getFields() != null) {
				Field f = d.getFields().get(field);
				if (f != null) {
					Map<String, String> props = f.getProperties();
					if (props != null) {
						String stringPeriod = props.get(PERIOD);
						if (stringPeriod != null) {
							long period = Long.valueOf(stringPeriod);
							pollService.setField(device, field, period);
						}
					}
				}
			}
			adapterHandler.subscribe(device, field);
		}
	}

	public void removeFieldSubscription(String device, String field) throws AdapterException {
		if (device == null || field == null)
			return;
		configuration.removeFieldSubscription(device, field);
		pollService.removeField(device, field);
		adapterHandler.unsubscribe(device, field);
	}

	public void update(CycleSpec spec) {
		configuration.setFieldSubscriptions(spec.getFieldSubscriptions());
		configuration.setName(spec.getName());
	}
}
