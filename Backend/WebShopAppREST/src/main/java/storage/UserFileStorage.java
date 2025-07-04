package storage;

import model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserFileStorage {
    private final String filePath = "/Users/nikolina/Desktop/Veb programiranje - materijali/WebShop/Backend/WebShopAppREST/src/main/webapp/files/users.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private List<User> users;

    public UserFileStorage() {
        loadUsers();
    }

    public void loadUsers() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                users = new ArrayList<>();
                return;
            }
            users = mapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            users = new ArrayList<>();
        }
    }

    public void saveUsers() {
        try {
            File file = new File(filePath);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        return users;
    }

    public User findByUsername(String username) {
        System.out.println(username);
        System.out.println("find");
        System.out.println(users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null));
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public User findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void updateUser(User updatedUser) {
    	 User existingUser = findById(updatedUser.getId()); 
         if (existingUser == null) {
             System.out.println("User with ID " + updatedUser.getId() + " not found for update.");
             return; 
         }
         
         if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
             existingUser.setPassword(updatedUser.getPassword());
         }

         existingUser.setEmailAddress(Objects.requireNonNullElse(updatedUser.getEmailAddress(), existingUser.getEmailAddress()));
         existingUser.setFirstName(Objects.requireNonNullElse(updatedUser.getFirstName(), existingUser.getFirstName()));
         existingUser.setLastName(Objects.requireNonNullElse(updatedUser.getLastName(), existingUser.getLastName()));
         existingUser.setDateOfBirth(Objects.requireNonNullElse(updatedUser.getDateOfBirth(), existingUser.getDateOfBirth()));
         existingUser.setGender(Objects.requireNonNullElse(updatedUser.getGender(), existingUser.getGender()));

         existingUser.setPrivateAccount(updatedUser.isPrivateAccount());
         existingUser.setLogicallyDeleted(updatedUser.isLogicallyDeleted());
         existingUser.setBlocked(updatedUser.isBlocked());
         
         existingUser.setPostIds(Objects.requireNonNullElse(updatedUser.getPostIds(), existingUser.getPostIds()));
         existingUser.setImageIds(Objects.requireNonNullElse(updatedUser.getImageIds(), existingUser.getImageIds()));
         existingUser.setFriendRequestsSent(Objects.requireNonNullElse(updatedUser.getFriendRequestsSent(), existingUser.getFriendRequestsSent()));
         existingUser.setFriendRequestsReceived(Objects.requireNonNullElse(updatedUser.getFriendRequestsReceived(), existingUser.getFriendRequestsReceived()));
         existingUser.setFriendListIds(Objects.requireNonNullElse(updatedUser.getFriendListIds(), existingUser.getFriendListIds()));

         if (updatedUser.getProfilePicturePath() != null) {
             existingUser.setProfilePicturePath(updatedUser.getProfilePicturePath());
         }

         saveUsers();
         System.out.println("User " + existingUser.getId() + " updated successfully.");

         
    }

    public boolean removeFriend(String userId, String friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        if (user == null || friend == null) {
            System.out.println("User or friend not found for removal.");
            return false; 
        }

        boolean userRemovedFriend = user.getFriendListIds().remove(friendId);
        boolean friendRemovedUser = friend.getFriendListIds().remove(userId);

        if (userRemovedFriend || friendRemovedUser) {
            updateUser(user);
            updateUser(friend);
            System.out.println("Successfully removed friend: " + friendId + " from " + userId);
            return true;
        }
        
        System.out.println("Friendship did not exist between " + userId + " and " + friendId);
        return false; 
    }

	public void update(String id, User updatedUser) {
        User existingUser = findById(id);
        if (existingUser != null) {
            updateUser(updatedUser); 
        } else {
            System.out.println("User with ID " + id + " not found for update (via 'update(id, user)' method).");
        }
	}
}