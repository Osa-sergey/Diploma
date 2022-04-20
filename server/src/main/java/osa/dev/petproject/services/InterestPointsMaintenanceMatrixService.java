package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import osa.dev.petproject.models.Coord;
import osa.dev.petproject.models.PreprocPointType;
import osa.dev.petproject.models.db.Maintenance;
import osa.dev.petproject.models.db.MaintenanceNumber;
import osa.dev.petproject.models.db.Optimization;
import osa.dev.petproject.models.db.PreprocPoint;
import osa.dev.petproject.repository.MaintenanceNumberRepository;
import osa.dev.petproject.repository.MaintenanceRepository;
import osa.dev.petproject.repository.PreprocRepository;
import osa.dev.petproject.tools.DistHelper;

import java.util.ArrayList;

@Service
public class InterestPointsMaintenanceMatrixService {

    private final PreprocRepository preprocRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceNumberRepository maintenanceNumberRepository;
    @Autowired
    public InterestPointsMaintenanceMatrixService(PreprocRepository preprocRepository,
                                                  MaintenanceRepository maintenanceRepository,
                                                  MaintenanceNumberRepository maintenanceNumberRepository) {
        this.preprocRepository = preprocRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceNumberRepository = maintenanceNumberRepository;
    }

    public void calculateMaintenanceMatrix(ArrayList<PreprocPoint> reachablePosPoints,
                                             Optimization optimization) {
        ArrayList<PreprocPoint> interestPoints = preprocRepository.
                                          getPreprocPointByTypeAndRoadmapID(PreprocPointType.INTEREST_POINT, optimization.getRoadmapId());
        for (PreprocPoint posPoint: reachablePosPoints) {
            int counter = 0;
            for (PreprocPoint interestPoint: interestPoints) {
                Coord posPointCoord = new Coord(posPoint.getLat(), posPoint.getLon());
                Coord interestPointCoord = new Coord(interestPoint.getLat(), interestPoint.getLon());
                if(DistHelper.getDist(posPointCoord, interestPointCoord) <= optimization.getAsRad()) {
                    counter++;
                    Maintenance maintenance = new Maintenance();
                    maintenance.setInterestPointId(interestPoint.getPointId());
                    maintenance.setPosPointId(posPoint.getPointId());
                    maintenance.setRoadmapId(optimization.getRoadmapId());
                    maintenance.setIsMaintenance(1);
                    maintenanceRepository.save(maintenance);
                } else {
                    Maintenance maintenance = new Maintenance();
                    maintenance.setInterestPointId(interestPoint.getPointId());
                    maintenance.setPosPointId(posPoint.getPointId());
                    maintenance.setRoadmapId(optimization.getRoadmapId());
                    maintenance.setIsMaintenance(0);
                    maintenanceRepository.save(maintenance);
                }
            }
            MaintenanceNumber number = new MaintenanceNumber();
            number.setPosPointId(posPoint.getPointId());
            number.setRoadmapId(optimization.getRoadmapId());
            number.setNumber(counter);
            maintenanceNumberRepository.save(number);
        }
    }
}
