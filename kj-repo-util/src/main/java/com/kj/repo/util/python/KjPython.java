package com.kj.repo.util.python;

import org.python.util.PythonInterpreter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KjPython {

	public static void exec() {
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("a=[5,2,3,9,4,0]; ");
		interpreter.exec("print(sorted(a));");
	}

	public static void main(String[] args) {
		exec();
	}

}
