package com.qhy;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Item {
    public int id;
    public String name;
    public Double height;
}

public class SqlHandler {
    public SqlHandler() {
    }

    // 获取sql语句
    public String createSql() {
        System.out.print("miDB> ");
        Scanner sc = new Scanner(System.in);
        String sql = sc.nextLine();
        if(sql.equals("quit")) {
            System.out.println("退出数据库成功，欢迎你的下次使用");
            System.exit(0); //正常退出系统
        }
        return sql;
    }

    //校检sql语句
    public boolean checkSql(String sql) {
        if(sql.startsWith("insert into")
                || sql.startsWith("drop")
                || sql.startsWith("update")
                || sql.startsWith("create")
                || sql.startsWith("select")
                || sql.equals("help")
                || sql.startsWith("show")
                || sql.startsWith("desc")) {
            return true;
        }
        return false;
    }

    //解析sql语句
    public String parseSql(String sql) throws IOException {
        // 如果是 help
        if(sql.equals("help")) {
            return "quit ---- 退出迷你数据库管理系统\n" +
                    "help ---- 显示所有命令\n" +
                    "show tables ---- 显示目前所有数据表\n" +
                    "desc table XXX ---- 显示数据表XXX中的表结构\n" +
                    "create table XXX(columnA varchar(10), columnB int, columnC decimal ---- 创建一个3列的名称为XXX的表格，列名称分别为columnA, columnB, columnC, 其类型分别为10个以内的字符、整型数和小数\n" +
                    "drop table XXX ---- 删除表格XXX\n" +
                    "select colX, colY, colX from XXX where colZ > 1.5 order by colZ desc ---- 从数据表XXX中选取三列，colX, colY, colX, 每一个记录必须满足colZ的值大于1.5且显示时按照colZ这一列的降序排列\n" +
                    "select * from XXX where colA <> '北林信息' ---- 从数据表选取所有列，但记录要满足列colA不是北林信息\n" +
                    "insert into XXX values('北林信息', 15, 25.5) ---- 向数据表XXX中追加一条记录，各个列的值分别为北林信息、15、25.5\n" +
                    "delete from XXX where colB = 10 ---- 把表格XXX中colB列的值是10的记录全部删除\n" +
                    "update XXX set colD = '计算机科学与技术' where colA = '北林信息' ---- 在数据表XXX中，把那些colA是北林信息的记录中的colD列全部改写为计算机科学与技术\n";
        }
        // 展示数据表
        if(sql.equals("show tables")) {
            //表示一个文件路径
            File file = new File("F:\\myDB");
            //用数组把文件夹下的文件存起来
            File[] files = file.listFiles();
            System.out.println("共有 " + files.length + " 个数据表");
            //foreach遍历数组
            for (File file2 : files) {
                //打印文件列表：只读取名称使用getName();
                System.out.print(file2.getName().substring(0, file2.getName().lastIndexOf(".")) + " ");
            }
            return "";
        }
        // 创建数据表
        if(sql.contains("create")) {
            // 匹配文件名
            Pattern pattern0 = Pattern.compile("create table (.+)[(](.+) (.+), (.+) (.+), (.+) (.+)[)]",Pattern.CASE_INSENSITIVE);
            Matcher matcher0 = pattern0.matcher(sql);
            matcher0.find();

            String filename = matcher0.group(1);

            // 匹配小括号
            String regex = "\\(([^}]*)\\)";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sql);

            String rules = null;

            // 匹配数据类型
            while (matcher.find()) {
                rules = matcher.group();
                rules = rules.substring(1, rules.length() - 1);
            }
            File file = new File("F:\\myDB\\" + filename + ".midb");
            // 文件不存在
            if(file.exists()) {
                System.out.println("数据表已存在！");
            } else {
                file.createNewFile();
                System.out.println("创建数据表 "+ filename + " 成功!");
                FileWriter fw = null;
                fw = new FileWriter(file,true);
                fw.write(rules+"\n");
                fw.close();
            }

            return "";
        }
        // 查看数据表结构
        if(sql.contains("desc")) {
            String[] strArr= sql.split(" ");
            // 读取第一行
            // open the file
            FileReader file = new FileReader("F:\\myDB\\" + strArr[1] + ".midb");
            BufferedReader buffer = new BufferedReader(file);
            // read the 1st line
            String line = buffer.readLine();
            System.out.println("列名称, 列类型, 列宽度");

            String[] cols = line.split(", ");
            for (int i = 0; i < cols.length; i++) {
                String item = cols[i];
                String[] part = item.split(" ");
                System.out.print(part[0] + ", ");
                if(part[1].equals("int") || part[1].equals("decimal")) {
                    System.out.println(part[1] + ", " + "1");
                } else {
                    System.out.println(part[1].substring(0, 7) + ", " + part[1].substring(8, 10));
                }
            }
            return "";
        }

