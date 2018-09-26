package com.kj.repo.util.js;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class KjJavaScript {

    public static ScriptEngineManager manager = new ScriptEngineManager();

    public static ScriptEngine engine(String js) throws ScriptException {
        ScriptEngine engine = manager.getEngineByName("javascript");
        engine.eval(js);
        return engine;
    }
    
}
