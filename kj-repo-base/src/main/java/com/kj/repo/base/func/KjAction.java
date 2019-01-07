package com.kj.repo.base.func;

import java.util.Objects;

/**
 * @author kj
 */
@FunctionalInterface
public interface KjAction {

    void doAction() throws Exception;

    default KjAction before(KjAction before) {
        Objects.requireNonNull(before);
        return () -> {
            before.doAction();
            doAction();
        };
    }

    default KjAction after(KjAction after) {
        Objects.requireNonNull(after);
        return () -> {
            doAction();
            after.doAction();
        };
    }

}
