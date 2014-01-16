package test;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * 表格简单例子
 * @author zhuchx
 */
public class TestTableModel {
	public void init() {
		JFrame jf = new JFrame("简单表格");
		//以二维数组和一维数组来创建一个JTable对象  
		Object[] columnTitle = { "姓名", "年龄", "性别" };
		Object[][] tableData = { new Object[] { "李清照", 29, "女" }, new Object[] { "苏格拉底", 56, "男" } };

		TableColumnModel columns = new DefaultTableColumnModel();
		TableColumn column1 = new TableColumn();
		column1.setHeaderValue("姓名");
		TableColumn column2 = new TableColumn();
		column2.setHeaderValue("性别");
		columns.addColumn(column1);
		columns.addColumn(column2);

		DefaultTableModel datas = new DefaultTableModel(2, 2);
		datas.setValueAt("李清照", 0, 0);
		datas.setValueAt("女", 0, 1);
		datas.setValueAt("苏格拉底", 1, 0);
		datas.setValueAt("男", 1, 1);

		datas.setColumnIdentifiers(columnTitle);

		TableModel datas2 = new DefaultTableModel(tableData, columnTitle);

		//		TableModel datas = new DefaultTableModel(tableData, columnTitle);
		//		JTable table = new JTable(tableData, columnTitle);
		//		JTable table = new JTable(datas, columns);
		//		JTable table = new JTable(datas);
		JTable table = new JTable(tableData, new Object[] {});

		//		JTable table = new JTable(datas2);
		//将JTable对象放在JScrollPane中，并将该JScrollPane放在窗口中显示出来  
		jf.add(new JScrollPane(table));
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);

		datas.setValueAt("cc", 0, 1);
	}

	public static void main(String[] args) {
		new TestTableModel().init();
	}
}
