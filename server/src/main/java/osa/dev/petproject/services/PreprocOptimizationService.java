package osa.dev.petproject.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import osa.dev.petproject.models.AdjListElement;
import osa.dev.petproject.models.BackboneAdjListElement;
import osa.dev.petproject.models.InputPointType;
import osa.dev.petproject.models.db.InputPoint;
import osa.dev.petproject.models.db.Optimization;
import osa.dev.petproject.models.db.PreprocPoint;
import osa.dev.petproject.models.db.RoadmapPoint;
import osa.dev.petproject.repository.InputPointRepository;
import osa.dev.petproject.repository.RoadmapPointRepository;
import osa.dev.petproject.tools.Pair;

import java.util.ArrayList;
import java.util.Iterator;

@Service
@Log4j2
public class PreprocOptimizationService {

    private final RoadmapPointRepository roadmapPointRepository;
    private final InputPointRepository inputPointRepository;
    private final DijkstraService dijkstraService;
    private final BackboneService backboneService;
    private final RoadmapRegularNetService roadmapRegularNetService;
    private final InterestAreaRegularNetService areaRegularNetService;
    private final ReachabilityPosPointsService reachabilityPosPointsService;
    private final InterestPointsMaintenanceMatrixService maintenanceMatrixService;

    @Autowired
    public PreprocOptimizationService(RoadmapPointRepository roadmapPointRepository,
                                      InputPointRepository inputPointRepository,
                                      DijkstraService dijkstraService,
                                      BackboneService backboneService,
                                      RoadmapRegularNetService roadmapRegularNetService,
                                      InterestAreaRegularNetService areaRegularNetService,
                                      ReachabilityPosPointsService reachabilityPosPointsService,
                                      InterestPointsMaintenanceMatrixService maintenanceMatrixService) {
        this.roadmapPointRepository = roadmapPointRepository;
        this.inputPointRepository = inputPointRepository;
        this.dijkstraService = dijkstraService;
        this.backboneService = backboneService;
        this.roadmapRegularNetService = roadmapRegularNetService;
        this.areaRegularNetService = areaRegularNetService;
        this.reachabilityPosPointsService = reachabilityPosPointsService;
        this.maintenanceMatrixService = maintenanceMatrixService;
    }

    public void preproc(Optimization opt) {
        Integer roadmapId = opt.getRoadmapId();
        ArrayList<AdjListElement> adjList = getRoadmapAdjList(roadmapId);
        InputPoint hb = inputPointRepository.findByRoadmapIdAndType(roadmapId, InputPointType.HOME_BASE).get(0);
        InputPoint hq = inputPointRepository.findByRoadmapIdAndType(roadmapId, InputPointType.HQ).get(0);
        Long hbId = hb.getPointId();
        log.info("Start dijkstra algorithm");
        //возвращает dist и path относительно порядка элементов в adjList
        Pair<ArrayList<Double>, ArrayList<Integer>> dijkstraRes = dijkstraService.dijkstraAlg(adjList, hbId);
        log.info("End dijkstra algorithm. Number of vertexes: " + dijkstraRes.getFirst().size());
        log.info("Start backbone creating");
        ArrayList<BackboneAdjListElement> backbone = backboneService.createBackbone(adjList, dijkstraRes);
        log.info("End backbone creating. Number of vertexes: " + backbone.size());
        cleanAdjListFromUnreachable(adjList, dijkstraRes);
        log.info("Start roadmap net creating");
        roadmapRegularNetService.createRegularNet(backbone, adjList, roadmapId);
        log.info("Start interest areas net creating");
        areaRegularNetService.createRegularNet(roadmapId);
        ArrayList<PreprocPoint> reachablePosPoints = reachabilityPosPointsService.getReachablePosPoints(roadmapId);
        log.info("Start maintenance matrix calculating");
        maintenanceMatrixService.calculateMaintenanceMatrix(reachablePosPoints, opt);
        log.info("Start BS reachability matrix calculating");
        reachabilityPosPointsService.calculateBSReachabilityMatrix(reachablePosPoints, opt);
        log.info("Start HQ reachability matrix calculating");
        reachabilityPosPointsService.calculateHQReachabilityVector(reachablePosPoints, hq, opt);
        log.info("End preproc calculating");
    }

    private ArrayList<AdjListElement> getRoadmapAdjList(Integer roadmapId) {
        ArrayList<AdjListElement> res = new ArrayList<>();
        ArrayList<Long> distinctPoints = roadmapPointRepository.getDistinctPointIds(roadmapId);
        for (Long point: distinctPoints) {
            AdjListElement item = new AdjListElement();
            item.setElementId(point);
            for (RoadmapPoint p: roadmapPointRepository.findRoadmapPointByPointIdAndRoadmapId(point, roadmapId)) {
                item.addAdjElement(new Pair<>(p.getNeibPointId(), p.getDist()));
            }
            res.add(item);
        }
        return res;
    }

    private void cleanAdjListFromUnreachable(ArrayList<AdjListElement> adjList,
                                             Pair<ArrayList<Double>, ArrayList<Integer>> dijkstraRes) {
        Iterator<AdjListElement> adjListIterator = adjList.iterator();
        Iterator<Double> distIterator = dijkstraRes.getFirst().iterator();
        Iterator<Integer> pathIterator = dijkstraRes.getSecond().iterator();
        while(adjListIterator.hasNext()) {
            adjListIterator.next();
            pathIterator.next();
            if(distIterator.next() == Double.MAX_VALUE) {
                distIterator.remove();
                adjListIterator.remove();
                pathIterator.remove();
            }
        }
    }
}
