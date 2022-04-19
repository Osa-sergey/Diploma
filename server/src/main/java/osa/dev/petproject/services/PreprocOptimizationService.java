package osa.dev.petproject.services;

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

@Service
public class PreprocOptimizationService {

    private final RoadmapPointRepository roadmapPointRepository;
    private final InputPointRepository inputPointRepository;
    private final DijkstraService dijkstraService;
    private final BackboneService backboneService;
    private final RoadmapRegularNetService roadmapRegularNetService;
    private final InterestAreaRegularNetService areaRegularNetService;
    private final SampleAchievablePosPointsService achievablePosPointsService;
    private final InterestPointsMaintenanceMatrixService maintenanceMatrixService;

    @Autowired
    public PreprocOptimizationService(RoadmapPointRepository roadmapPointRepository,
                                      InputPointRepository inputPointRepository,
                                      DijkstraService dijkstraService,
                                      BackboneService backboneService,
                                      RoadmapRegularNetService roadmapRegularNetService,
                                      InterestAreaRegularNetService areaRegularNetService,
                                      SampleAchievablePosPointsService achievablePosPointsService,
                                      InterestPointsMaintenanceMatrixService maintenanceMatrixService) {
        this.roadmapPointRepository = roadmapPointRepository;
        this.inputPointRepository = inputPointRepository;
        this.dijkstraService = dijkstraService;
        this.backboneService = backboneService;
        this.roadmapRegularNetService = roadmapRegularNetService;
        this.areaRegularNetService = areaRegularNetService;
        this.achievablePosPointsService = achievablePosPointsService;
        this.maintenanceMatrixService = maintenanceMatrixService;
    }

    public void preproc(Optimization opt) {
        Integer roadmapId = opt.getRoadmapId();
        ArrayList<AdjListElement> adjList = getRoadmapAdjList(roadmapId);
        InputPoint hb = inputPointRepository.findByRoadmapIdAndType(roadmapId, InputPointType.HOME_BASE).get(0);
        Long hbId = hb.getPointId();
        //возвращает dist и path относительно порядка элементов в adjList
        Pair<ArrayList<Double>, ArrayList<Integer>> dijkstraRes = dijkstraService.dijkstraAlg(adjList, hbId);
        ArrayList<BackboneAdjListElement> backbone = backboneService.createBackbone(adjList, dijkstraRes);
        roadmapRegularNetService.createRegularNet(backbone, adjList, roadmapId);
        areaRegularNetService.createRegularNet(roadmapId);
        ArrayList<PreprocPoint> achievablePosPoints = achievablePosPointsService.getAchievablePosPoints(roadmapId);
        maintenanceMatrixService.maintenanceMatrixCalculation(achievablePosPoints, opt);
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
}
