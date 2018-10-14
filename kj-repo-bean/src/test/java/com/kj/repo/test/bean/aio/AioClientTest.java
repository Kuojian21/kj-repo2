//package com.kj.repo.test.bean.aio;
//
//import java.nio.ByteBuffer;
//import java.util.concurrent.TimeUnit;
//
//import com.kj.repo.bean.aio.KjAioClient;
//
//public class AioClientTest {
//
//    public static void main(String[] args) {
//        KjAioClient client = new KjAioClient("127.0.0.1", 8888);
//        client.request(new KjAioClient.Action() {
//
//            @Override
//            public void request(KjAioClient.Holder holder) throws InterruptedException {
//                for (int i = 0; i < 100000000; i++) {
//                    byte[] data = new byte[4];
//                    data[0] = (byte) ((i >> 24) & 0xff);
//                    data[1] = (byte) ((i >> 16) & 0xff);
//                    data[2] = (byte) ((i >> 8) & 0xff);
//                    data[3] = (byte) ((i >> 0) & 0xff);
//                    System.out.println(i);
//                    holder.semaphore.acquire();
//                    holder.channel.write(ByteBuffer.wrap(data), 10, TimeUnit.MINUTES, holder, client.WRITER);
//                }
//            }
//        });
//    }
//
//}
