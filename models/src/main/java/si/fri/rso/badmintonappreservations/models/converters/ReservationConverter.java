package si.fri.rso.badmintonappreservations.models.converters;

import si.fri.rso.badmintonappreservations.lib.Reservation;
import si.fri.rso.badmintonappreservations.models.entities.ReservationEntity;

public class ReservationConverter {

    public static Reservation toDto(ReservationEntity entity) {

        Reservation dto = new Reservation();
        dto.setCourt(entity.getCourt());
        dto.setDateCreated(entity.getDateCreated());
        dto.setDuration(entity.getDuration());
        dto.setId(entity.getId());
        dto.setDateReserved(entity.getDateReserved());
        dto.setUser(entity.getUser());

        return dto;

    }

    public static ReservationEntity toEntity(Reservation dto) {

        ReservationEntity entity = new ReservationEntity();
        entity.setCourt(dto.getCourt());
        entity.setId(dto.getId());
        entity.setDateReserved(dto.getDateReserved());
        entity.setDuration(dto.getDuration());
        entity.setDateCreated(dto.getDateCreated());
        entity.setUser(dto.getUser());
        return entity;

    }
}
