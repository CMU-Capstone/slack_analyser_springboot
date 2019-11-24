package com.tbc.slackanalyser.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.mongodb.util.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class TokenManager {
    final String fileName = "./tokens.txt";

    public static void main(String[] args){
        TokenManager tokenManager = new TokenManager();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hackathonName", "Capstone");
        jsonObject.put("OAuthToken","xoxp-741285264438-739107245509-837279275031-2d7365ec89fbda9e77ccae0e64e6c2e2");
        jsonObject.put("botToken","xoxb-741285264438-808394586211-GNGkyUkSxD5CiJlPAIv46J5z");
        jsonArray.put(jsonObject);
        tokenManager.writeFile(jsonArray);
        JSONArray read = tokenManager.readFile();
        System.out.println("aa");
    }

    public void writeFile(JSONArray tokenList){
        String str = tokenList.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(str);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public JSONArray readFile(){
        File file = new File(fileName);
        BufferedReader br = null;
        String jsonString = "";
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null)
                jsonString+=st;
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray result;
        try{
            result = new JSONArray(jsonString);
        }
        catch (Exception e){
            result = new JSONArray();
        }
        return result;
    }

}
