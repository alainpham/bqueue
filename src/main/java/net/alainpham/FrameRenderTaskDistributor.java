package net.alainpham;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;


import org.apache.camel.Body;
import org.apache.camel.ExchangeProperty;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import net.alainpham.model.DistributedFrameRenderRequest;
import net.alainpham.model.RenderRegionRequest;

@ApplicationScoped
@Named("frameRenderTaskDistributor")
public class FrameRenderTaskDistributor {

    @ConfigProperty(name = "blenderqueue.hostname") 
    public String hostname;

    @ConfigProperty(name = "blenderqueue.render.files.local.folder") 
    public String renderfolder;

    @ConfigProperty(name = "blenderqueue.render.files.collector.folder") 
    public String renderCollectorfolder;
    

    List<RenderRegionRequest> calculateTasks(DistributedFrameRenderRequest distributedFrameRenderRequest) {

        List<RenderRegionRequest> renderRegionRequests = new ArrayList<RenderRegionRequest>();
        
        String renderId = distributedFrameRenderRequest.fileName +"."+  hostname + "." + System.currentTimeMillis();
        for (int x = 0; x < distributedFrameRenderRequest.frameDivider; x++) {
            for (int y = 0; y < distributedFrameRenderRequest.frameDivider; y++) {
                RenderRegionRequest request = new RenderRegionRequest();
                request.requesterHostname = hostname;
                request.outputPrefix = distributedFrameRenderRequest.fileName;
                request.resolutionX = distributedFrameRenderRequest.resolutionX;
                request.resolutionY = distributedFrameRenderRequest.resolutionY;
                request.frameDivider = distributedFrameRenderRequest.frameDivider;

                request.fileFormat = distributedFrameRenderRequest.fileFormat;
                request.colorDepth = distributedFrameRenderRequest.colorDepth;
                request.colorMode = distributedFrameRenderRequest.colorMode;
                request.useComposition = distributedFrameRenderRequest.useComposition;
                request.useAdaptiveSampling = distributedFrameRenderRequest.useAdaptiveSampling;
                request.useDenoising = distributedFrameRenderRequest.useDenoising;

                request.areaX = x;
                request.areaY = y;
                request.totalTiles = request.frameDivider * request.frameDivider;
                request.samples = distributedFrameRenderRequest.samples;
                request.renderId = renderId;
                renderRegionRequests.add(request);
            }
        }

        return renderRegionRequests;

    }

    RenderRegionRequest startWorker(RenderRegionRequest r){
        r.workerHostname=hostname;
        r.status="started";
        r.start= new Date();
        r.id=r.workerHostname + "-" +r.outputPrefix+"-x"+r.areaX+"-y"+r.areaY;
        return r;
    }

    RenderRegionRequest doneWorker(RenderRegionRequest r, @Header("renderedFilePath") String renderedFilePath){
        r.status="done";
        r.end=new Date();
        r.duration = Math.abs(r.end.getTime() - r.start.getTime());
        r.renderedFilePath = renderedFilePath;
        return r;
    }

    String computeCollectorQueue(RenderRegionRequest r){
        return "app.collector."+r.requesterHostname;
    }

    void computeCollectorData(@Body RenderRegionRequest r, @Headers Map<String,Object> headers){
        headers.put("CamelJmsDestinationName", "app.collector."+r.requesterHostname);
        headers.put("renderId",r.renderId);
        headers.put("resolutionX", r.resolutionX);
        headers.put("resolutionY", r.resolutionY);
        headers.put("areaX", r.areaX);
        headers.put("areaY", r.areaY);
        headers.put("frameDivider", r.frameDivider);
        headers.put("totalTiles", r.totalTiles);
        headers.put("renderedFilePath",r.renderedFilePath);
    }

    InputStream getLocalPictureAsStream(@ExchangeProperty("initialRequest") RenderRegionRequest r) throws FileNotFoundException{

        return new FileInputStream(new File(renderfolder+"/"+r.renderedFilePath));
    }

    InputStream fileAsStream(String fileName) throws FileNotFoundException{
        return new FileInputStream(new File(fileName));
    }

    Map<String,Object> imageDataFromPng(@Header("CamelFileName") String filename,@Header("areaX") Integer x,@Header("areaY") Integer y,@Header("frameDivider") Integer frameDivider,@Header("resolutionX") Integer resolutionX,@Header("resolutionY") Integer resolutionY) throws IOException{
        // String imageAsBase64 = encodeFileToBase64(new File(renderCollectorfolder+"/"+filename));
        Map<String,Object> imageData = new LinkedHashMap<String,Object>();
        imageData.put("id", filename);
        imageData.put("fileName", filename);
        imageData.put("areaX", x*resolutionX/frameDivider);
        imageData.put("areaY",(frameDivider-y-1)*resolutionY/frameDivider);
        imageData.put("resolutionX", resolutionX);
        imageData.put("resolutionY", resolutionY);
        return imageData;
    }

    private String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }
}
