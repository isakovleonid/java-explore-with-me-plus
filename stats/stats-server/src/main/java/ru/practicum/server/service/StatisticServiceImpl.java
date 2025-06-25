package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.dto.output.GetStatisticDto;
import ru.practicum.server.mapper.StatisticMapper;
import ru.practicum.server.model.Statistic;
import ru.practicum.server.storage.StatisticRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository repository;
    @Autowired
    private StatisticMapper mapper;

    @Override
    public StatisticDto addHit(StatisticDto statisticDto) {
        Statistic statisticToSave = mapper.toStatistic(statisticDto);
        Statistic savedStatistic = repository.save(statisticToSave);
        log.info("Hit with uri {} was saved", statisticDto.getUri());

        return mapper.toStatisticDto(savedStatistic);
    }

    @Override
    public List<GetStatisticDto> getStatistic(String start, String end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<GetStatisticDto> statistics;

        LocalDateTime dtStart = LocalDateTime.parse(start, formatter);
        LocalDateTime dtEnd = LocalDateTime.parse(end, formatter);

        if (!uris.isEmpty()) {
            if (unique) {

                statistics = repository.findHitsByUriInAndTimestampBetweenAndDistinctIp(
                        uris,
                        dtStart,
                        dtEnd
                );
                log.info("Retrieved {} statistics (unique IPs) for uris: {} from {} to {}", statistics.size(), uris, start, end);

            } else {

                statistics = repository.findHitsByUriInAndTimestampBetween(
                        uris,
                        dtStart,
                        dtEnd
                );
                log.info("Retrieved {} statistics (all IPs) for uris: {} from {} to {}", statistics.size(), uris, start, end);
            }
        } else {

            if (unique) {

                statistics = repository.findHitsByTimestampBetweenAndDistinctIp(
                        dtStart,
                        dtEnd
                );
                log.info("Retrieved {} statistics (unique IPs) for all uris from {} to {}", statistics.size(), start, end);

            } else {

                statistics = repository.findHitsByTimestampBetween(
                        dtStart,
                        dtEnd
                );
                log.info("Retrieved {} statistics (all IPs) for all uris from {} to {}", statistics.size(), start, end);
            }
        }

        return statistics;
    }
}