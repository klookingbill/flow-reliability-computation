package edu.uiowa.cs.warp;

import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WorkLoadTest {

	private WorkLoad workload;
	
	@BeforeEach
	void setUp() throws Exception {
		workload = new WorkLoad(0.9, 0.99, "StressTest4.txt");
	}
	
	@AfterEach
	void tearDown() throws Exception {
		
	}
	
	@Test
	void addFlowAddTest() {
		// adds non existing flow into the "StressTest4.txt" file
		workload.addFlow("F11");
		// ArrayList of actual flow names in original order of "StressTest4.txt"
		ArrayList<String> actual = workload.getFlowNamesInOriginalOrder();
		// new ArrayList of expected flows in expected order of "StressTest4.txt"
		ArrayList<String> exp = new ArrayList<> (Arrays.asList("F1", "F5", "F2", "F4", "F3",
				"F6", "F7", "F8", "F9", "F10", "F11"));
		/*
		 * Tests that the expected flow names with an additional flow are in the same order as 
		 * the actual flow names in the original order 
		 */
		assertEquals(exp, actual, "Did not correctly add a flow");
	}
	
	@Test
	void addFlowOverridingTest() {
		// adds pre-existing flow into "StressTest4.txt" for the second time 
		workload.addFlow("F9");
		ArrayList<String> actual = workload.getFlowNamesInOriginalOrder();
		ArrayList<String> exp = new ArrayList<> (Arrays.asList("F1", "F5", "F2", "F4", "F3",
				"F6", "F7", "F8", "F9", "F10", "F9"));
		/*
		 * Tests that the expected ArrayList of flows with a repeated flow are in the same order
		 * as the actual flow names with the repeated flow
		 */
		assertEquals(exp, actual, "Did not override new flow correctly");
	}
	
	@Test
	void addFlowMultipleAddsTest() {
		// adds new flow into "StressTest4.txt"
		workload.addFlow("F11");
		// adds second new flow 
		workload.addFlow("F12");
		// adds third new flow 
		workload.addFlow("F13");
		// adds fourth new flow
		workload.addFlow("F14");
		ArrayList<String> actual = workload.getFlowNamesInOriginalOrder();
		ArrayList<String> exp = new ArrayList<> (Arrays.asList("F1", "F5", "F2", "F4", "F3",
				"F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13", "F14"));
		/*
		 * Tests that the expected ArrayList with an addition of multiple flows is equal
		 * to the actual ArrayList with the new flows
		 */
		assertEquals(exp, actual, "Did not correctly add multiple flows");
	}
	
	@Test
	void addNodeToFlowExistsTest() {
		// add node that already exists in "StressTest4.txt" to "F1"
		workload.addNodeToFlow("F1", "E");
		String[] actual = workload.getNodesInFlow("F1");
		String[] exp = {"B", "C", "D", "E"};
		/*
		 * Tests that each element of the expected array of "F1" with the addition of a new 
		 * node to "F1" but that already exists within "StressTest4.txt" is equal to the 
		 * corresponding elements in the actual "F1" array when calling getNodesInFlow()
		 */
		assertArrayEquals(exp, actual, "Incorrectly inserted existing node into existing flow");
	}
	
	@Test
	void addNodeToFlowDoesntExistTest() {
		// add new node that doesn't exist anywhere in "StressTest4.txt" to "F1"
		workload.addNodeToFlow("F1", "newNode");
		String[] actual = workload.getNodesInFlow("F1");
		String[] exp = {"B", "C", "D", "newNode"};
		/*
		 * Tests that each element of the expected array of "F1" with the addition of a new 
		 * node new to the file is equal to the actual array elements when calling getNodesInFLow()
		 */
		assertArrayEquals(exp, actual, "Incorrectly inserted new node into existing flow");
	}
	
	@Test
	void getTotalTxAttemptsInFlowOneFlowTest() {
		// finds transmission attempts for "F1" and returns as the totalCost
		Integer actual = workload.getTotalTxAttemptsInFlow("F1");
		Integer exp = 4;
		/*
		 * Tests that the expected totalCost of "F1" is equal to the actual cost as found with
		 * getTotalTxAttemptsInFlow()
		 */
		assertEquals(exp, actual, "Produces the incorrect total cost");
	}
	
	@Test
	void getTotalTxAttemptsInFlowMultiFlowsTest() {
		// finds number of transmission attempts for "F1" and returns as totalCost
		Integer flowOneCost = workload.getTotalTxAttemptsInFlow("F1");
		// finds number of transmission attempts for "F2" and returns as totalCost
		Integer flowTwoCost = workload.getTotalTxAttemptsInFlow("F2");
		// finds number of transmission attempts for "F3" and returns as totalCost
		Integer flowThreeCost = workload.getTotalTxAttemptsInFlow("F3");
		// sum of "F1", "F2", and "F3" total cost
		Integer actual = flowOneCost + flowTwoCost + flowThreeCost;
		Integer exp = 21;
		/* 
		 * Tests that the expected sum of multiple flows' costs is equal to the actual sum as
		 * found with getTotalTxAttemptsInFlow() to conclude that the method can accurately find
		 * multiple flows' costs
		 */
		assertEquals(exp, actual, "Incorrectly adds total cost of multiple flows");
	}
	
	@Test
	void getFlowPriorityTest() {
		// sets actual to the priority of node "C" in "F1"
		var actual = workload.getFlowPriority("F1", "C");
		var exp = 0;
		/*
		 * Tests that the expected priority of node "C" in "F1" is equal to the actual
		 * priority as found with getFlowPriority()
		 */
		assertEquals(exp, actual, "Returns incorrect priority for src node");
	}
	
	@Test
	void setFlowPriorityTest() {
		// sets the priority of flow node in "F1" to 5
		workload.setFlowPriority("F1", 5);
		Integer actual = workload.getFlowPriority("F1");
		Integer exp = 5;
		/*
		 * Tests that the expected priority of flow node in "F1" is equal to the actual
		 * priority as set with setFlowPriority()
		 */
		assertEquals(exp, actual, "Does not set current flow priority");
	}
	
	@Test
	void getFlowTxAttemptsPerLinkTest() {
		// sets actual to the priority of "F1"
		Integer actual = workload.getFlowTxAttemptsPerLink("F1");
		Integer exp = 3;
		/* 
		 * Tests that the expected priority of flow "F1" is equal to the actual priority
		 * found with getFlowTxAttemptsPerLink()
		 */
		assertEquals(exp, actual, "Returns incorrect priority of specified flow");
	}

	@Test
	void getFlowTxAttemptsPerLinkEmptyTest() {
		// priority of an empty list, null input
		Integer actual = workload.getFlowTxAttemptsPerLink(null);
		Integer exp = 1;
		/*
		 * Tests that the expected priority of a null input, an empty list, is equal to 
		 * the actual priority of an empty list found with getFlowTxAttemptsPerLink()
		 */
		assertEquals(exp, actual, "Returns incorrect default priority of flow");
	}
	
	@Test
	void setFlowsInRMorderCorrectOrderTest() {
		// names of flows in "StressTest4.txt"
		String[] actual = workload.getFlowNames();
		String[] exp = {"F1", "F5", "F2", "F4", "F3", "F6", "F7", "F8", "F9", "F10"};
		/*
		 * Tests that the expected array of ordered flow names in "StressTest4.txt" is 
		 * equal to the actual order of flow names in this file as found with getFlowNames()
		 */
		assertArrayEquals(actual, exp, "Does not correctly order flows");
	}
	
	
	@Test
	void setFlowsInRMorderAddFlowsTest() {
		// adds new flow "F11" to "StressTest4.txt"
		workload.addFlow("F11");
		// adds new flow "F12" to "StressTest4.txt"
		workload.addFlow("F12");
		// adds new flow "F13" to "StressTest4.txt"
		workload.addFlow("F13");
		// orders flow names with three new flows
		String[] actual = workload.getFlowNames();
		String[] exp = {"F1", "F5", "F2", "F4", "F3", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13"};
		/*
		 * Tests that the expected array of ordered flows of "StressTest4.txt" with three new flows
		 * is equivalent to the actual ordered array with getFlowNames()
		 */
		assertArrayEquals(actual, exp, "Does not correctly order flows with new additional flows");
	}
	
	@Test
	void getNodeNamesOrderedAlphabeticallyTest() {
		// alphabetical order of flow names in "StressTest4.txt"
		String[] actual = workload.getFlowNames();
		String[] exp = {"F1", "F5", "F2", "F4", "F3", "F6", "F7", "F8", "F9", "F10"};
		/*
		 * Tests that the expected array of alphabetically ordered flows is equivalent to the 
		 * actual array order using getFlowNames() to find actual order
		 */
		assertArrayEquals(exp, actual, "Incorrectly orders flows alphabetically with new flows");
	}
	
	@Test
	void getNodeNamesOrderedAlphabeticallyAdditionalFlowsTest() {
		// adds new flow "F11" to "StressTest4.txt"
		workload.addFlow("F11");
		// adds new flow "F12" to "StressTest4.txt"
		workload.addFlow("F12");
		// adds new flow "F13" to "StressTest4.txt"
		workload.addFlow("F13");
		// alphabetically ordered array of flow names in "StressTest4.txt" with three new flows
		String[] actual = workload.getFlowNames();
		String[] exp = {"F1", "F5", "F2", "F4", "F3", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13"};
		/*
		 * Tests that the expected alphabetically ordered array of flows in "StressTest4.txt" with 
		 * three new flows is equal to the actual alphabetical order of flows using getFlowNames()
		 */
		assertArrayEquals(exp, actual, "Incorrectly orders flows alphabetically with new flows");
	}

	
	@Test
	void testGetFlowNames() {
		// Adding flows to the workload
		workload.addFlow("Flow1");
		workload.addFlow("Flow2");
		workload.addFlow("Flow3");
		// Expected array after adding new flows
		String[] expectedFlowNames = {"F1", "F5", "F2", "F4", "F3", "F6", "F7", "F8", "F9", "F10", "Flow1", "Flow2", "Flow3"};
		// Get actual flow names from workload
		String[] actualFlowNames = workload.getFlowNames();
		// Assert actual flow names match expected names
		assertArrayEquals(expectedFlowNames, actualFlowNames, "FlowNames are incorrect");
	}

	@Test
	void testGetFlowNamesEmpty() {
		// Creating empty workload
		WorkLoad emptyWorkload = new WorkLoad(0.9, 0.99, "Test1.txt");
		// Get flow names from the empty workload
		String[] actualFlowNames = emptyWorkload.getFlowNames();
		// Assert the array of flow names is empty
		assertEquals(0, actualFlowNames.length, "Array is not empty!");
	}
	
	@Test
	void testGetNodeIndexNonExistentNode() {
		// Expected index for a non-existent node is 0
		Integer expectedIndex = 0;
		// Get the actual index of a non-existent node "Z"
		Integer actualIndex = workload.getNodeIndex("Z");
		// Assert that the actual index matches the expected index
		assertEquals(expectedIndex, actualIndex, "The method should return 0 for a non-existent node");
	}
	
	@Test
    void testGetNodeIndexExistingNode() {
		// Get the index of existing node "C"
		Integer actualIndex = workload.getNodeIndex("C");
		// Assert that the actual index of node "C" is correct
		assertEquals(1, actualIndex, "The index for node C is incorrect");
	}

	@Test
	void testGetNodesInFlowValidFlowF1() {
		// Expected nodes for the valid flow "F1"
		String[] expectedNodes = {"B", "C", "D"};
		// Getting the actual nodes in the flow "F1"
		String[] actualNodes = workload.getNodesInFlow("F1");
		// Assert that the actual nodes match the expected nodes
		assertArrayEquals(expectedNodes, actualNodes, "Incorrect nodes for flow F1");
	}

	@Test
	void testGetNodesInFlowNonExistentFlow() {
		// Expected nodes for a non-existent flow is an empty array
		String[] expectedNodes = new String[0];  
		// Getting the actual nodes in the non-existent flow "F99"
        String[] actualNodes = workload.getNodesInFlow("F99");
        // Asserting that the actual nodes match the expected empty array
        assertArrayEquals(expectedNodes, actualNodes, "Expected empty array for non-existent flow F99");
    }
	
	@Test
    void testGetNodesInFlowNullFlow() {
		// Expected nodes for a null flow name is an empty array
        String[] expectedNodes = new String[0];
        // Getting the actual nodes in a null flow
        String[] actualNodes = workload.getNodesInFlow(null);
        // Asserting that the actual nodes match the expected empty array
        assertArrayEquals(expectedNodes, actualNodes, "Expected empty array for null flow name");
    }

	@Test
    void testGetHyperPeriodValidFlows() {
		// Expected hyper period for valid flows
		int expectedHyperPeriod = 300;
		// Getting the actual hyper period from the workload
        int actualHyperPeriod = workload.getHyperPeriod();
        // Asserting that the actual hyper period matches the expected value
        assertEquals(expectedHyperPeriod, actualHyperPeriod, "HyperPeriod calculation is incorrect for valid flows");
	}
	
	@Test
	void testGetHyperPeriodEmptyFlow() {
		// Creating a workload with no flows
        workload = new WorkLoad(0.9, 0.99, "Test1.txt");  
        // Expected hyper period when no flows exist
        int expectedHyperPeriod = 1;
        // Getting the actual hyper period from the empty workload
        int actualHyperPeriod = workload.getHyperPeriod();
        // Asserting that the actual hyper period matches the expected value
        assertEquals(expectedHyperPeriod, actualHyperPeriod, "HyperPeriod should be 1 when no flows exist");
	}

	@Test
	void testMaxFlowLengthStressTest4() {
		// Expected max flow length for StressTest4
		Integer expectedMaxFlowLength = 8;
		// Getting the actual max flow length from the workload
		Integer actualMaxFlowLength = workload.maxFlowLength();
		// Asserting that the actual max flow length matches the expected value
		assertEquals(expectedMaxFlowLength, actualMaxFlowLength, "Max flow length is incorrect for StressTest4.");
	}

	@Test
	void testMaxFlowLengthNoFlows() {
		// Creating an empty workload
		WorkLoad emptyWorkload = new WorkLoad(0.9, 0.99, "Test1.txt");
		// Expected max flow length when there are no flows
		Integer expectedMaxFlowLength = 0;
		// Getting the actual max flow length from the empty workload
		Integer actualMaxFlowLength = emptyWorkload.maxFlowLength();
		// Asserting that the actual max flow length matches the expected value
		assertEquals(expectedMaxFlowLength, actualMaxFlowLength, "Max flow length should be 0 for no flows.");
	}

	@Test
    void testGetFlowDeadlineValidFlow() {
		// Expected deadline for the valid flow "F1"
        Integer expectedDeadline = 20;
        // Getting the actual deadline for the flow "F1"
        Integer actualDeadline = workload.getFlowDeadline("F1");
        // Asserting that the actual deadline matches the expected value
        assertEquals(expectedDeadline, actualDeadline, "The deadline for flow F1 should be 20.");
	}

	@Test
	void testGetFlowDeadlineInvalidFlow() {
		// Getting the actual deadline for a non-existent flow "InvalidFlow"
		Integer actualDeadline = workload.getFlowDeadline("InvalidFlow");
		// Expected deadline for a non-existent flow
		Integer expectedDeadline = 100;
		// Asserting that the actual deadline matches the expected value
		assertEquals(expectedDeadline, actualDeadline, "The deadline for a non-existent flow should be 100.");
	}

	@Test
	void testSetFlowDeadlineValidFlow() {
		// New deadline to set for the valid flow "F1"
		Integer newDeadline = 50;
		// Setting the new deadline for the flow "F1"
		workload.setFlowDeadline("F1", newDeadline);
		// Getting the actual deadline after setting the new value
		Integer actualDeadline = workload.getFlowDeadline("F1");
		// Asserting that the actual deadline matches the new deadline
		assertEquals(newDeadline, actualDeadline, "The deadline for flow F1 should be set to 50.");
	}

	@Test
	void testSetFlowDeadlineUpdateExistingFlow() {
		// Initial deadline to set for the valid flow "F1"
		Integer initialDeadline = 30;
		workload.setFlowDeadline("F1", initialDeadline);
		// Updated deadline to set for the flow "F1"
		Integer updatedDeadline = 75;
		// Setting the updated deadline for the flow "F1"
		workload.setFlowDeadline("F1", updatedDeadline);
		// Getting the actual deadline after updating the value
		Integer actualDeadline = workload.getFlowDeadline("F1");
		// Asserting that the actual deadline matches the updated deadline
		assertEquals(updatedDeadline, actualDeadline, "The deadline for flow F1 should be updated to 75.");
	}
	
	@Test
	public void testGetNumTxAttemptsPerLink_ExistingFlow() {
		// Flow name to test
		String flowName = "F1";
		// Getting the transmission attempts per link for the existing flow
		Integer[] txAttemptsPerLink = workload.getNumTxAttemptsPerLink(flowName);
		// Asserting that the array is not null for an existing flow
		assertNotNull(txAttemptsPerLink, "The Tx attempts per link array should not be null for an existing flow.");
		// Asserting that the array size is as expected
		assertEquals(3, txAttemptsPerLink.length, "The array size should be equal to the number of links in the flow minus the total cost.");
		// Asserting that the Tx attempts per link match expected values
		assertArrayEquals(new Integer[]{3, 3, 0}, txAttemptsPerLink, "The Tx attempts per link should match expected values.");
	}
	
	@Test
	public void testGetNumTxAttemptsPerLink_MultipleFlows() {
		// Flow names to test
		String flowName1 = "F1";  
		String flowName2 = "F5";
		// Getting the transmission attempts per link for both flows
		Integer[] txAttemptsFlow1 = workload.getNumTxAttemptsPerLink(flowName1);
		Integer[] txAttemptsFlow2 = workload.getNumTxAttemptsPerLink(flowName2);
		// Asserting that the arrays are not null
		assertNotNull(txAttemptsFlow1, "The Tx attempts per link for F1 should not be null.");
		assertNotNull(txAttemptsFlow2, "The Tx attempts per link for F5 should not be null.");
		// Asserting that the array sizes are as expected
		assertEquals(3, txAttemptsFlow1.length, "The array size for F1 should match the number of links minus the total cost.");
		assertEquals(5, txAttemptsFlow2.length, "The array size for F5 should match the number of links minus the total cost.");
	}
}
