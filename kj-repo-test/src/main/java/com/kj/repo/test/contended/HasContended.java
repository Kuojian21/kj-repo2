package com.kj.repo.test.contended;

import lombok.Data;
import sun.misc.Contended;

@Data
@SuppressWarnings("restriction")
@Contended
public class HasContended {
	private volatile long a = 1L;
	private volatile long b = 2L;
}	
