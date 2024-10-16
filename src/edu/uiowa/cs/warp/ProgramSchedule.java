package edu.uiowa.cs.warp;


public class ProgramSchedule extends Table <String,InstructionTimeSlot>  {
private static final long serialVersionUID = 1L;

public ProgramSchedule () {
super();
   	}

/*package*/ ProgramSchedule (Integer row, Integer column) {
super(row,column);
   	}

}
