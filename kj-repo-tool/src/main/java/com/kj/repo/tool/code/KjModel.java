package com.kj.repo.tool.code;

import java.util.List;

import com.google.common.collect.Lists;

public class KjModel {
    private String name;
    private List<KjCell> cells = Lists.newArrayList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<KjCell> getCells() {
        return cells;
    }

    public void addCell(KjCell cell) {
        this.cells.add(cell);
    }

    public void setCells(List<KjCell> cells) {
        this.cells = cells;
    }


}
