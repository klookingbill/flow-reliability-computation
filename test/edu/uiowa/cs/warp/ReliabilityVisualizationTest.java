package edu.uiowa.cs.warp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

//import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import edu.uiowa.cs.warp.SystemAttributes.ScheduleChoices;


class ReliabilityVisualizationTest {

	private WorkLoad workload;
	private Integer numChannels;
	private WarpInterface warp;
	private ReliabilityVisualization viz;
	
	
	  @BeforeEach void setUp(){ // e2e = 1, min = 1
		 workload = new WorkLoad( 0.9,	0.99, "StressTest4.txt"); 
		 numChannels = 16; 
		 warp = SystemFactory.create(workload, numChannels, ScheduleChoices.PRIORITY);
		 viz = new ReliabilityVisualization(warp);
	  
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
	  
	  @Test void createColumnHeaderTest_containsCorrectColumnNames() {
		  //Ensure that the column header contains the correct amt of column names, excluding the first line ("Time Slot")
		  String[] header = viz.createColumnHeader();
		  int headerCount = 0;
		  for (int i = 1; i < header.length; i++) {
			  headerCount +=1;
		  }
		  assertNotEquals(headerCount, 0);
		  //In stresstest4, the correct column names will be capitals "A"-"L", containing 12 values total
		  assertEquals(headerCount, 12);
		  
		  //Compare string of header column names to intended correct column names and ensure printed in same order.
		  String correctColumns = "ABCDEFGHIJKL";
		  String[] inputColumnNames = new String[header.length-1];
		  for (int i = 1; i < header.length; i++) {
			  inputColumnNames[i - 1] = header[i];
		  }
		  String inputColumnNamesString = String.join(",", inputColumnNames).replace(",", "");
		  assertEquals(correctColumns, inputColumnNamesString);
		  
	  }
	  
	  @Test
	  public void createHeaderTest() {
		  Description header = viz.createHeader();
		  //System.out.println(header);
		  //assertEquals(header, "1");
	  }
	  

	  


}
