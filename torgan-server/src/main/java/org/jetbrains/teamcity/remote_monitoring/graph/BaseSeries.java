package org.jetbrains.teamcity.remote_monitoring.graph;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * simplest realization of Series
 */
public class BaseSeries extends Series {
	public BaseSeries(@NotNull String label) {
		super(label);
	}

	@Override
	public List<List<Long>> getValues() {
		return toListFormat();
	}
}
