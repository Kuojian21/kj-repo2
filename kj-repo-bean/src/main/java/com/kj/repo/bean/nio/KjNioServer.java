package com.kj.repo.bean.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;


public class KjNioServer extends KjNio{

	private ServerSocketChannel server;

	private KjNioServer(int port, Handle handle) throws IOException {
		try {
			server = ServerSocketChannel.open();  
	        server.configureBlocking(false);  
	        server.socket().bind(new InetSocketAddress(port));
	        server.register(selector, SelectionKey.OP_ACCEPT,handle); 
		} finally {
			if (this.server != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							KjNioServer.this.server.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}
	
	private class ServerHandle implements Handle{

		@Override
		public void read(Holder holder) throws Exception {
			
		}

		@Override
		public void write(Holder holder) throws Exception {
			
		}
		
	}

	public static KjNioServer bind(int port, Handle handle) {
		try {
			return new KjNioServer(port, handle);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
