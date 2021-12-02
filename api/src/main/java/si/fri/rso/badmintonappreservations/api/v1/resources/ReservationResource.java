package si.fri.rso.badmintonappreservations.api.v1.resources;

import si.fri.rso.badmintonappreservations.lib.Reservation;
import si.fri.rso.badmintonappreservations.services.beans.ReservationBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

   /*@GET
    @Path("/{courtId}")
    public Response getImageMetadata(@PathParam("courtId") Integer courtId) {

        Court cort = courtBean.getCourt(courtId);

        if (cort == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(cort).build();
    }*/



}
