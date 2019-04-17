package com.example.inspirationrewards.Bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class User implements Serializable, Comparable<User>
{
    String username;
    String password;
    boolean admin;
    String firstName;
    String lastName;
    String department;
    String position;
    String story;
    String imageBytes;
    int pointsToAward;
    String location;
    List<Reward> rewards;

    public User()
    {

    }

    public User(String userName, String pwd, boolean admin, String fName, String lName,
                String department, String position, String story, String image, int pointsToAward,
                String location, List<Reward> rewardList) {
        this.username = userName;
        this.password = pwd;
        this.admin = admin;
        this.firstName = fName;
        this.lastName = lName;
        this.department = department;
        this.position = position;
        this.story = story;
        this.imageBytes = image;
        this.pointsToAward = pointsToAward;
        this.location = location;
        this.rewards = rewardList;
    }


    public String getUserName() {
        return this.username;
    }

    public String getPwd() {
        return this.password;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public String getfName() {
        return this.firstName;
    }

    public String getlName() {
        return this.lastName;
    }

    public String getDepartment() {
        return this.department;
    }

    public String getPosition() {
        return this.position;
    }

    public String getStory() {
        return this.story;
    }

    public String getImage() {
        return this.imageBytes;
    }

    public int getPointsToAward() {
        return this.pointsToAward;
    }

    public String getLocation() {
        return this.location;
    }

    public List<Reward> getRewardList() {
        return this.rewards;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStory(String story)
    {
        this.story = story;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getTotal(User user)
    {
        int total = 0;
        rewards = user.getRewardList();
        if(rewards != null)
        {
            for (Reward rew : rewards)
                total += rew.getValue();
        }
        else
            total = 0;
        return total;
    }

    @Override
    public int compareTo(@NonNull User o)
    {
        if(this.getRewardList()==null || o.getRewardList()==null)
            return 1;


        return o.getTotal(o) - this.getTotal(this) ;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + username + '\'' +
                ", pwd='" + password + '\'' +
                ", admin=" + admin +
                ", fName='" + firstName + '\'' +
                ", lName='" + lastName + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", story='" + story + '\'' +
                ", image='" + "" + '\'' +
                ", pointsToAward=" + pointsToAward +
                ", location='" + location + '\'' +
                ", rewardList=" + rewards +
                '}';
    }

}
