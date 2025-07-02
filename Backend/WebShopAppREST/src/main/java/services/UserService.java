package services;


import model.User;
import storage.UserFileStorage;


public class UserService {

    private UserFileStorage userFileStorage;

    public void updateUserInfo(String id, User updatedUser) {

        User existingUser = userFileStorage.findById(id);

        if(existingUser == null){
            System.out.println("Korisnik nije pronadjen");
            return;
        }

        updatedUser.setId(existingUser.getId());

        if (updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty()) {
            updatedUser.setPassword(existingUser.getPassword());
        }

        if (updatedUser.getUsername() == null) updatedUser.setUsername(existingUser.getUsername());
        if (updatedUser.getEmailAddress() == null) updatedUser.setEmailAddress(existingUser.getEmailAddress());
        if (updatedUser.getFirstName() == null) updatedUser.setFirstName(existingUser.getFirstName());
        if (updatedUser.getLastName() == null) updatedUser.setLastName(existingUser.getLastName());
        if (updatedUser.getDateOfBirth() == null) updatedUser.setDateOfBirth(existingUser.getDateOfBirth());
        if (updatedUser.getGender() == null) updatedUser.setGender(existingUser.getGender());
        if (updatedUser.getRole() == null) updatedUser.setRole(existingUser.getRole());
        if (updatedUser.getProfilePicturePath() == null) updatedUser.setProfilePicturePath(existingUser.getProfilePicturePath());
        if (updatedUser.getFriendListIds() == null) updatedUser.setFriendListIds(existingUser.getFriendListIds());
        if (updatedUser.getPostIds() == null) updatedUser.setPostIds(existingUser.getPostIds());
        if (updatedUser.getImageIds() == null) updatedUser.setImageIds(existingUser.getImageIds());
        if (updatedUser.getFriendRequestsSent() == null) updatedUser.setFriendRequestsSent(existingUser.getFriendRequestsSent());
        if (updatedUser.getFriendRequestsReceived() == null) updatedUser.setFriendRequestsReceived(existingUser.getFriendRequestsReceived());

        updatedUser.setPrivateAccount(updatedUser.isPrivateAccount());

        userFileStorage.updateUser(updatedUser);

    }

    public User findUserById(String id) {
        return userFileStorage.findById(id);
    }
    
//
//    public ResponseEntity<String> removeFriend(String userId, String friendId) {
//        User user = userFileStorage.findById(userId);
//        User friend = userFileStorage.findById(friendId);
//        if (user == null || friend == null) {
//            return ResponseEntity.badRequest().body("User not found");
//        }
//        user.getFriendListIds().remove(friendId);
//        friend.getFriendListIds().remove(userId);
//        userFileStorage.updateUser(user);
//        userFileStorage.updateUser(friend);
//
//        return ResponseEntity.ok("Friend removed");
//    }
}

