package com.kj.repo.util.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class KjCommandLine {

	public static CommandLine parse(Options options, String[] args) throws ParseException {
		return new DefaultParser().parse(options, args);
	}

}
