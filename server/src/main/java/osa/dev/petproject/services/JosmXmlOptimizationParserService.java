package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import osa.dev.petproject.models.Coord;
import osa.dev.petproject.models.InputPointType;
import osa.dev.petproject.models.db.InputPoint;
import osa.dev.petproject.models.db.Roadmap;
import osa.dev.petproject.models.db.RoadmapPoint;
import osa.dev.petproject.models.db.RoadmapPointCoord;
import osa.dev.petproject.repository.InputPointRepository;
import osa.dev.petproject.repository.RoadmapPointCoordRepository;
import osa.dev.petproject.repository.RoadmapPointRepository;
import osa.dev.petproject.tools.DistHelper;
import osa.dev.petproject.tools.Pair;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

@Service
public class JosmXmlOptimizationParserService {

    private final RoadmapPointRepository roadmapPointRepo;
    private final InputPointRepository inputPointRepo;
    private final RoadmapPointCoordRepository roadmapPointCoordRepo;
    private final DocumentBuilderFactory factory;

    @Autowired
    public JosmXmlOptimizationParserService(RoadmapPointRepository roadmapPointRepo,
                                            InputPointRepository inputPointRepo,
                                            RoadmapPointCoordRepository roadmapPointCoordRepo) {
        this.roadmapPointCoordRepo = roadmapPointCoordRepo;
        factory = DocumentBuilderFactory.newInstance();
        this.roadmapPointRepo = roadmapPointRepo;
        this.inputPointRepo = inputPointRepo;
    }

