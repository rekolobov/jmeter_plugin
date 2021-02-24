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

public class Item extends BaseItem{

	public Item(String line, AggregationProperties properties) throws IllegalItemFormatException {
		String[] values = PerformanceMessageParser.DELIMITER_PATTERN.split(line);

		if (values == null || values.length < 3) {  //failureMessage may be empty
			throw new IllegalItemFormatException("", values);
		}

		responseTime = Long.parseLong(values[1]);
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

		boolean codes = properties.isCalculateResponseCodes();
		boolean assets = properties.isCheckAssertions();

		if (assets && !isSuccessful) {
			logLine = line;
		}
		if (codes && assets) {
			if (values.length < 5)
				throw new IllegalItemFormatException("\tresponseCode\tisSuccess", values);
			responseCode = values[3];
			isSuccessful = values[4].equals("1") || values[4].equalsIgnoreCase("true");
		} else if (codes) {
			if (values.length < 4)
				throw new IllegalItemFormatException("\tresponseCode", values);
			responseCode = values[3];
		} else if (assets) {
			if (values.length < 4)
				throw new IllegalItemFormatException("\tisSuccess", values);
			isSuccessful = values[3].equals("1") || values[3].equalsIgnoreCase("true");
		}
	}

	public String toString() {
		return "_Item_: responseTime=[" + responseTime
				+ "] testName=[" + testName
				+ "] responseCode=[" + responseCode
				+ "] isSuccessful=[" + isSuccessful
				+ "] testGroupName=[" + testGroupName;
	}
}
