package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import osa.dev.petproject.models.PreprocPointType;
import osa.dev.petproject.models.db.PreprocPoint;
import osa.dev.petproject.repository.PreprocRepository;

import java.util.ArrayList;

@Service
public class SampleAchievablePosPointsService {

    @Value(value = "${optimization.velocity}")
    private Double velocity;
    @Value(value = "${optimization.maxtime}")
    private Double maxTime;
    private final PreprocRepository preprocRepository;

    @Autowired
    public SampleAchievablePosPointsService(PreprocRepository preprocRepository) {
        this.preprocRepository = preprocRepository;
    }

    public ArrayList<PreprocPoint> getAchievablePosPoints(Integer roadmapId) {
        return preprocRepository.getPointsCloserThan(velocity*maxTime, roadmapId, PreprocPointType.POS_POINT);
    }
}
