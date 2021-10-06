package PongServer;

import sample.PropertyTest;

import java.io.*;
import java.util.Properties;

class SaveData {
    String Dirpath = "SaveData";
    String Filepath = "\\SaveDataP1.properties";

    SaveData(){
        File file = new File(Dirpath + Filepath);
        if(!file.exists()) {
            CreateFile();
            this.SetKeyValue("Total", "0");
            this.SetKeyValue("Player1", "0");
            this.SetKeyValue("Player2", "0");
            this.SetKeyValue("WiningPercentage", "0 / 0");
            //System.out.println(this.GetKeyValue("Player1"));
        }

    }

    /** 检测文件和目录是否存在，若不存在则create
     * @return true/false
     * */
    private boolean CreateFile(){
        //若目录不存在则创建
        File dir = new File(Dirpath);
        if(!dir.exists() && !dir.isDirectory()){
            System.out.println("目录不存在，即将创建");
            dir.mkdir();
        }

        //若SaveData文件不存在则创建
        File savedata = new File(Dirpath + Filepath);
        if(!savedata.exists()){
            System.out.println("文件不存在，即将创建");
            try {
                savedata.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /** 读取属性文件中相应键的值
     * @param key 键名
     * @return String 键值
     * */
    String GetKeyValue(String key){
        //CreateFile();

        Properties props = new Properties();
        //Create InputStream
        try {
            FileInputStream fins = new FileInputStream(Dirpath + Filepath);
            props.load(fins);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return props.getProperty(key);
    }

    /** 新properties文件的键值对 如果该主键已经存在，更新该主键的值； 如果该主键不存在，则插件一对键值。
     * @param key 键名
     * @param value 键值
     * */
    void SetKeyValue(String key, String value){
        //CreateFile();
        File file = new File(Dirpath + Filepath);
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(file);
            props.load(fis);

            FileOutputStream fos = new FileOutputStream(file);
            props.setProperty(key, value);

            props.store(fos, "The Lastest Update : " + key);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
