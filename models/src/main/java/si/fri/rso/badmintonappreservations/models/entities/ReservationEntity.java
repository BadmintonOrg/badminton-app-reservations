package si.fri.rso.badmintonappreservations.models.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "badminton_reservations")
@NamedQueries(value =
        {
                @NamedQuery(name = "ReservationEntity.getAll",
                        query = "SELECT oe FROM ReservationEntity oe")
        })
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "duration")
    private Integer duration;

    @Column(name="user_id")
    private Integer user;

    @Column(name="court_id")
    private int court;

    @Column(name="date_created")
    private Date dateCreated;

    @Column(name="date_reserved")
    private Date dateReserved;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getCourt() {
        return court;
    }

    public void setCourt(int court) {
        this.court = court;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateReserved() {
        return dateReserved;
    }

    public void setDateReserved(Date dateReserved) {
        this.dateReserved = dateReserved;
    }
}
