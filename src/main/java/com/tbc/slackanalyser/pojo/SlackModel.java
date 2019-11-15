package com.tbc.slackanalyser.pojo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SlackModel {
    final String OAuthToken = "xoxp-741285264438-739107245509-837279275031-2d7365ec89fbda9e77ccae0e64e6c2e2";
    final String botToken = "xoxb-741285264438-808394586211-GNGkyUkSxD5CiJlPAIv46J5z";
    public static void main(String[] args){
        SlackModel slackModel = new SlackModel();
//        slackModel.getChannelList();
//        slackModel.getChannelNames();
//        System.out.println(slackModel.getChannelIDByName("capstone"));
//        slackModel.getChannelMessageHistory("CMR6SBLRJ");
//        slackModel.getMessageTextList("CMR6SBLRJ");
//        slackModel.getMessageContainsWord("meeting","CMR6SBLRJ");
//        slackModel.getAllUserInfo();
//        System.out.println(slackModel.getUserEmail(slackModel.getUserID("Xiangyum")));
//        slackModel.filterRawMessage("CMR6SBLRJ");
    }


    public JSONArray getChannelList(){
        String urlString = "https://slack.com/api/channels.list?token=" + botToken;
        String response = getResponseString(urlString);
//        System.out.println(response);
        JSONObject jsonObject = new JSONObject(response);
        //TODO: find a more graceful way to handle exception, when we can not get channels, might be other reasons
        try{
            JSONArray channelList = jsonObject.getJSONArray("channels");
            return channelList;
        }catch (Exception e){
            System.out.println("Invalid token");
            return null;
        }
    }


    public List<String> getChannelNames(){
        JSONArray channelListJSONArray = getChannelList();
        List<String> result = new ArrayList<String>();
        for(int i = 0; i < channelListJSONArray.length(); i++){
            result.add(channelListJSONArray.getJSONObject(i).get("name").toString());
        }
//        for(int i = 0; i < result.size(); i++){
//            System.out.println(result.get(i));
//        }
        return result;
    }

    //If not found, return null
    public String getChannelIDByName(String channelName){
        JSONArray channelListJSONArray = getChannelList();
        for(int i = 0; i < channelListJSONArray.length(); i++){
            if(channelListJSONArray.getJSONObject(i).get("name").toString().equals(channelName)){
                return channelListJSONArray.getJSONObject(i).get("id").toString();
            }
        }
        return null;
    }

    public JSONArray getChannelMessageHistory(String channelID, String oldest){
        String urlString = "https://slack.com/api/channels.history?token=" + OAuthToken + "&channel=" + channelID+ "&oldest=" + oldest;
        String response = getResponseString(urlString);
//        System.out.println(response);
        JSONObject jsonObject = new JSONObject(response);
        try{
            JSONArray messageList = jsonObject.getJSONArray("messages");
            return messageList;
        }catch (Exception e){
            return null;
        }
    }

    public List<String> getMessageTextList(String channelID, String oldest){
        JSONArray messageJSONArray = getChannelMessageHistory(channelID, oldest);
        if(messageJSONArray == null){
            return null;
        }
        List<String> result = new ArrayList<String>();
        for(int i = 0; i < messageJSONArray.length(); i++){
            JSONObject jsonObject = messageJSONArray.getJSONObject(i);
            result.add(jsonObject.get("text").toString());
        }
//        for(int i = 0; i < result.size(); i++){
//            System.out.println(result.get(i));
//        }
        return result;
    }

    public JSONArray filterRawMessage(String channelName, String oldest){
        String channelID = getChannelIDByName(channelName);
        JSONArray rawMessageList = getChannelMessageHistory(channelID, oldest);
        JSONArray result = new JSONArray();
        for(int i = 0; i < rawMessageList.length(); i++){
            JSONObject messageJSON = rawMessageList.getJSONObject(i);
            if(messageJSON.keySet().contains("client_msg_id") && !messageJSON.get("text").toString().equals("")){
                JSONObject tmp = new JSONObject();
                tmp.put("_id", messageJSON.get("client_msg_id").toString());
                tmp.put("timeStamp", messageJSON.get("ts").toString());
                tmp.put("userID", messageJSON.get("user").toString());
                tmp.put("text", messageJSON.get("text").toString());
                tmp.put("channelName", channelName);
                result.put(tmp);
            }
        }
        return result;
    }

    public String getResponseString(String urlString){
        String response = "";
        try {
//            System.out.println(urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                response += str;
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
//        System.out.println(response);
        return response;
    }

    public List<String> getMessageContainsWord(String keyword, String channelID, String oldest){
        List<String> messageTextList = getMessageTextList(channelID, oldest);
        if(messageTextList == null){
            return null;
        }
        List<String> result = new ArrayList<String>();
        for(String str : messageTextList){
            if (str.toLowerCase().contains(keyword.toLowerCase())){
                result.add(str);
            }
        }
//        for(String str: result){
//            System.out.println(str);
//        }
        return result;
    }

    public JSONArray getAllUserInfo(){
        String urlStr = "https://slack.com/api/users.list?token=" + OAuthToken;
        String response = getResponseString(urlStr);
        JSONObject jsonObject = new JSONObject(response);
        JSONArray result = new JSONArray();
        try{
            JSONArray rawJSONArray = jsonObject.getJSONArray("members");
            for(int i = 0; i < rawJSONArray.length(); i++){
                JSONObject user = rawJSONArray.getJSONObject(i);
                JSONObject tmp = new JSONObject();
                tmp.put("id", user.get("id").toString());
                try{
                    tmp.put("email", user.getJSONObject("profile").get("email"));
                }catch (Exception e){
                    tmp.put("email", "");
                }
                result.put(tmp);
            }
        }catch(Exception e){
            return null;
        }
        return result;
    }

    public String getUserEmail(String userID){
        JSONArray userInfoList = getAllUserInfo();
        for(int i = 0; i < userInfoList.length(); i++){
            if(userInfoList.getJSONObject(i).get("id").toString().equals(userID)){
                return userInfoList.getJSONObject(i).get("email").toString();
            }
        }
        return null;
    }

}
