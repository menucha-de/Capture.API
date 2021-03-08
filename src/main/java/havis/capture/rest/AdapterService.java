package havis.capture.rest;

import havis.capture.AdapterException;
import havis.capture.Device;

import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface AdapterService extends AutoCloseable {
	// ###################### Devices #####################
	@PermitAll
	@GET
	@Path("devices")
	@Produces({ MediaType.APPLICATION_JSON })
	public Map<String, Device> getDevices() throws AdapterException;

	@PermitAll
	@GET
	@Path("devices/{device}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Device getDevice(@PathParam("device") String device) throws AdapterException;

	@PermitAll
	@POST
	@Path("devices")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_PLAIN })
	public String add(Device device) throws AdapterException;

	@PermitAll
	@DELETE
	@Path("devices/{device}")
	public void remove(@PathParam("device") String device) throws AdapterException;

	@PermitAll
	@GET
	@Path("devices/{device}/label")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getLabel(@PathParam("device") String device) throws AdapterException;

	@PermitAll
	@PUT
	@Path("devices/{device}/label")
	@Consumes({ MediaType.TEXT_PLAIN })
	public void setLabel(@PathParam("device") String device, String label) throws AdapterException;

	@PermitAll
	@GET
	@Path("devices/{device}/properties")
	@Produces({ MediaType.APPLICATION_JSON })
	public Map<String, String> getProperties(@PathParam("device") String device) throws AdapterException;

	@PermitAll
	@GET
	@Path("devices/{device}/properties/{name}")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getProperty(@PathParam("device") String device, @PathParam("name") String name) throws AdapterException;

	@PermitAll
	@PUT
	@Path("devices/{device}/properties/{name}")
	@Consumes({ MediaType.TEXT_PLAIN })
	public void setProperty(@PathParam("device") String device, @PathParam("name") String name, String value) throws AdapterException;

	// ###################### Fields #####################
	@PermitAll
	@GET
	@Path("devices/{device}/fields/{field}")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getValue(@PathParam("device") String device, @PathParam("field") String field) throws AdapterException;

	@PermitAll
	@PUT
	@Path("devices/{device}/fields/{field}")
	@Consumes({ MediaType.TEXT_PLAIN })
	public void setValue(@PathParam("device") String device, @PathParam("field") String field, String value) throws AdapterException;

	// ###################### Subscriptions #####################
	@PermitAll
	@POST
	@Path("subscriptions")
	@Consumes({ MediaType.TEXT_PLAIN })
	@Produces({ MediaType.TEXT_PLAIN })
	public String addSubscription(String uri) throws AdapterException;
	
	@PermitAll
	@DELETE
	@Path("subscriptions/{id}")
	public void removeSubscription(@PathParam("id") String id) throws AdapterException;
	
	@PermitAll
	@POST
	@Path("subscriptions/{id}/devices/{device}/fields/{field}")
	public void subscribe(@PathParam("id") String id, @PathParam("device") String device, @PathParam("field") String field) throws AdapterException;
	
	@PermitAll
	@DELETE
	@Path("subscriptions/{id}/devices/{device}/fields/{field}")
	public void unsubscribe(@PathParam("id") String id, @PathParam("device") String device, @PathParam("field") String field) throws AdapterException;
}