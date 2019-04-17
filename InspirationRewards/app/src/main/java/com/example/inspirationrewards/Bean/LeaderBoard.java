package com.example.inspirationrewards.Bean;

import java.io.Serializable;
import java.util.List;

public class LeaderBoard implements Serializable
{
    List<User> userList;

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public String toString()
    {
        return "LeaderBoard{" +
                "userList=" + userList +
                '}';
    }
}
