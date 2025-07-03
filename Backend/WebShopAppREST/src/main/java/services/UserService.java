package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import storage.UserFileStorage;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {

    @Context
    ServletContext ctx;

    private UserFileStorage storage;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("userStorage") == null) {
            ctx.setAttribute("userStorage", new UserFileStorage());
        }
        storage = (UserFileStorage) ctx.getAttribute("userStorage");
    }

    @GET
    public Response getAllUsers() {
        List<User> users = storage.getAllUsers();
        List<User> nonAdminUsers = users.stream()
                .filter(user -> !"Administrator".equals(user.getRole()))
                .collect(Collectors.toList());
        return Response.ok(nonAdminUsers).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") String id) {
        User user = storage.findById(id);
        if (user != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") String id, User updatedUser) {
        storage.update(id, updatedUser);
        return Response.ok(updatedUser).build();
    }

    @POST
    @Path("/{userId}/remove-friend/{friendId}")
    public Response removeFriend(@PathParam("userId") String userId, @PathParam("friendId") String friendId) {
        boolean success = storage.removeFriend(userId, friendId);
        return success ? Response.ok().build() : Response.status(Response.Status.BAD_REQUEST).build();
    }
}
