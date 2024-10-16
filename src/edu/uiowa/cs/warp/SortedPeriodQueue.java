package edu.uiowa.cs.warp;

import java.util.PriorityQueue;
import java.util.Collection;

public class SortedPeriodQueue <T extends SchedulableObject>  extends PriorityQueue <T>  {
private static final long serialVersionUID = 1L;

/*package*/ SortedPeriodQueue () {
super(1, new PeriodComparator<T>());
   	}

/*package*/ SortedPeriodQueue (Collection <T> schedulableObjects) {
super(new PeriodComparator<T>());
   		this.addAll(schedulableObjects);
   	}

}
