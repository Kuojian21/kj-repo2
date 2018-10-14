package com.kj.repo.crawler;

import cn.wanghaomiao.seimi.core.Seimi;

public class Boot {
    public static void main(String[] args) {
        Seimi s = new Seimi();
        s.start("basic");
    }
}