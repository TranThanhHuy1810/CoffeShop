package da.gui;

import da.conn.Database;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Ban extends JPanel {
    private JTextField txtMaBan, txtTenBan;
    private JComboBox<String> cbTrangThai;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnThem, btnSua, btnXoa, btnCapNhat;

    public Ban() {
        setLayout(new BorderLayout());

        // Panel nhập thông tin
        JPanel panelInput = new JPanel(new GridLayout(4, 2, 5, 5));
        panelInput.setBackground(new Color(0, 255, 128));
        panelInput.setBorder(BorderFactory.createTitledBorder("Thông tin bàn"));

        panelInput.add(new JLabel("Mã bàn:"));
        txtMaBan = new JTextField();
        panelInput.add(txtMaBan);

        panelInput.add(new JLabel("Tên bàn:"));
        txtTenBan = new JTextField();
        panelInput.add(txtTenBan);

        panelInput.add(new JLabel("Trạng thái:"));
        cbTrangThai = new JComboBox<>(new String[]{"Trống", "Có khách"});
        panelInput.add(cbTrangThai);

        add(panelInput, BorderLayout.NORTH);

        // Bảng dữ liệu
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Mã bàn", "Tên bàn", "Trạng thái"});
        table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaBan.setText(model.getValueAt(row, 0).toString());
                    txtTenBan.setText(model.getValueAt(row, 1).toString());
                    cbTrangThai.setSelectedItem(model.getValueAt(row, 2).toString());
                }
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel nút thao tác
        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.setBackground(new Color(0, 255, 128));
        btnThem = new JButton("Thêm");
        btnThem.addActionListener(e -> themBan());
        panelButtons.add(btnThem);

        btnSua = new JButton("Sửa");
        btnSua.addActionListener(e -> suaBan());
        panelButtons.add(btnSua);

        btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaBan());
        panelButtons.add(btnXoa);

        btnCapNhat = new JButton("Cập nhật");
        btnCapNhat.addActionListener(e -> loadData());
        panelButtons.add(btnCapNhat);

        add(panelButtons, BorderLayout.SOUTH);

        loadData();
    }

    private void themBan() {
        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO tables (table_id, table_name, status) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtMaBan.getText());
            pst.setString(2, txtTenBan.getText());
            pst.setString(3, cbTrangThai.getSelectedItem().toString());
            pst.executeUpdate();
            
            // Thông báo thành công
            JOptionPane.showMessageDialog(this, "Thêm bàn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadData(); // Load lại dữ liệu bảng
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm bàn! " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void suaBan() {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE tables SET table_name=?, status=? WHERE table_id=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtTenBan.getText());
            pst.setString(2, cbTrangThai.getSelectedItem().toString());
            pst.setString(3, txtMaBan.getText());
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                // Thông báo thành công
                JOptionPane.showMessageDialog(this, "Cập nhật bàn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData(); // Load lại dữ liệu bảng
            } else {
                // Thông báo không có thay đổi
                JOptionPane.showMessageDialog(this, "Không tìm thấy bàn để cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật bàn! " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void xoaBan() {
        try (Connection conn = Database.getConnection()) {
            String query = "DELETE FROM tables WHERE table_id=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtMaBan.getText());
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                // Thông báo thành công
                JOptionPane.showMessageDialog(this, "Xóa bàn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData(); // Load lại dữ liệu bảng
            } else {
                // Thông báo không có thay đổi
                JOptionPane.showMessageDialog(this, "Không tìm thấy bàn để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa bàn! " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tables")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("table_id"), rs.getString("table_name"), rs.getString("status")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
