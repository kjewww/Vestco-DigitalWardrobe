package com.example.wardrobedigital;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    List<MessageEntity> getAllMessages();

    @Insert
    long insertMessage(MessageEntity message);

    @Query("DELETE FROM messages")
    void clearAllMessages();
}