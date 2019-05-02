package com.kj.repo.curator.cache;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.ZKPaths;

/**
 * @author kj
 */
public class KjCache {

	public static CuratorFramework curatorFramework(String connectString, RetryPolicy retryPolicy) {
		return CuratorFrameworkFactory.newClient(connectString, retryPolicy);
	}

	public static void set(CuratorFramework curatorFramework, String path) {
		curatorFramework.setData().forPath(path);
	}
	
	public static TreeCache treeCache(CuratorFramework curatorFramework, String path) throws Exception {
		TreeCache cache = new TreeCache(curatorFramework, path);
		
		cache.getListenable().addListener(new TreeCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				switch (event.getType()) {
				case NODE_ADDED:
					System.out.println("Node add " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					break;
				case NODE_REMOVED:
					System.out.println("Node removed " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					break;
				case NODE_UPDATED:
					System.out.println("Node updated " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					break;
				default:
					break;
				}
			}
		});
		return cache.start();
	}

	public static NodeCache nodeCache(CuratorFramework curatorFramework, String path) throws Exception {
		NodeCache cache = new NodeCache(curatorFramework, path);
		cache.getListenable().addListener(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {

			}
		});
		cache.start();
		return cache;
	}

}
