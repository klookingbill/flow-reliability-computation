package edu.uiowa.cs.warp;

import java.util.ArrayList;
import java.util.Collections;

/**
 * ReliabilityAnalysis analyzes the end-to-end reliability of messages transmitted in flows for the
 * WARP system.
 * <p>
 * 
 * Let M represent the Minimum Packet Reception Rate on an edge in a flow. The end-to-end
 * reliability for each flow, flow:src->sink, is computed iteratively as follows:<br>
 * (1)The flow:src node has an initial probability of 1.0 when it is released. All other initial
 * probabilities are 0.0. (That is, the reset of the nodes in the flow have an initial probability
 * value of 0.0.) <br>
 * (2) each src->sink pair probability is computed as NewSinkNodeState = (1-M)*PrevSnkNodeState +
 * M*PrevSrcNodeState <br>
 * This value represents the probability that the message as been received by the node SinkNode.
 * Thus, the NewSinkNodeState probability will increase each time a push or pull is executed with
 * SinkNode as a listener.
 * <p>
 * 
 * The last probability state value for any node is the reliability of the message reaching that
 * node, and the end-to-end reliability of a flow is the value of the last Flow:SinkNode
 * probability.
 * <p>
 * 
 * CS2820 Fall 2024 Project: Implement this class to compute the probabilities that comprise the
 * ReliablityMatrix, which is the core of the reliability visualization that is requested in Warp.
 * <p>
 * 
 * To do this, you will need to retrieve the program source, parse the instructions for each node,
 * in each time slot, to extract the src and snk nodes in the instruction and then apply the message
 * success probability equation defined in the project assignment.
 * <p>
 * 
 * I recommend using the getInstructionParameters method of the WarpDSL class to extract the src and
 * snk nodes from the instruction string in a program schedule time slot.
 * 
 * @author sgoddard
 * @version 1.8 Fall 2024
 *
 */
public class ReliabilityAnalysis {
	
  /**
   * The end-to-end reliability target.
   */
  private Double e2e = 0.0;
  
  /**
   * The minimum packet reception rate.
   */
  private Double minPacketReceptionRate = 0.0;
  
  /**
   * The number of faults tolerated per edge.
   */
  private Integer numFaults = 0;
  
  /**
   * A boolean indicator that determines which helper method is used.
   */
  private boolean constructorIndicator;

  /**
   * Constructor initializing an object with the specified end-to-end reliability 
   * target and the minimum packet reception rate.
   * 
   * @param e2e                    end-to-end reliability target
   * @param minPacketReceptionRate minimum packet reception rate
   */
  public ReliabilityAnalysis (Double e2e, Double minPacketReceptionRate) {
    this.e2e = e2e;
    this.minPacketReceptionRate = minPacketReceptionRate;
  }

  /**
   * Constructor initializing an object with default end-to-end reliability target,
   * default minimum packet reception rate, and a specified number of faults.
   *
   * @param numFaults number of faults tolerated per edge
   */
  public ReliabilityAnalysis (Integer numFaults) {
	this.e2e = 0.99;
	this.minPacketReceptionRate = 0.9;
    this.numFaults = numFaults;
    this.constructorIndicator = true;
  }

  /**
   * Estimates the worst-case number of transmissions needed across a flow to meet
   * the end-to-end reliability target.
   *
   * @param flow  given flow where transmission attempts are measured
   * @return      ArrayList of transmission attempts for each node pair in flow
   */
  public ArrayList<Integer> numTxPerLinkAndTotalTxCost(Flow flow) {
	  ArrayList<Node> nodesInFlow = flow.nodes;
	  ArrayList<Integer> numTxArrayList = new ArrayList<>();
	  if(constructorIndicator) {
		  numTxArrayList = helperForConstructorWithNumFaults(nodesInFlow);		  
	  } else {
		  numTxArrayList = helperForConstructorWithE2EAndMinPacketReceptionRate(nodesInFlow);
	  }
	  return numTxArrayList;
  }
  
