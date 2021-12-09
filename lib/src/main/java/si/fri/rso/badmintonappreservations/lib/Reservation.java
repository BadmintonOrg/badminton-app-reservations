package si.fri.rso.badmintonappreservations.lib;

import java.util.Date;

public class Reservation {

    private Integer id;

    private Integer duration;

    private Integer user;

    private Integer court;

    private Date dateCreated;

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

    public Integer getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public Integer getCourt() {
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
