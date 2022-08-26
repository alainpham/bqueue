package net.alainpham;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;

@ApplicationScoped
@Named("renderAggregationStrat")
public class RenderAggregationStrat extends GroupedBodyAggregationStrategy {
    
}
