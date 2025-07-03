package services;

import model.Post;
import storage.PostFileStorage;
import storage.UserFileStorage;
import storage.CommentFileStorage; // <--- NEW Import: for deleting comments

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostService {

    @Context
    ServletContext ctx;

    private PostFileStorage postStorage;
    private UserFileStorage userFileStorage;
    private CommentFileStorage commentFileStorage; // <--- NEW: Comment storage

    private String imagesUploadBasePath;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("postStorage") == null) {
            ctx.setAttribute("postStorage", new PostFileStorage());
        }
        postStorage = (PostFileStorage) ctx.getAttribute("postStorage");

        if (ctx.getAttribute("userFileStorage") == null) {
            ctx.setAttribute("userFileStorage", new UserFileStorage());
        }
        userFileStorage = (UserFileStorage) ctx.getAttribute("userFileStorage");

        // --- NEW: Initialize CommentFileStorage ---
        if (ctx.getAttribute("commentFileStorage") == null) {
            ctx.setAttribute("commentFileStorage", new CommentFileStorage());
        }
        commentFileStorage = (CommentFileStorage) ctx.getAttribute("commentFileStorage");
        // --- END NEW ---

        imagesUploadBasePath = ctx.getRealPath("") + File.separator + "files" + File.separator + "images";
        File uploadDir = new File(imagesUploadBasePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        System.out.println("Image upload base directory: " + imagesUploadBasePath);
    }

    @GET
    @Path("/user/{userId}")
    public List<Post> getPostsByUser(@PathParam("userId") String userId) {
        return postStorage.getPostsByUser(userId);
    }

    @POST
    @Path("/add")
    public Response addPost(Post post) {

        if (post.getBase64Image() != null && !post.getBase64Image().isEmpty()) {
            try {
                if (post.getImagePath() == null || post.getImagePath().isEmpty()) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Image path not provided for base64 image.").build();
                }

                String fileName = new File(post.getImagePath()).getName();
                String fullFilePath = imagesUploadBasePath + File.separator + fileName;

                byte[] decodedBytes = Base64.getDecoder().decode(post.getBase64Image());

                try (FileOutputStream fos = new FileOutputStream(new File(fullFilePath))) {
                    fos.write(decodedBytes);
                    System.out.println("Image saved to: " + fullFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                   .entity("Error saving image file: " + e.getMessage()).build();
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("Invalid Base64 image data: " + e.getMessage()).build();
            }
            post.setBase64Image(null);
        } else {
            post.setImagePath(null);
        }

        try {
            postStorage.addPost(post);

            var user = userFileStorage.findById(post.getUserId());
            if (user != null) {
                var postIds = user.getPostIds();
                if (postIds == null) {
                    postIds = new ArrayList<>();
                    user.setPostIds(postIds);
                }
                postIds.add(post.getId());
                userFileStorage.saveUsers();
            }
            return Response.ok(post).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error adding post to storage: " + e.getMessage()).build();
        }
    }

    // --- NEW ENDPOINT: Delete Post ---
    @DELETE
    @Path("/{postId}")
    public Response deletePost(@PathParam("postId") String postId) {
        Post postToDelete = postStorage.findById(postId);
        
        if (postToDelete == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Post not found or already deleted.").build();
        }

        try {
            commentFileStorage.loadComments(); // Ensure comments are loaded before deletion

            System.out.println("Deleting comments for post: " + postToDelete.getId());
            if (postToDelete.getCommentIds() != null) {
                for (String commentId : postToDelete.getCommentIds()) {
                    commentFileStorage.deleteComment(commentId);
                    System.out.println("Deleted comment with ID: " + commentId);
                }
            }

            postStorage.deletePost(postId);
            System.out.println("Post logically deleted: " + postId);
            return Response.ok().build(); // Return 200 OK
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error deleting post: " + e.getMessage()).build();
        }
    }
    // --- END NEW ENDPOINT ---
}