/*
 * Copyright 2008-2012, David Karnok 
 * The file is part of the Open Imperium Galactica project.
 * 
 * The code should be distributed under the LGPL license.
 * See http://www.gnu.org/licenses/lgpl.html for details.
 */

package hu.openig.utils;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Java Swing GUI utilities.
 * @author akarnokd, 2012.09.24.
 */
public final class GUIUtils {
	/** Utility class. */
	private GUIUtils() { }
	/**
	 * Resizes the table columns based on the column and data preferred widths.
	 * @param table the original table
	 * @param model the data model
	 * @return the table itself
	 */
    public static JTable autoResizeColWidth(JTable table, AbstractTableModel model) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setModel(model);
 
        int margin = 5;
 
        for (int i = 0; i < table.getColumnCount(); i++) {
            int                     vColIndex = i;
            DefaultTableColumnModel colModel  = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn             col       = colModel.getColumn(vColIndex);
            int                     width     = 0;
 
            // Get width of column header
            TableCellRenderer renderer = col.getHeaderRenderer();
 
            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }
 
            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
 
            width = comp.getPreferredSize().width;
 
            // Get maximum width of column data
            for (int r = 0; r < table.getRowCount(); r++) {
                renderer = table.getCellRenderer(r, vColIndex);
                comp     = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false,
                        r, vColIndex);
                width = Math.max(width, comp.getPreferredSize().width);
            }
 
            // Add margin
            width += 2 * margin;
 
            // Set the width
            col.setPreferredWidth(width);
        }
 
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
            SwingConstants.LEFT);
 
        // table.setAutoCreateRowSorter(true);
//        table.getTableHeader().setReorderingAllowed(false);
 
//        for (int i = 0; i < table.getColumnCount(); i++) {
//            TableColumn column = table.getColumnModel().getColumn(i);
// 
//            column.setCellRenderer(new DefaultTableColour());
//        }
 
        return table;
    }

}