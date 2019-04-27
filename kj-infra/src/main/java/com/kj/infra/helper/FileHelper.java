package com.kj.infra.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileHelper {

	public static byte[] readBytes(String path, String... file) throws IOException {
		return Files.readAllBytes(Paths.get(path, file));
	}

	public static List<String> readLines(String path, String... file) throws IOException {
		return Files.readAllLines(Paths.get(path, file));
	}

}
