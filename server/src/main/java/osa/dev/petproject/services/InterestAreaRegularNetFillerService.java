package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import osa.dev.petproject.models.Coord;
import osa.dev.petproject.models.PreprocPointType;
import osa.dev.petproject.models.db.InputPoint;
import osa.dev.petproject.models.db.PreprocPoint;
import osa.dev.petproject.repository.PreprocRepository;
import osa.dev.petproject.tools.DistHelper;
import osa.dev.petproject.tools.Pair;

import java.util.ArrayList;

@Service
public class InterestAreaRegularNetFillerService {

    @Value("${optimization.areafreq}")
    private Double step;
    @Value("${preproc.points.start.id}")
    private Double firstId;
    private final PreprocRepository preprocRepository;
    private final IsPointInAreaService checkBelongingService;

    public InterestAreaRegularNetFillerService(PreprocRepository preprocRepository,
                                               IsPointInAreaService checkBelongingService) {
        this.preprocRepository = preprocRepository;
        this.checkBelongingService = checkBelongingService;
    }

    public void fillArea(ArrayList<InputPoint> area, Integer roadmapId) {
        Pair<Coord, Coord> boundingBox = getBoundingBox(area);
        ArrayList<Coord> points = getPointsInBoundingBox(boundingBox);
        points.removeIf(point -> !checkBelongingService.isBelonging(area, point));
        savePoints(points, roadmapId);
    }

    private Pair<Coord, Coord> getBoundingBox(ArrayList<InputPoint> area) {
        double minLat = 90L, maxLat = -90L, minLon = 180L, maxLon = -180L;
        for (InputPoint point: area) {
            double pointLat = point.getLat();
            double pointLon = point.getLon();
            if(pointLat >= maxLat) {
                maxLat = pointLat;
            }
            if(pointLat <= minLat) {
                minLat = pointLat;
            }
            if(pointLon >= maxLon) {
                maxLon = pointLon;
            }
            if(pointLon <= minLon) {
                minLon = pointLon;
            }
        }
        Coord leftTop = new Coord(maxLat, minLon);
        Coord rightBottom = new Coord(minLat, maxLon);
        return new Pair<>(leftTop, rightBottom);
    }

    private ArrayList<Coord> getPointsInBoundingBox(Pair<Coord, Coord> boundingBox) {
        ArrayList<Coord> points = new ArrayList<>();
        Coord leftBottom =  new Coord(boundingBox.getSecond().getLat(), boundingBox.getFirst().getLon());
        Coord rightTop = new Coord(boundingBox.getFirst().getLat(), boundingBox.getSecond().getLon());
        double height = DistHelper.getDist(boundingBox.getFirst(), leftBottom);
        double width = DistHelper.getDist(boundingBox.getFirst(), rightTop);
        int heightPointNumber = (int) Math.floor(height / step);
        int widthPointNumber = (int) Math.floor(width / step);
        double starLat = boundingBox.getFirst().getLat();
        double starLon = boundingBox.getFirst().getLon();
        double latStep = (boundingBox.getFirst().getLat() - leftBottom.getLat()) * step / height;
        double lonStep = (rightTop.getLon() - boundingBox.getFirst().getLon()) * step / width;
        for(int i = 0; i <= heightPointNumber; i++) {
            for(int j = 0; j <= widthPointNumber; j++) {
                Coord point = new Coord(starLat - i * latStep, starLon + j * lonStep);
                points.add(point);
            }
        }
        return points;
    }

    private void savePoints(ArrayList<Coord> coords, Integer roadmapId) {
        long pointId = (long) (firstId - preprocRepository.countByRoadmapID(roadmapId));
        ArrayList<PreprocPoint> preprocPointList = new ArrayList<>();
        for (Coord coord : coords) {
            PreprocPoint preprocPoint = new PreprocPoint();
            preprocPoint.setRoadmapID(roadmapId);
            preprocPoint.setType(PreprocPointType.INTEREST_POINT);
            preprocPoint.setLat(coord.getLat());
            preprocPoint.setLon(coord.getLon());
            preprocPoint.setPointId(pointId);
            preprocPointList.add(preprocPoint);
            pointId--;
        }
        preprocRepository.saveAll(preprocPointList);
    }
}
