package model;

import java.time.OffsetDateTime; // Keep OffsetDateTime as discussed
import java.util.List;

public class Post {

    private String id;
    private String userId;
    private String imagePath; // This will store the web-accessible path like /files/images/xyz.jpg
    private String base64Image; // <--- NEW FIELD: To temporarily hold the Base64 encoded image data
    private String text;
    private List<String> commentIds;
    private OffsetDateTime creationDate;
    private boolean logicallyDeleted;

    // Getters and Setters for existing fields...

    public String getBase64Image() { // <--- NEW GETTER
        return base64Image;
    }

    public void setBase64Image(String base64Image) { // <--- NEW SETTER
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
