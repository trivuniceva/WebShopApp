package services;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.User;
import dto.LoginRequest;
import storage.UserFileStorage;


@Path("")
public class LoginService {
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

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request, @Context HttpServletRequest httpRequest) {
        System.out.println("Login pokušaj za korisnika: " + request.getUsername());
        User user = storage.findByUsername(request.getUsername());

        if (user == null || user.getPassword() == null || !user.getPassword().equals(request.getPassword())) {
            return Response.status(401).entity("Pogrešan username ili lozinka.").build();
        }

        httpRequest.getSession().setAttribute("user", user);
        return Response.ok(user).build();
    }

    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public void logout(@Context HttpServletRequest request) {
        request.getSession().invalidate();
    }

    @GET
    @Path("/currentUser")
    @Produces(MediaType.APPLICATION_JSON)
    public User getCurrentUser(@Context HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }
}
