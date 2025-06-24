package server.storage;

import server.model.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<Statistic, Integer> {
    List<Statistic> findAllByUriInAndTimestampBetween(List<String> uri, LocalDateTime start, LocalDateTime end);
}
