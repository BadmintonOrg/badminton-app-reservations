package si.fri.rso.badmintonappreservations.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.badmintonappreservations.lib.Reservation;
import si.fri.rso.badmintonappreservations.models.converters.ReservationConverter;
import si.fri.rso.badmintonappreservations.models.entities.ReservationEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
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

    public Reservation getReservation(Integer id) {

        ReservationEntity reservationEntity = em.find(ReservationEntity.class, id);

        if (reservationEntity == null) {
            throw new NotFoundException();
        }

        Reservation res = ReservationConverter.toDto(reservationEntity);

        return res;
    }

    public Reservation createReservation(Reservation res) {

        ReservationEntity reservationEntity = ReservationConverter.toEntity(res);

        try {
            beginTx();
            em.persist(reservationEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (reservationEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return ReservationConverter.toDto(reservationEntity);
    }

    public boolean deleteReservation(Integer id) {

        ReservationEntity reservationEntity = em.find(ReservationEntity.class, id);

        if (reservationEntity != null) {
            try {
                beginTx();
                em.remove(reservationEntity);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }

    public Reservation putReservation(Integer id, Reservation res) {

        ReservationEntity c = em.find(ReservationEntity.class, id);

        if (c == null) {
            return null;
        }

        ReservationEntity updatedReservationEntity = ReservationConverter.toEntity(res);

        try {
            beginTx();
            updatedReservationEntity.setId(c.getId());
            updatedReservationEntity = em.merge(updatedReservationEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return ReservationConverter.toDto(updatedReservationEntity);
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}