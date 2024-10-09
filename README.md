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
addFlowAddTest
<br>addFlowOverridingTest
<br>addFlowMultipleAddsTest
### Tests for addNodeToFlow()
addNodeToFlowExistsTest
<br>addNodeToFlowDoesntExistTest
### Tests for getTotalTxAttemptsInFlow()
getTotalTxAttemptsInFlowOneFlowTest
<br>getTotalTxAttemptsInFlowMultiFlowsTest
### Tests for getFlowPriority()
getFlowPriorityTest
### Tests for setFlowPriority()
setFlowPriorityTest
### Tests for getFlowTxAttemptsPerLink()
getFlowTxAttemptsPerLinkTest
<br>getFlowTxAttemptsPerLinkEmptyTest
### Tests for setFlowsInRMorder()
setFlowsInRMorderCorrectOrderTest
<br>setFlowsInRMorderAddFlowsTest
### Tests for getNodeNamesOrderedAlphabetically()
getNodeNamesOrderedAlphabeticallyTest
<br>getNodeNamesOrderedAlphabeticallyAdditionalFlowsTest
