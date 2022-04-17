package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import osa.dev.petproject.models.InputPointType;
import osa.dev.petproject.models.db.InputPoint;
import osa.dev.petproject.repository.InputPointRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class InterestAreaRegularNetService {

    private final InputPointRepository inputPointRepository;
    private final InterestAreaRegularNetFillerService fillerService;

    @Autowired
    public InterestAreaRegularNetService(InputPointRepository inputPointRepository,
                                         InterestAreaRegularNetFillerService fillerService) {
        this.inputPointRepository = inputPointRepository;
        this.fillerService = fillerService;
    }

    public void createRegularNet(Integer roadmapId) {
        ArrayList<ArrayList<InputPoint>> areas = getAreas(roadmapId);
        for (ArrayList<InputPoint> area : areas) {
            fillerService.fillArea(area, roadmapId);
        }
    }

    private ArrayList<ArrayList<InputPoint>> getAreas(Integer roadmapId) {
        List<InputPoint> points = inputPointRepository.findByRoadmapIdAndType(roadmapId, InputPointType.INTEREST_AREA);
        //так как считывание в рамках одной зоны проходило последовательно
        // и по структуре файла точки зоны идут друг за другом, то мы можем использовать данную особенность
        // сохраним правильный порядок в массиве
        ArrayList<ArrayList<InputPoint>> res = new ArrayList<>();
        ArrayList<InputPoint> area = new ArrayList<>();
        area.add(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            InputPoint tmp = points.get(i);
            if(Objects.equals(tmp.getPrePoint(), area.get(area.size() - 1).getPointId())) {
                area.add(tmp);
            } else {
                res.add(area);
                area = new ArrayList<>();
                area.add(tmp);
            }
        }
        res.add(area);
        return res;
    }
}
