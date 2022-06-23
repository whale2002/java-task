package com.qhy;

import java.io.*;

public class myDB {
    public static void main(String[] args) {
        System.out.println("欢迎使用迷你数据库系统V1.0");
        System.out.println("请使用help命令查询所有操作介绍");
        System.out.println("请输入sql语句, 如想退出请输入quit");

        while(true) {
            // 实例化
            SqlHandler sqlHandler = new SqlHandler();
            // 输入 sql 语句
            String sql = sqlHandler.createSql();

            // 转化成小写
            sql = sql.toLowerCase();
            // 对 sql 语句进行检查
            if(sqlHandler.checkSql(sql)) {
                // 解析接收的sql语句
                String msg = null;
                try {
                    msg = sqlHandler.parseSql(sql);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 输出结果
                System.out.println(msg);
            } else {
                System.out.println("sql语句错误，请重新输入！");
            }
        }
    }
}
