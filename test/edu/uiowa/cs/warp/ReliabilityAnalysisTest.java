package edu.uiowa.cs.warp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uiowa.cs.warp.SystemAttributes.ScheduleChoices;

class ReliabilityAnalysisTest {

	private Double e2e;
	private Double mprr;
	private Integer numFaults;
	private Integer numChannels;
	private String testFileName;
	private WorkLoad workload;
	private Program program;
	private ReliabilityAnalysis ra;
	private ReliabilityTable reliabilities;
	
	@BeforeEach 
	void setUp() {
	  e2e = 0.99;
	  mprr = 0.9;
	  numFaults = 1;
	  testFileName = "Example1a.txt";
	  workload = new WorkLoad(mprr, e2e, testFileName);
	  numChannels = 16;
	  program = new Program(workload, numChannels, ScheduleChoices.PRIORITY);
	}

	@Test
	void testReliabilityAnalysisWithE2EAndMPRRInitialized() {
	  ra = new ReliabilityAnalysis(e2e, mprr);
	  assertNotNull(ra);
	}
	
	@Test
	void testReliabilityAnalysisWithNumFaultsInitialized() {
	  ra = new ReliabilityAnalysis(numFaults);
	  assertNotNull(ra);
	}
	
	@Test
	void testReliabilityAnalysisWithProgramInitialized() {
	  ra = new ReliabilityAnalysis(program);
	  assertNotNull(ra);
	}
	
	@Test
	void testCorrectE2EForProgram() {
	  assertEquals(e2e, program.getE2e());
	}
	
	@Test
	void testCorrectMPRRForProgram() {
	  assertEquals(mprr, program.getMinPacketReceptionRate());
	}
	
	void testCorrectFileNameForProgram() {
	  assertSame(testFileName, program.getName());
	}
	
	@Test
	void testCorrectNumChannelsForProgram() {
	  assertEquals(numChannels, program.getNumChannels());
	}
	
	@Test
	void testGetReliabilityHeader_ReliabilityHeaderIsInitialized() {
	  ra = new ReliabilityAnalysis(program);
	  assertNotNull(ra.getReliabilityHeader());
	}
	@Test
	void testGetReliabilityHeader_ReliabilityHeaderIsCorrectSize() {
	  ra = new ReliabilityAnalysis(program);
	  int totalNodes = 0;
	  for (Flow f : program.toWorkLoad().getFlows().values()) {
	    totalNodes += f.getNodes().size();
	  }
	  assertEquals(totalNodes, ra.getReliabilityHeader().length);
	}
	
	@Test
	void testGetReliabilityHeader_ReliabilityHeaderHasCorrectValues() {
	  ra = new ReliabilityAnalysis(program);
	  ArrayList<String> flowsWithNodes = new ArrayList<>();
	  for (Flow f : program.toWorkLoad().getFlows().values()) {
	    for (int i=0; i<f.getNodes().size(); i++) {
		  flowsWithNodes.add(f.toString() + ":" + f.getNodes().get(i));
		}
	  }
	  String[] flowsWithNodesAsArray = new String[flowsWithNodes.size()];
	  for (int i=0; i<flowsWithNodesAsArray.length; i++) {
	    flowsWithNodesAsArray[i] = flowsWithNodes.get(i);
	  }
	  assertArrayEquals(flowsWithNodesAsArray, ra.getReliabilityHeader());
	}
	
	@Test
	void testCreateHeaderMap_MapIsActuallyPopulated() {
	  ra = new ReliabilityAnalysis(program);
	  Map<String, Integer> headerMap = ra.createHeaderMap(ra.getReliabilityHeader());
	  assertNotNull(headerMap);
	}
	
	@Test
	void testCreateHeaderMap_MapIsTheCorrectSize() {
	  ra = new ReliabilityAnalysis(program);
	  Map<String, Integer> headerMap = ra.createHeaderMap(ra.getReliabilityHeader());
	  assertEquals(ra.getReliabilityHeader().length, headerMap.size());
	}
	
