package edu.uiowa.cs.warp;


/*package*/ interface SystemAttributes extends ReliabilityParameters {
public enum ScheduleChoices{
PRIORITY, RM, DM, RTHART, POSET_PRIORITY, POSET_RM, POSET_DM, WARP_POSET_PRIORITY, WARP_POSET_RM, WARP_POSET_DM, CONNECTIVITY_POSET_PRIORITY, CONNECTIVITY_POSET_RM, CONNECTIVITY_POSET_DM;}public Integer getNumChannels();

public Integer getNumFaults();

public String getName();

public String getSchedulerName();

public Integer getNumTransmissions();

public Boolean getOptimizationFlag();

}