        FileHandler fm = new FileHandler();
        Parse service = new Parse();
        String[] use;
        String data;
        String msg;
        String child;
        String content;

        // 增删改查
        // 增
        if(sql.contains("insert")) {
            // 分割sql语句
            use = insertSplit(sql);
            if(use.length == 0) {
                return "";
            }
            // 获得路径和内容
            child = service.parseDataInsertChild(use);
            content = service.parseDataInsertContent(use);
            // 操作文件
            msg = fm.insert(child, content);
            return msg;
        }
        // 删除整个数据表
        else if(sql.contains("drop")) {
            // 数据表名称
            data = deleteAllSplit(sql);
            //获得路径和内容
            child = data + ".midb";
            content = "";
            //操作文件
            msg = fm.deleteAll(child, content);
            return msg;
        }
        // 改
        else if((sql.contains("update")) && sql.contains("where")) {
            use = updateSplit(sql);
            //获得路径和内容（id, name）
            child = service.parseDataUpdateChild(use);
            content = service.parseDataUpdateContent(use);
            //操作文件
            msg = fm.update(child, content);
            return msg;
        }
        // 查所有
        else if((sql.contains("select"))) {
            // 获取数据表名
            String str1 = sql.substring(0, sql.indexOf("from"));
            String table = sql.substring(str1.length()).split(" ")[1];

            if(sql.contains("*")) {
                ArrayList<Item> arr = new ArrayList<Item>();
                FileReader file1 = new FileReader("F:\\myDB\\" + table + ".midb");
                BufferedReader buffer = new BufferedReader(file1);
                // read the 1st line
                String line = buffer.readLine();
                String[] records = line.split(", ");
                for (int i = 0; i < records.length; i++) {
                    System.out.print(records[i].split(" ")[0] + " ");
                }
                System.out.println();

                // 保存在数组当中
                while ((line = buffer.readLine()) != null) {
                    String[] item = line.split(" ");   // 分割得到数组
                    Item tmp = new Item();
                    tmp.id = Integer.parseInt(item[0]);
                    tmp.name = item[1];
                    tmp.height = Double.valueOf(item[2]);
                    arr.add(tmp);
                }

                for (int i = 0; i < arr.size(); i++) {
                    Item tmp = arr.get(i);
                    System.out.print(tmp.id + " ");
                    System.out.print(tmp.name + " ");
                    System.out.print(tmp.height + "\n");
                }
            } else {
                // 获取列名
                String cols = splitData(sql, "select", "from").replaceAll(" ", "");
                String[] colsName = cols.split(",");

                for (int i = 0; i < colsName.length; i++) {
                    System.out.print(colsName[i] + " ");
                }
                System.out.println();

                ArrayList<Item> array = new ArrayList<Item>();
                FileReader file1 = new FileReader("F:\\myDB\\" + table + ".midb");
                BufferedReader buffer = new BufferedReader(file1);
                // read the 1st line
                String line = buffer.readLine();

                // 保存在数组当中
                while ((line = buffer.readLine()) != null) {
                    String[] item = line.split(" ");   // 分割得到数组
                    Item tmp = new Item();
                    tmp.id = Integer.parseInt(item[0]);
                    tmp.name = item[1];
                    tmp.height = Double.valueOf(item[2]);
                    array.add(tmp);
                }

                for (int i = 0; i < array.size(); i++) {
                    Item tmp = array.get(i);
                    for (int j = 0; j < colsName.length; j++) {
                        if(colsName[j].equals("id")) {
                            System.out.print(tmp.id + " ");
                        } else if(colsName[j].equals("name")) {
                            System.out.print(tmp.name + " ");
                        } else if(colsName[j].equals("height")) {
                            System.out.print(tmp.height + " ");
                        }
                    }
                    System.out.println();
                }
            }

            // 有筛选条件
            if(sql.contains("where")) {

            } else {

            }

            return "";
        }
        // 都不匹配
        return "无法识别";
    }

    // 插入分割
    public String[] insertSplit(String sql) {
        // 去除values()中的空格
        String str1 = sql.substring(0, sql.indexOf("value"));
        String str2 = sql.substring(str1.length()).replaceAll(" ","");
        sql = str1 + str2;

        String[] use = new String[4];
        String[] temp1 = sql.split(" ");
        use[0] = temp1[2];  // 数据表名称

        File file = new File("F:\\myDB\\" + use[0] + ".midb");

        // 文件不存在
        if(!file.exists()) {
            System.out.println("该数据表不存在!");
            return new String[0];
        }

        // 只留下括号里面的内容, 转化成数组
        String part = temp1[3].substring(7, temp1[3].length()-1);
        String[] parts = part.split(",");

        FileReader fr = null;
        try {
            fr = new FileReader("F:\\myDB\\" + use[0] + ".midb");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buffer = new BufferedReader(fr);
        // read the 1st line
        String line = null;
        try {
            line = getBuffer(buffer).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 获取数据表类型
        assert line != null;
        String[] cols = line.split(", ");
        String[] types = new String[cols.length];

        for (int i = 0; i < cols.length; i++) {
            String item = cols[i];
            String[] tmp = item.split(" ");
            types[i] = tmp[1];
        }

        // 类型检查
        boolean flag = true;
        for (int i = 0; i < parts.length; i++) {
            if(types[i].equals("int")) {
                if(!isInteger(parts[i])) {
                    flag = false;
                    System.out.println("第 "+ (i+1) +" 个字段格式错误!");
                    break;
                }
            } else if(types[i].equals("decimal")) {
                if(!isNumber(parts[i])) {
                    flag = false;
                    System.out.println("第 "+ (i+1) +" 个字段格式错误!");
                    break;
                }
            } else if(types[i].startsWith("varchar")) {
                if(!parts[i].startsWith("'") || !parts[i].endsWith("'")) {
                    flag = false;
                    System.out.println("第 "+ (i+1) +" 个字段格式错误, 应为单引号包裹的字符串!");
                    break;
                }
            }
        }

        if(!flag) {
            return new String[0];
        }

        String[] temp2 = temp1[3].split("\\(");
        String[] temp3 = temp2[1].split(",");
        use[1] = temp3[0];  // 括号里的第一个值
        String[] temp4 = temp3[1].split("'");
        use[2] = temp4[1];  // 括号里的第二个值
        use[3] = temp3[2].replaceAll("\\)", "");  // 括号里的第三个值
        return use;
    }

    private BufferedReader getBuffer(BufferedReader buffer) {
        return buffer;
    }

    // 删除所有分割
    public String deleteAllSplit(String sql) {
        return sql.split(" ")[2];
    }

    // 修改分割
    public String[] updateSplit(String sql) {
        String[] use = new String[3];
        sql = sql.replaceAll(" set name='|' where id="," ");
        String[] temp = sql.split(" ");
        use[0] = temp[1];
        use[1] = temp[2];
        use[2] = temp[3];
        return use;
    }

    // 查询分割
    public String[] selectSplit(String sql) {
        return new String[0];
    }

    // 查询所有分割   返回数据表名称
    public String selectAllSplit(String sql) {
        return sql.split(" ")[3];
    }

    public static boolean isInteger(String input){
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);
        return mer.find();
    }

    public static boolean isNumber(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }

    public String splitData(String str, String strStart, String strEnd) {
        String tempStr;
        tempStr = str.substring(str.indexOf(strStart) + strStart.length(), str.lastIndexOf(strEnd));
        return tempStr;
    }
}

class Parse {
    public Parse() {
    }

    //得到插入的表名
    public String parseDataInsertChild(String[] data) {
        return data[0] + ".midb";
    }

    //得到插入的表名内容
    public String parseDataInsertContent(String[] data) {
        return data[1] + " " + data[2] + " " + data[3] + "\n";
    }

    //得到删除的表名
    public String parseDataDeleteChild(String[] data) {
        return data[0] + ".midb";
    }

    //得到删除的ID
    public String parseDataDeleteContent(String[] data) {
        return data[1];
    }

    //得到修改的表名
    public String parseDataUpdateChild(String[] data) {
        return data[0] + ".midb";
    }

    //得到修改的id和name
    public String parseDataUpdateContent(String[] data) {
        return data[1] + " " + data[2];
    }

    //得到查询的表名
    public String parseDataSelectChild(String[] data) {
        return data[0] + ".midb";
    }

    //得到查询的ID
    public String parseDataSelectContent(String[] data) {
        return data[1];
    }
}


