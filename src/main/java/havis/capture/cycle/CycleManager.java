package havis.capture.cycle;

import havis.capture.AdapterException;
import havis.transport.Subscriber;
import havis.transport.Subscriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CycleManager {

	private Map<String, CycleSpec> specs = new HashMap<String, CycleSpec>();

	private Map<String, AdapterCycle> cycles = new HashMap<String, AdapterCycle>();

	private final static Logger log = Logger.getLogger(CycleManager.class.getName());

	public CycleManager() {

	}

	public CycleManager(Map<String, CycleSpec> specs) throws AdapterException {
		for (CycleSpec spec : specs.values()) {
			CycleSpec copy = new CycleSpec(spec);
			try {
				AdapterCycle cycle = createAdapterCycle(spec);
				cycles.put(spec.getId(), cycle);
				// start cycle
				if (copy.isEnabled()) {
					startSpec(copy);
				}
			} catch (AdapterException ex) {
				log.log(Level.INFO, "Could not start cycle with ID " + spec.getId(), ex);
				copy.setEnabled(false);
			}

			this.specs.put(copy.getId(), copy);
		}
	}

	/**
	 * @return The defined cycles spec ids
	 */
	public synchronized Set<String> getCycleSpecs() {
		return specs.keySet();
	}

	/**
	 * 
	 * @param id The spec ID
	 * @return A copy of the spec
	 * @throws AdapterException If cycle spec does not exist
	 */
	public synchronized CycleSpec getCycleSpec(String id) throws AdapterException {
		// check if spec exist
		CycleSpec spec = specs.get(id);
		if (spec == null) {
			throw new AdapterException("Cycle spec with ID '" + id + "' does not exist");
		}
		return new CycleSpec(spec);
	}

	/**
	 * Sets a UUID as ID of the cycle spec. If cycle is enabled then the cycle will
	 * be started.
	 * 
	 * @param spec The cycle spec
	 * @return The generated cycle spec UUID
	 * @throws AdapterException If cycle could not be started
	 */
	public synchronized String define(CycleSpec spec) throws AdapterException {
		// create id
		do {
			spec.setId(UUID.randomUUID().toString());
		} while (specs.containsKey(spec.getId()));
		// start cycle if cycle is enabled
		CycleSpec copy = new CycleSpec(spec);
		AdapterCycle cycle = createAdapterCycle(copy);
		cycles.put(copy.getId(), cycle);
		if (copy.isEnabled()) {
			startSpec(copy);
		}
		// add cycle to the list
		specs.put(copy.getId(), copy);
		// return generated id
		return copy.getId();
	}

	/**
	 * If cycle is enabled then the cycle will be started. If cycle is disabled then
	 * the running cycle will be stopped
	 * 
	 * @param id   The UUID of the spec
	 * @param spec The changed cycle spec
	 * @throws AdapterException If enabled cycle should be updated and updated spec
	 *                          is enabled, too. If spec does not exist
	 */
	public synchronized void update(String id, CycleSpec spec) throws AdapterException {
		CycleSpec current = specs.get(id);
		// check if spec exist
		if (current == null) {
			throw new AdapterException("Cycle spec with ID '" + id + "' does not exist");
		}
		CycleSpec copy = new CycleSpec(spec);
		// set spec ID for consistence
		copy.setId(id);
		// update cycle
		if (current.isEnabled()) {
			if (copy.isEnabled()) {
				throw new AdapterException("Cannot update active cycle");
			}
			stopSpec(current, false);
		} else {
			if (copy.isEnabled()) {
				AdapterCycle cycle = cycles.get(spec.getId());
				try{
				if (cycle!=null){
					cycle.update(spec);
					cycle.applyConfiguration(spec);
					startSpec(copy);
				}
			}catch(Exception ex){
				throw new AdapterException("Cannot update active cycle");	
			}
			}
		}
		specs.put(id, copy);
	}

	/**
	 * Removes the cycle spec and stops the running cycle.
	 * 
	 * @param id The UUID of the spec
	 * @throws AdapterException If spec does not exist
	 */
	public synchronized void undefine(String id) throws AdapterException {
		// check if spec exist
		if (!specs.containsKey(id)) {
			throw new AdapterException("Cycle spec with ID '" + id + "' does not exist");
		}
		stopSpec(specs.remove(id), true);
	}

	/**
	 * Dispose all
	 * 
	 * @throws AdapterException
	 */
	public synchronized void dispose() throws AdapterException {
		for (CycleSpec spec : specs.values()) {
			stopSpec(spec, true);
		}
		specs.clear();
	}

	/**
	 * Adds field subscription
	 * 
	 * @param id     The spec id
	 * @param device The device id
	 * @param field  The field id
	 * @throws AdapterException If spec does not exist
	 */
	public synchronized void addFieldSubscription(String id, String device, String field) throws AdapterException {
		// check if spec exist
		CycleSpec spec = specs.get(id);
		if (spec == null) {
			throw new AdapterException("Cycle spec with ID '" + id + "' does not exist");
		}
		if (spec.isEnabled()) {
			cycles.get(id).addFieldSubscription(device, field);
		}
		spec.addFieldSubscription(device, field);
	}

	/**
	 * Removes field subscription
	 * 
	 * @param id     The spec id
	 * @param device The device id
	 * @param field  The field id
	 * @throws AdapterException If spec does not exist
	 */
	public synchronized void removeFieldSubscription(String id, String device, String field) throws AdapterException {
		// check if spec exist
		CycleSpec spec = specs.get(id);
		if (spec == null) {
			throw new AdapterException("Cycle spec with ID '" + id + "' does not exist");
		}
		if (spec.isEnabled()) {
			cycles.get(id).removeFieldSubscription(device, field);
		}
		spec.removeFieldSubscription(device, field);
	}

	private void startSpec(CycleSpec spec) throws AdapterException {
		AdapterCycle cycle = cycles.get(spec.getId());
		if (cycle != null) {
			cycle.start();
			cycle.evaluateCycleState();
		} else {
			throw new AdapterException("Cycle spec with ID '" + spec.getId() + "' does not exist");
		}
	}

	private void stopSpec(CycleSpec spec,boolean stop) throws AdapterException {
		AdapterCycle cycle = cycles.get(spec.getId());
		if (cycle != null) {
			cycle.dispose();
			cycles.remove(spec.getId());
		}
		if (!stop) {
			cycle = createAdapterCycle(spec);
			cycles.put(spec.getId(), cycle);
		}
	}

	public synchronized void evaluateCycleState() {
		for (AdapterCycle c : cycles.values()) {
			c.evaluateCycleState();
		}
	}

	public String defineSubscriber(String spec, Subscriber subscriber) throws AdapterException {
		// check if cycle exists
		getCycleSpec(spec);

		// check if cycle is running and subscriber to subscriberManager of the
		// cycle
		AdapterCycle cycle = cycles.get(spec);
		if (cycle != null) {
			try {
				subscriber.setId(cycle.add(subscriber));
				cycle.evaluateCycleState();
			} catch (Exception e) {
				throw new AdapterException("Failed to define susbcriber. " + e.getMessage(), e);
			}
		} else {
			throw new AdapterException("Failed to define susbcriber. Cycle is not active.");
		}
		return subscriber.getId();
	}

	public void updateSubscriber(String spec, Subscriber subscriber) throws AdapterException {
		// check if cycle exists
		getCycleSpec(spec);

		// check if cycle is running and subscriber to subscriberManager of the
		// cycle
		AdapterCycle cycle = cycles.get(spec);
		if (cycle != null) {
			try {
				cycle.update(subscriber);
				cycle.evaluateCycleState();
			} catch (Exception e) {
				throw new AdapterException("Failed to update susbcriber. " + e.getMessage(), e);
			}
		} else {
			throw new AdapterException("Failed to update susbcriber. Cycle is not active.");
		}
	}

	public void undefineSubscriber(String spec, Subscriber subscriber) throws AdapterException {
		// check if cycle exists
		getCycleSpec(spec);

		// check if cycle is running and subscriber to subscriberManager of the
		// cycle
		AdapterCycle cycle = cycles.get(spec);
		if (cycle != null) {
			try {
				cycle.removeSubscriber(subscriber);
				cycle.evaluateCycleState();
			} catch (Exception e) {
				throw new AdapterException("Failed to remove susbcriber. " + e.getMessage(), e);
			}
		} else {
			throw new AdapterException("Failed to remove susbcriber. Cycle is not active.");
		}
	}

	public String defineSubscriptor(String spec, Subscriptor subscriptor) throws AdapterException {
		// check if cycle exists
		getCycleSpec(spec);

		// check if cycle is running and subscriptor to subscriptorManager of the
		// cycle
		AdapterCycle cycle = cycles.get(spec);
		if (cycle != null) {
			try {
				subscriptor.setId(cycle.add(subscriptor));
				cycle.evaluateCycleState();
			} catch (Exception e) {
				throw new AdapterException("Failed to define subscriptor. " + e.getMessage(), e);
			}
		} else {
			throw new AdapterException("Failed to define susbcriptor. Cycle is not active.");
		}
		return subscriptor.getId();
	}

	public void updateSubscriptor(String spec, Subscriptor subscriptor) throws AdapterException {
		// check if cycle exists
		getCycleSpec(spec);

		// check if cycle is running and subscriptor to subscriptorManager of the
		// cycle
		AdapterCycle cycle = cycles.get(spec);
		if (cycle != null) {
			try {
				cycle.update(subscriptor);
				cycle.evaluateCycleState();
			} catch (Exception e) {
				throw new AdapterException("Failed to update subscriptor. " + e.getMessage(), e);
			}
		} else {
				throw new AdapterException("Failed to update susbcriptor. Cycle is not active.");

		}
	}

	public void undefineSubscriptor(String spec, Subscriptor subscriptor) throws AdapterException {
		// check if cycle exists
		getCycleSpec(spec);

		// check if cycle is running and subscriptor to subscriptorManager of the
		// cycle
		AdapterCycle cycle = cycles.get(spec);
		if (cycle != null) {
			try {
				cycle.removeSubscriptor(subscriptor);
				cycle.evaluateCycleState();
			} catch (Exception e) {
				throw new AdapterException("Failed to remove susbcriptor. " + e.getMessage(), e);
			}
		} else {
			throw new AdapterException("Failed to remove susbcriptor. Cycle is not active.");
		}
	}

	/**
	 * Creates a new adapter cycle with the specified values defined by the cycle
	 * spec. Here it is possible to use a different ReportFactory for an
	 * AdapterCycle.
	 * 
	 * @param spec The cycle spec
	 * @return The cycle
	 * @throws AdapterException If cycle creation failed
	 */
	protected abstract AdapterCycle createAdapterCycle(CycleSpec spec) throws AdapterException;
}
