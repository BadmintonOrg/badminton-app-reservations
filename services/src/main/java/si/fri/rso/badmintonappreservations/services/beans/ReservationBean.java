package si.fri.rso.badmintonappreservations.services.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import si.fri.rso.badmintonappreservations.lib.Court;
import si.fri.rso.badmintonappreservations.lib.Reservation;
import si.fri.rso.badmintonappreservations.lib.User;
import si.fri.rso.badmintonappreservations.models.converters.ReservationConverter;
import si.fri.rso.badmintonappreservations.models.entities.ReservationEntity;
import si.fri.rso.badmintonappreservations.services.config.RestProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class ReservationBean {

    private Logger log = Logger.getLogger(ReservationBean.class.getName());

    @Inject
    @DiscoverService(value = "badmiton-app-users-service", version = "1.0.0", environment = "dev")
    private Optional<WebTarget> targetUser;

    @Inject
    @DiscoverService(value = "badmiton-app-courts-service", version = "1.0.0", environment = "dev")
    private Optional<WebTarget> targetCourt;

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @Inject
    private RestProperties restProperties;

    @Inject
    private EntityManager em;

    @PostConstruct
    private void init() {
        httpClient = HttpClientBuilder.create().build();
        objectMapper = new ObjectMapper();
    }

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

        log.log(Level.INFO,String.valueOf(restProperties.getUserDiscovery()));
        if(targetUser.isPresent()&&restProperties.getUserDiscovery()){
            User usr = getUserFromService(res.getUser());
            if(usr!=null)
                res.setUserObj(usr);
        }

        if(targetCourt.isPresent()&&restProperties.getUserDiscovery()){
            Court crt = getCourtFromService(res.getCourt());
            if(crt!=null)
                res.setCourtObj(crt);
        }

        return res;
    }

    public User getUserFromService(Integer id){
        WebTarget service = targetUser.get().path("v1/users");
        log.log(Level.INFO,String.valueOf(service.getUri()));
        try {
            HttpGet request = new HttpGet(String.valueOf(service.getUri()) + "?filter=id:EQ:" + id);

            HttpResponse response = httpClient.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                if (entity != null)
                    return getOneUser(EntityUtils.toString(entity));
            } else {
                String msg = "Remote server '"  + "' is responded with status " + status + ".";
                log.log(Level.SEVERE,msg);
                return null;
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());

            return null;
        }
        return null;
    }

    public User getOneUser(String json){
        try {
            ArrayList<User> ar = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
            if(ar.size()>0)
                return ar.get(0);
        } catch (JsonProcessingException e) {
            log.log(Level.SEVERE,e.getMessage());
            return null;
        }
        return null;
    }

    public Court getCourtFromService(Integer id){
        WebTarget service = targetCourt.get().path("v1/courts");
        log.log(Level.INFO,String.valueOf(service.getUri()));
        try {
            HttpGet request = new HttpGet(String.valueOf(service.getUri()) + "?filter=id:EQ:" + id);

            HttpResponse response = httpClient.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                if (entity != null)
                    return getOneCourt(EntityUtils.toString(entity));
            } else {
                String msg = "Remote server '"  + "' is responded with status " + status + ".";
                log.log(Level.SEVERE,msg);
                return null;
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());

            return null;
        }
        return null;
    }

    public Court getOneCourt(String json){
        try {
            ArrayList<Court> ar = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Court.class));
            if(ar.size()>0)
                return ar.get(0);
        } catch (JsonProcessingException e) {
            log.log(Level.SEVERE,e.getMessage());
            return null;
        }
        return null;
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