package osa.dev.petproject.services;

import org.springframework.stereotype.Service;
import osa.dev.petproject.models.AdjListElement;
import osa.dev.petproject.tools.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class DijkstraService {

    public Pair<ArrayList<Double>, ArrayList<Integer>> dijkstraAlg(ArrayList<AdjListElement> adjList, Long hbId) {
        ArrayList<Double> dist = new ArrayList<>();
        ArrayList<Integer> path = new ArrayList<>();
        ArrayList<Boolean> check = new ArrayList<>();
        Map<Long, Integer> map = new HashMap<>();
        int count = adjList.size();
        for ( int i = 0; i < count; i++) {
            check.add(false);
            Long vert = adjList.get(i).getElementId();
            map.put(vert, i);
            if(vert.equals(hbId)) {
                dist.add(0.);
                path.add(i);
            }else {
                dist.add(Double.MAX_VALUE);
                path.add(Integer.MAX_VALUE);
            }
        }
        for (int i = 0; i < count; i++) {
            int curIndex = searchMinU(dist, check);
            if(dist.get(curIndex) == Double.MAX_VALUE) {
                break;
            }
            check.set(curIndex, true);
            ArrayList<Pair<Long, Double>> line = adjList.get(curIndex).getAdjElements();
            for(int j = 0; j < line.size(); j++) {
                int index = map.get(line.get(j).getFirst());
                double newDist = dist.get(curIndex) + line.get(j).getSecond();
                if(!check.get(index) && dist.get(index) > newDist) {
                    dist.set(index, newDist);
                    path.set(index, curIndex);
                }
            }
        }
        return new Pair<>(dist, path);
    }

    private int searchMinU(ArrayList<Double> dist, ArrayList<Boolean> check) {
        int index = 0;
        Double minD = Double.MAX_VALUE;
        for(int i = 0; i < dist.size(); i++){
            if(!check.get(i) && dist.get(i) <= minD) {// = нужно в тех случаях, когда остались только недостижимые точки
                minD = dist.get(i);
                index = i;
            }
        }
        return index;
    }
}
