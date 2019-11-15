package com.tbc.slackanalyser.controller;


import com.tbc.slackanalyser.pojo.MongoDBModel;
import com.tbc.slackanalyser.pojo.SlackModel;
import org.bson.Document;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("slack_analyser/api")
public class SlackController {


    @GetMapping("test")
    public String test(){
        return "Hello World";
    }


    @GetMapping("update")
    public ResponseEntity<HttpStatus> add() {
        SlackModel slackModel = new SlackModel();
        MongoDBModel mongoDBModel = new MongoDBModel();
        JSONArray userList = slackModel.getAllUserInfo();
        Map<String, String> userMap = new HashMap<>();
        for(int i = 0; i < userList.length(); i++){
            userMap.put(userList.getJSONObject(i).get("id").toString(), userList.getJSONObject(i).get("email").toString());
        }
        String newestTimeStamp = mongoDBModel.getNewestTimeStamp("Messages");
        List<String> channelList = slackModel.getChannelNames();
        for(String channelName : channelList){
            JSONArray messageList = slackModel.filterRawMessage(channelName, newestTimeStamp);
            for(int i = 0; i < messageList.length(); i++){
                messageList.getJSONObject(i).put("userEmail", userMap.getOrDefault(messageList.getJSONObject(i).get("userID").toString(),""));
            }
            if(messageList.length() > 0){
                mongoDBModel.doWrite( messageList,"Messages");
            }

        }
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @GetMapping("search")
    public List<Document> searchMessage(@RequestParam Map<String,String> allParam){
        MongoDBModel mongoDBModel = new MongoDBModel();
        return mongoDBModel.search(allParam);
    }
}
