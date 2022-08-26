package net.alainpham;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/renders")
public class ImageResource {

    @ConfigProperty(name = "blenderqueue.render.files.collector.folder") 
    public String renderCollectorfolder;

    @GET
    @Path("/{folder}/{fname}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response image(@PathParam("fname") String fname,@PathParam("folder") String folder ) throws IOException {
        FileInputStream fis =null;
        if(folder.equals("collected")){
            
            fis = new FileInputStream(new File(renderCollectorfolder+"/"+fname));
        }
        return Response.ok(fis).build();
    }
}
