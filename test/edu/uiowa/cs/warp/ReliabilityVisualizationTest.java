package edu.uiowa.cs.warp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import edu.uiowa.cs.warp.SystemAttributes.ScheduleChoices;


class ReliabilityVisualizationTest {

	private WorkLoad workload;
	private Integer numChannels;
	private WarpInterface warp;
	private ReliabilityAnalysis ra;
	private ReliabilityVisualization viz;
	private static final String OBJECT_NAME = "Reliability Analysis";
	
	
	  @BeforeEach 
	  void setUp(){ // e2e = 1, min = 1
		 workload = new WorkLoad(0.9,	0.99, "StressTest4.txt"); 
		 numChannels = 16; 
		 warp = SystemFactory.create(workload, numChannels, ScheduleChoices.PRIORITY);
		 ra = warp.toReliabilityAnalysis();
		 viz = new ReliabilityVisualization(warp);
	  
	  }
	  @Test
	  public void createTitle_TitleCreated() {
		  //ensure that title is created and has value.
		  String titleName = warp.getName();
		  assertNotNull(titleName);
		  assertTrue(titleName.length() != 0);
	  }
	  
	  @Test
	  public void createColumnHeaderTest_NonEmptyHeader() {
		//ensure that header is not empty after creation.
		String[] header = viz.createColumnHeader();	
		assert(header.length != 0);
			
	  }
	  
	  @Test
	  public void createColumnHeaderTest_containsCorrectValues() {
		  //Ensure that the first line in the column header reads the correct line, "Time Slot".
		  String[] header = viz.createColumnHeader();
		  String firstLine = header[0];
		  assertEquals(firstLine, "Time Slot");
	  }
	  
	  @Test 
	  void createColumnHeaderTest_containsCorrectColumnNames() {
		//Ensure that the column header contains the correct amt of column names, excluding the first line ("Time Slot")
		String[] header = viz.createColumnHeader();
		int headerCount = 0;
		for (int i = 1; i < header.length; i++) {
			headerCount +=1;
		}
		assertNotEquals(headerCount, 0);
		
		//Compare string of header column names to intended correct column names and ensure printed in same order.
		String[] correctColumns = warp.toWorkload().getNodeNamesOrderedAlphabetically(); //pull directly from warp
		String[] inputColumnNames = new String[header.length-1]; //pull from header
		for (int i = 1; i < header.length; i++) {
		  inputColumnNames[i - 1] = header[i];
		}
		String correctColumnsStr = (String.join(",", correctColumns)).replace(",", "");
		String inputColumnNamesStr = (String.join(",", inputColumnNames)).replace(",", "");
		
		//make sure both strings are the same length and contain the same values.
		assertEquals(correctColumnsStr.length(), inputColumnNamesStr.length());
		assertEquals(correctColumnsStr, inputColumnNamesStr);
		  
	  }
	  
	  @Test
	  public void createHeader_testIfHeaderActuallyCreated() {
		  //Ensure the header is actually created.
		  Description header = viz.createHeader();
		  assertNotNull(header);
	  }
	  
	  @Test
	  public void createHeader_testCorrectTitle() {
		  //Test to ensure that the correct title is created in method.
		  Description header = viz.createHeader();
		  assertEquals(header.get(0), String.format(OBJECT_NAME + " for graph %s\n", warp.getName()));
	  }
	  
	  @Test
	  public void createHeader_testCorrectSchedulerName() {  
		  //Ensure that scheduler name is correct.
		  Description header = viz.createHeader();
		  assertEquals(header.get(1), String.format("Scheduler Name: %s\n", warp.getSchedulerName()));
	  }
	  
	  @Test
	  public void createHeader_testIfNumFaultsAre0() {
		  //Test to make sure the num of faults is 0.
		  assertEquals(warp.getNumFaults(), 0);
	  }
	  
	  @Test
	  public void createHeader_testCorrectM() {
		  //Ensure that correct MinPacketReceptionRate is created.
		  Description header = viz.createHeader();
		  assertEquals(header.get(2), String.format("M: %s\n", warp.getMinPacketReceptionRate()));
	  }
	  
	  @Test
	  public void createHeader_testCorrectE2e() {
		  //Ensure correct e2e is created.
		  Description header = viz.createHeader();
		  assertEquals(header.get(3), String.format("E2E: %s\n", warp.getE2e()));
	  }
	  
	  @Test
	  public void createHeader_testCorrectnChannels() {
		  //Ensure correct num channels is created.
		  Description header = viz.createHeader();
		  assertEquals(header.get(4), String.format("nChannels: %s\n", warp.getNumChannels()));
	  }
	  
	  @Test
	  public void createFooter_testIfFooterActuallyCreated() {
		  //Ensure footer contains value and is not null.
		  Description footer = viz.createFooter();
		  assertNotNull(footer);
	  }
	  
	  @Test
	  public void createFooter_testIfWarpDeadlinesMet() {
		  //Ensure warp deadlines are met.
		  assertTrue(warp.deadlinesMet());
		  
	  }
	  
	  @Test
	  public void createFooter_testCorrectDeadlineMsg() {
		  //Ensure footer displays correct deadline message.
		  Description footer = viz.createFooter();
		  assertEquals(footer.get(0), "// All flows meet their deadlines\n");
	  }
	  
	 
	  
	
	  
	  

}
