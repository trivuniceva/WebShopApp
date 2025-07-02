package storage;


import model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(updatedUser.getId())) {
                users.set(i, updatedUser);
                saveUsers();
                return;
            }
        }
    }
}

