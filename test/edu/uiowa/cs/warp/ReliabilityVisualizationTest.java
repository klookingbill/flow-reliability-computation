package edu.uiowa.cs.warp;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import edu.uiowa.cs.warp.SystemAttributes.ScheduleChoices;


public class ReliabilityVisualizationTest {

	private WorkLoad workload;
	private Integer numChannels;
	private WarpInterface warp;
	private ReliabilityVisualization viz;
	
	@BeforeEach
	void setUp() throws Exception {
		// e2e = 1, min = 1
		
		//something that implements warp interface
		workload = new WorkLoad( 0.9, 0.99, "StressTest4.txt");
		numChannels = 16;
		warp = SystemFactory.create(workload, numChannels, ScheduleChoices.PRIORITY);

		viz = new ReliabilityVisualization(warp);

	}
	
	
	@Test
	public void createColumnHeaderTest() {
		
		
		String[] header = viz.createColumnHeader();
		assertNotNull(header);
	}

}
