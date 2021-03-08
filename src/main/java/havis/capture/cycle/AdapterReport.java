package havis.capture.cycle;

import havis.util.cycle.Initiation;
import havis.util.cycle.Termination;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class AdapterReport {
	private String applicationId;
	private String reportName;
	@XmlJavaTypeAdapter(DateTypeAdapter.class)
	private Date date;

	private long totalMilliseconds;
	private Initiation initiation;
	private String initiator;
	private Termination termination;
	private String terminator;

	private List<DeviceReport> devices;

	public AdapterReport() {

	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationID(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getTotalMilliseconds() {
		return totalMilliseconds;
	}

	public void setTotalMilliseconds(long totalMilliseconds) {
		this.totalMilliseconds = totalMilliseconds;
	}

	public Initiation getInitiation() {
		return initiation;
	}

	public void setInitiation(Initiation initiation) {
		this.initiation = initiation;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public Termination getTermination() {
		return termination;
	}

	public void setTermination(Termination termination) {
		this.termination = termination;
	}

	public String getTerminator() {
		return terminator;
	}

	public void setTerminator(String terminator) {
		this.terminator = terminator;
	}

	public List<DeviceReport> getDevices() {
		if (devices == null) {
			this.devices = new ArrayList<DeviceReport>();
		}
		return devices;
	}

	public void setDevices(List<DeviceReport> devices) {
		this.devices = devices;
	}

}
