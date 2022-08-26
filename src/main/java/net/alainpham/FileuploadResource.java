package net.alainpham;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;

@Path("/upload")
public class FileuploadResource {

    @Inject
    CamelContext context;

    @POST
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response fileUpload(@PathParam("name") String name ,InputStream stream) throws IOException {
        context.createProducerTemplate().requestBodyAndHeader("direct:broadcast-blendfile", stream, "id", name);
        return Response.ok("ok").build();
    }
}
