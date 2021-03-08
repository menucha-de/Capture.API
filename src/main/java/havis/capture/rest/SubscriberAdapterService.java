package havis.capture.rest;

import havis.capture.Adapter;
import havis.capture.AdapterException;
import havis.capture.AdapterHandler;
import havis.capture.AdapterHandlerFactory;
import havis.capture.AdapterListener;
import havis.capture.AdapterManager;
import havis.capture.DeviceUsabilityChangedEvent;
import havis.capture.FieldUsabilityChangedEvent;
import havis.capture.FieldValueChangedEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class SubscriberAdapterService implements AdapterService {

	private final static Logger log = Logger.getLogger(SubscriberAdapterService.class.getName());

	/**
	 * Contains all queues which has been created for a subscription. The queues
	 * contain the objects which shall be send to the client.
	 */
	protected Map<String, BlockingQueue<Object>> queues = new ConcurrentHashMap<>();
	/**
	 * Contains all iterators which are used to send the objects of the queues
	 * to the clients.
	 */
	protected Map<String, Iterator<Object>> iterators = new ConcurrentHashMap<String, Iterator<Object>>();
	/**
	 * Contains all AdapterHandlers which has been created for a subscription
	 */
	protected Map<String, AdapterHandler> subscriptions = new ConcurrentHashMap<String, AdapterHandler>();
	/**
	 * Used to write the Events as JSON in the queues
	 */
	protected ObjectMapper mapper = new ObjectMapper();
	/**
	 * The max number of events in the queue until the connection will be
	 * destroyed.
	 */
	protected int queueSize = 200;

	/**
	 * Stops the keepAliveService and closes all AdapterHandlers which has been
	 * created
	 */
	@Override
	public void close() throws Exception {
		Map<String, Iterator<Object>> iterators = new HashMap<String, Iterator<Object>>(this.iterators);
		for (Iterator<Object> iterator : iterators.values()) {
			iterator.remove();
		}
	}

	/**
	 * Creates an AdapterHandler with a listener which will push the events in
	 * the queue for the client.
	 * 
	 * @param factory
	 *            The AdapterHandler factory to create a new handler.
	 * @param uuid
	 *            The ID of the client
	 * @throws AdapterException
	 */
	private void createSubscribeHandler(AdapterHandlerFactory factory, final String uuid) throws AdapterException {
		AdapterHandler handler = null;
		try {
			handler = factory.create();
			handler.setListener(new AdapterListener() {

				@Override
				public void valueChanged(Adapter source, final FieldValueChangedEvent event) {
					// get the queue of the client
					BlockingQueue<Object> queue = queues.get(uuid);
					if (queue != null) {
						try {
							queue.add(mapper.writeValueAsString(event));
						} catch (JsonProcessingException e) {
							log.log(Level.FINE, e.getMessage(), e);
						}
						checkQueue(queue, uuid);
					}
				}

				@Override
				public void usabilityChanged(Adapter source, final DeviceUsabilityChangedEvent event) {
					BlockingQueue<Object> queue = queues.get(uuid);
					if (queue != null) {
						try {
							queue.add(mapper.writeValueAsString(event));
						} catch (JsonProcessingException e) {
							log.log(Level.FINE, e.getMessage(), e);
						}
						checkQueue(queue, uuid);
					}
				}

				@Override
				public void usabilityChanged(Adapter source, final FieldUsabilityChangedEvent event) {
					BlockingQueue<Object> queue = queues.get(uuid);
					if (queue != null) {
						try {
							queue.add(mapper.writeValueAsString(event));
						} catch (JsonProcessingException e) {
							log.log(Level.FINE, e.getMessage(), e);
						}
						checkQueue(queue, uuid);
					}
				}
			});
			subscriptions.put(uuid, handler);
		} catch (Exception e) {
			if (handler != null) {
				try {
					handler.setListener(null);
					handler.close();
				} catch (Exception e1) {
					log.log(Level.FINE, e1.getMessage(), e1);
				}
			}
			throw new AdapterException(e.getMessage(), e);
		}
	}

	private void checkQueue(BlockingQueue<Object> queue, String uuid) {
		if (queue.size() > queueSize) {
			Iterator<Object> iterator = iterators.remove(uuid);
			if (iterator != null) {
				iterator.remove();
			}
		}
	}

	protected Iterator<Object> getStream(final AdapterManager manager, final String uuid) throws AdapterException {
		return getStream(new AdapterHandlerFactory() {

			@Override
			public AdapterHandler create() {
				return manager.createInstance();
			}
		}, uuid);
	}

	protected Iterator<Object> getStream(AdapterHandlerFactory factory, final String uuid) throws AdapterException {
		Iterator<Object> iterator = iterators.get(uuid);
		if (iterator != null) {
			iterator.remove();
		}

		final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
		queues.put(uuid, queue);
		createSubscribeHandler(factory, uuid);

		iterator = new Iterator<Object>() {
			boolean hasNext = true;

			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public Object next() {
				try {
					Object element = queue.take();
					if (element == null) {
						hasNext = false;
					}
					return element;
				} catch (InterruptedException e) {
					return null;
				}
			}

			@Override
			public void remove() {
				try {
					log.log(Level.FINE, "Stream has been closed.");
					AdapterHandler handler = subscriptions.remove(uuid);
					handler.setListener(null);
					handler.close();
					queues.remove(uuid);
				} catch (Exception e) {
					log.log(Level.WARNING, e.getMessage(), e);
				} finally {
					iterators.remove(uuid);
				}
			}
		};
		iterators.put(uuid, iterator);
		return iterator;
	}

	@Override
	public void subscribe(String uuid, String device, String field) throws AdapterException {
		AdapterHandler handler = subscriptions.get(uuid);
		if (handler != null) {
			handler.subscribe(device, field);
		} else {
			throw new AdapterException("There is no subscription for id " + uuid);
		}
	}

	@Override
	public void unsubscribe(String uuid, String device, String field) throws AdapterException {
		AdapterHandler handler = subscriptions.get(uuid);
		if (handler != null) {
			handler.unsubscribe(device, field);
		}
	}
}
