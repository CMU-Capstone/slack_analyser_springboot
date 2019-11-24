package com.tbc.slackanalyser.pojo;

public class AddTokenModel {
    String hackathonName;
    String oAuthToken;
    String botToken;

    public String getHackathonName() {
        return hackathonName;
    }

    public String getoAuthToken() {
        return oAuthToken;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setHackathonName(String hackathonName) {
        this.hackathonName = hackathonName;
    }

    public void setoAuthToken(String oAuthToken) {
        this.oAuthToken = oAuthToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
