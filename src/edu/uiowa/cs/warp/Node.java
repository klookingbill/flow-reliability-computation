package edu.uiowa.cs.warp;

import java.util.ArrayList;
import java.util.Set;

public class Node extends SchedulableObject implements Comparable <Node>  {
private static final Integer DEFAULT_CHANNEL = 0;

private Integer channel;

public void setChannel(Integer value) {
   this.channel = value;
}

public Integer getChannel() {
   return this.channel;
}

private Integer index;

public void setIndex(Integer value) {
   this.index = value;
}

public Integer getIndex() {
   return this.index;
}

private ArrayList <Edge> edges;

public ArrayList <Edge> getEdges() {
   return this.edges;
}

private Set<String> conflicts;

public Set<String> getConflicts() {
   return this.conflicts;
}

/*package*/ Node (String name, Integer priority, Integer index) {
super();
    	setName(name);
    	setPriority(priority);
    	this.index = index;
    	this.channel = DEFAULT_CHANNEL;
    	this.edges = new ArrayList<Edge>();
    	this.conflicts = new HashSet<String>();
    }

@Override
public String toString ()
{
return getName();
    }
public void addConflict(String name) {
conflicts.add(name);
    }

public Integer numEdges() {
return edges.size();
    }

@Override
public void print (String headerMsg)
{
System.out.printf("\n%s",headerMsg);
   		this.print();
   	}
@Override
public void print ()
{
super.print();
   		System.out.print("Edge info for this partiion\n");
   		for (Edge edge: edges) {
   			edge.print();
   		}
   	}
@Override
public int compareTo (Node node)
{
return node.getPriority() > this.getPriority() ? -1 : 1;
    }
public void addEdge(Edge edge) {
edges.add(edge);
    }

}
