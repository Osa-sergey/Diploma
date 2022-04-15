package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import osa.dev.petproject.models.AdjListElement;
import osa.dev.petproject.models.InputPointType;
import osa.dev.petproject.models.db.InputPoint;
import osa.dev.petproject.models.db.Optimization;
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

    @Autowired
    public PreprocOptimizationService(RoadmapPointRepository roadmapPointRepository,
                                      InputPointRepository inputPointRepository,
                                      DijkstraService dijkstraService) {
        this.roadmapPointRepository = roadmapPointRepository;
        this.inputPointRepository = inputPointRepository;
        this.dijkstraService = dijkstraService;
    }

    public void preproc(Optimization opt) {
        ArrayList<AdjListElement> adjList = getRoadmapAdjList(opt.getRoadmapId());
        InputPoint hb = inputPointRepository.findByRoadmapIdAndType(opt.getRoadmapId(), InputPointType.HOME_BASE).get(0);
        Long hbId = hb.getPointId();
        //возвращает dist и path относительно порядка элементов в adjList
        Pair<ArrayList<Double>, ArrayList<Integer>> dijkstraRes = dijkstraService.dijkstraAlg(adjList, hbId);
        System.out.println("");
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
