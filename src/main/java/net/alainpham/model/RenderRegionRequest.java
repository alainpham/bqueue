package net.alainpham.model;

import java.io.Serializable;
import java.util.Date;


public class RenderRegionRequest implements Serializable{

    public String id;
    public String renderId;
    public String requesterHostname;
    public String workerHostname;
    public String status;

    public Date start;
    
    public Date end;

    public Long duration; 

    public String outputPrefix;
    public Integer resolutionX;
    public Integer resolutionY;
    public Integer samples;
    public Integer frameDivider;
    
    public String fileFormat; // PNG openexr
    public String colorDepth;
    public String colorMode;
    public Boolean useComposition;
    public Boolean useAdaptiveSampling;
    public Boolean useDenoising;

    public Integer areaX;
    public Integer areaY;
    public Integer totalTiles;
    public String renderedFilePath;

}
