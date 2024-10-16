package edu.uiowa.cs.warp;


/**
* ReliabilityVisualization creates the visualizations for
 * the reliability analysis of the WARP program. <p>
 * 
 * CS2820 Fall 2024 Project: Implement this class to create
 * the visualizations that are requested in Warp. Your solution
 * should support both the file and Java Swing Window (gui) visualizations.
 *  *
 * I recommend using class ProgramVisualization as an example of how to implement
 * this class. Your solution will likely be similar to the code in ProgramVisualization.
 * 
 * @author sgoddard
 * @version 1.8 Fall 2024
 *  * */public class ReliabilityVisualization extends VisualizationObject {
private static final String SOURCE_SUFFIX = ".ra";

private static final String OBJECT_NAME = "Reliability Analysis";

private WarpInterface warp;
private ReliabilityAnalysis ra;
/*package*/ ReliabilityVisualization (WarpInterface warp) {
super(new FileManager(), warp, SOURCE_SUFFIX);
   		this.warp = warp;
   		this.ra = warp.toReliabilityAnalysis();
   	}

}
