package services;

import model.FriendRequest;
import storage.FriendRequestFileStorage;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/friend-requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendRequestService {

    @Context
    ServletContext ctx;

    private FriendRequestFileStorage storage;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("friendRequestStorage") == null) {
            ctx.setAttribute("friendRequestStorage", new FriendRequestFileStorage());
        }
        storage = (FriendRequestFileStorage) ctx.getAttribute("friendRequestStorage");
    }

    @GET
    @Path("/received/{userId}")
    public Response getPendingRequests(@PathParam("userId") String userId) {
        List<FriendRequest> pending = storage.getReceivedRequests(userId);
        return Response.ok(pending).build();
    }

    @GET
    @Path("/sent/{userId}")
    public Response getSentRequests(@PathParam("userId") String userId) {
        List<FriendRequest> sent = storage.getSentRequests(userId);
        return Response.ok(sent).build();
    }

    @POST
    @Path("/send")
    public Response sendRequest(FriendRequest request) {
        FriendRequest newRequest = storage.sendRequest(request.getSenderId(), request.getReceiverId());
        return newRequest != null
                ? Response.ok(newRequest).build()
                : Response.status(Response.Status.BAD_REQUEST).entity("Request failed").build();
    }

    @POST
    @Path("/accept/{requestId}")
    public Response acceptRequest(@PathParam("requestId") String requestId) {
        boolean success = storage.acceptRequest(requestId);
        return success ? Response.ok().build() : Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/reject/{requestId}")
    public Response rejectRequest(@PathParam("requestId") String requestId) {
        boolean success = storage.rejectRequest(requestId);
        return success ? Response.ok().build() : Response.status(Response.Status.BAD_REQUEST).build();
    }
}
