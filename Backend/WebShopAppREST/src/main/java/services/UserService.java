package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import storage.UserFileStorage;
import storage.FriendRequestFileStorage;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {

    @Context
    ServletContext ctx;

    private UserFileStorage userStorage;
    private FriendRequestFileStorage friendRequestStorage;
    private static boolean initialized = false;

    @PostConstruct
    public void init() {
        if (!initialized) {
            synchronized (UserService.class) {
                if (!initialized) {
                    if (ctx.getAttribute("userStorage") == null) {
                        ctx.setAttribute("userStorage", new UserFileStorage());
                    }
                    userStorage = (UserFileStorage) ctx.getAttribute("userStorage");

                    if (ctx.getAttribute("friendRequestStorage") == null) {
                        ctx.setAttribute("friendRequestStorage", new FriendRequestFileStorage());
                    }
                    friendRequestStorage = (FriendRequestFileStorage) ctx.getAttribute("friendRequestStorage");

                    initialized = true;
                }
            }
        }
    }

    private UserFileStorage getUserStorage() {
        if (userStorage == null) {
            init();
            if (userStorage == null) {
                userStorage = (UserFileStorage) ctx.getAttribute("userStorage");
                if (userStorage == null) {
                    userStorage = new UserFileStorage();
                    ctx.setAttribute("userStorage", userStorage);
                }
            }
        }
        return userStorage;
    }

    private FriendRequestFileStorage getFriendRequestStorage() {
        if (friendRequestStorage == null) {
            init();
            if (friendRequestStorage == null) {
                friendRequestStorage = (FriendRequestFileStorage) ctx.getAttribute("friendRequestStorage");
                if (friendRequestStorage == null) {
                    friendRequestStorage = new FriendRequestFileStorage();
                    ctx.setAttribute("friendRequestStorage", friendRequestStorage);
                }
            }
        }
        return friendRequestStorage;
    }

    @GET
    public Response getAllUsers() {
        List<User> users = getUserStorage().getAllUsers();
        List<User> nonAdminUsers = users.stream()
                .filter(user -> !"Administrator".equals(user.getRole()))
                .collect(Collectors.toList());
        return Response.ok(nonAdminUsers).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") String id) {
        User user = getUserStorage().findById(id);
        if (user != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") String id, User updatedUser) {
        if (!id.equals(updatedUser.getId())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User ID in path does not match object ID.").build();
        }
        getUserStorage().updateUser(updatedUser);
        return Response.ok(updatedUser).build();
    }

    @POST
    @Path("/{userId}/remove-friend/{friendId}")
    public Response removeFriend(@PathParam("userId") String userId, @PathParam("friendId") String friendId) {
        boolean success = getUserStorage().removeFriend(userId, friendId);

        if (success) {
            getFriendRequestStorage().removeFriendRequestRecords(userId, friendId);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to remove friend.").build();
        }
    }
}
