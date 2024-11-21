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
	  
	  //something that implements warp interface // 
		  workload = new WorkLoad( 0.9,	0.99, "StressTest4.txt"); 
		  numChannels = 16; 
		  warp = SystemFactory.create(workload, numChannels, ScheduleChoices.PRIORITY);
	  
	  
	  viz = new ReliabilityVisualization(warp);
	  
	  }
	  
	  @Test
		public void createColumnHeaderTest() {
			
			String[] header = viz.createColumnHeader();
			for (int i = 0; i < header.length; i++) {
				System.out.println(header[i]);
			}
			//System.out.println(header);
			 //assertNotNull(header);
			
		}
	  
	  @Test
	  public void createHeaderTest() {
		  Description header = viz.createHeader();
		  System.out.println(header);
		  assertEquals(header, "1");
	  }
	  

	  


}
