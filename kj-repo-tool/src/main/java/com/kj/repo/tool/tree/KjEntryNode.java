/*
package com.kj.repo.tool.tree;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class KjEntryNode implements KjNode {

    private ZipEntry zipEntry;

    public KjEntryNode(ZipEntry entry) {
        this.zipEntry = entry;
    }

    @Override
    public <T extends KjNode> T[] getChilds() {
        ZipFile zipFile = new ZipFile(this.file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
        }
    }

    @Override
    public NodeType type() {
        return null;
    }
}
*/
