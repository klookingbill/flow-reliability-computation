package edu.uiowa.cs.warp;

import java.util.PriorityQueue;
import java.util.Collection;

public class ReleaseTimeQueue <T extends SchedulableObject>  extends PriorityQueue <T>  {
private static final long serialVersionUID = 1L;

/*package*/ ReleaseTimeQueue () {
super(1, new ReleaseTimeComparator<T>());
   	}

/*package*/ ReleaseTimeQueue (Collection <T> schedulableObjects) {
super(new ReleaseTimeComparator<T>());
   		this.addAll(schedulableObjects);
   	}

}
