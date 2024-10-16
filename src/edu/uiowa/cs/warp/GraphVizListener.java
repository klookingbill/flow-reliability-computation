package edu.uiowa.cs.warp;

import edu.uiowa.cs.warpdsls.WARPBaseListener;
import WARPParser.WarpNameContext;
import WARPParser.WarpContext;
import WARPParser.FlowNameContext;
import WARPParser.SrcNodeContext;
import WARPParser.SnkNodeContext;
import WARPParser.FlowContext;

/**
* Extend WARP Base Listener Class to extract info for Graph Viz file.
 * GraphVizListener reads the graphFile string and calls the functions
 * in this class, as it walks the input graphFile structure to build
 * the GraphViz contents. */public final class GraphVizListener extends WARPBaseListener {
/*package*/ GraphViz gv;
private GraphVizListener (GraphViz gv) {
this.gv = gv; // use GraphViz object passed to this object
     // Read input file and build AST of graphFile
        try {
        	CharStream inputGraph = CharStreams.fromString(gv.graphFile);
        	var lexer = new WARPLexer(inputGraph);
        	var tokens = new CommonTokenStream(lexer);
        	var parser = new WARPParser(tokens);
        	ParseTree warpTree = parser.warp(); // begin parsing at rule 'warp'
        	var warp = new ParseTreeWalker();
        	 // Now populate build the Graph Viz file content by walking the input Graph tokens
        	warp.walk(this, warpTree);
        } catch (Exception e) {
        	e.printStackTrace();
        	System.err.println("ERROR: Unable to parse graphFile" + e.getMessage());
        }
    }

public static void buildGraphViz(GraphViz gv) {
/*
    	 * Create the listener object, which will read the workLoad 
    	 * description and then buid the node and flow objects that
    	 * instantiates the workLoad. This object is not needed
    	 * after that.
    	 */
    	new GraphVizListener(gv); 
    }

@Override
public void enterWarpName (WarpNameContext ctx)
{
var graphName = ctx.getText();  // get the name of the graph from name
        gv.initializeGraphVizContent(graphName); // start creating the gv file content
    }
@Override
public void exitWarp (WarpContext ctx)
{
gv.finalizeGraphVizContent();
    }
@Override
public void enterFlowName (FlowNameContext ctx)
{
var currentFlow = ctx.getText();
        gv.addFlowToGraphViz(currentFlow);
    }
@Override
public void exitSrcNode (SrcNodeContext ctx)
{
var nodeName = ctx.getText(); // get the src node name
        gv.addSrcNodeToGraphViz(nodeName);
    }
@Override
public void exitSnkNode (SnkNodeContext ctx)
{
var nodeName = ctx.getText(); // get the snk node name
        gv.addSnkNodeToGraphViz(nodeName);
    }
@Override
public void exitFlow (FlowContext ctx)
{
gv.finalizeCurrentFlowInGraphViz();
    }
}
