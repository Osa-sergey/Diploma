package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import osa.dev.petproject.models.Coord;
import osa.dev.petproject.models.PreprocPointType;
import osa.dev.petproject.models.db.*;
import osa.dev.petproject.repository.PreprocRepository;
import osa.dev.petproject.repository.ReachabilityBSRepository;
import osa.dev.petproject.repository.ReachabilityHQRepository;
import osa.dev.petproject.tools.DistHelper;

import java.util.ArrayList;

@Service
public class ReachabilityPosPointsService {

    @Value(value = "${optimization.velocity}")
    private Double velocity;
    @Value(value = "${optimization.maxtime}")
    private Double maxTime;
    private final PreprocRepository preprocRepository;
    private final ReachabilityBSRepository bsRepository;
    private final ReachabilityHQRepository hqRepository;

    @Autowired
    public ReachabilityPosPointsService(PreprocRepository preprocRepository,
                                        ReachabilityBSRepository bsRepository, ReachabilityHQRepository hqRepository) {
        this.preprocRepository = preprocRepository;
        this.bsRepository = bsRepository;
        this.hqRepository = hqRepository;
    }

    public ArrayList<PreprocPoint> getReachablePosPoints(Integer roadmapId) {
        return preprocRepository.getPointsCloserThan(velocity*maxTime, roadmapId, PreprocPointType.POS_POINT);
    }

    public void calculateBSReachabilityMatrix(ArrayList<PreprocPoint> posPoints, Optimization optimization) {
        for (int i = 0; i < posPoints.size(); i++) {
            Coord posPoint1Coord = new Coord(posPoints.get(i).getLat(), posPoints.get(i).getLon());
            for (int j = 0; j < posPoints.size(); j++) {
                Coord posPoint2Coord = new Coord(posPoints.get(j).getLat(), posPoints.get(j).getLon());
                if(DistHelper.getDist(posPoint1Coord, posPoint2Coord) <= optimization.getBsRad()) {
                    ReachabilityBS reachability = new ReachabilityBS();
                    reachability.setPosPointId1(posPoints.get(i).getPointId());
                    reachability.setPosPointId2(posPoints.get(j).getPointId());
                    reachability.setRoadmapId(optimization.getRoadmapId());
                    reachability.setIsReachable(1);
                    bsRepository.save(reachability);
                } else {
                    ReachabilityBS reachability = new ReachabilityBS();
                    reachability.setPosPointId1(posPoints.get(i).getPointId());
                    reachability.setPosPointId2(posPoints.get(j).getPointId());
                    reachability.setRoadmapId(optimization.getRoadmapId());
                    reachability.setIsReachable(0);
                    bsRepository.save(reachability);
                }
            }
        }
    }

    public void calculateHQReachabilityVector(ArrayList<PreprocPoint> posPoints,
                                              InputPoint hq,
                                              Optimization optimization) {
        Coord hqCoord = new Coord(hq.getLat(), hq.getLon());
        for (PreprocPoint point: posPoints) {
            Coord pointCoord = new Coord(point.getLat(), point.getLon());
            if(DistHelper.getDist(hqCoord, pointCoord) <= optimization.getAsRad()){
                ReachabilityHQ reachability = new ReachabilityHQ();
                reachability.setPosPointId(point.getPointId());
                reachability.setRoadmapId(optimization.getRoadmapId());
                reachability.setIsReachable(1);
                hqRepository.save(reachability);
            } else {
                ReachabilityHQ reachability = new ReachabilityHQ();
                reachability.setPosPointId(point.getPointId());
                reachability.setRoadmapId(optimization.getRoadmapId());
                reachability.setIsReachable(0);
                hqRepository.save(reachability);
            }
        }
    }
}
