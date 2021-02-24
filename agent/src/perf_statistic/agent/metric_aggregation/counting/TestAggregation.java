package perf_statistic.agent.metric_aggregation.counting;

import perf_statistic.agent.metric_aggregation.AggregationProperties;
import perf_statistic.agent.metric_aggregation.counting.items.BaseItem;
import perf_statistic.agent.metric_aggregation.counting.items.Item;

public class TestAggregation extends BaseAggregation {

	public TestAggregation(BaseItem item, AggregationProperties properties) {
		super(item.getTestName(), properties);
		addItem(item);
	}

	public void addItem(BaseItem item) {
		super.addItem(item);
	}
}
