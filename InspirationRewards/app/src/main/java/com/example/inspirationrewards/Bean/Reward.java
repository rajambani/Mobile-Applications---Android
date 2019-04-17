package com.example.inspirationrewards.Bean;

import java.io.Serializable;
import java.util.Date;

public class Reward implements Serializable
{
    String username;
    String name;
    String date;
    String notes;
    int value;

    public Reward(String userId, String name, String date, String notes, int value) {
        this.username = userId;
        this.name = name;
        this.date = date;
        this.notes = notes;
        this.value = value;
    }

    public String getUserId() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", notes='" + notes + '\'' +
                ", value=" + value +
                '}';
    }
}
