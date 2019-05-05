package com.kj.infra.algorithm;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * 
 * @author kuojian21
 *
 */
public class Algorithm {

	public static <T> List<List<T>> cartesian(List<List<T>> objs) {
		List<List<T>> result = Lists.newArrayList();
		int size = objs.size();
		List<List<T>> tObjs = objs.stream().map(Lists::newArrayList).collect(Collectors.toList());
		while (tObjs.size() > 0) {
			result.add(tObjs.stream().map(x -> x.get(0)).collect(Collectors.toList()));
			tObjs.get(size - 1).remove(0);
			for (int i = size - 1; i >= 0; i--) {
				if (tObjs.get(i).size() > 0) {
					for (int j = i + 1; j < size; j++) {
						tObjs.add(Lists.newArrayList(objs.get(j)));
					}
					break;
				} else {
					if (i == 0) {
						return result;
					}
					tObjs.get(i - 1).remove(0);
					tObjs.remove(i);
				}
			}
		}
		return result;
	}

	/**
	 * @author kuojian21
	 */
	@Data
	public static class Cartesian<T> {
		private final List<List<T>> objs;
		private final int size;
		private final List<List<T>> result;
		private final List<List<T>> tObjs;

		public Cartesian(List<List<T>> objs) {
			this.objs = objs;
			this.size = objs.size();
			this.result = Lists.newArrayList();
			this.tObjs = objs.stream().map(Lists::newArrayList).collect(Collectors.toList());
			this.handle();
		}

		public void handle() {
			List<T> t = Lists.newArrayList();
			for (int i = 0; i < size; i++) {
				t.add(tObjs.get(i).get(0));
			}
			result.add(t);
			for (int i = size - 1; i >= 0; i--) {
				tObjs.get(i).remove(0);
				if (tObjs.get(i).size() > 0) {
					for (int j = i + 1; j < size; j++) {
						tObjs.add(Lists.newArrayList(objs.get(j)));
					}
					this.handle();
					return;
				} else {
					tObjs.remove(i);
				}
			}
		}
	}

}
