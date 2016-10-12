package com.tngtech.jira.plugins.utils;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class UtilsTest {
	@Test
	public void shouldCreateOneElementList() {
		String entry = "entry";
		List<String> entryList = ImmutableList.of(entry);

		assertThat(entryList, notNullValue());
		assertThat(entryList.size(), is(1));
		assertThat(entryList.get(0), equalTo(entry));
	}

	@Test
	public void shouldSortMap() {
		Map<Long, String> unsortedMap = new LinkedHashMap<Long, String>();
		unsortedMap.put(0L, "A");
		unsortedMap.put(1L, "C");
		unsortedMap.put(2L, "B");
		unsortedMap.put(3L, "B");
		unsortedMap.put(4L, "D");
		Map<Long, String> sortedMap = Utils.sortMapByValue(unsortedMap);

		assertThat(sortedMap.size(), is(unsortedMap.size()));
		int i = 0;
		for (Map.Entry<Long, String> entry : sortedMap.entrySet()) {
			Long expectedLong = null;
			String expectedString = null;
			switch (i) {
			case 0:
				expectedLong = 0L;
				expectedString = "A";
				break;
			case 1:
				expectedLong = 2L;
				expectedString = "B";
				break;
			case 2:
				expectedLong = 3L;
				expectedString = "B";
				break;
			case 3:
				expectedLong = 1L;
				expectedString = "C";
				break;
			case 4:
				expectedLong = 4L;
				expectedString = "D";
				break;
			}
			assertThat(entry.getKey(), equalTo(expectedLong));
			assertThat(entry.getValue(), equalTo(expectedString));
			i++;
		}
	}

	@Test
	public void shouldReturnStringList() {
		List<Integer> input = new ArrayList<Integer>();
		input.add(Integer.valueOf(42));
		input.add(Integer.valueOf(1337));

		List<String> output = Utils.toStringList(input);

		assertThat(output.size(), is(input.size()));
		assertThat(output.get(0), equalTo("42"));
		assertThat(output.get(1), equalTo("1337"));
	}

}
