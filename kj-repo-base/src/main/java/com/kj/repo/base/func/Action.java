package com.kj.repo.base.func;

import java.util.Objects;

@FunctionalInterface
public interface Action {

    void doAction() throws Exception;

    default Action before(Action before) {
        Objects.requireNonNull(before);
        return () -> {
            before.doAction();
            doAction();
        };
    }

    default Action after(Action after) {
        Objects.requireNonNull(after);
        return () -> {
            doAction();
            after.doAction();
        };
    }

}
