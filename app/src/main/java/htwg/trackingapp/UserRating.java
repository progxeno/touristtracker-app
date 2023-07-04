package htwg.trackingapp;

import java.util.Date;

/**
 * Created by Alex on 25.06.14.
 */

/**
 * @description     This class handles the UserRating objects and provides getter & setter
 */
public class UserRating {
    private int id;
    private String title;
    private String text;
    private float stars;
    private long timeStamp;
    private boolean isSentToServer;

    public UserRating(){}

    public UserRating(String title, String text, float stars) {
        super();
        Date date = new Date();
        this.timeStamp = date.getTime();
        this.title = title;
        this.text = text;
        this.stars = stars;
        this.isSentToServer = false;

    }

    //getters & setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getStars() {
        return stars;
    }

    public long getTimeStamp() {return timeStamp;}

    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp;}

    public void setStars(float stars) {
        this.stars = stars;
    }

    public boolean isSentToServer() {
        return isSentToServer;
    }

    public void setSentToServer(boolean sentToServer) {
        this.isSentToServer = sentToServer;
    }

    @Override
    public String toString() {
        return "UserRating{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", stars=" + stars +
                ", timeStamp=" + timeStamp +
                ", sentToServer=" + isSentToServer +
                '}';
    }
}
