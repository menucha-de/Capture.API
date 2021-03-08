package havis.capture.provider;

import havis.capture.AdapterException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AdapterExceptionMapper implements ExceptionMapper<AdapterException> {

	@Override
	public Response toResponse(AdapterException e) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}