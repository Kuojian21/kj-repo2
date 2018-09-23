package com.kj.repo.bean.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class AioClient {

	public void connect() throws IOException{
		final AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();  
		channel.connect(new InetSocketAddress("localhost", 8888), null, new CompletionHandler<Void,Void>(){

			@Override
			public void completed(Void result, Void attachment) {
				write(channel,ByteBuffer.wrap(("1").getBytes()));
				write(channel,ByteBuffer.wrap(("1").getBytes()));
				write(channel,ByteBuffer.wrap(("1").getBytes()));
				write(channel,ByteBuffer.wrap(("1").getBytes()));
			}
				
			@Override
			public void failed(Throwable exc, Void attachment) {
				
			}
			
		});
	}
	
	public void read(final AsynchronousSocketChannel channel){
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		channel.read(buffer, buffer , new CompletionHandler<Integer,ByteBuffer>(){
			@Override
			public void completed(Integer result, ByteBuffer buffer) {
				System.out.println("result:" + result);
				String str = new String(buffer.array());
				System.out.println("str:" + str);
				if(channel.isOpen()){
					write(channel,ByteBuffer.wrap((str + "1").getBytes()));
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				
			}
			
		});
	}
	
	public void write(final AsynchronousSocketChannel channel,ByteBuffer buffer){
		channel.write(buffer, null,  new CompletionHandler<Integer,Void>(){
			@Override
			public void completed(Integer result, Void attachment) {
				System.out.println("result:" + result);
				if(channel.isOpen()){
					read(channel);
				}
			}
			@Override
			public void failed(Throwable exc, Void attachment) {
				
			}
		});
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		new AioClient().connect();
		Thread.sleep(100000);
	}
}
