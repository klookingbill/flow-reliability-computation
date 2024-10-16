package edu.uiowa.cs.warp;

import javax.swing.JFrame;
import javax.swing.JTable;

/**
* GuiVisualization Class to create a Java Swing window that displays a table of data with column
 * headings.
 * 
 * @author sgoddard
 * @version 1.5
 *  * */public class GuiVisualization {
/*package*/ JFrame frame;
/*package*/ JTable jtable;
/**
* Constructor to create a Java Swing Window for a table of data with column headings. The window
 *    * is initially not visible, and is made visible by a call to setVisible().
 *    * 
 *    * @param title Window title
 *    * @param columnNames Column heading names
 *    * @param table table of strings representing the data */public GuiVisualization (String title, String[] columnNames, String[][] table) {
// Frame initialization
    frame = new JFrame();
   
    // Frame Title
    frame.setTitle(title);
   
    // Initializing the JTable with input parameters
    jtable = new JTable(table, columnNames);
    jtable.setBounds(30, 40, 200, 300); // default size
    // resizeColumnWidth(jtable); // uncomment to auto size column widths
   
    // attached the table to a JScrollPane and then add both to the frame
    JScrollPane sp = new JScrollPane(jtable); // gives table a vertical scrollbar
    frame.add(sp);
    // Frame Size
    frame.setSize(500, 200);
    jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // creates horizontal scrollbar
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // Frame Visible = false
    frame.setVisible(false);
     }

/**
* Make the table visible. */public void setVisible() {
frame.setVisible(true);
     }

private void resizeColumnWidth(JTable table) {
final TableColumnModel columnModel = table.getColumnModel();
    for (int column = 0; column < table.getColumnCount(); column++) {
      int width = 70; // Min width
      for (int row = 0; row < table.getRowCount(); row++) {
        TableCellRenderer renderer = table.getCellRenderer(row, column);
        Component comp = table.prepareRenderer(renderer, row, column);
        width = Math.max(comp.getPreferredSize().width + 5, width);
      }
      if (width > 300)
        width = 300;
      columnModel.getColumn(column).setPreferredWidth(width);
    }
     }

}
