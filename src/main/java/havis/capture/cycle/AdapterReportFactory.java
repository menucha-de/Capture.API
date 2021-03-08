package havis.capture.cycle;

import havis.capture.AdapterHandler;
import havis.capture.Device;
import havis.capture.Field;
import havis.util.cycle.Initiation;
import havis.util.cycle.ReportFactory;
import havis.util.cycle.Termination;
import havis.util.cycle.common.ListCycleData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdapterReportFactory implements ReportFactory<AdapterNotify, ListCycleData<AdapterNotify>> {

	private final static Logger log = Logger.getLogger(AdapterReportFactory.class.getName());

	private AdapterHandler adapterHandler;
	private CycleSpec spec;

	public AdapterReportFactory(AdapterHandler adapterHandler, CycleSpec spec) {
		this.adapterHandler = adapterHandler;
		this.spec = spec;
	}

	@Override
	public Object create(ListCycleData<AdapterNotify> data, Date date, long totalMilliseconds, Initiation initiation, String initiator,
			Termination termination, String terminator) {
		if (data == null || spec.isReportIfEmpty() || data.getData().size() > 0) {
			AdapterReport report = new AdapterReport();
			if (spec != null) {
				report.setApplicationID(spec.getApplicationId());
				report.setReportName(spec.getName());
			}
			report.setDate(date);
			report.setTotalMilliseconds(totalMilliseconds);
			report.setTermination(termination);
			report.setTerminator(terminator);
			report.setInitiation(initiation);
			report.setInitiator(initiator);
			try {
				Map<String, DeviceReport> reports = new HashMap<String, DeviceReport>();
				if (data != null && data.getData() != null) {
					for (AdapterNotify entry : data.getData()) {

						DeviceReport deviceReport = reports.get(entry.getDevice());
						if (deviceReport == null) {
							deviceReport = new DeviceReport();
							deviceReport.setName(getName(entry.getDevice()));
							reports.put(entry.getDevice(), deviceReport);
							report.getDevices().add(deviceReport);
						}

						FieldReport fieldReport = new FieldReport();
						deviceReport.getFields().add(fieldReport);
						fieldReport.setName(getName(entry.getDevice(), entry.getField()));
						fieldReport.setValue(entry.getValue());
						fieldReport.setDate(entry.getDate());
					}
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Failed to create device reports", e);
			}
			return report;
		}
		return null;
	}

	/**
	 * Will return the label of the device. If the label is not set then the
	 * name will be used. If the name is missing as well then the ID will be
	 * returned.
	 * 
	 * @param device
	 *            The device ID
	 * @return The device name for the report
	 */
	protected String getName(String device) {
		String name = null;
		if (adapterHandler != null) {
			try {
				name = adapterHandler.getLabel(device);
			} catch (Exception e) {
				log.log(Level.FINE, "Failed to get device label.", e);
			}
			if (name == null || name.isEmpty()) {
				try {
					Device d = adapterHandler.getDevices().get(device);
					if (d != null) {
						name = d.getName();
					}

				} catch (Exception e) {
					log.log(Level.FINE, "Failed to get device label.", e);
				}
			}
		}
		if (name == null || name.isEmpty()) {
			name = device;
		}
		return name;
	}

	/**
	 * Will return the label of the field. If the label is not set then the name
	 * will be used. If the name is missing as well then the ID will be
	 * returned.
	 * 
	 * @param device
	 *            The device ID
	 * @param field
	 *            The field ID
	 * @return The field name for the report
	 */
	protected String getName(String device, String field) {
		String name = null;
		if (adapterHandler != null) {
			try {
				name = adapterHandler.getLabel(device, field);
			} catch (Exception e) {
				log.log(Level.FINE, "Failed to get field label.", e);
			}
			if (name == null || name.isEmpty()) {
				try {
					Device d = adapterHandler.getDevices().get(device);
					if (d != null && d.getFields() != null) {
						Field f = d.getFields().get(field);
						if (f != null) {
							name = f.getName();
						}
					}

				} catch (Exception e) {
					log.log(Level.FINE, "Failed to get field label.", e);
				}
			}
		}
		if (name == null || name.isEmpty()) {
			name = field;
		}
		return name;
	}

	@Override
	public void dispose() {

	}

}
