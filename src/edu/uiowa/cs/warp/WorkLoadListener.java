package edu.uiowa.cs.warp;

import edu.uiowa.cs.warpdsls.WARPBaseListener;
import WARPParser.WarpNameContext;
import WARPParser.FlowNameContext;
import WARPParser.PriorityContext;
import WARPParser.PeriodContext;
import WARPParser.DeadlineContext;
import WARPParser.PhaseContext;
import WARPParser.SrcNodeContext;
import WARPParser.SnkNodeContext;
import WARPParser.FlowContext;

/**
* @author sgoddard
 *  * *//*package*/ final class WorkLoadListener extends WARPBaseListener {
/*package*/ WorkLoad workLoad;
/*package*/ String currentFlow;
public void testPrintStdOut(String msg) {
System.out.printf("\n In WARPworkLoad Listener!!\n");
    	System.out.printf(msg);
    }

private WorkLoadListener (WorkLoad workLoad) {
this.workLoad = workLoad; // used to populate the workLoad as the input file is read
        this.currentFlow = new String();
     // Read input file and build AST of graphFile
        try {
        	CharStream inputGraph = CharStreams.fromString(workLoad.toString());
        	var lexer = new WARPLexer(inputGraph);
        	var tokens = new CommonTokenStream(lexer);
        	var parser = new WARPParser(tokens);
        	ParseTree warpTree = parser.warp(); // begin parsing at rule 'warp'
        	var warp = new ParseTreeWalker();
        	 // Now populate build the workLoad by walking the input Graph tokens
        	warp.walk(this, warpTree);
        } catch (Exception e) {
        	e.printStackTrace();
        	System.err.println("ERROR: Unable to parse graphFile" + e.getMessage());
        }
    }

public static void buildNodesAndFlows(WorkLoad workLoad) {
/*
    	 * Create the listener object, which will read the workLoad 
    	 * description and then buid the node and flow objects that
    	 * instantiates the workLoad. This object is not needed
    	 * after that.
    	 */
    	new WorkLoadListener(workLoad); 
    }

@Override
public void enterWarpName (WarpNameContext ctx)
{
var name = ctx.getText();  // get the name of the graph 
        workLoad.setName(name); // store the  name for later reference
    }
@Override
public void enterFlowName (FlowNameContext ctx)
{
currentFlow = ctx.getText();
        workLoad.addFlow(currentFlow);
    }
@Override
public void exitPriority (PriorityContext ctx)
{
var priority = Integer.parseInt(ctx.getText());  // get priority from the AST
        workLoad.setFlowPriority(currentFlow, priority);
    }
@Override
public void exitPeriod (PeriodContext ctx)
{
var period = Integer.parseInt(ctx.getText());// get period from the AST
        workLoad.setFlowPeriod(currentFlow, period);
    }
@Override
public void exitDeadline (DeadlineContext ctx)
{
var deadline = Integer.parseInt(ctx.getText());// get deadline from the AST
        workLoad.setFlowDeadline(currentFlow, deadline);
    }
@Override
public void exitPhase (PhaseContext ctx)
{
var phase = Integer.parseInt(ctx.getText());// get phase from the AST
        workLoad.setFlowPhase(currentFlow, phase);
    }
@Override
public void exitSrcNode (SrcNodeContext ctx)
{
var nodeName = ctx.getText(); // get the src node name
        workLoad.addNodeToFlow(currentFlow, nodeName);
    }
@Override
public void exitSnkNode (SnkNodeContext ctx)
{
var nodeName = ctx.getText(); // get the snk node name
        workLoad.addNodeToFlow(currentFlow, nodeName);
    }
@Override
public void exitFlow (FlowContext ctx)
{
workLoad.finalizeCurrentFlow(currentFlow);
    }
}
