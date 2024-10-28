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
  private Double e2e = 0.0;
  private Double minPacketReceptionRate = 0.0;
  private Integer numFaults = 0;
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
   * @param flow  given flow where transmissions attempts are measured
   * @return      ArrayList of transmission attempts for each node pair in flow
   */
  public ArrayList<Integer> numTxPerLinkAndTotalTxCost(Flow flow) {
    var nodesInFlow = flow.nodes;
	//last entry is worst-case cost of isolated e2e transmission
	var nNodesInFlow = nodesInFlow.size(); 
	//ArrayList to store transmission attempts per link & total cost at the end
	ArrayList<Integer> txAttempts = new ArrayList<>(Collections.nCopies(nNodesInFlow + 1, 0));
	var nHops = nNodesInFlow - 1;
	// get min reliability for each link in the flow
	Double minLinkReliablityNeded = Math.max(e2e, Math.pow(e2e, (1.0 / (double) nHops))); 
	//create ReliabilityTable with a row for each time slot
	ReliabilityTable reliabilityWindow = new ReliabilityTable();
	//initialize first time slot with reliability 1.0
	ReliabilityRow initialRow = new ReliabilityRow(nNodesInFlow, 0.0);
	initialRow.set(0, 1.0);  
	reliabilityWindow.add(initialRow);
	Double e2eReliabilityState = initialRow.get(nNodesInFlow - 1);                                                                          
	var timeSlot = 0; //start time at 0
	while (e2eReliabilityState < e2e) { //change to while and increment timeSlot 	                                        
	  //retrieve previous row and create new row for current time slot
	  ReliabilityRow prevRow = reliabilityWindow.get(timeSlot);
	  ReliabilityRow currentRow = new ReliabilityRow(nNodesInFlow, 0.0);
	  //loop through nodes to update reliabilities
	  for (int nodeIndex = 0; nodeIndex < nHops; nodeIndex++) { 
	    double prevSrcState = prevRow.get(nodeIndex);//reliability at source node
	    double prevSnkState = prevRow.get(nodeIndex + 1);//reliability at sink node
	    double nextSnkState;
	    //if sink node hasn't reached min reliability and source has the packet
	    if (prevSnkState < minLinkReliablityNeded && prevSrcState > 0) {
	      nextSnkState = (1.0 - minPacketReceptionRate) * prevSnkState 
	                                + minPacketReceptionRate * prevSrcState;
	      txAttempts.set(nodeIndex, txAttempts.get(nodeIndex) + 1);//increment transmission attempt
	    } else {
	      nextSnkState = prevSnkState;//carry forward  previous state
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
	  //if the case of second constructor usage
	  if (constructorIndicator) {
	    var maxFaultsInFlow = nHops * numFaults;
	    txAttempts.add(nHops + maxFaultsInFlow);
	  } 
	  return txAttempts;   
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
    ReliabilityAnalysis test2 = new ReliabilityAnalysis(2);  //2 faults allowed
    ArrayList<Integer> txCostsWithFaults = test2.numTxPerLinkAndTotalTxCost(flow);
    System.out.println("transmission costs with c(numFaults)");
    System.out.println(txCostsWithFaults);
  }
}
