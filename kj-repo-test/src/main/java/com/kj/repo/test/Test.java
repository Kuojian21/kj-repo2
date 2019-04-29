package com.kj.repo.test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;

public class Test {

	public static void main(String[] args) {
		List<Integer> list = Lists.newArrayList();

		Integer v = list.stream().max(Comparator.comparingInt(k -> k)).orElse(0);
		System.out.println(v);

		IntStream.range(1, 100).boxed().forEach(i -> {
			throw new RuntimeException("");
		});

	}

	public static String get() {
		return "{\n" +
						"  \"code\": 1,\n" +
						"  \"list\": [\n" +
						"    {\n" +
						"      \"id\": \"0\",\n" +
						"      \"name\": \"未知桶\",\n" +
						"      \"code\": \"en\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"1\",\n" +
						"      \"name\": \"🇨🇳中文\",\n" +
						"      \"code\": \"zh\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"2\",\n" +
						"      \"name\": \"台湾\",\n" +
						"      \"code\": \"zh-TW\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"3\",\n" +
						"      \"name\": \"🇬🇧英文\",\n" +
						"      \"code\": \"en\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"4\",\n" +
						"      \"name\": \"🇰🇷韩国\",\n" +
						"      \"code\": \"ko\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"5\",\n" +
						"      \"name\": \"🇯🇵日本\",\n" +
						"      \"code\": \"ja\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"6\",\n" +
						"      \"name\": \"🇹🇭泰国\",\n" +
						"      \"code\": \"th\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"7\",\n" +
						"      \"name\": \"🇲🇾马来西亚\",\n" +
						"      \"code\": \"ms\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"8\",\n" +
						"      \"name\": \"🇮🇩印度尼西亚\",\n" +
						"      \"code\": \"ms\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"9\",\n" +
						"      \"name\": \"🇷🇺俄罗斯\",\n" +
						"      \"code\": \"ru\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"10\",\n" +
						"      \"name\": \"🇵🇭菲律宾\",\n" +
						"      \"code\": \"en-PH\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"11\",\n" +
						"      \"name\": \"🇧🇷巴西\",\n" +
						"      \"code\": \"pt-BR\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"12\",\n" +
						"      \"name\": \"🇻🇳越南\",\n" +
						"      \"code\": \"vi\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"13\",\n" +
						"      \"name\": \"🇲🇲缅甸\",\n" +
						"      \"code\": \"en\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"14\",\n" +
						"      \"name\": \"🇮🇳印度\",\n" +
						"      \"code\": \"hi\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"15\",\n" +
						"      \"name\": \"🇩🇪德国\",\n" +
						"      \"code\": \"en\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"16\",\n" +
						"      \"name\": \"🇹🇷土耳其\",\n" +
						"      \"code\": \"tr\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"17\",\n" +
						"      \"name\": \"🇫🇷法国\",\n" +
						"      \"code\": \"fr\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"18\",\n" +
						"      \"name\": \"🇪🇸西班牙\",\n" +
						"      \"code\": \"es\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"19\",\n" +
						"      \"name\": \"🇸🇦阿拉伯\",\n" +
						"      \"code\": \"ar\"\n" +
						"    },\n" +
						"    {\n" +
						"      \"id\": \"20\",\n" +
						"      \"name\": \"混合桶\",\n" +
						"      \"code\": \"en\"\n" +
						"    }\n" +
						"  ]\n" +
						"}";
	}

}
