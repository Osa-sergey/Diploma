package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import osa.dev.petproject.models.Coord;
import osa.dev.petproject.models.PreprocPointType;
import osa.dev.petproject.models.db.PreprocPoint;
import osa.dev.petproject.repository.PreprocRepository;
import osa.dev.petproject.tools.Pair;

import java.util.ArrayList;

@Service
public class RoadmapRegularNetFillerService {

    @Value("${optimization.roadfreq}")
    private Double step;
    @Value("${preproc.points.start.id}")
    private Double firstId;
    private final PreprocRepository preprocRepository;

    @Autowired
    public RoadmapRegularNetFillerService(PreprocRepository preprocRepository) {
        this.preprocRepository = preprocRepository;
    }

    public void backboneEdgeFill(Coord p1Coord, Double p1Dist,
                                 Coord p2Coord, Double p2Dist,
                                 Integer roadmapId) {
        double distBetween = p2Dist - p1Dist;
        double residue = residue(p1Dist);
        double distToPoint;
        if(distBetween > residue) {
            int max = (int) Math.floor((distBetween-residue)/step);

            ArrayList<Pair<Double, Coord>> points = new ArrayList<>();
            for (int i = 0; i <= max; i++) {
                distToPoint = residue + step * i;
                Coord coord = calcCoords(p1Coord, p2Coord, distToPoint, distBetween);
                points.add(new Pair<>(p1Dist+distToPoint, coord));
            }
            savePoints(points, roadmapId);
        }
    }

    private Double residue(Double dist) {
        return dist != 0 ? Math.abs(dist - (Math.floor(dist/step) + 1) * step) : 0;
    }

    public void noBackboneEdgeFill(Coord p1Coord, Double p1Dist,
                                   Coord p2Coord, Double p2Dist,
                                   Double distBetween, Integer roadmapId) {
        double p1Residue = residue(p1Dist);
        double p2Residue = residue(p2Dist);
        ArrayList<Pair<Double, Coord>> points = new ArrayList<>();
        double dist;
        Coord coord;
        if(distBetween > p1Residue + p2Residue){
            double allResDist = distBetween - p1Residue - p2Residue;
            int difInSteps = (int) Math.floor(Math.abs(p1Dist - p2Dist)/step);
            double comResDist = allResDist - difInSteps * step;
            int comSteps = (int) Math.floor(comResDist / step);
            comSteps /= 2;
            int added = (p1Dist <= p2Dist) ? difInSteps : 0;
            for (int i = 0; i <= comSteps + added; i++) {
                double distToPoint = p1Residue + step * i;
                coord = calcCoords(p1Coord, p2Coord, distToPoint, distBetween);
                points.add(new Pair<>(p1Dist+distToPoint, coord));
            }
            added = (p2Dist <= p1Dist) ? difInSteps : 0;
            for (int i = 0; i <= comSteps+added; i++) {
                double distToPoint = p2Residue + step * i;
                coord = calcCoords(p2Coord, p1Coord, distToPoint, distBetween);
                points.add(new Pair<>(p2Dist+distToPoint, coord));
            }
        } else if (distBetween == p1Residue + p2Residue) {
            dist = p1Dist + p1Residue;
            coord = calcCoords(p1Coord, p2Coord, p1Residue, distBetween);
            points.add(new Pair<>(dist, coord));
        } else if(p1Dist < p2Dist && distBetween >= p1Residue){
            dist = p1Dist + p1Residue;
            coord = calcCoords(p1Coord, p2Coord, p1Residue, distBetween);
            points.add(new Pair<>(dist, coord));
        } else if(p2Dist < p1Dist && distBetween >= p2Residue){
            dist = p2Dist + p2Residue;
            coord = calcCoords(p2Coord, p1Coord, p2Residue, distBetween);
            points.add(new Pair<>(dist, coord));
        }
        savePoints(points, roadmapId);
    }

    private Coord calcCoords(Coord p1Coord, Coord p2Coord, double distToPoint, double distBetweenp1p2) {
        double lat, lon;
        lat = p1Coord.getLat() + (distToPoint)/distBetweenp1p2 * (p2Coord.getLat() - p1Coord.getLat());
        lon = p1Coord.getLon() + (distToPoint)/distBetweenp1p2 * (p2Coord.getLon() - p1Coord.getLon());
        return new Coord(lat, lon);
    }

    public void savePoints(ArrayList<Pair<Double, Coord>> points, Integer roadmapId) {
        long pointId = (long) (firstId - preprocRepository.countByRoadmapID(roadmapId));
        ArrayList<PreprocPoint> preprocPointList = new ArrayList<>();
        for (Pair<Double, Coord> point : points) {
            PreprocPoint preprocPoint = new PreprocPoint();
            preprocPoint.setRoadmapID(roadmapId);
            preprocPoint.setType(PreprocPointType.POS_POINT);
            preprocPoint.setLat(point.getSecond().getLat());
            preprocPoint.setLon(point.getSecond().getLon());
            preprocPoint.setPointId(pointId);
            preprocPoint.setDistFromHb(point.getFirst());
            preprocPointList.add(preprocPoint);
            pointId--;
        }
        preprocRepository.saveAll(preprocPointList);
    }
}
