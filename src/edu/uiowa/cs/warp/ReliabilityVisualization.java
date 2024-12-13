package edu.uiowa.cs.warp;

import java.util.ArrayList;

/**
 * ReliabilityVisualization creates the visualizations for the reliability
 * analysis of the WARP program.
 * <p>
 * 
 * CS2820 Fall 2024 Project: Implement this class to create the visualizations
 * that are requested in Warp. Your solution should support both the file and
 * Java Swing Window (gui) visualizations.
 *
 * I recommend using class ProgramVisualization as an example of how to
 * implement this class. Your solution will likely be similar to the code in
 * ProgramVisualization.
 * 
 * @author sgoddard
 * @version 1.8 Fall 2024
 *
 */
public class ReliabilityVisualization extends VisualizationObject {
	/**
	 * The suffix denoting a ReliabilityAnalysis object.
	 */
	private static final String SOURCE_SUFFIX = ".ra";

	/**
	 * The name of the ReliabilityAnalysis object.
	 */
	private static final String OBJECT_NAME = "Reliability Analysis";

	/**
	 * The WARP interface used to visualize the ReliabilityAnalysis object.
	 */
	private WarpInterface warp;

	/**
	 * The ReliabilityAnalysis object that the visualization will be created for.
	 */
	private ReliabilityAnalysis ra;

	/**
	 * Constructor used to initialize the ReliabilityVisualization object using a
	 * FileManager object, the WARP interface, and the suffix denoting a
	 * ReliabilityAnalysis object. The ReliabilityAnalysis object is initialized by
	 * converting the WARP interface into a ReliabilityAnalysis object.
	 * 
	 * @param warp the given WARP interface
	 */
	ReliabilityVisualization(WarpInterface warp) {
		super(new FileManager(), warp, SOURCE_SUFFIX);
		this.warp = warp;
		this.ra = warp.toReliabilityAnalysis();
	}

	/**
	 * Creates a string title for the current WARP reliability visualization.
	 *
	 * @return string title of current visualization
	 */
	private String createTitle() {
		return String.format(OBJECT_NAME + " for graph %s\n", warp.getName());
	}

	/**
	 * Creates header description object including information regarding the
	 * reliability. Begins with a title and WARP scheduler name. Outputs number of
	 * faults in case of e2e and the number of channels.
	 *
	 * @return description serving as a header
	 */
	@Override
	protected Description createHeader() {
		Description header = new Description();

		header.add(createTitle());
		header.add(String.format("Scheduler Name: %s\n", warp.getSchedulerName()));

		/*
		 * The following parameters are output based on a special schedule or the fault
		 * model
		 */
		if (warp.getNumFaults() > 0) { // only specify when deterministic fault model is assumed
			header.add(String.format("numFaults: %d\n", warp.getNumFaults()));
		}
		header.add(String.format("M: %s\n", String.valueOf(warp.getMinPacketReceptionRate())));
		header.add(String.format("E2E: %s\n", String.valueOf(warp.getE2e())));
		header.add(String.format("nChannels: %d\n", warp.getNumChannels()));
		return header;
	}

	/**
	 * Creates column header by alphabetically ordering node names and adding them
	 * to an array. The first element in the list reads "Time Slot", and each
	 * subsequent line represents a time slot.
	 * 
	 * @return array of column names
	 */
	@Override
	protected String[] createColumnHeader() {
		String[] header = ra.getReliabilityHeader();
		return header;
	}

	/**
	 * Populates a string matrix of the reliability source code if none exists.
	 * 
	 * @return matrix of visualization data
	 */
	@Override
	protected String[][] createVisualizationData() {
		if (visualizationData == null) {
			int numRows = ra.getReliabilities().getNumRows();
			int numColumns = ra.getReliabilities().getNumColumns();
			visualizationData = new String[numRows][numColumns + 1];

			for (int row = 0; row < numRows; row++) {
				visualizationData[row][0] = String.format("%s", row);
				for (int column = 0; column < numColumns; column++) {
					visualizationData[row][column + 1] = ra.getReliabilities().get(row, column).toString();
				}
			}
		}
		return visualizationData;
	}

	/**
	 * Creates a GuiVisualization containing the new reliability title, column
	 * header, and data visualization.
	 * 
	 * @return GuiVisualization for the given reliability
	 */
	@Override
	public GuiVisualization displayVisualization() {
		return new GuiVisualization(createTitle(), createColumnHeader(), createVisualizationData());
	}

	/*
	 * File Visualization for workload defined in Example.txt follows. Your output
	 * in the file ExamplePriority-0.9M-0.99E2E.ra should match this output, where
	 * \tab characters are used as column delimiters. Reliability Analysis for graph
	 * Example created with the following parameters: Scheduler Name: Priority M:
	 * 0.9 E2E: 0.99 nChannels: 16 F0:A F0:B F0:C F1:C F1:B F1:A 1.0 0.9 0.0 1.0 0.0
	 * 0.0 1.0 0.99 0.81 1.0 0.0 0.0 1.0 0.999 0.972 1.0 0.0 0.0 1.0 0.999 0.9963
	 * 1.0 0.0 0.0 1.0 0.999 0.9963 1.0 0.9 0.0 1.0 0.999 0.9963 1.0 0.99 0.81 1.0
	 * 0.999 0.9963 1.0 0.999 0.972 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 * 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999
	 * 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963 1.0 0.999 0.9963
	 */
}
