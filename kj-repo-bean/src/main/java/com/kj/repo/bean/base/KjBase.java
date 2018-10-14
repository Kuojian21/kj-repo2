package com.kj.repo.bean.base;

import com.kj.repo.base.func.Action;
import com.kj.repo.util.base.KjExit;

public class KjBase {

    public KjBase(Action action) {
        KjExit.exit(action.after(() -> System.out.println("close " + this.getClass().getName())));
    }

}
