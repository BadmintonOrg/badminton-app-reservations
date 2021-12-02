package si.fri.rso.badmintonappreservations.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.badmintonappreservations.lib.Reservation;
import si.fri.rso.badmintonappreservations.models.converters.ReservationConverter;
import si.fri.rso.badmintonappreservations.models.entities.ReservationEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class ReservationBean {

    private Logger log = Logger.getLogger(ReservationBean.class.getName());

    @Inject
    private EntityManager em;

    public List<Reservation> getReservations(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, ReservationEntity.class, queryParameters).stream()
                .map(ReservationConverter::toDto).collect(Collectors.toList());
    }

    //TO-DO
}