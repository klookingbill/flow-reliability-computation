package edu.uiowa.cs.warp;


public class Edge extends SchedulableObject implements Comparable <Edge>  {
private static final String UNKNOWN = "Unknown";

private String flow;

public void setFlow(String value) {
   this.flow = value;
}

public String getFlow() {
   return this.flow;
}

private String src;

public void setSrc(String value) {
   this.src = value;
}

public String getSrc() {
   return this.src;
}

private String snk;

public void setSnk(String value) {
   this.snk = value;
}

public String getSnk() {
   return this.snk;
}

private Integer instance;

public void setInstance(Integer value) {
   this.instance = value;
}

public Integer getInstance() {
   return this.instance;
}

private String coordinator;

public void setCoordinator(String value) {
   this.coordinator = value;
}

public String getCoordinator() {
   return this.coordinator;
}

/**
 * <pre>
 *           1..1     1..1
 * Edge ------------------------> EdgeState
 *           &lt;       state
 * </pre>
 */
private EdgeState state;

public void setState(EdgeState value) {
   this.state = value;
}

public EdgeState getState() {
   return this.state;
}

private Integer numTx;

public Integer getNumTx() {
   return this.numTx;
}

/*package*/ Edge () {
super();
   		this.flow = UNKNOWN;
   		this.src = UNKNOWN;
   		this.snk = UNKNOWN;
   		this.instance = 0;
   		this.coordinator = UNKNOWN;
   		this.state = EdgeState.NOT_READY;
   		this.numTx = 0;
   	}

/*package*/ Edge (String flow, String src, String snk, Integer priority, Integer period, Integer deadline, Integer phase, Integer numTx) {
super(priority, period, deadline, phase);
   		this.flow = flow;
   		this.src =src;
   		this.snk = snk;
   		this.instance = 0;
   		this.coordinator = UNKNOWN;
   		this.state = EdgeState.NOT_READY;	
   		this.numTx = numTx;
   	}

@Override
public String toString ()
{
String result = String.format("%s:(%s,%s)", 
    			this.flow, this.src, this.snk);
        return result;
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
   		System.out.printf("\tFlow:%s\n", this.flow);
   		System.out.printf("\tSrc:%s\n", this.src);
   		System.out.printf("\tSnk:%s\n", this.snk);
   	}
@Override
public int compareTo (Edge edge)
{
return edge.getPriority() > this.getPriority() ? 1 : -1;
    }
private enum EdgeState(){
   RELEASED, EXECUTING, NOT_READY;}}
