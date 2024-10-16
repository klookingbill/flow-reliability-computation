package edu.uiowa.cs.warp;


/**
* The Warp class sets default values of constants and also sets the warp parameters for the
 * given input. It creates and visualizes any requested output files and runs verification
 * checks to make sure deadlines and reliability targets are met, and that there are no 
 * channel conflicts. Runs additional tests to make sure everything will run smoothly. This
 * class also prints out all warp parameters along with Boolean expressions for if any flags
 * are requested. 
 * @author sgoddard
 * @version 1.8 Fall 2024 */public class Warp {
private static final Integer NUM_CHANNELS = 16;

private static final Double MIN_LQ = 0.9;

private static final Double E2E = 0.99;

private static final String DEFAULT_OUTPUT_SUB_DIRECTORY = "OutputFiles/";

private static final ScheduleChoices DEFAULT_SCHEDULER = ScheduleChoices.PRIORITY;

private static final Integer DEFAULT_FAULTS_TOLERATED = 0;

private static Integer nChannels;
private static Integer numFaults;
private static Double minLQ;
private static Double e2e;
private static String outputSubDirectory;
public static Boolean guiRequested;
private static Boolean gvRequested;
private static Boolean wfRequested;
private static Boolean raRequested;
private static Boolean laRequested;
private static Boolean caRequested;
private static Boolean simRequested;
private static Boolean allRequested;
private static Boolean latencyRequested;
private static Boolean schedulerRequested = false;
private static Boolean verboseMode;
private static String inputFile;
private static ScheduleChoices schedulerSelected;
/**
* Main method sets warp parameters with given input arguments and prints out the parameters
 *    * if in verbose mode. It creates and visualizes the new workload. If the all output files 
 *    * flag is requested, it visualizes all workLoad Program choices and creates and visualizes
 *    * the Warp System with all warp System choices. If not all output files are requested, it
 *    * still visualizes the ones asked for of warp workload, source program, and other requested
 *    * output items.
 *    * @param args Command-line arguments passed to the application. */public static void main(String[] args) {
// parse command-line options and set WARP system parameters
    setWarpParameters(args);
   
    // and print out the values if in verbose mode
    if (verboseMode) {
      printWarpParameters();
    }
   
    // Create and visualize the workload
    // inputFile string, which may be null,
    WorkLoad workLoad = new WorkLoad(numFaults, minLQ, e2e, inputFile);
    if (allRequested) {
      for (WorkLoadChoices choice : WorkLoadChoices.values()) {
        visualize(workLoad, choice); // visualize all Program choices
      }
      // Create and visualize the Warp System
      if (schedulerRequested) {
        WarpInterface warp = SystemFactory.create(workLoad, nChannels, schedulerSelected);
        verifyPerformanceRequirements(warp);
        for (SystemChoices choice : SystemChoices.values()) {
          visualize(warp, choice); // visualize all System choices
        }
      } else { // create a system for all scheduler choices
        for (ScheduleChoices sch : ScheduleChoices.values()) {
          schedulerSelected = sch;
          WarpInterface warp = SystemFactory.create(workLoad, nChannels, schedulerSelected);
          verifyPerformanceRequirements(warp);
          for (SystemChoices choice : SystemChoices.values()) {
            visualize(warp, choice); // visualize all System choices
          }
        }
      }
    } else { // visualize warp workload, source program and other requested items
      visualize(workLoad, WorkLoadChoices.INPUT_GRAPH);
      if (wfRequested) {
        visualize(workLoad, WorkLoadChoices.COMUNICATION_GRAPH);
      }
      if (gvRequested) {
        visualize(workLoad, WorkLoadChoices.GRAPHVIZ);
      }
      WarpInterface warp = SystemFactory.create(workLoad, nChannels, schedulerSelected);
      verifyPerformanceRequirements(warp);
      visualize(warp, SystemChoices.SOURCE);
      if (caRequested) {
        visualize(warp, SystemChoices.CHANNEL);
      }
      if (laRequested) {
        visualize(warp, SystemChoices.LATENCY);
      }
      if (latencyRequested || laRequested) {
        visualize(warp, SystemChoices.LATENCY_REPORT);
      }
      if (raRequested) {
        visualize(warp, SystemChoices.RELIABILITIES);
      }
    }
   
     }

/**
* Creates holder objects to store results and creates the parser to process and comprehend 
 *    * the input data. Also checks that all arguments are valid and sets values for parser. It 
 *    * checks which flags are present and stores this as a Boolean value. Additionally, checks 
 *    * if schedulerSelected value is null and cannot run is null. Then checks value of 
 *    * schedulerSelected to see what the value wants to do and runs method before it breaks.
 *    * @param args */private static void setWarpParameters(String[] args) {
// move command line parsing into this
                                                         // function--need to set up globals?
   
    // create holder objects for storing results ...
    // BooleanHolder debug = new BooleanHolder();
    StringHolder schedulerSelected = new StringHolder();
    IntHolder channels = new IntHolder();
    IntHolder faults = new IntHolder();
    DoubleHolder m = new DoubleHolder();
    DoubleHolder end2end = new DoubleHolder();
    BooleanHolder gui = new BooleanHolder();
    BooleanHolder gv = new BooleanHolder();
    BooleanHolder wf = new BooleanHolder();
    BooleanHolder ra = new BooleanHolder();
    BooleanHolder la = new BooleanHolder();
    BooleanHolder ca = new BooleanHolder();
    BooleanHolder s = new BooleanHolder();
    BooleanHolder all = new BooleanHolder();
    BooleanHolder latency = new BooleanHolder();
    BooleanHolder verbose = new BooleanHolder();
    StringHolder input = new StringHolder();
    StringHolder output = new StringHolder();
   
    // create the parser and specify the allowed options ...
    ArgParser parser = new ArgParser("java -jar warp.jar");
    parser.addOption("-sch, --schedule %s {priority,rm,dm,rtHart,poset} #scheduler options",
        schedulerSelected);
    parser.addOption("-c, --channels %d {[1,16]} #number of wireless channels", channels);
    parser.addOption("-m %f {[0.5,1.0]} #minimum link quality in the system", m);
    parser.addOption(
        "-e, --e2e %f {[0.5,1.0]} #global end-to-end communcation reliability for all flows",
        end2end);
    parser.addOption("-f, --faults %d {[1,10]} #number of faults per edge in a flow (per period)",
        faults);
    parser.addOption("-gui %v #create a gui visualizations", gui);
    parser.addOption("-gv %v #create a graph visualization (.gv) file for GraphViz", gv);
    parser.addOption(
        "-wf  %v #create a WARP (.wf) file that shows the maximum number of transmissions on each segment of the flow needed to meet the end-to-end reliability",
        wf);
    parser.addOption(
        "-ra  %v #create a reliability analysis file (tab delimited .csv) for the warp program",
        ra);
    parser.addOption(
        "-la  %v #create a latency analysis file (tab delimited .csv) for the warp program", la);
    parser.addOption(
        "-ca  %v #create a channel analysis file (tab delimited .csv) for the warp program", ca);
    parser.addOption("-s  %v #create a simulator input file (.txt) for the warp program", s);
    parser.addOption("-a, --all  %v #create all output files (activates -gv, -wf, -ra, -s)", all);
    parser.addOption("-l, --latency  %v #generates end-to-end latency report file (.txt)", latency);
    parser.addOption("-i, --input %s #<InputFile> of graph flows (workload)", input);
    parser.addOption("-o, --output %s #<OutputDIRECTORY> where output files will be placed",
        output);
    parser.addOption(
        "-v, --verbose %v #Echo input file name and parsed contents. Then for each flow instance: show maximum E2E latency and min/max communication cost for that instance of the flow",
        verbose);
    // parser.addOption ("-d, -debug, --debug %v #Debug mode: base directory =
    // $HOME/Documents/WARP/", debug);
   
   
    // match the arguments ...
    parser.matchAllArgs(args);
   
    // Set WARP system configuration options
    if (channels.value > 0) {
      nChannels = channels.value; // set option specified
    } else {
      nChannels = NUM_CHANNELS; // set to default
    }
    if (faults.value > 0) { // global variable for # of Faults tolerated per edge
      numFaults = faults.value; // set option specified
    } else {
      numFaults = DEFAULT_FAULTS_TOLERATED; // set to default
    }
    if (m.value > 0.0) { // global variable for minimum Link Quality in system
      minLQ = m.value; // set option specified
    } else {
      minLQ = MIN_LQ; // set to default
    }
    if (end2end.value > 0.0) { // global variable for minimum Link Quality in system
      e2e = end2end.value; // set option specified
    } else {
      e2e = E2E; // set to default
    }
    if (output.value != null) { // default output subdirectory (from working directory)
      outputSubDirectory = output.value; // set option specified
    } else {
      outputSubDirectory = DEFAULT_OUTPUT_SUB_DIRECTORY; // set to default
    }
   
    guiRequested = gui.value; // GraphVis file requested flag
    gvRequested = gv.value; // GraphVis file requested flag
    wfRequested = wf.value; // WARP file requested flag
    raRequested = ra.value; // Reliability Analysis file requested flag
    laRequested = la.value; // Latency Analysis file requested flag
    caRequested = ca.value; // Latency Analysis file requested flag
    simRequested = s.value; // Simulation file requested flag
    allRequested = all.value; // all out files requested flag
    latencyRequested = latency.value; // latency report requested flag
    verboseMode = verbose.value; // verbose mode flag (mainly for running in IDE)
    // debugMode = debug.value; // debug mode flag (mainly for running in IDE)
    inputFile = input.value; // input file specified
    if (schedulerSelected.value != null) { // can't switch on a null value so check then switch
      schedulerRequested = true;
      switch (schedulerSelected.value) {
        case "priority":
          Warp.schedulerSelected = ScheduleChoices.PRIORITY;
          break;
   
        case "rm":
          Warp.schedulerSelected = ScheduleChoices.RM;
          break;
   
        case "dm":
          Warp.schedulerSelected = ScheduleChoices.DM;
          break;
   
        case "rtHart":
          Warp.schedulerSelected = ScheduleChoices.RTHART;
          break;
   
        case "poset":
          Warp.schedulerSelected = ScheduleChoices.POSET_PRIORITY;
          break;
   
        default:
          Warp.schedulerSelected = ScheduleChoices.PRIORITY;
          break;
      }
    } else { // null value when no scheduler specified; so use default
      Warp.schedulerSelected = DEFAULT_SCHEDULER;
    }
     }

/**
* Prints out all system configuration parameters including the scheduler, channels,
 *    * number of faults, minimum Link Quality, end-to-end reliability, and if any flags
 *    * are requested. Prints out the input file if given and if there is not one, it 
 *    * alerts that one will be needed. */private static void printWarpParameters() {
// print all system configuration parameters
    // Print out each of the system configuration values
    System.out.println("WARP system configuration values:");
    System.out.println("\tScheduler=" + schedulerSelected);
    System.out.println("\tnChanels=" + nChannels);
    System.out.println("\tnumFaults=" + numFaults);
    System.out.println("\tminLQ=" + minLQ);
    System.out.println("\tE2E=" + e2e);
    System.out.println("\tguiRequest flag=" + guiRequested);
    System.out.println("\tgvRequest flag=" + gvRequested);
    System.out.println("\twfRequest flag=" + wfRequested);
    System.out.println("\traRequest flag=" + raRequested);
    System.out.println("\tlaRequest flag=" + laRequested);
    System.out.println("\tcaRequest flag=" + caRequested);
    System.out.println("\tsimRequest flag=" + simRequested);
    System.out.println("\tallOutFilesRequest flag=" + allRequested);
    System.out.println("\tlatency flag=" + latencyRequested);
    if (inputFile != null) {
      System.out.println("\tinput file=" + inputFile);
    } else {
      System.out.println("\tNo input file specified; will be requested when needed.");
    }
    System.out.println("\toutputSubDirectory=" + outputSubDirectory);
    System.out.println("\tverbose flag=" + verboseMode);
    // System.out.println ("\tdebug flag=" + debugMode);
     }

/**
* Creates a visualization instance of input WorkLoad workLoad and WorkLoadChoices
 *    * choice. If the visualization is null and if verboseMode holds True, prints out
 *    * viz as a String, otherwise, convert viz to a file and if gui flag is requested,
 *    * convert viz to a display.
 *    * @param workLoad
 *    * @param choice */private static void visualize(WorkLoad workLoad, WorkLoadChoices choice) {
var viz =
        VisualizationFactory.createVisualization(workLoad, outputSubDirectory, choice);
    if (viz != null) {
      if (verboseMode) {
        System.out.println(viz.toString());
      }
      viz.toFile();
      if (guiRequested) {
        viz.toDisplay();
      }
    }
     }

/**
* Creates a visualization instance of input WarpInterface warp with SystemChoices 
 *    * choice. If visualization is null, turns viz to a file, and if gui and schedule 
 *    * flags are requested, turn viz into a display.
 *    * @param warp
 *    * @param choice */private static void visualize(WarpInterface warp, SystemChoices choice) {
var viz = VisualizationFactory.createVisualization(warp, outputSubDirectory, choice);
    if (viz != null) {
      viz.toFile();
      if (guiRequested && schedulerRequested) {
        /* Only display window when a specific scheduler has been requested */
        viz.toDisplay();
      }
    }
     }

/**
* Runs verification checks over if deadlines are met, if reliability targets
 *    * are met, and if there are channel conflicts for the input WarpInterface warp.
 *    * @param warp */private static void verifyPerformanceRequirements(WarpInterface warp) {
verifyDeadlines(warp);
    verifyReliabilities(warp);
    verifyNoChannelConflicts(warp);
     }

/**
* Checks that the reliability targets are met in input warp. If reliability
 *    * is not met, prints an error statement. If verboseMode is True with the input, 
 *    * prints out statement that flows meet reliability in this instance.
 *    * @param warp */private static void verifyReliabilities(WarpInterface warp) {
if (schedulerSelected != ScheduleChoices.RTHART) {
      /* RealTime HART doesn't adhere to reliability targets */
      if (!warp.reliabilitiesMet()) {
        System.err.printf(
            "\n\tERROR: Not all flows meet the end-to-end "
                + "reliability of %s under %s scheduling.\n",
            String.valueOf(e2e), schedulerSelected.toString());
      } else if (verboseMode) {
        System.out.printf(
            "\n\tAll flows meet the end-to-end reliability " + "of %s under %s scheduling.\n",
            String.valueOf(e2e), schedulerSelected.toString());
      }
    }
     }

/**
* Checks if the deadlines are met in input warp. If deadlines are not met, prints
 *    * out an error message and visualizes a Deadline Report. If verboseMode is true for
 *    * input, prints out a message telling the deadlines are all met.
 *    * @param warp */private static void verifyDeadlines(WarpInterface warp) {
if (!warp.deadlinesMet()) {
      System.err.printf("\n\tERROR: Not all flows meet their deadlines under %s scheduling.\n",
          schedulerSelected.toString());
      visualize(warp, SystemChoices.DEADLINE_REPORT);
    } else if (verboseMode) {
      System.out.printf("\n\tAll flows meet their deadlines under %s scheduling.\n",
          schedulerSelected.toString());
    }
     }

/**
* Determines if there is a channel conflict in the WarpInterface warp given 
 *    * as input. Creates a Channel Analysis visualization if not already requested.
 *    * @param warp */private static void verifyNoChannelConflicts(WarpInterface warp) {
if (warp.toChannelAnalysis().isChannelConflict()) {
      System.err
          .printf("\n\tERROR: Channel conficts exists. See Channel Visualization for details.\n");
      if (!caRequested) { // only need to create the visualization if not already requested
        visualize(warp, SystemChoices.CHANNEL);
      }
    } else if (verboseMode) {
      System.out.printf("\n\tNo channel conflicts detected.\n");
    }
     }

}
