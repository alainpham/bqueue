package net.alainpham;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
// import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.component.exec.ExecBinding;

@ApplicationScoped
public class Main extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        

		// restConfiguration()
		// .apiContextRouteId("api-docs")
		// .bindingMode(RestBindingMode.json)
		// .contextPath("/camel")
		// .apiContextPath("/api-docs")
		// .apiProperty("cors", "true")
		// .apiProperty("api.title", "test")
		// .apiProperty("api.version", "test")
		// .dataFormatProperty("prettyPrint", "true");
        // ;


        
    }
    
}
