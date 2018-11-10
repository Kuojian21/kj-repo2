package com.kj.repo.log.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLog {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(Slf4jLog.class);
		logger.info("msg");
	}

}
