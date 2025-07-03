package services;

import model.Image;
import storage.CommentFileStorage;
import storage.ImageFileStorage;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/images")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImageService {

    @Context
    ServletContext ctx;

    private ImageFileStorage imageStorage;
    private CommentFileStorage commentFileStorage; 

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("imageStorage") == null) {
            ctx.setAttribute("imageStorage", new ImageFileStorage());
        }
        imageStorage = (ImageFileStorage) ctx.getAttribute("imageStorage");
        
        if (ctx.getAttribute("commentFileStorage") == null) {
            ctx.setAttribute("commentFileStorage", new CommentFileStorage());
        }
        commentFileStorage = (CommentFileStorage) ctx.getAttribute("commentFileStorage");
   
    }

    @GET
    @Path("/user/{userId}")
    public List<Image> getImagesByUser(@PathParam("userId") String userId) {
        return imageStorage.getImagesByUser(userId);
    }

    @POST
    @Path("/add")
    public Image addImage(Image image) {
        return imageStorage.addImage(image);
    }

    @DELETE
    @Path("/{imageId}")
    public void deleteImage(@PathParam("imageId") String imageId) {
    	Image imageToDelete = imageStorage.findById(imageId);

        if (imageToDelete != null) {
            System.out.println(imageToDelete.getCommentIds());
            if (imageToDelete.getCommentIds() != null) {
                for (String commentId : imageToDelete.getCommentIds()) {
                   commentFileStorage.deleteComment(commentId);
                   
                }
            }
            imageStorage.deleteImage(imageId);
        }
    }
}
