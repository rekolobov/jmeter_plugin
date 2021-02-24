/*
 * Copyright 2000-2021 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package perf_statistic.agent.metric_aggregation.counting.items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import perf_statistic.agent.common.BaseFileReader;
import perf_statistic.agent.metric_aggregation.AggregationProperties;
import perf_statistic.common.PerformanceMessageParser;
import perf_statistic.common.StringUtils;

import java.util.Arrays;

abstract public class BaseItem {

	protected String responseCode = null;
	protected boolean isUserLog = false;
	protected long responseTime;
	protected String testName;
	protected boolean isSuccessful;
	protected String testGroupName = StringUtils.EMPTY;

//	save only if check assertions and item is not successful
	protected String logLine;

	public final long getResponseTime() {
		return responseTime;
	}

	public final String getTestName() {
		return testName;
	}

	public final boolean isSuccessful() {
		return isSuccessful;
	}

	@NotNull
	public final String getTestGroupName() {
		return testGroupName  != null ? testGroupName : StringUtils.EMPTY;
	}

	@Nullable
	public final String getLogLine() {
		return logLine;
	}

	public abstract String toString();

	public boolean isUserLog() {
		return isUserLog;
	}

	public final String getResponseCode() {
		return responseCode;
	}

	public static class IllegalItemFormatException  extends BaseFileReader.FileFormatException {
		public IllegalItemFormatException(String message) {
			super(message);
		}
		public IllegalItemFormatException(String wrongFiledNames, String[] actualData) {
			super("Result item format must included asserted result. Format: startTime\tresponseTime\ttestName" + wrongFiledNames + "...\nFound" + Arrays.toString(actualData));
		}
	}
}
