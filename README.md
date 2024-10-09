# CS2820 Fall 2024 WARP Project Code

This code base will be used for the University of Iowa CS 2820 Introduction to Software
Development course. The code was developed by Steve Goddard for the WARP sensor network 
research project. It was first written in Swift and rewritten in Java. It was then 
rewritten again in an object-oriented programming style. It was a quick
hack, and it needs a lot of cleanup and refactoring. A perfect code base to teach
the value of software development fundamentals!
<br>
<br>
Adding arguments to the Run Configuration allows you to execute more with your code. 
-i gives an input file and -o an output. -v is verbose, -sch priority is the priority 
scheduler, and --all runs all options. Whenever using --all, you will want to use -sch 
priority or change Warp to not run every option in the --all, otherwise, you will end up 
with a load of files. 
<br>
<br>
Java Doc comments made by jcbates: Workload.java, Flow.java, ProgramVisualization, and Program.java
<br>
<br>
Java Doc comments made by klookingbill: Warp.java and VisualizationImplementation.java  
10/8/2024
<br>
# **Junit Tests Completed by jcbates:**
### Tests for setFlowDeadline()
testSetFlowDeadlineUpdateExistingFlow 
<br>testSetFlowDeadlineValidFlow

### Tests for getFlowDeadline()
<br>testGetFlowDeadlineInvalidFlow
<br>testGetFlowDeadlineValidFlow

### Tests for testMaxFlowLength()
<br>testMaxFlowLengthNoFlows 
<br>testMaxFlowLengthStressTest4

### Tests for getHyperPeriod()
<br>testGetHyperPeriodEmptyFlow
<br>testGetHyperPeriodValidFlows

### Tests for getNodesInFlow()
<br>testGetNodesInFlowNullFlow 
<br>testGetNodesInFlowNonExistentFlow
<br>testGetNodesInFlowValidFlowF1 

### Tests for getNodesInFlow()
<br>testGetNodeIndexExistingNode
<br>testGetNodeIndexNonExistentNode

### Tests for getFlowNames()
<br>testGetFlowNamesEmpty
<br>testGetFlowNames
<br>
<br>
# **Junit Test Completed by klookingbill:**
### Tests for addFlow()
These tests check that the method can successfully add a non-existing flow to "StressTest4.txt" file, add a pre-existing flow to the file, and add multiple new flows to the file. 
<br>
<br>addFlowAddTest
<br>addFlowOverridingTest
<br>addFlowMultipleAddsTest
### Tests for addNodeToFlow()
These methods tests that a node that already exists in the "StressTest4.txt" file can successfully be added to a flow and that a node that does not already exist in the file is also correctly added to a flow.
<br>
<br>addNodeToFlowExistsTest
<br>addNodeToFlowDoesntExistTest
### Tests for getTotalTxAttemptsInFlow()
These tests check that the total transmission attempts is correctly calculated for a single flow and for multiple nodes. 
<br>
<br>getTotalTxAttemptsInFlowOneFlowTest
<br>getTotalTxAttemptsInFlowMultiFlowsTest
### Tests for getFlowPriority()
Because of the simplicity of this method, I have one test that makes sure the priority of a specified node is correctly computed.
<br>
<br>getFlowPriorityTest
### Tests for setFlowPriority()
My test checks that the priority of the given node is equal to the input of the given priority.
<br>
<br>setFlowPriorityTest
### Tests for getFlowTxAttemptsPerLink()
These tests check that the method correctly finds the priority of the specified flow and that it will also find the correct priority of an empty flow.
<br>
<br>getFlowTxAttemptsPerLinkTest
<br>getFlowTxAttemptsPerLinkEmptyTest
### Tests for setFlowsInRMorder()
One of my tests checks that the method correctly orders the flows in a given file with just the flows in the file. The second test checks that the method still accurately orders the flows even when adding in new flows.
<br>
<br>setFlowsInRMorderCorrectOrderTest
<br>setFlowsInRMorderAddFlowsTest
### Tests for getNodeNamesOrderedAlphabetically()
My first test ensures that the method correctly orders the initially existing flows in the file in alphabetical order. The second test checks that the method still orders correctly even when adding in new flows to the file. 
<br>
<br>getNodeNamesOrderedAlphabeticallyTest
<br>getNodeNamesOrderedAlphabeticallyAdditionalFlowsTest
