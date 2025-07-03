package services;

import model.FriendRequest;
import model.User;
import storage.FriendRequestFileStorage;
import storage.UserFileStorage;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/friend-requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendRequestService {

    @Context
    ServletContext ctx;

    private FriendRequestFileStorage friendRequestStorage;
    private UserFileStorage userStorage;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("friendRequestStorage") == null) {
            ctx.setAttribute("friendRequestStorage", new FriendRequestFileStorage());
        }
        friendRequestStorage = (FriendRequestFileStorage) ctx.getAttribute("friendRequestStorage");

        if (ctx.getAttribute("userStorage") == null) {
            ctx.setAttribute("userStorage", new UserFileStorage());
        }
        userStorage = (UserFileStorage) ctx.getAttribute("userStorage");
    }

    @GET
    @Path("/received/{userId}")
    public Response getPendingRequests(@PathParam("userId") String userId) {
        List<FriendRequest> pending = friendRequestStorage.getReceivedRequests(userId);
        return Response.ok(pending).build();
    }

    @GET
    @Path("/sent/{userId}")
    public Response getSentRequests(@PathParam("userId") String userId) {
        List<FriendRequest> sent = friendRequestStorage.getSentRequests(userId);
        return Response.ok(sent).build();
    }

    @POST
    @Path("/send")
    public Response sendRequest(FriendRequest request) {
        String senderId = request.getSenderId();
        String receiverId = request.getReceiverId();

        User sender = userStorage.findById(senderId);
        User receiver = userStorage.findById(receiverId);

        if (sender == null || receiver == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Sender or receiver not found.").build();
        }

        if (senderId.equals(receiverId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot send friend request to self.").build();
        }

        boolean requestExists = friendRequestStorage.getAllRequests().stream()
                .anyMatch(req ->
                        (req.getSenderId().equals(senderId) && req.getReceiverId().equals(receiverId) && !req.getStatus().equals("rejected")) ||
                        (req.getSenderId().equals(receiverId) && req.getReceiverId().equals(senderId) && !req.getStatus().equals("rejected")) ||
                        (sender.getFriendListIds().contains(receiverId))
                );
        if (requestExists) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Friend request or friendship already exists.").build();
        }

        FriendRequest newRequest = new FriendRequest();
        newRequest.setId(UUID.randomUUID().toString());
        newRequest.setSenderId(senderId);
        newRequest.setReceiverId(receiverId);
        newRequest.setStatus("pending");
        newRequest.setDate(LocalDateTime.now());

        sender.getFriendRequestsSent().add(newRequest.getId());
        receiver.getFriendRequestsReceived().add(newRequest.getId());

        userStorage.updateUser(sender);
        userStorage.updateUser(receiver);

        friendRequestStorage.saveNewRequest(newRequest);

        return Response.ok(newRequest).build();
    }

    @POST
    @Path("/accept/{requestId}")
    public Response acceptRequest(@PathParam("requestId") String requestId) {
        FriendRequest request = friendRequestStorage.findById(requestId);
        if (request == null || !request.getStatus().equals("pending")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request ID or request is not pending.").build();
        }

        request.setStatus("accepted");
        friendRequestStorage.updateRequest(request);

        User sender = userStorage.findById(request.getSenderId());
        User receiver = userStorage.findById(request.getReceiverId());

        if (sender == null || receiver == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Sender or receiver user not found.").build();
        }

        if (!sender.getFriendListIds().contains(receiver.getId())) {
            sender.getFriendListIds().add(receiver.getId());
        }
        if (!receiver.getFriendListIds().contains(sender.getId())) {
            receiver.getFriendListIds().add(sender.getId());
        }

        sender.getFriendRequestsSent().remove(requestId);
        receiver.getFriendRequestsReceived().remove(requestId);

        userStorage.updateUser(sender);
        userStorage.updateUser(receiver);

        return Response.ok().entity("Friend request accepted.").build();
    }

    @POST
    @Path("/reject/{requestId}")
    public Response rejectRequest(@PathParam("requestId") String requestId) {
        FriendRequest request = friendRequestStorage.findById(requestId);
        if (request == null || !request.getStatus().equals("pending")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request ID or request is not pending.").build();
        }

        request.setStatus("rejected");
        friendRequestStorage.updateRequest(request);

        User sender = userStorage.findById(request.getSenderId());
        User receiver = userStorage.findById(request.getReceiverId());

        if (sender == null || receiver == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Sender or receiver user not found.").build();
        }

        sender.getFriendRequestsSent().remove(requestId);
        receiver.getFriendRequestsReceived().remove(requestId);

        userStorage.updateUser(sender);
        userStorage.updateUser(receiver);

        return Response.ok().entity("Friend request rejected.").build();
    }
}
