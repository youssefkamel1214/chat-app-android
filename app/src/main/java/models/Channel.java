package models;

import java.util.ArrayList;
import java.util.Calendar;

public class Channel implements Comparable<Channel> {
  private   String Id;
  private   String Name;
  private   String Imageurl;
  private   String Adminid;
  private   String LastMessage;
  private   String  messegetybe;
  private Calendar LastTime;
  private ArrayList<String>Users;
  private ArrayList<String>UsersNames;
  private ArrayList<String>Imagesurls;
  public Channel(String id, String lastMessege, String messegetybe,
                 Calendar lastTime, ArrayList<String> users, ArrayList<String> usersNames, ArrayList<String> imagesurls) {
    Id = id;
    LastMessage = lastMessege;
    LastTime = lastTime;
    Users = users;
    this.messegetybe=messegetybe;
    UsersNames = usersNames;
    Imagesurls = imagesurls;
  }

  public String getAdminid() {
    return Adminid;
  }

  public void setAdminid(String adminid) {
    Adminid = adminid;
  }

  public String getMessegetybe() {
    return messegetybe;
  }

  public void setMessegetybe(String messegetybe) {
    this.messegetybe = messegetybe;
  }

  public void setName(String name) {
    if(Name==null)
      return;
    Name = name;
  }

  public void setImageurl(String imageurl) {
    if(Imageurl==null)
      return;
    Imageurl = imageurl;
  }

  public String getImageurl(String Userid) {
    if(Imageurl==null)
      return Imagesurls.get(1-Users.indexOf(Userid));
    return Imageurl;
  }

  public String getName(String Userid) {
    if(Name==null)
      return UsersNames.get(1-Users.indexOf(Userid));
    return Name;
  }

  public String getLastMessage() {
    return LastMessage;
  }

  public void setLastMessage(String lastMessage) {
    LastMessage = lastMessage;
  }

  public Calendar getLastTime() {
    return LastTime;
  }

  public void setLastTime(Calendar lastTime) {
    LastTime = lastTime;
  }


  public String getId() {
    return Id;
  }

  @Override
  public int compareTo(Channel other) {
    if (this.LastTime.after(other.LastTime))
      return  1;
    else if(this.LastTime.before(other.LastTime))
      return  -1;
    else
      return  0;
  }
}
