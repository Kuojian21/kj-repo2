package com.kj.repo.demo.js;

import java.io.File;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.kj.repo.util.engine.KjScriptEngine;

public class JavaScriptDemo {

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        demo();
    }


    /**
     * @return
     */
    public static void demo() throws ScriptException, NoSuchMethodException {
        String script =
                "var demo={" +
                        "   run:function(){" +
                        "       return 'run method : return:\"abc'+this.next('test')+'\"';" +
                        "   }," +
                        "   next:function(str){" +
                        "       return ' 我来至next function '+str+')'" +
                        "   }" +
                        "}";
        ScriptEngine engine = KjScriptEngine.jsEngine();
        engine.eval(script);
        System.out.println(((Invocable) engine).invokeMethod(engine.get("demo"), "run"));

        engine.eval("var demo={array:['test',true,1,1.0,2.11111]}");
        Object obj = engine.eval("demo.array");
        System.out.println(obj);
        System.out.println(engine.eval("a=1+2+3+(2*2)"));
        script = "function say(name){ return 'hello,'+name; }";
        engine.eval(script);
        System.out.println(((Invocable) engine).invokeFunction("say", "test"));

        File file = new File("/Users/kuojian21/kj");
        engine.put("file", file);
        System.out.println(engine.eval("'path:'+file.getAbsoluteFile()"));

    }

}