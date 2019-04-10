package in.dsij.pas.fcm;

/**
 * Created by Vikas on 10/27/2017.
 */

public class CloudMessage {

 /*
 * Sender : "57677563972"
Type : 1
Time : "12/1/2018 4:06:56 PM"
NotificationTitle : "Stock Market Game Chat"
NotificationMessage : "with resp"
commentId : "2"
name : "Dalip Mehta"
 * */

    private long commentId;
    private String name;
    private String NotificationTitle;
    private int Type;
    private String NotificationMessage;
    private String Time;
    private String comment;
    private String createdDate;
    private String image;
    private String pageURL;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getpageURL() {
        return pageURL;
    }

    public void setpageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getComment() {
        return comment;
    }

    public CloudMessage setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public int getType() {
        return Type;
    }

    public CloudMessage setType(int type) {
        Type = type;
        return this;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotificationTitle() {
        return NotificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        NotificationTitle = notificationTitle;
    }

    public String getNotificationMessage() {
        return NotificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        NotificationMessage = notificationMessage;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
