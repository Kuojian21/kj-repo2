package com.kj.repo.bean.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.collect.Lists;

public class KjReader {

    public static List<String> readLines(InputStream is) throws IOException {
        List<String> result = Lists.newLinkedList();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = null;
        while ((line = br.readLine()) != null) {
            result.add(line);
        }
        return result;
    }

    public static List<String> readLines(String file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return readLines(is);
        } catch (IOException e) {
            e.printStackTrace();
            return Lists.newLinkedList();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
