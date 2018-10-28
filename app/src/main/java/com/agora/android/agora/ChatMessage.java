package com.agora.android.agora;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Date;

public class ChatMessage {

    private String mMessageText;
    private String mMessageUserID;
    private long messageTime;

    public ChatMessage(String messageText, String messageUserID) {
        mMessageText = messageText;
        mMessageUserID = messageUserID;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return mMessageText;
    }

    public void setMessageText(String messageText) {
        mMessageText = messageText;
    }

    public String getMessageUser() {
        return mMessageUserID;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}