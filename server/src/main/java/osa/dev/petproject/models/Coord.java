package osa.dev.petproject.models;

import lombok.Data;

@Data
public class Coord {

    private Double lat;
    private Double lon;

    @Override
    public String toString() {
        return "Coord{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
