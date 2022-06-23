package com.qhy;

import java.io.*;

public class FileHandler {
    private String parent = "F:\\myDB";
    private String child;
    private static String newString = "";
    private static String delete = "";
    private static String update = "";

    public FileHandler() {
    }

    //增
    public String insert(String child,String content) throws IOException {
        String[] data = content.split(" ");
        String id = data[0];

        //利用查的方法判断是否id重复
        if(select(child,id).equals("没有找到要查询的表，查询失败") || select(child,id).equals("没有要查询的ID")) {
            File file = new File(parent, child);
            FileWriter fw = new FileWriter(file,true);
            fw.write(content);
            fw.close();
            return "增加成功";
        } else {
            return "id重复，增加失败";
        }
    }

    //删除所有
    public String deleteAll(String child,String content) throws IOException {
        File file = new File(parent, child);
        if(!file.exists()) {
            return "没有找到要删除的表，删除失败";
        } else {
            if(file.delete()){
                return file.getName() + " 已被删除！";
            } else {
                return "文件删除失败！";
            }
        }
    }

    //改
    public String update(String child,String content) throws IOException {
        String s = "";
        update = "修改失败";
        String[] data = content.split(" ");
        String id = data[1];
        String name = data[0];

        File file = new File(parent, child);
        if(!file.exists()) {
            return "没有找到要修改的表，删除修改";
        } else {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                s = br.readLine();
                if(s == null){
                    break;
                } else if(s.startsWith(id)) {
                    update = "修改成功";
                    String[] temp = s.split(" ");
                    s = id + " " + name + " " + temp[2];
                    System.out.println(s);
                }
                newString += s + "\n";
            }
            deleteAll(child,"");
            insert(child, newString);
            newString ="";
            return update;
        }
    }

    //查一条数据
    public String select(String child,String id) throws IOException {
        String s = "";
        File file = new File(parent,child);
        if(!file.exists()) {
            return "没有找到要查询的表，查询失败";
        } else {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                s = br.readLine();
                if(s == null){
                    return "没有要查询的ID";
                } else if(s.startsWith(id)) {
                    return s;
                }
            }
        }
    }

    //查所有
    public String selectAll(String child) throws IOException {
        String s = "";
        File file = new File(parent, child);
        if(!file.exists()) {
            return "没有找到要查询的表，查询失败";
        } else {
            // open the file
            FileReader file1 = new FileReader("F:\\myDB\\" + child);
            BufferedReader buffer = new BufferedReader(file1);
            // read the 1st line
            String line = buffer.readLine();

            String[] arr = line.split(", ");

            for (int i = 0; i < arr.length; i++) {
                System.out.print(arr[i].split(" ")[0] + " ");
            }
            System.out.print("\n");

            int i = 0;
            while ((line = buffer.readLine()) != null) {
                System.out.println(line);
                i++;
            }
            System.out.println("共 " + i + " 条记录");

            return "";
        }
    }
}
