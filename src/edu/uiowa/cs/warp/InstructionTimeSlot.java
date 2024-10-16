package edu.uiowa.cs.warp;


public class InstructionTimeSlot extends Row <String>  {
private static final long serialVersionUID = 1L;

public InstructionTimeSlot () {
super();
   	}

/*package*/ InstructionTimeSlot (Integer numNodes, String element) {
super(numNodes, element);
   	}

/*package*/ InstructionTimeSlot (String[] rowArray) {
super(rowArray);
   	}

}
