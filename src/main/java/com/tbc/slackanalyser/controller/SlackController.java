package com.tbc.slackanalyser.controller;


import com.tbc.slackanalyser.pojo.AddTokenModel;
import com.tbc.slackanalyser.pojo.MongoDBModel;
import com.tbc.slackanalyser.pojo.SlackModel;
import com.tbc.slackanalyser.pojo.TokenManager;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("slack_analyser/api")
public class SlackController {

    @CrossOrigin(origins = "*")
    @GetMapping("test")
    public String test(){
        return "Hello World";
    }

    @CrossOrigin(origins = "*")
    @GetMapping("update")
    public ResponseEntity<HttpStatus> add() {
        SlackModel slackModel = new SlackModel();
        MongoDBModel mongoDBModel = new MongoDBModel();
        TokenManager tokenManager = new TokenManager();
        JSONArray tokenList = tokenManager.readFile();
        for(int i = 0; i < tokenList.length(); i++){
            JSONObject jsonObject = tokenList.getJSONObject(i);
            String hackathonName = jsonObject.get("hackathonName").toString();
            String OAuthToken = jsonObject.get("OAuthToken").toString();
            String botToken = jsonObject.get("botToken").toString();
            JSONArray userList = slackModel.getAllUserInfo(OAuthToken);
            Map<String, String> userMap = new HashMap<>();
            for(int j = 0; j < userList.length(); j++){
                userMap.put(userList.getJSONObject(j).get("id").toString(), userList.getJSONObject(j).get("email").toString());
            }
            String newestTimeStamp = mongoDBModel.getNewestTimeStamp("Messages",hackathonName);
            List<String> channelList = slackModel.getChannelNames(botToken);
            for(String channelName : channelList){
                JSONArray messageList = slackModel.filterRawMessage(channelName, newestTimeStamp, OAuthToken, botToken, hackathonName);
                for(int j = 0; j < messageList.length(); j++){
                    messageList.getJSONObject(j).put("userEmail", userMap.getOrDefault(messageList.getJSONObject(j).get("userID").toString(),""));
                }
                if(messageList.length() > 0){
                    mongoDBModel.doWrite( messageList,"Messages");
                }

            }
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("search")
    public List<Document> searchMessage(@RequestParam Map<String,String> allParam){
        MongoDBModel mongoDBModel = new MongoDBModel();
        return mongoDBModel.search(allParam);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("getAllTokens")
    public String getAllTokens(){
        TokenManager tokenManager = new TokenManager();
        return tokenManager.readFile().toString();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("updateToken")
    public ResponseEntity<HttpStatus> updateToken (@RequestBody AddTokenModel addTokenBody) {
        TokenManager tokenManager = new TokenManager();
        String hackathonName = addTokenBody.getHackathonName();
        String OAuthToken = addTokenBody.getoAuthToken();
        String botToken = addTokenBody.getBotToken();
        JSONArray jsonArray = tokenManager.readFile();
        for(int i = 0; i < jsonArray.length(); i++){
            if(jsonArray.getJSONObject(i).get("hackathonName").toString().equalsIgnoreCase(hackathonName)){
                jsonArray.remove(i);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hackathonName", hackathonName);
        jsonObject.put("OAuthToken",OAuthToken);
        jsonObject.put("botToken",botToken);
        jsonArray.put(jsonObject);
        tokenManager.writeFile(jsonArray);
        return ResponseEntity.ok(HttpStatus.OK);
    }


}
