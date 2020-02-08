package models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import models.Automobil;

import java.io.*;

import java.nio.file.Files;
import java.util.ArrayList;

public class Data {
    public static boolean writeToJSON(ArrayList<Automobil> list,String path){
        try {
            Writer writer=new FileWriter(path);
            Gson gson = new Gson();
            gson.toJson(list,writer);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Automobil> readFromJson(String path){
        if(!new File(path).exists()){
            return new ArrayList<>();
        }
        try {
            JsonReader reader=new JsonReader(new FileReader(path));
            Gson gson = new Gson();
            return gson.fromJson(reader,new TypeToken<ArrayList<Automobil>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
