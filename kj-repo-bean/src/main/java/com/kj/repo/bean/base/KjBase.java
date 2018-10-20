package com.kj.repo.bean.base;

import com.kj.repo.base.func.KjAction;
import com.kj.repo.util.base.KjExit;

public class KjBase {

    public KjBase(KjAction action) {
        KjExit.exit(action.after(() -> System.out.println("close " + this.getClass().getName())));
    }

}