    public void parse(InputStream inputStream, Integer roadmapId) {
        Document doc = null;
        try {
            doc = factory.newDocumentBuilder().parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<Long, Coord> nodes = nodeParsing(doc, roadmapId);
        InputPoint hb = inputPointRepo.findByRoadmapIdAndType(roadmapId, InputPointType.HOME_BASE).get(0);
        Coord hbCoord = new Coord(hb.getLat(), hb.getLon());
        wayParsing(doc, roadmapId, nodes);
        clearNodesFromInterestAreaPoints(roadmapId, nodes);
        saveRoadmapPointCoords(roadmapId, nodes);
        Pair<Long, Long> neibsForHb = DistHelper.getNeibsForHb(hbCoord, nodes);
        addHBToRoadmapPoints(hb, neibsForHb, roadmapId);
    }

    private void clearNodesFromInterestAreaPoints(Integer roadmapId, Map<Long, Coord> nodes) {
        for (InputPoint point: inputPointRepo.findByRoadmapIdAndType(roadmapId, InputPointType.INTEREST_AREA)) {
            nodes.remove(point.getPointId());
        }
    }

    private void saveRoadmapPointCoords(Integer roadmapId, Map<Long, Coord> nodes) {
        ArrayList<RoadmapPointCoord> pointCoordList = new ArrayList<>();
        for (Map.Entry<Long, Coord> entry : nodes.entrySet()) {
            RoadmapPointCoord pointCoord = new RoadmapPointCoord();
            pointCoord.setLat(entry.getValue().getLat());
            pointCoord.setLon(entry.getValue().getLon());
            pointCoord.setPointId(entry.getKey());
            pointCoord.setRoadmapId(roadmapId);
            pointCoordList.add(pointCoord);
        }
        roadmapPointCoordRepo.saveAll(pointCoordList);
    }

    private void addHBToRoadmapPoints(InputPoint hb, Pair<Long, Long> neibsForHb, Integer roadmapId) {
        RoadmapPoint p1 = roadmapPointRepo.
                findRoadmapPointByPointIdAndNeibPointIdAndRoadmapId(neibsForHb.getFirst(), neibsForHb.getSecond(), hb.getRoadmapId());
        RoadmapPoint p2 = roadmapPointRepo.
                findRoadmapPointByPointIdAndNeibPointIdAndRoadmapId(neibsForHb.getSecond(), neibsForHb.getFirst(), hb.getRoadmapId());
        RoadmapPointCoord p1Coord = roadmapPointCoordRepo.findRoadmapPointCoordByPointIdAndRoadmapId(neibsForHb.getFirst(), roadmapId);
        RoadmapPointCoord p2Coord = roadmapPointCoordRepo.findRoadmapPointCoordByPointIdAndRoadmapId(neibsForHb.getSecond(), roadmapId);
        if(p1 != null && p2 != null){
           changeCurrentRelations(hb, p1);
           changeCurrentRelations(hb, p2);
        } else {

            saveRelationFromRoadmapToHBPoint(p1Coord, hb, neibsForHb.getFirst());
            saveRelationFromRoadmapToHBPoint(p2Coord, hb, neibsForHb.getSecond());
        }
        saveRoadmapPointCoord(hb.getPointId(), hb.getLat(), hb.getLon(), roadmapId);
        saveRelationFromHBToRoadmapPoint(hb, p1Coord);
        saveRelationFromHBToRoadmapPoint(hb, p2Coord);
    }

    private void changeCurrentRelations(InputPoint hb, RoadmapPoint roadmapPoint) {
        RoadmapPointCoord coord = roadmapPointCoordRepo.findRoadmapPointCoordByPointIdAndRoadmapId(roadmapPoint.getPointId(), roadmapPoint.getRoadmapId());
        roadmapPoint.setNeibPointId(hb.getPointId());
        Coord hbCoord = new Coord(hb.getLat(), hb.getLon());
        Coord rpCoord = new Coord(coord.getLat(), coord.getLon());
        Double dist = DistHelper.getDist(rpCoord, hbCoord);
        roadmapPoint.setDist(dist);
        roadmapPointRepo.save(roadmapPoint);
    }

    private void saveRelationFromRoadmapToHBPoint(RoadmapPointCoord pCoord, InputPoint hb, Long pointId) {
        RoadmapPoint p = new RoadmapPoint();
        p.setRoadmapId(hb.getRoadmapId());
        p.setPointId(pointId);
        saveNeibAndDist(pCoord, hb, p, hb.getPointId());
    }

    private void saveRelationFromHBToRoadmapPoint(InputPoint hb, RoadmapPointCoord roadmapPointCoord) {
        RoadmapPoint p = new RoadmapPoint();
        p.setRoadmapId(hb.getRoadmapId());
        p.setPointId(hb.getPointId());
        saveNeibAndDist(roadmapPointCoord, hb, p, roadmapPointCoord.getPointId());
    }

    private void saveNeibAndDist(RoadmapPointCoord pCoord, InputPoint hb, RoadmapPoint p, Long pointId) {
        p.setNeibPointId(pointId);
        Coord hbCoord = new Coord(hb.getLat(), hb.getLon());
        Coord rpCoord = new Coord(pCoord.getLat(), pCoord.getLon());
        Double dist = DistHelper.getDist(rpCoord, hbCoord);
        p.setDist(dist);
        roadmapPointRepo.save(p);
    }

    private void saveRoadmapPointCoord(Long id, Double lat, Double lon, Integer roadmapId){
        RoadmapPointCoord pointCoord = new RoadmapPointCoord();
        pointCoord.setLat(lat);
        pointCoord.setLon(lon);
        pointCoord.setPointId(id);
        pointCoord.setRoadmapId(roadmapId);
        roadmapPointCoordRepo.save(pointCoord);
    }

    private Map<Long, Coord> nodeParsing(Document doc, Integer roadmapId) {
        Map<Long, Coord> nodes = new HashMap<>();
        NodeList nodeList = doc.getElementsByTagName("node");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.hasChildNodes()) {
                saveNodesWithChildren(node, roadmapId, nodes);
            } else {
                saveNodeCoord(node, nodes);
            }
        }
        return nodes;
    }

    private void saveNodesWithChildren(Node node, Integer roadmapId, Map<Long, Coord> nodes) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(nodeHasName(child, "tag") && nodeHasAttributeWithValue(child, "k", "штаб")) {
                saveHQOrHomeBasePoint(node, roadmapId, InputPointType.HQ);
                return;
            } else
            if(nodeHasName(child, "tag") && nodeHasAttributeWithValue(child, "k", "локация БС")) {
                saveHQOrHomeBasePoint(node, roadmapId, InputPointType.HOME_BASE);
                return;
            }
        }
        saveNodeCoord(node, nodes);
    }

    private void saveHQOrHomeBasePoint(Node node, Integer roadmapId, InputPointType type) {
        InputPoint ip = new InputPoint();
        ip.setRoadmapId(roadmapId);
        ip.setLat(Double.valueOf(getAttributeValue(node, "lat")));
        ip.setLon(Double.valueOf(getAttributeValue(node, "lon")));
        ip.setPointId(Long.valueOf(getAttributeValue(node, "id")));
        ip.setType(type);
        inputPointRepo.save(ip);
    }

    private void saveNodeCoord(Node node, Map<Long, Coord> nodes) {
        Coord coord = new Coord();
        coord.setLat(Double.valueOf(getAttributeValue(node, "lat")));
        coord.setLon(Double.valueOf(getAttributeValue(node, "lon")));
        nodes.put(Long.valueOf(getAttributeValue(node, "id")), coord);
    }

    private void wayParsing(Document doc, Integer roadmapId, Map<Long, Coord> nodes) {
        NodeList wayList = doc.getElementsByTagName("way");
        for (int i = 0; i < wayList.getLength(); i++) {
            Node way = wayList.item(i);
            NodeList children = way.getChildNodes();
            boolean isInterestArea = false;
            List<Long> ids = new ArrayList<>();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if(nodeHasName(child, "nd")) {
                    ids.add(Long.valueOf(getAttributeValue(child, "ref")));
                } else if(nodeHasName(child, "tag") && nodeHasAttributeWithValue(child, "k", "зона интереса")) {
                    isInterestArea = true;
                }
            }
            if(isInterestArea){
                saveInterestAreaPoints(ids, roadmapId, nodes);
            }else {
                saveRoadmapPoints(ids, roadmapId, nodes);
            }
        }
    }

    private void saveInterestAreaPoints(List<Long> ids, Integer roadmapId, Map<Long, Coord> nodes) {
        int last = ids.size()-1;
        Coord coord;
        for (int j = 0; j < last; j++) {
            InputPoint ip = new InputPoint();
            ip.setRoadmapId(roadmapId);
            ip.setType(InputPointType.INTEREST_AREA);
            coord = nodes.get(ids.get(j));
            ip.setLat(coord.getLat());
            ip.setLon(coord.getLon());
            ip.setPointId(ids.get(j));
            if(j == 0) {
                ip.setPrePoint(ids.get(last-1));
                ip.setPostPoint(ids.get(1));
            } else {
                ip.setPrePoint(ids.get(j-1));
                ip.setPostPoint(ids.get(j+1));
            }
            inputPointRepo.save(ip);
        }
    }

    private void saveRoadmapPoints(List<Long> ids, Integer roadmapId, Map<Long, Coord> nodes) {
        int last = ids.size()-1;
        Coord coord;
        Coord neibCoord;
        ArrayList<RoadmapPoint> roadmapPointsList = new ArrayList<>();
        for (int j = 0; j < ids.size(); j++) {
            coord = nodes.get(ids.get(j));
            RoadmapPoint rp = new RoadmapPoint();
            rp.setRoadmapId(roadmapId);
            rp.setPointId(ids.get(j));
            if(j == 0){
                rp.setNeibPointId(ids.get(1));
                neibCoord = nodes.get(ids.get(1));
                rp.setDist(DistHelper.getDist(coord, neibCoord));
                roadmapPointsList.add(rp);
            } else if(j == last) {
                rp.setNeibPointId(ids.get(last-1));
                neibCoord = nodes.get(ids.get(last-1));
                rp.setDist(DistHelper.getDist(coord, neibCoord));
                roadmapPointsList.add(rp);
            } else {
                rp.setNeibPointId(ids.get(j+1));
                neibCoord = nodes.get(ids.get(j+1));
                rp.setDist(DistHelper.getDist(coord, neibCoord));
                roadmapPointsList.add(rp);
                rp = new RoadmapPoint();
                rp.setRoadmapId(roadmapId);
                rp.setPointId(ids.get(j));
                rp.setNeibPointId(ids.get(j-1));
                neibCoord = nodes.get(ids.get(j-1));
                rp.setDist(DistHelper.getDist(coord, neibCoord));
                roadmapPointsList.add(rp);
            }
        }
        roadmapPointRepo.saveAll(roadmapPointsList);
    }

    private boolean nodeHasName(Node node, String nodeName) {
        return node.getNodeName().equals(nodeName);
    }

    private boolean nodeHasAttributeWithValue(Node node, String attrName, String attrValue) {
        return node.getAttributes().getNamedItem(attrName).getNodeValue().equals(attrValue);
    }

    private String getAttributeValue(Node node, String attrName) {
        return node.getAttributes().getNamedItem(attrName).getNodeValue();
    }
}
