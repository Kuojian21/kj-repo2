package com.kj.repo.btrace;

import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.ProbeClassName;
import com.sun.btrace.annotations.ProbeMethodName;
import com.sun.btrace.annotations.Sampled;
import com.sun.btrace.annotations.Self;
import com.sun.btrace.BTraceUtils;

@BTrace
public class KjBtrace {

	@OnMethod(clazz = "java.lang.Thread", location = @Location(value = Kind.LINE, line = -1))
	@Sampled(kind = Sampled.Sampler.Adaptive)
	public static void method(@Self Object o, @ProbeClassName String probeClass, @ProbeMethodName String probeMethod) {

	}

	public static void timer() {

	}

}
