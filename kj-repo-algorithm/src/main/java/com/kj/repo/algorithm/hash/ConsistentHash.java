package com.kj.repo.algorithm.hash;

import java.util.HashMap;
import java.util.Map;
import com.google.common.base.Strings;

public class ConsistentHash {

	private int total;
	private byte[] slot;
	private Map<Byte, String> map = new HashMap<Byte, String>();

	public ConsistentHash(Map<String, Integer> nodes) {
		for (Integer i : nodes.values()) {
			this.total += i;
		}
		this.total *= 160;
		slot = new byte[total];
		byte count = 1;
		for (Map.Entry<String, Integer> node : nodes.entrySet()) {
			for (int i = 1, len = node.getValue() * 160; i <= len; i++) {
				Integer value = ((node.getKey() + i).hashCode() % this.total + this.total) % this.total;
				slot[value] = count;
			}
			map.put(count++, node.getKey());
		}
		if (slot[0] == 0) {
			slot[0] = 1;
		}
	}

	public String node(String key) {
		if (Strings.isNullOrEmpty(key)) {
			return null;
		}

		for (int i = (key.hashCode() % this.total + this.total) % this.total; i >= 0; i--) {
			if (slot[i] > 0) {
				return map.get(slot[i]);
			}
		}
		return null;
	}

	public static void main(String[] args) {
		Map<String, Integer> nodes = new HashMap<String, Integer>();
		for (int i = 0; i < 10; i++) {
			nodes.put("node" + i, 1);
		}
		ConsistentHash hash = new ConsistentHash(nodes);
		for (int i = 0; i < 10; i++) {
			System.out.println(("node" + i).hashCode());
			System.out.println(i + ":" + hash.node("node" + i));
		}

	}

}
