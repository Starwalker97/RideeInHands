package com.example.rideeinhands.models;

import com.google.firebase.Timestamp;

public class Message {
    String Type;
    String Text;

    public Message() {
    }

    public Message( String type, String text) {
        Type = type;
        Text = text;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
