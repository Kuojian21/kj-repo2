package com.kj.repo.log.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kj.repo.log.slf4j.Slf4jLog;

public class CommonLog {
	public static void main(String[] args) throws ClassNotFoundException {
		Log log = LogFactory.getLog(CommonLog.class);
		log.info("msg");
		Logger logger = LoggerFactory.getLogger(Slf4jLog.class);
		logger.info("msg");
	}
}
