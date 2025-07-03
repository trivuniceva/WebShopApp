package storage;

import model.Comment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommentFileStorage {
    private final String filePath = "/Users/nikolina/Desktop/Veb programiranje - materijali/WebShop/Backend/WebShopAppREST/src/main/webapp/files/comments.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private List<Comment> comments;

    public CommentFileStorage() {
        mapper.registerModule(new JavaTimeModule());
        loadComments();
    }

    public void loadComments() {
    	System.out.println("loading....");
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                comments = new ArrayList<>();
                return;
            }
            comments = mapper.readValue(file, new TypeReference<List<Comment>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            comments = new ArrayList<>();
        }
    }

    public void saveComments() {
        try {
            File file = new File(filePath);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, comments);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Comment> getAllComments() {
        return new ArrayList<>(comments);
    }

    public Comment findById(String id) {
        return comments.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Comment> findByObjectId(String objectId) {
        return comments.stream()
                .filter(c -> c.getObjectId().equals(objectId) && !c.isLogicallyDeleted())
                .collect(Collectors.toList());
    }

    public Comment addComment(Comment newComment) {

    	System.out.println("addddd kom ");
    	
        if (newComment.getId() == null || newComment.getId().isEmpty()) {
            newComment.setId(UUID.randomUUID().toString());
        }
        comments.add(newComment);
        
        saveComments();
        return newComment;
    }

    public void updateComment(Comment updatedComment) {
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getId().equals(updatedComment.getId())) {
                comments.set(i, updatedComment);
                saveComments();
                return;
            }
        }
    }

    public void deleteComment(String id) {
        Comment commentToDelete = findById(id);
        if (commentToDelete != null) {
            commentToDelete.setLogicallyDeleted(true);
            saveComments();
        }
    }
}