package edu.uiowa.cs.warp;

import java.util.Comparator;

public class DeadlineComparator <T extends SchedulableObject> implements Comparator <T>  {
@Override
public int compare (T obj1, T obj2)
{
return obj1.deadlineComparison(obj2); 
   	}
}
