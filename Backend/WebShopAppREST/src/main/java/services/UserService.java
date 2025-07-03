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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    
    @GET
    @Path("/{userId}/common-friends/{otherUserId}")
    public Response getCommonFriends(@PathParam("userId") String userId, @PathParam("otherUserId") String otherUserId) {
        User user1 = getUserStorage().findById(userId);
        User user2 = getUserStorage().findById(otherUserId);

        if (user1 == null || user2 == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("One or both users not found.").build();
        }

        // Kreiranje Set-ova za brže pretraživanje
        Set<String> user1Friends = new HashSet<>(user1.getFriendListIds());
        Set<String> user2Friends = new HashSet<>(user2.getFriendListIds());

        // Pronalaženje preseka (zajedničkih prijatelja ID-eva)
        Set<String> commonFriendIds = user1Friends.stream()
                                            .filter(user2Friends::contains)
                                            .collect(Collectors.toSet());

        // Konvertovanje ID-eva u objekte User
        List<User> commonFriends = new ArrayList<>();
        for (String friendId : commonFriendIds) {
            User friend = getUserStorage().findById(friendId);
            if (friend != null) {
                // Ne šalji osetljive informacije kao što su lozinka
                friend.setPassword(null); // Čisti lozinku pre slanja na frontend
                friend.setFriendListIds(null); // Možda ne želiš da šalješ i njihove liste prijatelja
                friend.setFriendRequestsReceived(null);
                friend.setFriendRequestsSent(null);
                commonFriends.add(friend);
            }
        }

        return Response.ok(commonFriends).build();
    }

}
