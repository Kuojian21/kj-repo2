package com.kj.repo.test.contended;

import lombok.Data;

@Data
public class NoContended {
	private volatile long a = 1L;
	private volatile long b = 2L;
}
