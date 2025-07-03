package services;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dto.CommentDTO;
import model.Comment;
import model.Image;
import storage.CommentFileStorage;
import storage.ImageFileStorage; // Make sure this import is present
import storage.UserFileStorage;

@Path("/comments")
public class CommentService {

    @Context
    ServletContext ctx;

    private CommentFileStorage commentStorage;
    private UserFileStorage userStorage;
    private ImageFileStorage imageStorage; // Declare the field

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("commentStorage") == null) {
            ctx.setAttribute("commentStorage", new CommentFileStorage());
        }
        if (ctx.getAttribute("userStorage") == null) {
            ctx.setAttribute("userStorage", new UserFileStorage());
        }
        // ADD THIS BLOCK FOR IMAGE STORAGE
        if (ctx.getAttribute("imageStorage") == null) { // Check if it's already in context
            ctx.setAttribute("imageStorage", new ImageFileStorage()); // Initialize it
        }

        commentStorage = (CommentFileStorage) ctx.getAttribute("commentStorage");
        userStorage = (UserFileStorage) ctx.getAttribute("userStorage");
        imageStorage = (ImageFileStorage) ctx.getAttribute("imageStorage"); // Assign it
    }

    @GET
    @Path("/object/{objectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCommentsForObject(@PathParam("objectId") String objectId) {
        List<Comment> comments = commentStorage.findByObjectId(objectId);

        // Map to CommentDTO list
        List<CommentDTO> dtos = comments.stream().map(comment -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(comment.getId());
            dto.setObjectId(comment.getObjectId());
            dto.setUserId(comment.getUserId());
            dto.setText(comment.getText());
            dto.setCommentDate(comment.getCommentDate());

            var user = userStorage.findById(comment.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
            } else {
                dto.setUsername("Nepoznat korisnik");
            }
            return dto;
        }).collect(Collectors.toList());

        return Response.ok(dtos).build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addComment(Comment newComment) {
        System.out.println("ae macko");
        if (newComment == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Komentar ne sme biti null").build();
        }
        
        Comment addedComment = commentStorage.addComment(newComment);
        System.out.println("nesto se desava");

        if ("IMAGE".equals(newComment.getObjectType())) {
            Image imageToUpdate = imageStorage.findById(newComment.getObjectId()); // This line caused the NPE
            System.out.println("image");
            if (imageToUpdate != null) {
                
                if (imageToUpdate.getCommentIds() == null) {
                    imageToUpdate.setCommentIds(new ArrayList<>());
                }
                imageToUpdate.getCommentIds().add(addedComment.getId());
                // imageStorage.saveImages(); // Ensure this method exists and works
                // Note: It's good practice to ensure imageStorage has a save method.
                // It was present in your ImageFileStorage, so this is fine.
            }
        }

        return Response.ok(addedComment).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteComment(@PathParam("id") String id) {
        Comment comment = commentStorage.findById(id);
        if (comment == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Komentar nije pronađen").build();
        }
        commentStorage.deleteComment(id);
        return Response.noContent().build();
    }
}