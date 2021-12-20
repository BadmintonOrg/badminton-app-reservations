package si.fri.rso.badmintonappreservations.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
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

    @Operation(description = "Get all reservations in a list", summary = "Get all reservations")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of reservations",
                    content = @Content(schema = @Schema(implementation = Reservation.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getReservations() {
        List<Reservation> reservations = resBean.getReservations(uriInfo);

        return Response.status(Response.Status.OK).entity(reservations).build();
    }

    @Operation(description = "Get data for a reservation.", summary = "Get data for a reservation")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Reservation",
                    content = @Content(
                            schema = @Schema(implementation = Reservation.class))
            )})
    @GET
    @Path("/{resId}")
    public Response getReservation(@Parameter(description = "Reservation ID.", required = true)
                                       @PathParam("resId") Integer resId) {

        Reservation res = resBean.getReservation(resId);

        if (res == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(res).build();
    }

    @Operation(description = "Add reservation.", summary = "Add reservation")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Reservation successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response createReservation(@RequestBody(
            description = "DTO object with reservation data.",
            required = true, content = @Content(
            schema = @Schema(implementation = Reservation.class))) Reservation res) {

        if (res.getCourt() == null || res.getDateCreated() == null || res.getDateReserved() == null || res.getUser() == null || res.getDuration() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            res = resBean.createReservation(res);
        }

        return Response.status(Response.Status.CREATED).entity(res).build();

    }

    @Operation(description = "Delete reservation.", summary = "Delete reservation")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Reservation successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{resId}")
    public Response deleteReservation(@Parameter(description = "Reservation ID.", required = true)
                                          @PathParam("resId") Integer resId){

        boolean deleted = resBean.deleteReservation(resId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Operation(description = "Update data for a reservation.", summary = "Update reservation")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Reservation successfully updated."
            )
    })
    @PUT
    @Path("{resId}")
    public Response putReservation(@Parameter(description = "Metadata ID.", required = true)
                                       @PathParam("resId") Integer resId,
                                   @RequestBody(
                                           description = "DTO object with reservation data.",
                                           required = true, content = @Content(
                                           schema = @Schema(implementation = Reservation.class)))
                                           Reservation res){

        res = resBean.putReservation(resId, res);

        if (res == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }




}
