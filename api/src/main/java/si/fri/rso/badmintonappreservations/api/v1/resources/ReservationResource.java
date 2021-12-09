package si.fri.rso.badmintonappreservations.api.v1.resources;

import si.fri.rso.badmintonappreservations.lib.Reservation;
import si.fri.rso.badmintonappreservations.services.beans.ReservationBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private Logger log = Logger.getLogger(ReservationResource.class.getName());

    @Inject
    private ReservationBean resBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getReservations() {
        List<Reservation> reservations = resBean.getReservations(uriInfo);

        return Response.status(Response.Status.OK).entity(reservations).build();
    }

    @GET
    @Path("/{resId}")
    public Response getReservation(@PathParam("resId") Integer resId) {

        Reservation res = resBean.getReservation(resId);

        if (res == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(res).build();
    }

    @POST
    public Response createReservation(Reservation res) {

        if (res.getCourt() == null || res.getDateCreated() == null || res.getDateReserved() == null || res.getUser() == null || res.getDuration() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            res = resBean.createReservation(res);
        }

        return Response.status(Response.Status.CREATED).entity(res).build();

    }

    @DELETE
    @Path("{resId}")
    public Response deleteReservation(@PathParam("resId") Integer resId){

        boolean deleted = resBean.deleteReservation(resId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("{resId}")
    public Response putReservation(@PathParam("resId") Integer resId,
                             Reservation res){

        res = resBean.putReservation(resId, res);

        if (res == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }




}
