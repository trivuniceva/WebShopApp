package storage;

import model.FriendRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class FriendRequestFileStorage {

    private final String filePath = "/Users/nikolina/Desktop/Veb programiranje - materijali/WebShop/Backend/WebShopAppREST/src/main/webapp/files/friendRequests.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private List<FriendRequest> requests;

    public FriendRequestFileStorage() {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        loadRequests();
    }

    public void loadRequests() {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                requests = new ArrayList<>();
                return;
            }
            requests = mapper.readValue(file, new TypeReference<List<FriendRequest>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            requests = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            requests = new ArrayList<>();
        }
    }

    public void saveRequests() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), requests);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FriendRequest> getAllRequests() {
        return new ArrayList<>(requests);
    }

    public FriendRequest findById(String id) {
        if (requests == null) return null;
        return requests.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void updateRequest(FriendRequest updatedRequest) {
        if (requests == null) return;
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getId().equals(updatedRequest.getId())) {
                requests.set(i, updatedRequest);
                saveRequests();
                return;
            }
        }
    }

    public void saveNewRequest(FriendRequest newRequest) {
        if (requests == null) {
            requests = new ArrayList<>();
        }
        requests.add(newRequest);
        saveRequests();
    }

    public List<FriendRequest> getReceivedRequests(String userId) {
        return requests.stream()
                .filter(fr -> fr.getReceiverId().equals(userId) && "pending".equalsIgnoreCase(fr.getStatus()))
                .collect(Collectors.toList());
    }

    public List<FriendRequest> getSentRequests(String userId) {
        return requests.stream()
                .filter(fr -> fr.getSenderId().equals(userId))
                .collect(Collectors.toList());
    }
    
    
    public void removeFriendRequestRecords(String user1Id, String user2Id) {
        if (requests == null) return;

        requests.removeIf(req ->
            (req.getSenderId().equals(user1Id) && req.getReceiverId().equals(user2Id) ||
             req.getSenderId().equals(user2Id) && req.getReceiverId().equals(user1Id)) &&
            "accepted".equalsIgnoreCase(req.getStatus())
        );
        saveRequests();
    }
}
