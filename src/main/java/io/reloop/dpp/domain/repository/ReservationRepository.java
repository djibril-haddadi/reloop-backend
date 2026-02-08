package io.reloop.dpp.domain.repository;

import io.reloop.dpp.domain.model.Reservation;
import io.reloop.dpp.domain.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByCompany_IdOrderByCreatedAtDesc(UUID companyId);

    List<Reservation> findByCompany_IdAndStatusOrderByCreatedAtDesc(UUID companyId, ReservationStatus status);

    List<Reservation> findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(String customerEmail);

    List<Reservation> findByCustomerEmailIgnoreCaseAndStatusOrderByCreatedAtDesc(String customerEmail, ReservationStatus status);
}
