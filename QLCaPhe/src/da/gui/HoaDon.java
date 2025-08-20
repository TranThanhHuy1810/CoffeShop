package da.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import da.conn.Database;

import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class HoaDon extends JPanel {
    private JTable tableBillInfo, tableBillDetails;
    private DefaultTableModel modelBillInfo, modelBillDetails;
    private JTextField txtSearch;
    private JButton btnViewDetails, btnRefresh;
    private Connection conn;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public HoaDon() {
        setLayout(new BorderLayout());

        // Kết nối database
        conn = Database.getConnection();

        // Panel tìm kiếm và refresh
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        btnViewDetails = new JButton("Xem chi tiết");
        btnRefresh = new JButton("Làm mới");

        panelTop.add(new JLabel("Tìm kiếm mã hóa đơn:"));
        panelTop.add(txtSearch);
        panelTop.add(btnViewDetails);
        panelTop.add(btnRefresh);
        add(panelTop, BorderLayout.NORTH);

        // Bảng hóa đơn (billinfo)
        modelBillInfo = new DefaultTableModel();
        modelBillInfo.setColumnIdentifiers(new String[]{
                "Mã HĐ", "Khách hàng", "Bàn", "Nhân viên", "Check-in", "Check-out", "SL", "Tổng tiền", "Giảm giá"
        });
        tableBillInfo = new JTable(modelBillInfo);
        rowSorter = new TableRowSorter<>(modelBillInfo);
        tableBillInfo.setRowSorter(rowSorter);
        add(new JScrollPane(tableBillInfo), BorderLayout.CENTER);

        // Bảng chi tiết hóa đơn (bill)
        modelBillDetails = new DefaultTableModel();
        modelBillDetails.setColumnIdentifiers(new String[]{
                "Tên món", "Số lượng", "Giá", "Tổng tiền", "Trạng thái"
        });
        tableBillDetails = new JTable(modelBillDetails);
        add(new JScrollPane(tableBillDetails), BorderLayout.SOUTH);

        // Load dữ liệu
        loadBillInfo();

        // Sự kiện tìm kiếm mã hóa đơn
        txtSearch.addCaretListener(e -> searchBillInfo());

        // Sự kiện xem chi tiết hóa đơn
        btnViewDetails.addActionListener(e -> showBillDetails());

        // Làm mới danh sách
        btnRefresh.addActionListener(e -> loadBillInfo());
    }

    // 🔍 Hàm tìm kiếm hóa đơn theo mã HĐ
    private void searchBillInfo() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            rowSorter.setRowFilter(null); // Hiển thị tất cả nếu không nhập gì
        } else {
            try {
                rowSorter.setRowFilter(RowFilter.regexFilter("^" + searchText, 0)); // Lọc theo cột "Mã HĐ"
            } catch (Exception ignored) {
            }
        }
    }

    // Load danh sách hóa đơn từ database
    private void loadBillInfo() {
        modelBillInfo.setRowCount(0); // Xóa dữ liệu cũ
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM billinfo")) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("billInfo_id"));
                row.add(rs.getString("customer_id"));
                row.add(rs.getString("table_id"));
                row.add(rs.getString("employee_id"));
                row.add(rs.getTimestamp("check_in") != null ? sdf.format(rs.getTimestamp("check_in")) : "");
                row.add(rs.getTimestamp("check_out") != null ? sdf.format(rs.getTimestamp("check_out")) : "");
                row.add(rs.getInt("total_quantity"));
                row.add(rs.getInt("total_price"));
                row.add(rs.getInt("discount"));
                modelBillInfo.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hiển thị chi tiết hóa đơn theo billInfo_id
    private void showBillDetails() {
        int selectedRow = tableBillInfo.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xem!");
            return;
        }

        int billInfoId = (int) tableBillInfo.getValueAt(selectedRow, 0);
        modelBillDetails.setRowCount(0); // Xóa dữ liệu cũ

        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM bill WHERE billInfo_id = ?")) {
            ps.setInt(1, billInfoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("food_name"));
                row.add(rs.getInt("quantity"));
                row.add(rs.getInt("price"));
                row.add(rs.getInt("total_price"));
                row.add(rs.getString("status"));
                modelBillDetails.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
