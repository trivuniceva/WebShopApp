package storage;


import model.FriendRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestFileStorage {

    private final String filePath = "/Users/nikolina/Desktop/Veb programiranje - materijali/WebShop/Backend/WebShopAppREST/src/main/webapp/files/friendRequests.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private List<FriendRequest> requests;

    public FriendRequestFileStorage() {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        loadRequests();
    }

    private void loadRequests() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                requests = new ArrayList<>();
                System.out.println("FriendRequestFileStorage: fajl ne postoji, kreiran prazan spisak.");
                return;
            }
            requests = mapper.readValue(file, new TypeReference<List<FriendRequest>>() {});
            System.out.println("FriendRequestFileStorage: Učitani zahtevi:");
            for(FriendRequest r : requests) {
                System.out.println("Request: id=" + r.getId() + ", sender=" + r.getSenderId() + ", receiver=" + r.getReceiverId() + ", status=" + r.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            requests = new ArrayList<>();
        }
    }

    private void saveRequests() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), requests);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FriendRequest> getAllRequests() {
        System.out.println("FriendRequestFileStorage: getAllRequests called, ukupno zahteva: " + (requests != null ? requests.size() : 0));
        return requests;
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
                System.out.println("FriendRequestFileStorage: Zahtev " + updatedRequest.getId() + " je ažuriran.");
                return;
            }
        }
        System.out.println("FriendRequestFileStorage: Zahtev " + updatedRequest.getId() + " nije pronađen za ažuriranje.");
    }

    public void saveNewRequest(FriendRequest newRequest) {
        if (requests == null) {
            requests = new ArrayList<>();
        }
        requests.add(newRequest);
        saveRequests();
        System.out.println("FriendRequestFileStorage: Novi zahtev " + newRequest.getId() + " je sačuvan.");
    }
    
    public List<FriendRequest> getReceivedRequests(String userId) {
        List<FriendRequest> received = new ArrayList<>();
        for (FriendRequest r : requests) {
            if (r.getReceiverId().equals(userId) && "pending".equalsIgnoreCase(r.getStatus())) {
                received.add(r);
            }
        }
        return received;
    }

    public List<FriendRequest> getSentRequests(String userId) {
        List<FriendRequest> sent = new ArrayList<>();
        for (FriendRequest r : requests) {
            if (r.getSenderId().equals(userId) && "pending".equalsIgnoreCase(r.getStatus())) {
                sent.add(r);
            }
        }
        return sent;
    }

}
