package edu.uiowa.cs.warp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.uiowa.cs.warp.SystemAttributes.ScheduleChoices;

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
   * The program that will be parsed instruction by instruction to compute probabilities for each flow at each 
   * time slot of the ReliabilityTable.
   */
  private Program myProgram;
  
  /**
   * The ReliabilityTable that will be built in this class.
   */
  private ReliabilityTable reliabilities;


  /**
   * Constructor initializing an object with the specified end-to-end reliability 
   * target and the minimum packet reception rate.
   *
   * @param e2e                    end-to-end reliability target
   * @param minPacketReceptionRate minimum packet reception rate
   */
  public ReliabilityAnalysis(Double e2e, Double minPacketReceptionRate) {
    this.e2e = e2e;
    this.minPacketReceptionRate = minPacketReceptionRate;
  }

  /**
   * Constructor initializing an object with default end-to-end reliability target,
   * default minimum packet reception rate, and a specified number of faults.
   *
   * @param numFaults number of faults tolerated per edge
   */
  public ReliabilityAnalysis(Integer numFaults) {
    this.e2e = 0.99;
    this.minPacketReceptionRate = 0.9;
    this.numFaults = numFaults;
    this.constructorIndicator = true;
  }
  
  public ReliabilityAnalysis(Program program) {
	this.myProgram = program;
    this.e2e = myProgram.getE2e();
	this.minPacketReceptionRate = myProgram.getMinPacketReceptionRate();
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
    if (constructorIndicator) {
      numTxArrayList = helperForConstructorNumFaults(nodesInFlow);
    } else {
      numTxArrayList = helperForConstructorE2EAndMPRR(nodesInFlow);
    }
    return numTxArrayList;
  }
  
  /**
   * Helper method for computing the transmission costs for each node and the total transmission
   * cost in a given flow where transmission costs for each node are variable. MPRR represents
   * the minimum packet reception rate.
   *
   * @param nodesInFlow ArrayList of nodes in the given flow
   * @return            ArrayList of the transmission costs for each node and the given flow
   */
  public ArrayList<Integer> helperForConstructorE2EAndMPRR(ArrayList<Node> nodesInFlow) {
    int nNodesInFlow = nodesInFlow.size(); 
    ArrayList<Integer> txAttempts = new ArrayList<>(Collections.nCopies(nNodesInFlow + 1, 0));
    int nHops = nNodesInFlow - 1;
    Double minLinkReliablityNeded = Math.max(e2e, Math.pow(e2e, (1.0 / (double) nHops))); 
    //create ReliabilityTable with a row for each time slot
    ReliabilityTable reliabilityWindow = new ReliabilityTable();
    ReliabilityRow currentRow = new ReliabilityRow(nNodesInFlow, 0.0);
    currentRow.set(0, 1.0);
    reliabilityWindow.add(currentRow);
    Double e2eReliabilityState = currentRow.get(nNodesInFlow - 1);
    int timeSlot = 0; //start time at 0
    while (e2eReliabilityState < e2e) {
      //retrieve previous row and create new row for current time slot
      ReliabilityRow prevRow = reliabilityWindow.get(timeSlot);
      //loop through nodes to update reliabilities
      for (int nodeIndex = 0; nodeIndex < nHops; nodeIndex++) { 
        int srcNodeIndex = nodeIndex;
        int snkNodeIndex = nodeIndex + 1;
        double prevSrcState = prevRow.get(srcNodeIndex);
        double prevSnkState = prevRow.get(snkNodeIndex);
        double nextSnkState;
        //if sink node hasn't reached min reliability and source has the packet
        if (prevSnkState < minLinkReliablityNeded && prevSrcState > 0) {
          nextSnkState = ((1.0 - minPacketReceptionRate) * prevSnkState) 
                                + (minPacketReceptionRate * prevSrcState);
          txAttempts.set(nodeIndex, txAttempts.get(nodeIndex) + 1);
        } else {
          nextSnkState = prevSnkState;
        }
        //update current row with max reliability for each node
        currentRow.set(nodeIndex, Math.max(currentRow.get(nodeIndex), prevSrcState));
        currentRow.set(nodeIndex + 1, nextSnkState);
      }
      //update the E2E reliability state with last node's value
      e2eReliabilityState = currentRow.get(nNodesInFlow - 1);
      reliabilityWindow.add(currentRow);
      timeSlot++;
    }
    //set total transmission cost as last element
    txAttempts.set(nNodesInFlow, reliabilityWindow.size());
    return txAttempts;
  }
  
  /**
   * Helper method for computing the transmission costs for each node and the total transmission
   * cost in a given flow where transmission costs for each node are fixed. 
   *
   * @param flowNodes ArrayList of nodes in the given flow
   * @return          ArrayList of the transmission costs for each node and the given flow
   */
  public ArrayList<Integer> helperForConstructorNumFaults(ArrayList<Node> flowNodes) {
    ArrayList<Integer> numTxArrayList = new ArrayList<>();
    for (int i = 0; i < flowNodes.size(); i++) {
      numTxArrayList.add(numFaults + 1);
    }
    int numEdgesInFlow = flowNodes.size() - 1;
    int maxFaultsInFlow = numEdgesInFlow * numFaults;
    numTxArrayList.add(numEdgesInFlow + maxFaultsInFlow);
    return numTxArrayList;
  }
  
  /**
   * Generates an array of strings containing the header, which is used in getReliabilities().
   * It iterates through each flow and its respective nodes, and then adds them to the header object.
   * 
   * @return header the header of the ReliabilityTable
   */
  public String[] getReliabilityHeader() {
    WorkLoad workload = myProgram.toWorkLoad();
	String[] flows = workload.getFlowNames();
	List<String> headerList = new ArrayList<>();
	  
	/* Iterate through each flow in flows; get the nodes in the flow and add nodes + flows to header */
	for (int flowIndex = 0; flowIndex < flows.length; flowIndex++) {
	  String flowName = flows[flowIndex];
	  String[] nodes = workload.getNodesInFlow(flowName);
	  for (String node : nodes) {
	    headerList.add(String.format("F" + flowIndex + ":" + node));
	  }
	}
		
	String[] header = headerList.toArray(new String[0]);
	return header;
  }
 
  /**
   * Takes the header and assigns it as a key in the map, 
   * with a corresponding integer value to designate the index of that key.
   * 
   * @param headers the header of the ReliabilityTable
   * @return headerMap a map with the element of each header as the key, and its index as a value
   */
  private Map<String, Integer> createHeaderMap(String[] headers) {
    /* Create a new map to hold header and its value */
	Map<String, Integer> headerMap = new HashMap<>();
	/* Iterate through length of headers and put each header as a key with i as its value */
	for (int i = 0; i < headers.length; i++) {
	  headerMap.put(headers[i], i);
	}
	return headerMap;
  }
  
  /**
   * Takes in reliabilities, numRows, and sourceCol to set sourceNodes
   * with value 1.0.
   *  
   * @param reliabilities the ReliabilityTable being built
   * @param numRows the number of rows of the ReliabilityTable
   * @param sourceCol the column for which the source node is being set
   */
  private void initializeSourceNodes(ReliabilityTable reliabilities, int numRows, int sourceCol) {
    for (int row = 0; row < numRows; row++) {
	  reliabilities.get(row).set(sourceCol, 1.0);
	}
  }
  
  /**
   * Iterates through each column in the columns list and resets reliabilities at the designated timeSlot.
   * 
   * @param columns the list of columns of the ReliabilityTable
   * @param reliabilities the ReliabilityTable being built
   * @param timeSlot the time slot at which to reset the reliabilties
   */
  private void resetColumns(List<Integer> columns, ReliabilityTable reliabilities, int timeSlot) {
    if (columns != null) {
	  for (int col : columns) {
	    reliabilities.get(timeSlot).set(col, 0.0);
	  }
	}
  }
  
  /**
   * Starts iterating at nodeIndex 1 and iterates until the integer val of nodesInFlow. 
   * It ignores the first val of the nodesInFlow (src node), and then creates columns that will be populated with 
   * probability vals (Src columns are populated with 1.0 in initializeSourceColumn).
   * 
   * @param nodesInFlow the array containing the nodes in the flow
   * @param flowIndex the index of the flow
   * @param headerMap a map with the element of each header as the key, and its index as a value
   * @return flowNonSourceColumns the columns that are not source nodes
   */
  private List<Integer> getNonSourceColumns(String[] nodesInFlow, int flowIndex, Map<String, Integer> headerMap) {
    List<Integer> flowNonSourceColumns = new ArrayList<>();
	for (int nodeIndex = 1; nodeIndex < nodesInFlow.length; nodeIndex++) {
	  String header = "F" + flowIndex + ":" + nodesInFlow[nodeIndex];
	  flowNonSourceColumns.add(headerMap.get(header));
	}
	return flowNonSourceColumns;
  }

  /**
   * Builds the ReliabilityTable containing flow success probabilities
   * at every node in each flow for a given time slot.
   * 
   * @return the fully built ReliabilityTable 
   */
  public ReliabilityTable getReliabilities() {
    /* Initialize variables to be used, including header of table,
     * ProgramSchedule, WorkLoad, and string array for flow names */
	String[] headers = getReliabilityHeader(); 
	ProgramSchedule schedule = myProgram.getSchedule();
	WorkLoad workLoad = myProgram.toWorkLoad();
	String[] flows = workLoad.getFlowNames();
	int numRows = schedule.getNumRows();
	int numColumns = headers.length;
	reliabilities = new ReliabilityTable(numRows, numColumns);
	/* Store each header with its column index in map */
	Map<String, Integer> headerMap = createHeaderMap(headers);
	/* Store any columns that are not source nodes */
	Map<Integer, List<Integer>> nonSourceColumns = new HashMap<>();
	
	for (int flowIndex = 0; flowIndex < flows.length; flowIndex++) { // iterate through each flow
	  /* Get nodes in the designated flow */
	  String[] nodesInFlow = workLoad.getNodesInFlow(flows[flowIndex]);
	  /* Generate header to find column index in headerMap */
	  String sourceHeader = String.format("F" + flowIndex + ":" + nodesInFlow[0]);
	  int sourceCol = headerMap.get(sourceHeader);
	  initializeSourceNodes(reliabilities, numRows, sourceCol); // Configures sourceNode to 1.0
	  /* Call getNonSourceColumns to configure sourceNode */
	  nonSourceColumns.put(flowIndex, getNonSourceColumns(nodesInFlow, flowIndex, headerMap));
	}  
	WarpDSL instructions = new WarpDSL(); // instantiate WarpDSL object
	/* Loop through each time slot in the schedule (loops through each row) */
	for (int timeSlot = 0; timeSlot < numRows; timeSlot++) {
	  if (timeSlot % myProgram.toWorkLoad().getFlows().get(flows[timeSlot % flows.length]).getPeriod() == 0 && timeSlot > 0) {
	    resetColumns(nonSourceColumns.get(0), reliabilities, timeSlot);
      }
	  /* Iterate through each column in schedule */
	  for (int nodeColumn = 0; nodeColumn < schedule.getNumColumns(); nodeColumn++) {
	    /* Get instruction at current time slot and node */
	    String instruction = schedule.get(timeSlot, nodeColumn);
	    /* Check to make sure instruction isn't empty/non-populated */ 
	    if (instruction == null || instruction.trim().isEmpty()) {
	      continue;
	    }
	    /* Create an ArrayList of InstructionParameters */    
	    ArrayList<WarpDSL.InstructionParameters> params = instructions.getInstructionParameters(instruction);
	    /* Iterate through each parameter in ArrayList, check to make sure the param is able to be used */
	    for (WarpDSL.InstructionParameters param : params) {
	      if (!param.getName().equals("push") && !param.getName().equals("pull")) {
	        continue;
	      }
	      /* Get current flow name from current parameter */  
	      String flowName = param.getFlow();
	      /* Set default index to -1 in case no match found */
	      int flowIndex = -1;
	      /* Make sure flow is at the correct index in the flows array */
	      for (int i = 0; i < flows.length; i++) {
	        if (flows[i].equals(flowName)) {
	          flowIndex = i;
	          break;
	        }
	      }
	      if (flowIndex == -1) continue;
	      /* Format src and snk headers with the flowIndex and parameters */ 
	      String srcHeader = String.format("F%d:%s", flowIndex, param.getSrc());
	      String snkHeader = String.format("F%d:%s", flowIndex, param.getSnk());
	      /* Use headerMap to get column index */
	      Integer srcCol = headerMap.get(srcHeader);
	      Integer snkCol = headerMap.get(snkHeader);
	      /* Retrieve the reliability of the sink node at the current time slot from the reliabilities */ 
	      double PrevSnkNodeState = reliabilities.get(timeSlot).get(snkCol);
	      double PrevSrcNodeState;
	      /* Retrieve srcNodeState; if timeSlot is not zero, retrieve from previous time slot */ 
	      if (timeSlot > 0) {
	        PrevSrcNodeState = reliabilities.get(timeSlot - 1).get(srcCol);
	      } 
	      else {
	        PrevSrcNodeState = reliabilities.get(timeSlot).get(srcCol);
	      }
	      /* Update state of SinkNode using formula listed under #6 in the project guide */  
	      double newSinkNodeState = (1.0 - minPacketReceptionRate)* PrevSnkNodeState + minPacketReceptionRate * PrevSrcNodeState;
	      /* Updates the reliability value for the sink node at the current time slot */  
	      reliabilities.get(timeSlot).set(snkCol, newSinkNodeState);
	      for (int nextRow = timeSlot + 1; nextRow < numRows; nextRow++) {
	        if (flowIndex == 0 && nextRow % myProgram.toWorkLoad().getFlowPeriod(flowName) == 0) {
	          break;
	        }
	        reliabilities.get(nextRow).set(snkCol, newSinkNodeState);
	      }
	    }
	  }
    }
    return reliabilities;
  }
    
    
  // TODO: this is very long implementation, maybe we can trim it down somewhat?
  
  /**
   * Checks if the flow success probability for each sink node
   * at the end of each period meets the end-to-end reliability
   * for the program.
   * 
   * @return true if the end-to-end reliability is met, false otherwise
   */
  public Boolean verifyReliabilities() {
	/* ArrayList to store time slots we will have to check. */
	List<Integer> timeSlotChecks = new ArrayList<>();
	/* Look at each flow in the program. */
	for (Flow f : myProgram.toWorkLoad().getFlows().values()) {
		for (int timeslot = 0; timeslot< reliabilities.getNumRows(); timeslot++) {
			/* Makes sure that the first time slot after the reset due to period is added to arrayList. */
			if (timeslot % f.getPeriod() == f.getPeriod() - 1) {
				timeSlotChecks.add(timeslot);
			}
		}
	}
	/* Removes any duplicates from the arrayList (looked this up on Stack Overflow). 
	 * Had to import Collectors from the stream library. */
	timeSlotChecks = timeSlotChecks.stream().distinct().collect(Collectors.toList());
	
	/* Runs through each time slot stored in the arrayList, 
	 * checks the snk node probability for each flow at 
	 * the ReliabilityRow at this time slot to see if it fails meets 
	 * end-to-end reliability. */
	for (Integer integer : timeSlotChecks) {
		for (int i=0; i<reliabilities.get(integer).size(); i++) {
			if (i % myProgram.toWorkLoad().getFlows().size() == myProgram.toWorkLoad().getFlows().size() - 1) {
				if (reliabilities.get(integer).get(i) < e2e) {
					return false;
				}
			}
		}
	}
    return true;
  }
  
  
  public static void main(String[] args) {
	  	double teste2e = 0.99;
	  	double testMinPacketReceptionRate = 0.9;
	  	String testFileName = "Example1a.txt";
	  	WorkLoad testWorkload = new WorkLoad(testMinPacketReceptionRate, teste2e, testFileName);
	  	Integer nChannels = 16;
	  	Program testProgram = new Program(testWorkload, nChannels, ScheduleChoices.PRIORITY);
	  	
	    //System.out.println(readMyFile);
	    ReliabilityAnalysis ra = new ReliabilityAnalysis(testProgram);
	    //ra.getReliabilities();
	    System.out.println(ra.getReliabilities());
	    System.out.println(ra.verifyReliabilities());
        //System.out.println(testProgram.getSchedule()); //:)
	    
  }
  
}