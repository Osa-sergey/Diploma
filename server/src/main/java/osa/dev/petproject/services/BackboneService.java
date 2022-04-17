package osa.dev.petproject.services;

import org.springframework.stereotype.Service;
import osa.dev.petproject.models.AdjListElement;
import osa.dev.petproject.models.BackboneAdjListElement;
import osa.dev.petproject.tools.Pair;

import java.util.ArrayList;

@Service
public class BackboneService {

    public ArrayList<BackboneAdjListElement> createBackbone(ArrayList<AdjListElement> adjList,
                                                            Pair<ArrayList<Double>, ArrayList<Integer>> dijkstraRes) {
        ArrayList<BackboneAdjListElement> backbone = new ArrayList<>();
        for (int i = 0; i < adjList.size(); i++) {
            Long elementId = adjList.get(i).getElementId();
            BackboneAdjListElement backboneElement = new BackboneAdjListElement();
            backboneElement.setElementId(elementId);
            backboneElement.setDistFromHB(dijkstraRes.getFirst().get(i));
            backbone.add(backboneElement);
        }
        for (int i = 0; i < adjList.size(); i++) {
            int j = i;
            while (dijkstraRes.getSecond().get(j) != j) {
                Integer next = dijkstraRes.getSecond().get(j);
                if(next == Integer.MAX_VALUE) {
                    break;
                }
                Long p1 = adjList.get(j).getElementId();
                Long p2 = adjList.get(next).getElementId();
                if(!backbone.get(j).getAdjElements().contains(p2)) {
                    backbone.get(j).addAdjElement(p2);
                    backbone.get(next).addAdjElement(p1);
                }
                j = next;
            }
        }
        backbone = cleanFromUnreachable(backbone);
        return backbone;
    }

    private ArrayList<BackboneAdjListElement> cleanFromUnreachable(ArrayList<BackboneAdjListElement> backbone) {
         backbone.removeIf(i -> i.getDistFromHB() == Double.MAX_VALUE);
         return backbone;
    }
}
