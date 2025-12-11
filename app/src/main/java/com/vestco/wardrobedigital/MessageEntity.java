package com.vestco.wardrobedigital;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class MessageEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "type")
    private int type; // 1 = user, 2 = AI

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public MessageEntity(String content, int type, long timestamp) {
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
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
}