	@Test
	void testInitializeSourceNodes_ReliabilityTableChangesAfterMethodInvocation() {
	  ra = new ReliabilityAnalysis(program);
	  reliabilities = new ReliabilityTable(ra.getReliabilities().getNumRows(), ra.getReliabilities().getNumColumns());
	  ra.initializeSourceNodes(reliabilities, reliabilities.getNumRows(), 0);
	  ReliabilityTable reliabilitiesCopy = new ReliabilityTable(ra.getReliabilities().getNumRows(), ra.getReliabilities().getNumColumns());
	  assertNotEquals(reliabilities, reliabilitiesCopy);
	}
	
	@Test
	void testResetColumns_ReliabilityTableChangesAfterMethodInvocation() {
	  ra = new ReliabilityAnalysis(program);
	  reliabilities = ra.getReliabilities();
	  ra.resetColumns(ra.getNonSourceColumns(program.toWorkLoad().getNodesInFlow("F0"), 0, ra.createHeaderMap(ra.getReliabilityHeader())), reliabilities, 9);
	  ReliabilityTable reliabilitiesCopy = ra.getReliabilities();
	  assertNotEquals(reliabilities, reliabilitiesCopy);
	}
	
	@Test
	void testGetNonSourceColumns_NonSourceColumnsInFlowArrayListIsActuallyPopulated() {
	  ra = new ReliabilityAnalysis(program);
	  List<Integer> nonSourceCols = ra.getNonSourceColumns(program.toWorkLoad().getNodesInFlow("F0"), 0, ra.createHeaderMap(ra.getReliabilityHeader()));
	  assertNotNull(nonSourceCols);
	}
	
	@Test
	void testGetNonSourceColumns_NonSourceColumnsInFlowArrayListIsCorrectSize() {
	  ra = new ReliabilityAnalysis(program);
	  List<Integer> nonSourceCols = ra.getNonSourceColumns(program.toWorkLoad().getNodesInFlow("F0"), 0, ra.createHeaderMap(ra.getReliabilityHeader()));
	  assertEquals(nonSourceCols.size(), program.toWorkLoad().getNodesInFlow("F0").length - 1);
	}
	
	@Test
	void testGetReliabilities_ReliabilityTableIsActuallyPopulated() {
	  ra = new ReliabilityAnalysis(program);
	  reliabilities = ra.getReliabilities();
	  assertNotNull(reliabilities);
	}
	
	@Test
	void testGetReliabilities_ReliabilityTableHasCorrectNumRows() {
	  ra = new ReliabilityAnalysis(program);
	  reliabilities = ra.getReliabilities();
	  assertEquals(program.getSchedule().getNumRows(), reliabilities.getNumRows());
	}
	
	@Test
	void testGetReliabilities_ReliabilityTableHasCorrectNumColumns() {
	  ra = new ReliabilityAnalysis(program);
	  reliabilities = ra.getReliabilities();
	  int numColumns = 0;
	  for (Flow f : program.toWorkLoad().getFlows().values()) {
	    numColumns += f.getNodes().size();
	  }
	  assertEquals(numColumns, reliabilities.getNumColumns());
	}
	
	@Test
	void testVerifyReliabilities_UnderE2e() {
	    Double e2e = 0.95; 
	    Double mprr = 0.85;
	    WorkLoad workLoad = new WorkLoad(mprr, e2e, "Example1a.txt");

	    Program program = new Program(workLoad, 16, ScheduleChoices.PRIORITY);
	    ReliabilityAnalysis analysis = new ReliabilityAnalysis(program);

	    boolean result = analysis.verifyReliabilities();

	    assertTrue(result);
	}
	@Test
	void testVerifyReliabilities_OverE2e() {
	    Double e2e = 1.0; 
	    Double mprr = 0.85;
	    WorkLoad workLoad = new WorkLoad(mprr, e2e, "Example1a.txt");

	    Program program = new Program(workLoad, 16, ScheduleChoices.PRIORITY);
	    ReliabilityAnalysis analysis = new ReliabilityAnalysis(program);

	    boolean result = analysis.verifyReliabilities();

	    assertFalse(result);
	}

}
