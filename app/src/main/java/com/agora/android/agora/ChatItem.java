package com.agora.android.agora;

public class ChatItem {

    private String mId;
    private String mName;
    private String mLastMessage;

    public ChatItem() {
    }

    public ChatItem(String id, String name, String lastMessage) {
        mId = id;
        mName = name;
        mLastMessage = lastMessage;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessage(String lastMessage) {
        mLastMessage = lastMessage;
    }
}