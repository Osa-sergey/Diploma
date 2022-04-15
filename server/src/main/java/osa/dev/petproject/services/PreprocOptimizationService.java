package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        ArrayList<ArrayList<Pair<Long, Double>>> adjList = getRoadmapAdjList(opt.getRoadmapId());
        InputPoint hb = inputPointRepository.findByRoadmapIdAndType(opt.getRoadmapId(), InputPointType.HOME_BASE).get(0);
        Long hbId = hb.getPointId();
        //возвращает dist и path относительно порядка элементов в adjList
        Pair<ArrayList<Double>, ArrayList<Integer>> dijkstraRes = dijkstraService.dijkstraAlg(adjList, hbId);
        System.out.println("");
    }

    private ArrayList<ArrayList<Pair<Long, Double>>> getRoadmapAdjList(Integer roadmapId) {
        ArrayList<ArrayList<Pair<Long, Double>>> res = new ArrayList<>();
        ArrayList<Long> distinctPoints = roadmapPointRepository.getDistinctPointIds(roadmapId);
        for (Long point: distinctPoints) {
            ArrayList<Pair<Long, Double>> line = new ArrayList<>();
            line.add(new Pair<>(point, null));
            for (RoadmapPoint p: roadmapPointRepository.findRoadmapPointByPointIdAndRoadmapId(point, roadmapId)) {
                line.add(new Pair<>(p.getNeibPointId(), p.getDist()));
            }
            res.add(line);
        }
        return res;
    }
}
