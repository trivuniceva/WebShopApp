package services;

import model.Post;
import storage.PostFileStorage;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostService {

    @Context
    ServletContext ctx;

    private PostFileStorage postStorage;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("postStorage") == null) {
            ctx.setAttribute("postStorage", new PostFileStorage());
        }
        postStorage = (PostFileStorage) ctx.getAttribute("postStorage");
    }

    @GET
    @Path("/user/{userId}")
    public List<Post> getPostsByUser(@PathParam("userId") String userId) {
        return postStorage.getPostsByUser(userId);
    }

    @POST
    @Path("/add")
    public Post addPost(Post post) {
        return postStorage.addPost(post);
    }
}

