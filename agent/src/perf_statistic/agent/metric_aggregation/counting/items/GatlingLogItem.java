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

import perf_statistic.agent.metric_aggregation.AggregationProperties;
import perf_statistic.common.PerformanceMessageParser;
import perf_statistic.common.StringUtils;

public class GatlingLogItem extends BaseItem{

	public GatlingLogItem(String line, AggregationProperties properties) throws IllegalItemFormatException {
		String[] values = PerformanceMessageParser.DELIMITER_PATTERN.split(line);

		if(values[0].contains("USER")){
			isUserLog = true;
		}

		if(!isUserLog) {
			responseTime = Long.parseLong(values[4]) - Long.parseLong(values[3]);
			if (properties.isUsedTestGroups()) {
				String[] testNameParts = values[2].split(":");
				if (testNameParts.length < 2) {
					throw new IllegalItemFormatException("Test label must contains thread group name separated to test name by ':' ! Find: " + values[2]);
				}
				testGroupName = testNameParts[0].trim();
				testName = StringUtils.checkTestName(testNameParts[1].trim());
			} else {
				testName = StringUtils.checkTestName(values[2]);
			}

			if (properties.isCheckAssertions()) {
				if (values.length < 5)
					throw new IllegalItemFormatException("\tisSuccess", values);
				isSuccessful = values[5].equals("OK");
			}

			responseCode = "200";
		}
	}

	public String toString() {
		return "_GatlingItem_: responseTime=[" + responseTime
				+ "] testName=[" + testName
				+ "] isSuccessful=[" + isSuccessful
				+ "] testGroupName=[" + testGroupName;
	}
}
