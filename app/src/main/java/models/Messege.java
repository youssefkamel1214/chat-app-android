package models;

import java.util.Calendar;

public class Messege {
    private String type,id,content_txt,senderid,channelid,content_url,username;
    Calendar timesend;
    private byte[] imagesbytes;

    public void setContent_txt(String content_txt) {
        this.content_txt = content_txt;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public void setImagesbytes(byte[] imagesbytes) {
        this.imagesbytes = imagesbytes;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getContent_txt() {
        return content_txt;
    }

    public String getSenderid() {
        return senderid;
    }

    public String getChannelid() {
        return channelid;
    }

    public String getContent_url() {
        return content_url;
    }

    public Calendar getTimesend() {
        return timesend;
    }

    public byte[] getImagesbytes() {
        return imagesbytes;
    }

    public String getUsername() {
        return username;
    }

    public Messege(String type, String id, String senderid, String channelid, Calendar timesend, String username) {
        this.type = type;
        this.id = id;
        this.senderid = senderid;
        this.channelid = channelid;
        this.timesend = timesend;
        this.username=username;
    }


}
