package edu.uiowa.cs.warp;

import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Collection;

public class SchedulableObjectQueue <T extends SchedulableObject>  extends PriorityQueue <T>  {
private static final long serialVersionUID = 1L;

/*package*/ SchedulableObjectQueue (Comparator <T> comparitor) {
super(1, comparitor);
   	}

/*package*/ SchedulableObjectQueue (Comparator <T> comparitor, Collection <T> schedulableObjects) {
super(comparitor);
   		this.addAll(schedulableObjects);
   	}

}
