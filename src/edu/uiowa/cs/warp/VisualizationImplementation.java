/**
 * 
 */
package edu.uiowa.cs.warp;

import java.io.File;

/**
 * Handles all visualizations dealt with in the Warp program. This includes creating
 * visualizations with WarpInterface warp, Workload workload. Its main function
 * is checking the input to determine which type of visualization is being requested 
 * and running appropriate visualization.
 * @author sgoddard
 * @version 1.5
 */

public class VisualizationImplementation implements Visualization {

  private Description visualization;
  private Description fileContent;
  private GuiVisualization window;
  private String fileName;
  private String inputFileName;
  private String fileNameTemplate;
  private FileManager fm = null;
  private WarpInterface warp = null;
  private WorkLoad workLoad = null;
  private VisualizationObject visualizationObject;

  /**
   * Constructor sets VisualizationImplementation attributes from given input. Creates 
   * new FileManager(), converts WarpInterface warp input to Wordload. This allows the
   * program to get the input file name and create the file name template from outputDirectory
   * input. Creates visualization with SystemChoices choice input.
   * @param warp
   * @param outputDirectory
   * @param choice
   */
  public VisualizationImplementation(WarpInterface warp, String outputDirectory,
      SystemChoices choice) {
    this.fm = new FileManager();
    this.warp = warp;
    inputFileName = warp.toWorkload().getInputFileName();
    this.fileNameTemplate = createFileNameTemplate(outputDirectory);
    visualizationObject = null;
    createVisualization(choice);
  }
  
  /**
   * Constructor sets VisualizationImplementation attributes from given input. Creates 
   * new FileManager(). Sets input workLoad to a new workLoad and gets the input file name
   * from new workLoad. Creates file name template from outputDirectory input and creates
   * visualization with WorkLoadChoices choice input.
   * @param workLoad the Workload being used
   * @param outputDirectory create fileNameTemplate with
   * @param choice create visualization of 
   */
  public VisualizationImplementation(WorkLoad workLoad, String outputDirectory,
      WorkLoadChoices choice) {
    this.fm = new FileManager();
    this.workLoad = workLoad;
    inputFileName = workLoad.getInputFileName();
    this.fileNameTemplate = createFileNameTemplate(outputDirectory);
    visualizationObject = null;
    createVisualization(choice);
  }

  /**
   * Converts visualizationObject to a display
   */
  @Override
  public void toDisplay() {
    // System.out.println(displayContent.toString());
    window = visualizationObject.displayVisualization();
    if (window != null) {
      window.setVisible();
    }
  }

  /**
   * Converts file contents to a String
   */
  @Override
  public void toFile() {
    fm.writeFile(fileName, fileContent.toString());
  }

  /**
   * @return visualization as a String
   */
  @Override
  public String toString() {
    return visualization.toString();
  }

  /**
   * Checks which SystemChoices choice is requested and runs 
   * appropriate visualization
   * @param choice
   */
  private void createVisualization(SystemChoices choice) {
    switch (choice) { // select the requested visualization
      case SOURCE:
        createVisualization(new ProgramVisualization(warp));
        break;

      case RELIABILITIES:
        // TODO Implement Reliability Analysis Visualization
        createVisualization(new ReliabilityVisualization(warp));
        break;

      case SIMULATOR_INPUT:
        // TODO Implement Simulator Input Visualization
        createVisualization(new NotImplentedVisualization("SimInputNotImplemented"));
        break;

      case LATENCY:
        // TODO Implement Latency Analysis Visualization
        createVisualization(new LatencyVisualization(warp));
        break;

      case CHANNEL:
        // TODO Implement Channel Analysis Visualization
        createVisualization(new ChannelVisualization(warp));
        break;

      case LATENCY_REPORT:
        createVisualization(new ReportVisualization(fm, warp,
            new LatencyAnalysis(warp).latencyReport(), "Latency"));
        break;

      case DEADLINE_REPORT:
        createVisualization(
            new ReportVisualization(fm, warp, warp.toProgram().deadlineMisses(), "DeadlineMisses"));
        break;

      default:
        createVisualization(new NotImplentedVisualization("UnexpectedChoice"));
        break;
    }
  }

  /**
   * Checks which WorkLoadChoices choice is requested and runs
   * appropriate graph visualization
   * @param choice
   */
  private void createVisualization(WorkLoadChoices choice) {
    switch (choice) { // select the requested visualization
      case COMUNICATION_GRAPH:
        // createWarpVisualization();
        createVisualization(new CommunicationGraph(fm, workLoad));
        break;

      case GRAPHVIZ:
        createVisualization(new GraphViz(fm, workLoad.toString()));
        break;

      case INPUT_GRAPH:
        createVisualization(workLoad);
        break;

      default:
        createVisualization(new NotImplentedVisualization("UnexpectedChoice"));
        break;
    }
  }

  /**
   * Creates requested visualization and sets program's attributes
   * @param <T>
   * @param obj
   */
  private <T extends VisualizationObject> void createVisualization(T obj) {
    visualization = obj.visualization();
    fileContent = obj.fileVisualization();
    /* display is file content printed to console */
    fileName = obj.createFile(fileNameTemplate); // in output directory
    visualizationObject = obj;
  }

  /**
   * Creates newDirectory of the workingDirectory and input outputDirectory
   * and returns a fileNameTemplate of the newDirectory.
   * @param outputDirectory
   * @return fileNameTemplate of the input outputDirectory
   */
  private String createFileNameTemplate(String outputDirectory) {
    String fileNameTemplate;
    var workingDirectory = fm.getBaseDirectory();
    var newDirectory = fm.createDirectory(workingDirectory, outputDirectory);
    // Now create the fileNameTemplate using full output path and input filename
    if (inputFileName.contains("/")) {
      var index = inputFileName.lastIndexOf("/") + 1;
      fileNameTemplate = newDirectory + File.separator + inputFileName.substring(index);
    } else {
      fileNameTemplate = newDirectory + File.separator + inputFileName;
    }
    return fileNameTemplate;
  }

}
