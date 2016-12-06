/**
 * Copyright (C) 2012-2016  Dušan Vejnovič  <vaadin@dussan.org>
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
package org.dussan.vaadin.dcharts.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.dussan.vaadin.dcharts.events.ChartData;
import org.dussan.vaadin.dcharts.events.ChartEventType;

public class ChartDataHelper {

	private static final String ID = "id";
	private static final String EVENT_TYPE = "event";
	private static final String DATA = "data";
	private static final String EVENT_SPLITTER = "_::_";
	private static final String DATA_SPLITTER = "_:::_";

	private static boolean isNumber(String value) {
		return (value != null && NumberUtils.isNumber(value));
	}

	private static boolean isLongNumber(String value) {
		if (isNumber(value)) {
			Double number = convertToDouble(value);
			return (number == Math.floor(number));
		}
		return false;
	}

	private static Long converToLong(String value) {
		if (isNumber(value)) {
			return Long.parseLong(value);
		}
		return null;
	}

	private static Double convertToDouble(String value) {
		if (isNumber(value)) {
			return Double.parseDouble(value);
		}
		return null;
	}

	private static List<Object> convertToArray(String datas, String splitter) {
		List<String> tempData = new ArrayList<String>();
		if (datas.contains(splitter)) {
			tempData.addAll(Arrays.asList(datas.split(splitter)));
		} else {
			tempData.add(datas);
		}

		List<Object> objectData = new ArrayList<Object>();
		for (String data : tempData) {
			if (!splitter.equals(DATA_SPLITTER) && data.contains(DATA_SPLITTER)) {
				objectData.add(convertToArray(data, DATA_SPLITTER));
			} else {
				if (isNumber(data)) {
					if (isLongNumber(data)) {
						objectData.add(converToLong(data));
					} else {
						objectData.add(convertToDouble(data));
					}
				} else {
					objectData.add(data);
				}
			}
		}
		return objectData;
	}

	public static ChartData process(Map<String, String> chartData) {
		try {
			if (chartData.containsKey(ID) && chartData.containsKey(EVENT_TYPE)
					&& chartData.containsKey(DATA)) {
				String chartId = chartData.get(ID);
				ChartEventType chartEventType = ChartEventType
						.fromString(chartData.get(EVENT_TYPE));
				Long seriesIndex = null;
				Long pointIndex = null;

				List<Object> data = convertToArray(chartData.get(DATA),
						EVENT_SPLITTER);
				if (data != null && data.size() > 2) {
					seriesIndex = (Long) data.get(0);
					data.remove(0);
					pointIndex = (Long) data.get(0);
					data.remove(0);
				}

				if (chartId != null && !chartId.isEmpty()
						&& chartEventType != null) {
					return new ChartData(chartId, chartEventType, seriesIndex,
							pointIndex, data == null ? null : data.toArray());
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

}
