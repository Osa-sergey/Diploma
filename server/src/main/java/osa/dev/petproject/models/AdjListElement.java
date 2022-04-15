package osa.dev.petproject.models;

import lombok.Data;
import osa.dev.petproject.tools.Pair;

import java.util.ArrayList;

@Data
public class AdjListElement {

    private Long elementId;
    private ArrayList<Pair<Long, Double>> adjElements = new ArrayList<>();

    public void addAdjElement(Pair<Long, Double> element) {
        adjElements.add(element);
    }
}
