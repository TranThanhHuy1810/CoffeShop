package da.gui;

import da.conn.Database;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ThongKe extends JPanel {
        private JTable tableTopProducts, tableBills;
        private DefaultTableModel modelTopProducts, modelBills;
        private JPanel chartPanel;
        private Connection conn;

        public ThongKe() {
            setLayout(new BorderLayout());
            conn = Database.getConnection();

            // Panel biểu đồ
            chartPanel = new JPanel(new BorderLayout());
            chartPanel.setPreferredSize(new Dimension(600, 400));
            add(chartPanel, BorderLayout.NORTH);

            // Bảng sản phẩm bán chạy
            modelTopProducts = new DefaultTableModel();
            modelTopProducts.setColumnIdentifiers(new String[]{"Tên món", "Số lượng bán"});
            tableTopProducts = new JTable(modelTopProducts);

            // Bảng hóa đơn
            modelBills = new DefaultTableModel();
            modelBills.setColumnIdentifiers(new String[]{"Mã HĐ", "Ngày", "Tổng tiền"});
            tableBills = new JTable(modelBills);

            // Chia khu vực bảng
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    new JScrollPane(tableTopProducts), new JScrollPane(tableBills));
            splitPane.setResizeWeight(0.5);
            add(splitPane, BorderLayout.CENTER);

            // Tải dữ liệu
            loadRevenueChart();
            loadTopProducts();
            loadBills();
        }

        private void loadRevenueChart() {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            String query = "SELECT DATE(check_out) AS ngay, SUM(total_price) AS doanh_thu FROM billinfo WHERE check_out IS NOT NULL GROUP BY ngay";
            
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    dataset.addValue(rs.getInt("doanh_thu"), "Doanh thu", rs.getString("ngay"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            JFreeChart chart = ChartFactory.createBarChart(
                    "Doanh thu theo ngày", "Ngày", "Doanh thu",
                    dataset);
            
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            chartPanel.removeAll();
            chartPanel.add(new ChartPanel(chart));
            chartPanel.revalidate();
        }

        private void loadTopProducts() {
            modelTopProducts.setRowCount(0);
            String query = "SELECT food_name, SUM(quantity) AS total_sold FROM bill GROUP BY food_name ORDER BY total_sold DESC LIMIT 10";
            
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("food_name"));
                    row.add(rs.getInt("total_sold"));
                    modelTopProducts.addRow(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void loadTopProducts() {
    modelTopProducts.setRowCount(0);
    String query = "SELECT TOP 10 MAHANG, SUM(SL) AS THANHTIEN FROM CTHD GROUP BY MAHANG ORDER BY THANHTIEN DESC";

    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getString("MAHANG"));
            row.add(rs.getInt("THANHTIEN"));
            modelTopProducts.addRow(row);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
