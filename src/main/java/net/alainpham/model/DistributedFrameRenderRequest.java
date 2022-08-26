package net.alainpham.model;

import java.io.Serializable;

public class DistributedFrameRenderRequest implements Serializable{

    public String fileName;
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
}
