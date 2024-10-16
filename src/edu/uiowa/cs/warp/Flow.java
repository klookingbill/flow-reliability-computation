package edu.uiowa.cs.warp;

import java.util.ArrayList;

/**
* @author sgoddard
 * @author jcbates
 * Allows for Schedulable Objects to be compared to flow charataristics and tracks faults  
 *  * */public class Flow extends SchedulableObject implements Comparable <Flow>  {
private static final Integer UNDEFINED = -1;

private static final Integer DEFAULT_FAULTS_TOLERATED = 0;

private static final Integer DEFAULT_INDEX = 0;

private static final Integer DEFAULT_PERIOD = 100;

private static final Integer DEFAULT_DEADLINE = 100;

private static final Integer DEFAULT_PHASE = 0;

/*package*/ Integer initialPriority = UNDEFINED;
/*package*/ Integer index;
/*package*/ Integer numTxPerLink;
/*package*/ ArrayList <Node> nodes;
/*package*/ ArrayList <Integer> linkTxAndTotalCost;
/*package*/ ArrayList <Edge> edges;
/*package*/ Node nodePredecessor;
/*package*/ Edge edgePredecessor;
/**
* Constructor that sets name, priority, and index *//*package*/ Flow (String name, Integer priority, Integer index) {
super(name, priority, DEFAULT_PERIOD, DEFAULT_DEADLINE, DEFAULT_PHASE);
    	this.index = index;
        /*
         *  Default numTxPerLink is 1 transmission per link. Will be updated based
         *  on flow updated based on flow length and reliability parameters
         */
        this.numTxPerLink = DEFAULT_FAULTS_TOLERATED + 1; 
        this.nodes = new ArrayList<>();
        this.edges  = new ArrayList<>();
        this.linkTxAndTotalCost = new ArrayList<>();
        this.edges = new ArrayList<>();	
        this.nodePredecessor = null;
        this.edgePredecessor = null;
    }

/**
* Constructor *//*package*/ Flow () {
super();
    	this.index = DEFAULT_INDEX;
    	/*
    	 *  Default numTxPerLink is 1 transmission per link. Will be updated based
    	 *  on flow updated based on flow length and reliability parameters
    	 */
    	this.numTxPerLink = DEFAULT_FAULTS_TOLERATED + 1; 
    	this.nodes = new ArrayList<>();
    	this.linkTxAndTotalCost = new ArrayList<>();
    	this.edges = new ArrayList<>();
    	this.nodePredecessor = null;
        this.edgePredecessor = null;
    }

/**
* @return the initialPriority */public Integer getInitialPriority() {
return initialPriority;
   	}

/**
* @return the index */public Integer getIndex() {
return index;
   	}

/**
* @return the numTxPerLink */public Integer getNumTxPerLink() {
return numTxPerLink;
   	}

/**
* @return the nodes */public ArrayList <Node> getNodes() {
return nodes;
   	}

/**
* @return the nodes */public ArrayList <Edge> getEdges() {
return edges;
   	}

/**
* @return the linkTxAndTotalCost */public ArrayList <Integer> getLinkTxAndTotalCost() {
return linkTxAndTotalCost;
   	}

/**
* @param initialPriority the initialPriority to set */public void setInitialPriority(Integer initialPriority) {
this.initialPriority = initialPriority;
   	}

/**
* @param index the index to set */public void setIndex(Integer index) {
this.index = index;
   	}

/**
* @param numTxPerLink the numTxPerLink to set */public void setNumTxPerLink(Integer numTxPerLink) {
this.numTxPerLink = numTxPerLink;
   	}

@Override
public String toString ()
{
return getName();
    }
/**
* @param takes in an edge 
 * 	 * Add and edge to the flow. */public void addEdge(Edge edge) {
/* set predecessor and add edge to flow */
   		edge.setPredecessor(edgePredecessor);
   		edges.add(edge);
   		/* update predecessor for next edge added */
   		edgePredecessor = edge;
   	}

/**
* @param takes in a node
 * 	 * Add and edge to the flow. */public void addNode(Node node) {
/* set predecessor and add edge to flow */
   		node.setPredecessor(nodePredecessor);
   		nodes.add(node);
   		/* update predecessor for next edge added */
   		nodePredecessor = node;
   	}

@Override
public int compareTo (Flow flow)
{
return flow.getPriority() > this.getPriority() ? -1 : 1;
    }
/**
* @param nodes the nodes to set */public void setNodes(ArrayList <Node> nodes) {
this.nodes = nodes;
   	}

/**
* @param linkTxAndTotalCost the linkTxAndTotalCost to set */public void setLinkTxAndTotalCost(ArrayList <Integer> linkTxAndTotalCost) {
this.linkTxAndTotalCost = linkTxAndTotalCost;
   	}

}
