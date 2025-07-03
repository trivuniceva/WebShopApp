package services;

import model.Post;
import storage.PostFileStorage;
import storage.UserFileStorage;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response; // Make sure Response is imported

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64; // For Base64 decoding
import java.util.List;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) // <--- Back to JSON
public class PostService {

    @Context
    ServletContext ctx;

    private PostFileStorage postStorage;
    private UserFileStorage userFileStorage;

    private String imagesUploadBasePath; // Changed name for clarity

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

        // Set the base path for saving images.
        // This will be the absolute path to your deployed webapp's 'files/images' folder.
        // E.g., /Users/nikolina/web/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/WebShopAppREST/files/images
        imagesUploadBasePath = ctx.getRealPath("") + File.separator + "files" + File.separator + "images";
        File uploadDir = new File(imagesUploadBasePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // Create the directory if it doesn't exist
        }
        System.out.println("Image upload base directory: " + imagesUploadBasePath);
    }

    @GET
    @Path("/user/{userId}")
    // @Produces(MediaType.APPLICATION_JSON) // Already at class level
    public List<Post> getPostsByUser(@PathParam("userId") String userId) {
        return postStorage.getPostsByUser(userId);
    }

    @POST
    @Path("/add")
    // @Consumes(MediaType.APPLICATION_JSON) // Already at class level
    public Response addPost(Post post) { // Now accepts Post directly

        // 1. Handle image upload if base64Image is provided
        if (post.getBase64Image() != null && !post.getBase64Image().isEmpty()) {
            try {
                // Ensure the imagePath is set by the frontend correctly
                // e.g., /WebShopAppREST/files/images/postId_filename.jpg
                if (post.getImagePath() == null || post.getImagePath().isEmpty()) {
                    // This should not happen if frontend sets it correctly, but as a fallback
                    // you might generate a default name or return an error.
                    return Response.status(Response.Status.BAD_REQUEST).entity("Image path not provided for base64 image.").build();
                }

                // Extract just the filename from the web-accessible imagePath
                String fileName = new File(post.getImagePath()).getName();
                String fullFilePath = imagesUploadBasePath + File.separator + fileName;

                byte[] decodedBytes = Base64.getDecoder().decode(post.getBase64Image()); // Decode Base64 string

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
            // Clear the base64Image field from the Post object
            // so it's not saved to the JSON file (it's binary data, not metadata)
            post.setBase64Image(null);
        } else {
            // If no base64Image was provided, ensure imagePath is null
            post.setImagePath(null);
        }

        // 2. Add the post metadata to storage
        try {
            postStorage.addPost(post); // This saves the Post object (without base64Image)

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
}