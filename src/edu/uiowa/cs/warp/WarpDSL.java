package edu.uiowa.cs.warp;

import java.util.ArrayList;
import edu.uiowa.cs.warpdsls.WARPdslBaseListener;
import WARPdslParser.ActionContext;
import WARPdslParser.FlowNameContext;
import WARPdslParser.CmdContext;
import WARPdslParser.SrcNodeContext;
import WARPdslParser.SnkNodeContext;
import WARPdslParser.ChannelContext;

/**
* @author sgoddard
 *  * */public class WarpDSL {
private static final String UNKNOWN = "unknown";

public static final String UNUSED = "unused";

public static final String PUSH = "push";

public ArrayList <InstructionParameters> getInstructionParameters(String instruction) {
ArrayList<InstructionParameters> instructionParametersArrayList;
    	var dsl = new ListenerDsl();
        // Read input file and build AST of graph
        try {
        	CharStream inputInstruction = CharStreams.fromString(instruction);
        	var lexer = new WARPdslLexer(inputInstruction);
        	var instructionTokens = new CommonTokenStream(lexer);
        	var parser = new WARPdslParser(instructionTokens);
        	ParseTree instructionTree = parser.instruction(); // begin parsing at rule 'warp'
        	var warp = new ParseTreeWalker();
        	warp.walk(dsl, instructionTree);
        } catch (Exception e) {
        	e.printStackTrace();
        	System.err.println("Unable to parse instruction instruction from the schedule entry:" + e.getMessage());
        }
        instructionParametersArrayList = dsl.getInstructionParameters();
        return instructionParametersArrayList;
    }

public class InstructionParameters {
private String name = UNUSED;

public String getName() {
   return this.name;
}

private String flow = UNUSED;

public String getFlow() {
   return this.flow;
}

private String src = UNUSED;

public String getSrc() {
   return this.src;
}

private String snk = UNUSED;

public String getSnk() {
   return this.snk;
}

private String channel = UNUSED;

public String getChannel() {
   return this.channel;
}

private String coordinator = UNKNOWN;
private String listener = UNKNOWN;

public void setListener(String value) {
   this.listener = value;
}

public String getListener() {
   return this.listener;
}

/**
* @return the coordinator */public String getCoordinator() {
return coordinator;
   		}

/**
* @param coordinator the coordinator to set */public void setCoordinator(String coordinator) {
this.coordinator = coordinator;
   		}

private void setName(String name) {
this.name = name;
        }

private void setFlow(String flow) {
this.flow = flow;
        }

private void setSrc(String src) {
this.src = src;
        }

private void setSnk(String snk) {
this.snk = snk;
        }

private void setChannel(String channel) {
this.channel = channel;
        }

public String unused() {
return UNUSED;
   		}

}
private final class ListenerDsl extends WARPdslBaseListener {
/*package*/ Boolean inAction;
/*package*/ ArrayList <InstructionParameters> instructionParametersArrayList;
/*package*/ InstructionParameters instructionParameters;
/*package*/ ListenerDsl () {
inAction = false;
            instructionParametersArrayList = new ArrayList<InstructionParameters>();
        }

public ArrayList <InstructionParameters> getInstructionParameters() {
return instructionParametersArrayList;
        }

@Override
public void enterAction (ActionContext ctx)
{
inAction = true;
            instructionParameters = new InstructionParameters();  // create a new instance of the parameter structure with default initialization
        }
@Override
public void exitAction (ActionContext ctx)
{
inAction = false;
            instructionParametersArrayList.add(instructionParameters);  // this action is done, so add the parameters to the list
        }
@Override
public void exitFlowName (FlowNameContext ctx)
{
if (inAction) {
                String flowName = ctx.getText(); // get the flow name
                instructionParameters.setFlow(flowName);
            }
        }
@Override
public void exitCmd (CmdContext ctx)
{
String command = ctx.getText(); // get the command/instruction name
            instructionParameters.setName(command);
        }
@Override
public void exitSrcNode (SrcNodeContext ctx)
{
String srcName = ctx.getText(); // get the src node name
            instructionParameters.setSrc(srcName);
            var command = instructionParameters.getName();
            if (command.equals(PUSH)) {
            	/* if push then coordinator is src 
            	 */
            	instructionParameters.setCoordinator(srcName);
            }else {
            	/* otherwise listener is src */
            	instructionParameters.setListener(srcName);
            }
        }
@Override
public void exitSnkNode (SnkNodeContext ctx)
{
String snkName = ctx.getText(); // get the snk node name
            instructionParameters.setSnk(snkName);
            var command = instructionParameters.getName();
            if (command.equals(PUSH)) {
            	/* if push then listener is snk */
            	instructionParameters.setListener(snkName);
            }else {
            	/* otherwise coordinator is snk 
            	 */
            	instructionParameters.setCoordinator(snkName);
            }
        }
@Override
public void exitChannel (ChannelContext ctx)
{
String channelString = ctx.getText(); // get the snk node name
        	String channel; // resulting channel that will be extracted from the channel string
        	Integer index = channelString.indexOf('#');
        	if (index >= 0 && index < channelString.length()) {
        		channel = channelString.substring(index+1); // get string starting at first channel number
        	} else {
        		channel = UNKNOWN; // no valid channel in the channelString, so set to UNKNOWN
        	}	
            instructionParameters.setChannel(channel); 
        }
}
}
