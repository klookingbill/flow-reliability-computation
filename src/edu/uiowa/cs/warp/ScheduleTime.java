package edu.uiowa.cs.warp;


public class ScheduleTime {
private Integer startTime;

public void setStartTime(Integer value) {
   this.startTime = value;
}

public Integer getStartTime() {
   return this.startTime;
}

private Integer endTime;

public void setEndTime(Integer value) {
   this.endTime = value;
}

public Integer getEndTime() {
   return this.endTime;
}

/*package*/ ScheduleTime (Integer startTime, Integer endTime) {
this.startTime = startTime;
   		this.endTime = endTime;
   	}

}
