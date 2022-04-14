package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import osa.dev.petproject.models.Coord;
import osa.dev.petproject.models.InputPointType;
import osa.dev.petproject.models.db.InputPoint;
import osa.dev.petproject.models.db.RoadmapPoint;
import osa.dev.petproject.repository.InputPointRepository;
import osa.dev.petproject.repository.RoadmapPointRepository;
import osa.dev.petproject.tools.DistHelper;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

@Service
public class JosmXmlOptimizationParserService {

    private final RoadmapPointRepository roadmapPointRepo;
    private final InputPointRepository inputPointRepo;
    private final DocumentBuilderFactory factory;

    @Autowired
    public JosmXmlOptimizationParserService(RoadmapPointRepository roadmapPointRepo,
                                            InputPointRepository inputPointRepo) {
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
        wayParsing(doc, roadmapId, nodes);
    }

    private Map<Long, Coord> nodeParsing(Document doc, Integer roadmapId) {
        Map<Long, Coord> nodes = new HashMap<>();
        NodeList nodeList = doc.getElementsByTagName("node");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.hasChildNodes()) {
                NodeList children = node.getChildNodes();
                saveNodesWithChildren(node, children, roadmapId, nodes);
            } else {
                saveNodeCoord(node, nodes);
            }
        }
        return nodes;
    }

    private void saveNodesWithChildren(Node node, NodeList children, Integer roadmapId, Map<Long, Coord> nodes) {
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(nodeHasName(child, "tag") && nodeHasAttributeWithValue(child, "k", "штаб")) {
                saveHQOrHomeBasePoint(node, roadmapId, InputPointType.HQ);
            } else
            if(nodeHasName(child, "tag") && nodeHasAttributeWithValue(child, "k", "локация БС")) {
                saveHQOrHomeBasePoint(node, roadmapId, InputPointType.HOME_BASE);
            } else {
                saveNodeCoord(node, nodes);
            }
        }
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
            if(way.hasChildNodes()) {
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
        for (int j = 0; j < ids.size(); j++) {
            coord = nodes.get(ids.get(j));
            RoadmapPoint rp = new RoadmapPoint();
            rp.setRoadmapId(roadmapId);
            rp.setLat(coord.getLat());
            rp.setLon(coord.getLon());
            rp.setPointId(ids.get(j));
            if(j == 0){
                rp.setNeibPointId(ids.get(1));
                neibCoord = nodes.get(ids.get(1));
                rp.setDist(DistHelper.getDist(coord, neibCoord));
                roadmapPointRepo.save(rp);
            } else if(j == last) {
                rp.setNeibPointId(ids.get(last-1));
                neibCoord = nodes.get(ids.get(last-1));
                rp.setDist(DistHelper.getDist(coord, neibCoord));
                roadmapPointRepo.save(rp);
            } else {
                rp.setNeibPointId(ids.get(j+1));
                neibCoord = nodes.get(ids.get(j+1));
                rp.setDist(DistHelper.getDist(coord, neibCoord));
                roadmapPointRepo.save(rp);
                rp = new RoadmapPoint();
                rp.setRoadmapId(roadmapId);
                rp.setLat(coord.getLat());
                rp.setLon(coord.getLon());
                rp.setPointId(ids.get(j));
                rp.setNeibPointId(ids.get(j-1));
                neibCoord = nodes.get(ids.get(j-1));
                rp.setDist(DistHelper.getDist(coord, neibCoord));
                roadmapPointRepo.save(rp);
            }
        }
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
