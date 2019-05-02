package com.kj.infra.tool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class TLArgsParse {

	public static CommandLine parse(Options options, String[] args) throws ParseException {
		return new DefaultParser().parse(options, args);
	}

}
