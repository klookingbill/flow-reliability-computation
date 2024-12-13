package edu.uiowa.cs.warp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uiowa.cs.warp.SystemAttributes.ScheduleChoices;
import edu.uiowa.cs.warp.WarpDSL.InstructionParameters;

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
  
  //Create new var of type Program
 
  
  private WarpDSL myDSL = new WarpDSL();
  
  private  FileManager myFile = new FileManager();
  
  private  String myFileName = "Example1a.txt";
  
  private  String readMyFile = myFile.readFile(myFileName);
  
  private Program myProgram;

  private WorkLoad workLoad;


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
	this.workLoad = program.toWorkLoad();
    this.myDSL = myDSL;
    this.myFile = myFile;
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
   * getReliabilityHeader() generates a an array of strings containing the header, which is used in GetReliabilities().
   * It iterates through each flow and its respective nodes, and then adds them to the header object.
   * @return header
   */
  
  public String[] getReliabilityHeader() {
	  var workload = myProgram.toWorkLoad();
	  var flows = workload.getFlowNames();
	  List<String> headerList = new ArrayList<>();
	  
	  //iterate through each flow in flows; get the nodes in the flow and add nodes + flows to header
	  for (int flowIndex = 0; flowIndex < flows.length; flowIndex++) {
		  String flowName = flows[flowIndex];
		  String[] nodes = workload.getNodesInFlow(flowName);
		  for (String node : nodes) {
			  headerList.add(String.format("F" + flowIndex + ":" + node));
			}
		}
		
		var header = headerList.toArray(new String[0]);
		return header;

	}
  /**
   * createHeaderMap() takes the header and assigns it as a key in the map, with a corresponding integer value to designate the index of that key.
   * @param headers
   * @return headerMap
   */
 
  private Map<String, Integer> createHeaderMap(String[] headers) {
	  //create a new map to hold header and its val
	   Map<String, Integer> headerMap = new HashMap<>();
	   //iterate through length of headers and put each header as a key w/ i as its value
	   for (int i = 0; i < headers.length; i++) {
	       headerMap.put(headers[i], i);
	    }
	    return headerMap;
	}
  /**
   * initializeSourceNodes() takes in reliabilities, numRows, and sourceCol to set sourceNodes
   * with value 1.0. 
   * @param reliabilities
   * @param numRows
   * @param sourceCol
   */
  
  private void initializeSourceNodes(ReliabilityTable reliabilities, int numRows, int sourceCol) {
	    for (int row = 0; row < numRows; row++) {
	        reliabilities.get(row).set(sourceCol, 1.0);
	    }
	}
  /**
   * resetColumns() iterates through each column in the columns list and resets reliabilities at the designated timeSlot.
   * @param columns
   * @param reliabilities
   * @param timeSlot
   */
  
  private void resetColumns(List<Integer> columns, ReliabilityTable reliabilities, int timeSlot) {
	    if (columns != null) {
	        for (int col : columns) {
	            reliabilities.get(timeSlot).set(col, 0.0);
	        }
	    }
	}
  /**
   * getNonSourceColumns() starts iterating at nodeIndex 1 and iterates until the integer val of nodesInFlow. 
   * It ignores the first val of the nodesInFlow (src node), and then creates columns that will be populated with 
   * probability vals (Src columns are populated with 1.0 in initializeSourceColumn)
   * @param nodesInFlow
   * @param flowIndex
   * @param headerMap
   * @return flowNonSourceColumns
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
 * 
 * @return
 */
 
  public ReliabilityTable getReliabilities() {
	  //Initialize variables
	  String[] headers = getReliabilityHeader(); 
	  ProgramSchedule schedule = myProgram.getSchedule();
	  WorkLoad workLoad = myProgram.toWorkLoad();
	  String[] flows = workLoad.getFlowNames();
	  int numRows = schedule.getNumRows();
	  int numColumns = headers.length;
	  ReliabilityTable reliabilities = new ReliabilityTable(numRows, numColumns);
	  //headerMap stores each header w/ its column index
	  var headerMap = createHeaderMap(headers);
	  //nonSourceColumns will hold the columns that are not src Nodes
	  Map<Integer, List<Integer>> nonSourceColumns = new HashMap<>();
	  //iterate through each flow
	  for (int flowIndex = 0; flowIndex < flows.length; flowIndex++) {
		  //get nodes in the designated flow
	      String[] nodesInFlow = workLoad.getNodesInFlow(flows[flowIndex]);
	      //generate header to find column index in headerMap
	      String sourceHeader = String.format("F" + flowIndex + ":" + nodesInFlow[0]);
	      int sourceCol = headerMap.get(sourceHeader);
	      //Configures sourceNode to 1.0
	      initializeSourceNodes(reliabilities, numRows, sourceCol);
	      //calls getNonSourceColumns to configure
	      nonSourceColumns.put(flowIndex, getNonSourceColumns(nodesInFlow, flowIndex, headerMap));
	  }
	  //instantiate WarpDSL object    
	  WarpDSL instructions = new WarpDSL();
	  //loop through each time slot in the schedule (loops through each row)
	  for (int timeSlot = 0; timeSlot < numRows; timeSlot++) {
		  //TODO: THIS NEEDS TO BE CHANGED TO USE MOD BY THE PERIOD, NOT BY 10! 
		  //int flowPeriod = myProgram.toWorkLoad().getFlows().get(flows[timeSlot]).getPeriod();
		  //reset the columns if period is completed
		  //TODO: the below line needs to be altered. Ideally, it would be (timeSlot % period == 0)... 10 is a placeholder for now, but it works
		  //TODO: right now, there is an index out of bounds exception for getting the flow period... figure out why this is happening
	      if (timeSlot % 10 == 0 && timeSlot > 0) {
	    	  resetColumns(nonSourceColumns.get(0), reliabilities, timeSlot);
	            
	      }
	      //iterate through each column in schedule
	      for (int nodeColumn = 0; nodeColumn < schedule.getNumColumns(); nodeColumn++) {
	    	//get instruction @ current time slot and node
	          String instruction = schedule.get(timeSlot, nodeColumn);
	          //checks to make sure instruction isn't empty/not populated 
	          if (instruction == null || instruction.trim().isEmpty()) {
	              continue;
	          }
	          //create an array list of InstructionParameters    
	          ArrayList<WarpDSL.InstructionParameters> params = instructions.getInstructionParameters(instruction);
	        //iterate through each parameter in params ArrayList, check to make sure the param is able to be used
	          for (WarpDSL.InstructionParameters param : params) {
	              if (!param.getName().equals("push") && !param.getName().equals("pull")) {
	                    continue;
	              }
	            //get curr flowName from curr param  
	              String flowName = param.getFlow();
	            //set default val to -1 in case no match found
	              int flowIndex = -1;
	            //make sure flow is at the correct index in the flows array
	              //TODO: might need to modify this?
	              for (int i = 0; i < flows.length; i++) {
	                  if (flows[i].equals(flowName)) {
	                      flowIndex = i;
	                      break;
	                   }
	              }
	              if (flowIndex == -1) continue;
	            //format src and snk headers with the flowIndex and parameters 
	              String srcHeader = String.format("F%d:%s", flowIndex, param.getSrc());
	              String snkHeader = String.format("F%d:%s", flowIndex, param.getSnk());
	            //use headerMap to get column index
	              Integer srcCol = headerMap.get(srcHeader);
	              Integer snkCol = headerMap.get(snkHeader);
	            //TODO: not sure if this is needed? Here for safekeeping rn
	              if (srcCol == null || snkCol == null) continue;
	           // retrieves the reliability of the sink node at the curr time slot from the reliabilities  
	              double PrevSnkNodeState = reliabilities.get(timeSlot).get(snkCol);
	              //initialize var
	              double PrevSrcNodeState;
	              
	            //retrieve srcNodeState; if timeSlot is not zero, retrieve from previous time slot 
	              if (timeSlot > 0) {
	                PrevSrcNodeState = reliabilities.get(timeSlot - 1).get(srcCol);
	                } 
	              else {
	                	PrevSrcNodeState = reliabilities.get(timeSlot).get(srcCol);
	                }
	            //update state of SinkNode using formula listed under #6 in the project guide  
	              double newSinkNodeState = (1-minPacketReceptionRate)* PrevSnkNodeState + minPacketReceptionRate * PrevSrcNodeState;
	         
	            //updates the reliability value for the sink node at the curr time slot  
	              reliabilities.get(timeSlot).set(snkCol, newSinkNodeState);     
	            //TODO: THIS NEEDS TO BE FIXED!!! SHOULD BE MODDED BY PERIOD OF THE CURR FLOW!
	              for (int nextRow = timeSlot + 1; nextRow < numRows; nextRow++) {
	                  	if (flowIndex == 0 && nextRow % 10 == 0) {
	                       break;
	                    }
	                  reliabilities.get(nextRow).set(snkCol, newSinkNodeState);
	                }
	            }
	        }
	    }
	 
	  return reliabilities;
	}
	/*//build new program
    myProgram.buildOriginalProgram();
    //Create ProgramSchedule containing instructions
    ProgramSchedule returnedProgramSchedule = myProgram.getSchedule();
    //create vars to store numColumns & numRows
    int numColumns = returnedProgramSchedule.getNumColumns();
    int numRows = returnedProgramSchedule.getNumRows();
    

    String[] header = getReliabilityHeader();
    System.out.println(getReliabilityHeader());
    HashMap<String, Integer> map = new HashMap<>();
    int headerLength = header.length;
    for (int column = 0; column < header.length; column++) {
    	map.put(header[column], column);
    }
    //new ReliabilityTable w/ num columns.size x numrows
    //once you have columnheader, when parse the instruction, get the parameters ie flow name and syncnode, can rebuild 
    //
  
    //create new ReliabilityAnalysis object
    //ReliabilityAnalysis ra = new ReliabilityAnalysis(myProgram);
    //Create FlowMap flow containing flows of curr program TODO might not need to be used, can get it straight from workload
    //use getFlowPhase and getFLOW PERIOD FOR EACH ENTRY IN INSTRUCTION PARAMETERS
    FlowMap flow = myProgram.toWorkLoad().getFlows();
   
    
    //create ReliabilityTable data, and init. with the needed amount of columns and rows. Inits probabilities for all nodes
    //if necessary values will be set to 1.0- this is when the source node starts 
    ReliabilityTable data = getNodeInfo(numColumns, numRows, flow);
    //double[] currentProb = hashmap(numcol, headerlength)?
//    ArrayList<Double> currentProb = new ArrayList<>();
//    for (int i=0; i<data.size(); i++) {
//    	currentProb.add(data.get)
//    }
    //TODO: is this the correct way of getting the length of header? For example, running with curr test will give 3. Not correct?
//    HashMap<Integer, String> colIndexes = new HashMap<>(); //hashmap to store index of columns. We need to populate this with the below for loop
//    for (int i = 0; i < headerLength; i++) {
//    	colIndexes.put(i, header[i]);
//    }
//    	//colIndexes.put(, i); //need to make a way to put where it is
//    //int timeSlot = 0;
//    ArrayList<Double> updatedSnkProbs = new ArrayList<>();
//    //iterate through the numRows, increasing timeSlot param each iteration
//    for (int timeSlot = 0; timeSlot < numRows; timeSlot++) {
//    	//TODO: we need to figure out a way to reset the flows if a new period releases-
//    	//that will be the current probability we are working with for this loop
//    	//double[] nextProb = new double[numRows];
//    	for (int scheduleCol = 0; scheduleCol < numColumns; scheduleCol++) {
//    		
//    		String instruction = returnedProgramSchedule.get(timeSlot, scheduleCol);
//    		if (!(instruction.isEmpty())) {
//    			ArrayList<InstructionParameters> instructionsArrayList = myDSL.getInstructionParameters(instruction);
//    			
//    			for (InstructionParameters param: instructionsArrayList) {
//    				String flowName = param.getFlow();
//    				//Flow f = flow.get(flowName);
//    				//Integer period = f.getPeriod();
//    				//Integer phase = f.getPhase();
//    				String srcNode = param.getSrc();
//    				String snkNode = param.getSnk();
//    				
//    				if (flowName.equals(WarpDSL.UNUSED) || srcNode.equals(WarpDSL.UNUSED) || snkNode.equals(WarpDSL.UNUSED)) {
//    					continue;
//    				}
//    			
//    				String srcNodeColumnKey = flowName + ":" + srcNode;
//    				System.out.println(srcNodeColumnKey);
//    				String snkNodeColumnKey = flowName + ":" + snkNode;
//    				
//    				
//    				
//    				//need to add integer of srcCol and snkCol where we take srcColKey and snkColKey from hashmap
//    				//then use these integers to update the hashmap below w/ the doubles
//    				
//    				Integer srcNodeColumn = map.get(srcNodeColumnKey);
//    				Integer snkNodeColumn = map.get(snkNodeColumnKey);
//    				
//    				//need to update current probabilties for prevSnk and prevSrc by taking current prob from hashmap
//    				double prevSnk = data.get(timeSlot, snkNodeColumn);
//    				double prevSrc = data.get(timeSlot, srcNodeColumn);
//    				double updatedSnk = (1.0 - minPacketReceptionRate) * prevSnk + minPacketReceptionRate * prevSrc;
//    				//nextProb will be used here, assign nextProb[snkCol] = updatedSnk;
//    				//nextProb[snkNodeColumn] = updatedSnk;
//    			}	
//    			
//    		}
//    	
//    	}
//    	//ReliabilityRow row = new ReliabilityRow();
//    	//for (int j = 0; j < numColumns; j++) {
//    	//	row.add(nextProb[j]);
//    	//}
//    	//data.add(row);
//    	//currentProb = nextProb;
//    }
//    System.out.println(updatedSnkProbs);

    return data;
    */
    
    

  public Boolean verifyReliabilities() {
    // TODO Auto-generated method stub
	//Check bottom right corner of the map, check prob
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
        //System.out.println(testProgram.getSchedule()); //:)
	    
  }
  
}