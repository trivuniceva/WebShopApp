package model;


import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String username;
    private String password;
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String role;
    private String profilePicturePath;

    private List<String> postIds;
    private List<String> imageIds;
    private List<String> friendRequestsSent;
    private List<String> friendRequestsReceived;
    private List<String> friendListIds;

    private boolean privateAccount;
    private boolean logicallyDeleted;
    private boolean blocked;

    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public List<String> getPostIds() {
        return postIds;
    }

    public void setPostIds(List<String> postIds) {
        this.postIds = postIds;
    }

    public List<String> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<String> imageIds) {
        this.imageIds = imageIds;
    }

    public List<String> getFriendRequestsSent() {
        return friendRequestsSent;
    }

    public void setFriendRequestsSent(List<String> friendRequestsSent) {
        this.friendRequestsSent = friendRequestsSent;
    }

    public List<String> getFriendRequestsReceived() {
        return friendRequestsReceived;
    }

    public void setFriendRequestsReceived(List<String> friendRequestsReceived) {
        this.friendRequestsReceived = friendRequestsReceived;
    }

    public List<String> getFriendListIds() {
    	if (friendListIds == null) {
            friendListIds = new ArrayList<>();
        }
        return friendListIds;
    }

    public void setFriendListIds(List<String> friendListIds) {
        this.friendListIds = friendListIds;
    }

    public boolean isPrivateAccount() {
        return privateAccount;
    }

    public void setPrivateAccount(boolean privateAccount) {
        this.privateAccount = privateAccount;
    }

    public boolean isLogicallyDeleted() {
        return logicallyDeleted;
    }

    public void setLogicallyDeleted(boolean logicallyDeleted) {
        this.logicallyDeleted = logicallyDeleted;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", role='" + role + '\'' +
                ", postIds=" + postIds +
                ", imageIds=" + imageIds +
                ", friendRequestsSent=" + friendRequestsSent +
                ", friendRequestsReceived=" + friendRequestsReceived +
                ", friendListIds=" + friendListIds +
                ", privateAccount=" + privateAccount +
                ", logicallyDeleted=" + logicallyDeleted +
                ", blocked=" + blocked +
                '}';
    }
}
