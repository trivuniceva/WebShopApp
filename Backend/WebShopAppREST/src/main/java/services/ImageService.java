package services;

import model.Image;
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

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("imageStorage") == null) {
            ctx.setAttribute("imageStorage", new ImageFileStorage());
        }
        imageStorage = (ImageFileStorage) ctx.getAttribute("imageStorage");
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
        imageStorage.deleteImage(imageId);
    }
}
