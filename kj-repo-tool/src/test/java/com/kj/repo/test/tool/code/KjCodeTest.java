package com.kj.repo.test.tool.code;

import java.sql.SQLException;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.kj.repo.tool.code.KjCode;
import com.mysql.cj.jdbc.Driver;

public class KjCodeTest {

    public static void main(String[] args) throws SQLException {
        if (args.length != 4) {
            return;
        }
        
        /*
         * -DsocksProxyHost= -DsocksProxyPort=8088
         */
		System.setProperty("socksProxyHost", "127.0.0.1");
		System.setProperty("socksProxyPort", "8088");
        
        KjCode.mysql(new SimpleDriverDataSource(new Driver(), args[0], args[1], args[2]), args[3]);

    }

}
