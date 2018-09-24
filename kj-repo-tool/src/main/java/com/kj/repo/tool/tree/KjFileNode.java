package com.kj.repo.tool.tree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KjFileNode implements KjNode {

	private final File file;

	public KjFileNode(File file) {
		super();
		this.file = file;
	}

	@Override
	public KjFileNode[] getChilds() {
		if (this.type() == NodeType.LEAF) {
			return new KjFileNode[0];
		}
		List<KjFileNode> nodes = null;
		try {
			File[] files = file.listFiles();
			int len = files.length;
			nodes = new ArrayList<KjFileNode>(len);
			for (int i = 0; i < len; i++) {
				if (files[i].getParentFile().getCanonicalPath().equals(this.file.getCanonicalPath())) {
					nodes.add(new KjFileNode(files[i]));
				}
			}

		} catch (IOException e) {

		}
		return nodes.toArray(new KjFileNode[0]);
	}

	public boolean isZip() {
		return this.file.getName().endsWith(".zip") || this.file.getName().endsWith(".jar");
	}

	@Override
	public NodeType type() {
		return this.file.isFile() || this.file.list().length == 0 ? NodeType.LEAF : NodeType.COMP;
	}

	public File getFile() {
		return file;
	}
}
