package com.kj.repo.shyiko.binlog;

import java.io.IOException;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kj
 */
@Slf4j
public class KjBinaryLogClient {

    public static BinaryLogClient client(String host, int port, String username, String password) {
        BinaryLogClient client = new BinaryLogClient(host, port, username, password);
        return client;
    }

    public static void main(String[] args) throws IOException {
        BinaryLogClient client = client(args[0], Integer.parseInt(args[1]), args[2], args[3]);
        Gson gson = new Gson();
        client.registerEventListener(event -> {
            if (event.getData() instanceof QueryEventData) {

            } else if (event.getData() instanceof UpdateRowsEventData) {

            } else if (event.getData() instanceof DeleteRowsEventData) {

            } else if (event.getData() instanceof WriteRowsEventData) {

            } else if (event.getData() instanceof TableMapEventData) {

            }
            log.info("{} {}", event.getData().getClass(), gson.toJson(event));
        });
        client.connect();
    }

}
