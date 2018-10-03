package com.kj.repo.util.compiler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class KjJavaCompiler {

    private static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private static StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    private static URLClassLoader classLoader;
    private static File out;

    static {
        try {
            out = new File(System.getProperty("user.home") + File.separator + "output");
            if (!out.exists()) {
                out.mkdirs();
            }
            classLoader = new URLClassLoader(new URL[]{out.toURI().toURL()});
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Class<?> compile(String name, String body) throws ClassNotFoundException, IOException {
        try {
            JavaFileObject obj = new SimpleJavaFileObject(
                    URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE) {
                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                    return body;
                }
            };

            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(out));
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null,
                    Arrays.asList(obj));
            if (task.call()) {
                return classLoader.loadClass(name);
            }
            return null;
        } finally {
            fileManager.close();
        }

    }

}
