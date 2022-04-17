package osa.dev.petproject.tools;

import osa.dev.petproject.models.Coord;

import java.util.Map;

public class DistHelper {

    public static Double getDist(Coord coord1, Coord coord2) {
        Coord rad1 = new Coord(degToRad(coord1.getLat()), degToRad(coord1.getLon()));
        Coord rad2 = new Coord(degToRad(coord2.getLat()), degToRad(coord2.getLon()));
        double s = Math.acos(Math.sin(rad1.getLat()) * Math.sin(rad2.getLat()) +
                Math.cos(rad1.getLat()) * Math.cos(rad2.getLat()) * Math.cos(rad1.getLon() - rad2.getLon()));
        return 6371 * s;
    }

    private static Double degToRad(Double deg) {
        return deg * Math.PI / 180;
    }

    public static Pair<Long, Long> getNeibsForHb(Coord hb, Map<Long, Coord> nodes) {
        Long first = 0L, second = 0L;
        Double dist = Double.MAX_VALUE, curDist;
        for (Map.Entry<Long, Coord> entry : nodes.entrySet()) {
            curDist = getDist(hb, entry.getValue());
            if(dist > curDist){
                dist = curDist;
                second = first;
                first = entry.getKey();
            }
        }
        return new Pair<>(first, second);
    }
}
