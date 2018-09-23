package com.kj.repo.util.retry;

import java.util.function.Function;

import com.google.common.base.Predicate;

public class KjRetry {

    public static <T, R, X extends Throwable> R retry(Function<T, R> function, Predicate<X> predicate,
                                                      T input, int times) throws X {
        X x = null;
        for (int i = 0; i < times; i++) {
            try {
                return function.apply(input);
            } catch (Throwable ex) {
                x = (X) ex;
                if (!predicate.apply(x)) {
                    throw x;
                }
            }
        }
        throw x;
    }

    public static <T, R, X extends Throwable> R retry(Function<T, R> function, Predicate<X> predicate,
                                                      T input, int times, int sleep) throws X {
        return retry(function, p -> {
            try {
                Thread.sleep(sleep);
                return true;
            } catch (InterruptedException e) {
                return false;
            }
        }, input, times);
    }

}
