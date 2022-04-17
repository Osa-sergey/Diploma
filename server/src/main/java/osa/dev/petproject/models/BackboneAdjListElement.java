package osa.dev.petproject.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class BackboneAdjListElement {

    private Long elementId;
    private Double distFromHB;
    private ArrayList<Long> adjElements = new ArrayList<>();

    public void addAdjElement(Long element) {
        adjElements.add(element);
    }
}
