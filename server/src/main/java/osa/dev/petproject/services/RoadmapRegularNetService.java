package osa.dev.petproject.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import osa.dev.petproject.models.AdjListElement;
import osa.dev.petproject.models.BackboneAdjListElement;
import osa.dev.petproject.models.Coord;
import osa.dev.petproject.models.db.RoadmapPointCoord;
import osa.dev.petproject.repository.RoadmapPointCoordRepository;
import osa.dev.petproject.tools.Pair;

@Service
public class RoadmapRegularNetService {

    private final RoadmapPointCoordRepository coordRepository;
    private final RoadmapRegularNetFillerService fillerService;

    @Autowired
    public RoadmapRegularNetService(RoadmapPointCoordRepository coordRepository,
                                    RoadmapRegularNetFillerService fillerService) {
        this.coordRepository = coordRepository;
        this.fillerService = fillerService;
    }

    public void createRegularNet(ArrayList<BackboneAdjListElement> backbone,
                              ArrayList<AdjListElement> adjList,
                              Integer roadmapId) {
        Map<Long, Coord> coordMap = new HashMap<>();
        Map<Long, Integer> backboneMap = new HashMap<>();
        for (RoadmapPointCoord coord: coordRepository.findRoadmapPointCoordByRoadmapId(roadmapId)) {
            coordMap.put(coord.getPointId(), new Coord(coord.getLat(), coord.getLon()));
        }
        for (int i = 0; i < backbone.size(); i++) {
            backboneMap.put(backbone.get(i).getElementId(), i);
        }
        fillRegularNet(backbone, backboneMap, adjList, coordMap, roadmapId);
    }

    private void fillRegularNet(ArrayList<BackboneAdjListElement> backbone,
                                Map<Long, Integer> backboneMap,
                                ArrayList<AdjListElement> adjList,
                                Map<Long, Coord> coordMap,
                                Integer roadmapId) {
        Set<Pair<Long, Long>> processedEdges = new HashSet<>();
        for (BackboneAdjListElement e: backbone) {
            Coord p1Coord = coordMap.get(e.getElementId());
            Double p1Dist = e.getDistFromHB();
            for (Long p2: e.getAdjElements()) {
                Coord p2Coord = coordMap.get(p2);
                Double p2Dist = backbone.get(backboneMap.get(p2)).getDistFromHB();
                if(p1Dist < p2Dist){
                    fillerService.backboneEdgeFill(p1Coord, p1Dist, p2Coord, p2Dist, roadmapId);
                }
                processedEdges.add(new Pair<>(e.getElementId(), p2));
            }
        }
        ArrayList<Pair<Double, Coord>> terminalPoints = new ArrayList<>();
        for (AdjListElement e: adjList) {
            Long p1 = e.getElementId();
            if(e.getAdjElements().size() == 1) {
                double distFromHb = backbone.get(backboneMap.get(p1)).getDistFromHB();
                terminalPoints.add(new Pair<>(distFromHb, coordMap.get(p1)));
            }
            for (Pair<Long, Double> neib: e.getAdjElements()) {
                Long p2 = neib.getFirst();
                Pair<Long, Long> edge = new Pair<>(p1, p2);
                if(!processedEdges.contains(edge)) {
                    Coord p1Coord = coordMap.get(p1);
                    Coord p2Coord = coordMap.get(p2);
                    Double p1Dist = backbone.get(backboneMap.get(p1)).getDistFromHB();
                    Double p2Dist = backbone.get(backboneMap.get(p2)).getDistFromHB();
                    fillerService.noBackboneEdgeFill(p1Coord, p1Dist, p2Coord, p2Dist, neib.getSecond(), roadmapId);
                    processedEdges.add(edge);
                    processedEdges.add(new Pair<>(p2, p1));
                }
            }
        }
        fillerService.savePoints(terminalPoints, roadmapId);
    }
}
