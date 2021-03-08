package havis.capture.rest.async;

import havis.capture.Device;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

public interface AdapterServiceAsync extends RestService {

	@GET
	@Path("devices")
	void getDevices(MethodCallback<Map<String, Device>> callback);

	@GET
	@Path("devices/{device}/fields/{field}/label")
	void getLabel(@PathParam("device") String device, @PathParam("field") String field, MethodCallback<String> callback);

	@PUT
	@Path("devices/{device}/fields/{field}/label")
	void setLabel(@PathParam("device") String device, @PathParam("field") String field, String label, MethodCallback<Void> callback);
}