package osa.dev.petproject.services;

import org.springframework.stereotype.Service;
import osa.dev.petproject.models.Coord;
import osa.dev.petproject.models.db.InputPoint;
import osa.dev.petproject.tools.Pair;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

@Service
public class IsPointInAreaService {

    private final Random rnd = new Random();

    public boolean isBelonging(ArrayList<InputPoint> area, Coord point) {
        int count = 0;
        Pair<Coord, Coord> ray = getRandomRay(point);
        for (int i = 0; i < area.size()-1; i++) {
            Coord firstCoord = new Coord(area.get(i).getLat(), area.get(i).getLon());
            Coord secondCoord = new Coord(area.get(i+1).getLat(), area.get(i+1).getLon());
            Pair<Coord, Coord> edge = new Pair<>(firstCoord, secondCoord);
            int res = isCrossing(ray, edge);
            if (res == 2) return true;
            count += res;
        }
        return count % 2 != 0;
    }

    private Pair<Coord, Coord> getRandomRay(Coord point) {
        Coord rndPoint = new Coord();
        boolean sign = rnd.nextBoolean();
        boolean sign1 = rnd.nextBoolean();
        double latDelta = sign ? 0.001 + Math.random() : - 0.001 - Math.random();
        double lonDelta = sign1 ? 0.001 + Math.random() : - 0.001 - Math.random();
        rndPoint.setLat(point.getLat() + latDelta);
        rndPoint.setLon(point.getLon() + lonDelta);
        return new Pair<>(point, rndPoint);
    }

    public int isCrossing(Pair<Coord, Coord> ray, Pair<Coord, Coord> edge) {
        double x1 = ray.getFirst().getLat();
        double x2 = ray.getSecond().getLat();
        double x3 = edge.getFirst().getLat();
        double x4 = edge.getSecond().getLat();
        double y1 = ray.getFirst().getLon();
        double y2 = ray.getSecond().getLon();
        double y3 = edge.getFirst().getLon();
        double y4 = edge.getSecond().getLon();
        double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if(denominator < 0.0001) {
            // параллельны или совпадают
            if(Objects.equals(ray.getFirst().getLat(), edge.getFirst().getLat())) {
                return 2;
            } else {
                return 0;
            }
        } else {
            //пересекаются
            double p1 = ((x1*y2 - y1*x2)*(x3 - x4) - (x1 - x2)*(x3*y4 - y3*x4)) / denominator;
            double p2 = ((x1*y2 - y1*x2)*(y3 - y4) - (y1 - y2)*(x3*y4 - y3*x4)) / denominator;
            //проверка принадлежности лучу
            double xDir = x2 - x1;
            double yDir = y2 - y1;
            if(xDir >= 0 && p1 < x1) return 0;
            if(xDir < 0 && p1 > x1) return 0;
            if(yDir >= 0 && p2 < y1) return 0;
            if(yDir < 0 && p2 > y1) return 0;
            // проверка принадлежности отрезку
            if((p1 >= x3 && p1 <= x4 || p1 >= x4 && p1 <= x3)
                    && (p2 >= y3 && p2 <= y4 || p2 >= y4 && p2 <= y3)) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
