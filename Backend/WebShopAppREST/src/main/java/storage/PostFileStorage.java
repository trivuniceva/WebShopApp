package storage;

import model.Post;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PostFileStorage {

    private final String filePath = "/Users/nikolina/Desktop/Veb programiranje - materijali/WebShop/Backend/WebShopAppREST/src/main/webapp/files/posts.json";

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private List<Post> posts;

    public PostFileStorage() {
        loadPosts();
    }

    private void loadPosts() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                posts = new ArrayList<>();
                return;
            }
            posts = mapper.readValue(file, new TypeReference<List<Post>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            posts = new ArrayList<>();
        }
    }

    public List<Post> getPostsByIds(List<String> ids) {
        List<Post> result = new ArrayList<>();
        for (String id : ids) {
            posts.stream()
                    .filter(post -> post.getId().equals(id) && !post.isLogicallyDeleted())
                    .findFirst()
                    .ifPresent(result::add);
        }
        return result;
    }

    public void savePosts() {
        try {
            File file = new File(filePath);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, posts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPost(Post post) {
        posts.add(post);
        savePosts();
    }
    
    public List<Post> getPostsByUser(String userId) {
        List<Post> userPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.getUserId().equals(userId) && !post.isLogicallyDeleted()) {
                userPosts.add(post);
            }
        }
        return userPosts;
    }


}
