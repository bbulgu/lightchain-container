package localnet;

import skipGraph.NodeInfo;
import util.Util;
import org.apache.log4j.Logger;
import java.util.*;

public class LocalSkipGraph {

  private Logger logger;
  Map<Integer, NodeInfo> nodesMap;
  Map<Integer, LocalLookupTable> nodeTable;
  List<NodeInfo> nodes;
  NodeInfo latestInsertion;
  int levels;

  public LocalSkipGraph(List<NodeInfo> nodes, int levels) {

    this.nodes = nodes;
    this.nodeTable = new HashMap<>();
    this.nodesMap = new HashMap<>();
    this.levels = levels;
    this.logger = Logger.getLogger(this.getClass());
    for(NodeInfo node : nodes) {
      nodesMap.put(node.getNumID(), node);
    }

    build();
  }

  /*
    builds the skip graph structure
   */
  private void build() {

    List<Integer> sortedNodes = new ArrayList<>(this.nodesMap.keySet());
    Collections.sort(sortedNodes);

    for(int i = 0 ; i < sortedNodes.size() ; ++i) {
      this.nodes.set(i, nodesMap.get(sortedNodes.get(i)));
      this.nodeTable.put(sortedNodes.get(i), new LocalLookupTable(this.levels));
    }

    for(int i = 0 ; i < this.nodes.size() ; ++i) {
      int j = i + 1;
      for (int level = 0; level <= this.levels && j < this.nodes.size(); level++) {

        int commonBits = Util.commonBits(this.nodes.get(i).getNameID(), this.nodes.get(j).getNameID());
        while (j < this.nodes.size() && commonBits < level) {
          j++;
        }

        if (j < this.nodes.size()) {
          this.nodeTable.get(this.nodes.get(i).getNumID()).updateRight(this.nodes.get(j), level);
          this.nodeTable.get(this.nodes.get(j).getNumID()).updateLeft(this.nodes.get(i), level);
        }
      }
    }
  }

  public void insertNode(NodeInfo node) {

    logger.info("Inserting node with numID: " + node.getNumID() + " and nameID: " + node.getNameID());

    this.nodes.add(node);
    nodesMap.put(node.getNumID(), node);

    latestInsertion = node;

    build();
  }

  public void delete(int numID) {

    logger.info("deleting node with numID: " + numID);

    int index = -1;
    for(int i = 0 ; i < this.nodes.size() ; ++i) {
      if(this.nodes.get(i).getNumID() == numID) {
        index = i;
        break;
      }
    }

    if(index == -1) {
      logger.error("Deleting a non-existing node with numID: " + numID);
      return ;
    }

    this.nodesMap.remove(numID);
    this.nodes.remove(index);

    build();
  }

  public NodeInfo searchByNumID(int target) {

    logger.info("Searching by Numerical ID for: " + target);

    int index = 0;
    for(int i = 0 ; i < this.nodes.size() ; ++i) {

      if(this.nodes.get(i).getNumID() == target)
        return this.nodes.get(index);

      index++;
    }

    if(index >= this.nodes.size()) {
      logger.error("Node Not found with numID: " + target);
      return null;
    }

    return this.nodes.get(index);
  }

  public NodeInfo searchByNameID(String target) {

    logger.info("Searchgin by Name ID for: " + target);

    int index = 0;
    int best = 0;
    for(int i = 0 ; i < this.nodes.size() ; ++i) {

      int commonBits = Util.commonBits(this.nodes.get(i).getNameID(), target);
      if(commonBits > best) {
        best = commonBits;
        index = i;
      }
    }

    return this.nodes.get(index);
  }

  public List<NodeInfo> getNodesWithName(String name) {

    List<NodeInfo> result = new ArrayList<>();
    for(NodeInfo node : this.nodes) {
      if(name.equals(node.getNameID()))
        result.add(node);
    }

    return result;
  }

  public int getNumNodes() {
    return this.nodes.size();
  }

  public NodeInfo getLatestInsertion() {
    return latestInsertion;
  }

}