  /**
   * Helper method for computing the transmission costs for each node and the total transmission cost 
   * in a given flow where transmission costs for each node are variable.
   * 
   * @param flowNodes ArrayList of nodes in the given flow
   * @return          ArrayList of the transmission costs for each node and the given flow
   */
  public ArrayList<Integer> helperForConstructorWithE2EAndMinPacketReceptionRate(ArrayList<Node> nodesInFlow) {
	    var nNodesInFlow = nodesInFlow.size(); 
	    ArrayList<Integer> txAttempts = new ArrayList<>(Collections.nCopies(nNodesInFlow + 1, 0));
	    var nHops = nNodesInFlow - 1;
	    Double minLinkReliablityNeded = Math.max(e2e, Math.pow(e2e, (1.0 / (double) nHops))); 
	    //create ReliabilityTable with a row for each time slot
	    ReliabilityTable reliabilityWindow = new ReliabilityTable();
	    //initialize first time slot with reliability 1.0
	    ReliabilityRow currentRow = new ReliabilityRow(nNodesInFlow, 0.0);
	    currentRow.set(0, 1.0);
	    reliabilityWindow.add(currentRow);
	    Double e2eReliabilityState = currentRow.get(nNodesInFlow - 1);                                                                          
	    var timeSlot = 0; // start time at 0
	    while (e2eReliabilityState < e2e) { // change to while and increment increment timeSlot becuase
	                                        // we don't know how long this schedule window will last
	      //retrieve previous row and create new row for current time slot
	      ReliabilityRow prevRow = reliabilityWindow.get(timeSlot);
	      //loop through nodes to update reliabilities
	      for (int nodeIndex = 0; nodeIndex < nHops; nodeIndex++) { 
	    	  int srcNodeIndex = nodeIndex;
	    	  int snkNodeIndex = nodeIndex + 1;     //reliability at source node
	    	    double prevSrcState = prevRow.get(srcNodeIndex);
	            double prevSnkState = prevRow.get(snkNodeIndex); //reliability at sink node
	            double nextSnkState;
	            //if sink node hasn't reached min reliability and source has the packet
	            //System.out.println(reliabilityWindow.size());
	            if (prevSnkState < minLinkReliablityNeded && prevSrcState > 0) {
	                nextSnkState = ((1.0 - minPacketReceptionRate) * prevSnkState) 
	                                + (minPacketReceptionRate * prevSrcState);
	                txAttempts.set(nodeIndex, txAttempts.get(nodeIndex) + 1);  //increment transmission attempt
	            } else {
	                nextSnkState = prevSnkState;  //carry forward  previous state
	            }
	            //update current row with max reliability for each node
	            currentRow.set(nodeIndex, Math.max(currentRow.get(nodeIndex), prevSrcState));
	            currentRow.set(nodeIndex + 1, nextSnkState);
	        }
	       //update the E2E reliability state with last node's value
	       e2eReliabilityState = currentRow.get(nNodesInFlow - 1);
           //add current row to reliability window
	       reliabilityWindow.add(currentRow);
	       timeSlot++;
	    }
	    //set total transmission cost as last element
	    txAttempts.set(nNodesInFlow, reliabilityWindow.size());
	    //return list of transmission attempts per link and total cost
	    return txAttempts;
  }
  
  /**
   * Helper method for computing the transmission costs for each node and the total transmission cost 
   * in a given flow where transmission costs for each node are fixed. 
   * 
   * @param flowNodes ArrayList of nodes in the given flow
   * @return          ArrayList of the transmission costs for each node and the given flow
   */
  public ArrayList<Integer> helperForConstructorWithNumFaults(ArrayList<Node> flowNodes) {
	    ArrayList<Integer> numTxArrayList = new ArrayList<>();
	    for (int i = 0; i < flowNodes.size(); i++) {
	                numTxArrayList.add(numFaults + 1);
	    }
	    int numEdgesInFlow = flowNodes.size() - 1;
	    int maxFaultsInFlow = numEdgesInFlow * numFaults;
	    numTxArrayList.add(numEdgesInFlow + maxFaultsInFlow);
	    return numTxArrayList;
  }

  public ReliabilityAnalysis(Program program) {
    // TODO Auto-generated constructor stub
  }
  
  public ReliabilityTable getReliabilities() {
	// TODO implement this operation
	throw new UnsupportedOperationException("not implemented");
  }

  public Boolean verifyReliabilities() {
    // TODO Auto-generated method stub
    return true;
  }
  
  /**
   * begin testing area
   */
  public static void main(String[] args) {  
    Node node1 = new Node("n1", 1, 0);
    Node node2 = new Node("n2", 2, 1);
    Node node3 = new Node("n3", 3, 2);
    
    Flow flow = new Flow("test", 1, 0);
    flow.addNode(node1);
    flow.addNode(node2);
    flow.addNode(node3);
    
    //case 1: ReliabilityAnalysis(Double e2e, Double minPacketReceptionRate)
    ReliabilityAnalysis test = new ReliabilityAnalysis(0.99, 0.9);  
    ArrayList<Integer> txCostsWithRates = test.numTxPerLinkAndTotalTxCost(flow);
    System.out.println("transmission costs c(e2e, minPacketReceptionRate)");
    System.out.println(txCostsWithRates);

    //case 2: ReliabilityAnalysis(Integer numFaults)
    ReliabilityAnalysis test2 = new ReliabilityAnalysis(2);  
    ArrayList<Integer> txCostsWithFaults = test2.numTxPerLinkAndTotalTxCost(flow);
    System.out.println("transmission costs with c(numFaults)");
    System.out.println(txCostsWithFaults);
    
  }
}