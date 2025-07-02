package dto;


import java.util.List;

public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String role;
    private String profilePicturePath;

    private List<String> friendListIds;
    private List<String> postIds;
    private List<String> imageIds;
    private List<String> friendRequestsSent;
    private List<String> friendRequestsReceived;

    private boolean privateAccount;
    private boolean logicallyDeleted;
    private boolean blocked;

    public UserDTO() {}

    public UserDTO(String id, String username, String email, String firstName, String lastName, String dateOfBirth,
                   String gender, String role, String profilePicturePath, List<String> friendListIds,
                   List<String> postIds, List<String> imageIds, List<String> friendRequestsSent,
                   List<String> friendRequestsReceived, boolean privateAccount, boolean logicallyDeleted, boolean blocked) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.role = role;
        this.profilePicturePath = profilePicturePath;
        this.friendListIds = friendListIds;
        this.postIds = postIds;
        this.imageIds = imageIds;
        this.friendRequestsSent = friendRequestsSent;
        this.friendRequestsReceived = friendRequestsReceived;
        this.privateAccount = privateAccount;
        this.logicallyDeleted = logicallyDeleted;
        this.blocked = blocked;
    }

    // Getteri i setteri za sve atribute

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<String> getFriendListIds() {
        return friendListIds;
    }

    public void setFriendListIds(List<String> friendListIds) {
        this.friendListIds = friendListIds;
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
}
