//package com.kj.repo.test.bean.aio;
//
//import com.kj.repo.bean.aio.KjAioServer;
//
//public class AioServerTest {
//
//    public static void main(String[] args) throws InterruptedException {
//        KjAioServer.bind(8888, new ServerAction());
//        Thread.sleep(10000000);
//    }
//}
//
//class ServerAction implements KjAioServer.Action {
//    byte[] bytes;
//    int t;
//
//    @Override
//    public void response(KjAioServer.Holder holder, byte[] data) {
//        if (data.length % 4 != 0) {
//            bytes = new byte[data.length % 4];
//            for (int i = (data.length / 4 * 4), j = 0, len = data.length; i < len; i++, j++) {
//                bytes[j] = data[i];
//            }
//        }
//
//        if (data.length >= 4) {
//            int y = t;
//            for (int i = 0, len = (data.length / 4 * 4) - 1; i <= len; i += 4) {
//                int x = (data[i] & 0xff) << 24 | (data[i + 1] & 0xff) << 16 | (data[i + 2] & 0xff) << 8
//                        | (data[i + 3] & 0xff) << 0;
//                if (x != y + 1) {
//                    System.out.println(x);
//                }
//                y = x;
//                System.out.println(x);
//            }
//            t = y;
//        }
//
//    }
//}