package edu.uiowa.cs.warp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class WorkLoadTest {

	private WorkLoad workload;
	// put in beforeall & afterall 
	
	@BeforeEach
	void setUp() throws Exception {
		workload = new WorkLoad(0.9, 0.99, "StressTest4.txt");
	}
	
	@AfterEach
	void tearDown() throws Exception {
		
	}
	
	@Test
	void addFlowTest() {
		workload.addFlow("F11");
		ArrayList<String> actual = workload.getFlowNamesInOriginalOrder();
		ArrayList<String> exp = new ArrayList<> (Arrays.asList("F1", "F5", "F2", "F4", "F3",
				"F6", "F7", "F8", "F9", "F10", "F11"));
		assertEquals(exp, actual, "Did not correctly add a flow");
	}
	
	@Test
	void addFlowOverridingTest() {
		workload.addFlow("F9");
		ArrayList<String> actual = workload.getFlowNamesInOriginalOrder();
		ArrayList<String> exp = new ArrayList<> (Arrays.asList("F1", "F5", "F2", "F4", "F3",
				"F6", "F7", "F8", "F9", "F10", "F9"));
		assertEquals(exp, actual, "Did not override new flow correctly");
	}
	
	@Test
	void addFlowMultipleAddsTest() {
		workload.addFlow("F11");
		workload.addFlow("F12");
		workload.addFlow("F13");
		workload.addFlow("F14");
		ArrayList<String> actual = workload.getFlowNamesInOriginalOrder();
		ArrayList<String> exp = new ArrayList<> (Arrays.asList("F1", "F5", "F2", "F4", "F3",
				"F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13", "F14"));
		assertEquals(exp, actual, "Did not correctly add multiple flows");
	}
	
	@Test
	void addNodeToFlowExistsTest() {
		workload.addNodeToFlow("F1", "E");
		String[] actual = workload.getNodesInFlow("F1");
		String[] exp = {"B", "C", "D", "E"};
		assertArrayEquals(exp, actual, "Did not correctly insert new node into existing flow");
	}
	
	@Test
	void addNodeToFlowDoesntExistTest() {
		workload.addNodeToFlow("F11", "A");
		String[] actual = workload.getNodesInFlow("F11");
		String[] exp = {"A"};
		assertArrayEquals(exp, actual, "Incorrectly inserted new node into new flow");
	}
	
	//@Test 
	@Timeout(5) 
	void testGetFlowNames() {
	     workload.addFlow("Flow1");
	     workload.addFlow("Flow2");
	     workload.addFlow("Flow3");
	     String[] expectedFlowNames = {"Flow1", "Flow2", "Flow3"};
	     String[] actualFlowNames = workload.getFlowNames();
	     assertEquals(expectedFlowNames, actualFlowNames, "FlowNames are incorrect");
	 }

	//@Test
	@Timeout(5) 
	void testGetFlowNamesEmpty() {
		 String[] actualFlowNames = workload.getFlowNames();
	     assertEquals(new String[0], actualFlowNames, "Workload is Empty");
	}
}
