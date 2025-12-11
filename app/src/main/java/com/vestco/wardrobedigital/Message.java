package com.vestco.wardrobedigital;

public class Message {
    public static final int TYPE_USER = 1;
    public static final int TYPE_AI = 2;

    private int id;
    private String content;
    private int type; // 1 = user, 2 = AI
    private long timestamp;

    public Message(int id, String content, int type, long timestamp) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Message(String content, int type, long timestamp) {
        this(0, content, type, timestamp);
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isUser() {
        return type == TYPE_USER;
    }
}