package model;

import java.time.OffsetDateTime;
import java.util.List;

public class Post {

    private String id;
    private String userId;
    private String imagePath; 
    private String base64Image;
    private String text;
    private List<String> commentIds;
    private OffsetDateTime creationDate;
    private boolean logicallyDeleted;


    public String getBase64Image() { 
        return base64Image;
    }

    public void setBase64Image(String base64Image) { 
        this.base64Image = base64Image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(List<String> commentIds) {
        this.commentIds = commentIds;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isLogicallyDeleted() {
        return logicallyDeleted;
    }

    public void setLogicallyDeleted(boolean logicallyDeleted) {
        this.logicallyDeleted = logicallyDeleted;
    }
}
