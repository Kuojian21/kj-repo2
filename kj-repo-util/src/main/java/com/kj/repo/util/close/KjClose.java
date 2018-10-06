package com.kj.repo.util.close;

import java.io.Closeable;
import java.lang.reflect.Method;

import com.kj.repo.base.close.KjCloseable;

public class KjClose {

    public static boolean close(Object... objs) {
        return close("close", objs);
    }

    public static boolean close(String method, Object... objs) {
        try {
            for (Object obj : objs) {
                if (obj != null) {
                    if (obj instanceof Closeable) {
                        ((Closeable) obj).close();
                    } else if (obj instanceof KjCloseable) {
                        ((KjCloseable) obj).close();
                    } else {
                        Method m = obj.getClass().getMethod(method);
                        if (method != null) {
                            m.invoke(obj);
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
