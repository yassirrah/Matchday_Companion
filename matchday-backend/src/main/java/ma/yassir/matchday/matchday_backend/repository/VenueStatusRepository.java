package ma.yassir.matchday.matchday_backend.repository;

import ma.yassir.matchday.matchday_backend.domain.VenueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueStatusRepository extends JpaRepository<VenueStatus, String> {

}
