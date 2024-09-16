/**
 * 
 */
package edu.uiowa.cs.warp;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Reads the input file, whose name is passed as input parameter to the constructor, and builds a
 * Description object based on the contents. Each line of the file is an entry (string) in the
 * Description object.
 * 
 * @author sgoddard
 * @version 1.8 Fall 2024
 */
public class WorkLoadDescription extends VisualizationObject {

  private static final String EMPTY = "";
  private static final String INPUT_FILE_SUFFIX = ".wld";

  private Description description;
  private String inputGraphString;
  private FileManager fm;
  private String inputFileName;

  WorkLoadDescription(String inputFileName) {
    super(new FileManager(), EMPTY, INPUT_FILE_SUFFIX); // VisualizationObject constructor
    this.fm = this.getFileManager();
    initialize(inputFileName);
  }

  @Override
  public Description visualization() {
    return description;
  }

  @Override
  public Description fileVisualization() {
    return description;
  }

  @Override
  public String toString() {
    return inputGraphString;
  }

  public String getInputFileName() {
    return inputFileName;
  }

  private void initialize(String inputFile) {
    // Get the input graph file name and read its contents
    InputGraphFile gf = new InputGraphFile(fm);
    inputGraphString = gf.readGraphFile(inputFile);
    this.inputFileName = gf.getGraphFileName();
    description = new Description(inputGraphString);
  }
  
  public static void main(String[] args) {
	  WorkLoadDescription myWorkLoad = new WorkLoadDescription("StressTest.txt");
	  Description myDescription = myWorkLoad.description;
	  System.out.println(myDescription.getFirst().split(" ")[0]);
	  myDescription.remove(0);
	  myDescription.remove(myDescription.size()-1);
	  Collections.sort(myDescription);
	  for (int i=1; i<myDescription.size()+1; i++) {
		  System.out.print("Flow " + i + ": " + myDescription.get(i-1));
	  }
  }
}
  
  

