package da.gui;

import da.conn.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class KhachHang extends JPanel {
    private JTextField txtMaKH, txtTenKH, txtSDT, txtEmail;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnThem, btnSua, btnXoa, btnCapNhat, btnKhoiTao;

    public KhachHang() {
        setLayout(new BorderLayout());

        // 🔹 Panel nhập thông tin khách hàng
        JPanel panelInput = new JPanel(new GridLayout(4, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

        panelInput.add(new JLabel("Mã KH:"));
        txtMaKH = new JTextField();
        panelInput.add(txtMaKH);

        panelInput.add(new JLabel("Tên KH:"));
        txtTenKH = new JTextField();
        panelInput.add(txtTenKH);

        panelInput.add(new JLabel("SĐT:"));
        txtSDT = new JTextField();
        panelInput.add(txtSDT);

        panelInput.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panelInput.add(txtEmail);

        add(panelInput, BorderLayout.NORTH);

        // 🔹 Bảng hiển thị danh sách khách hàng
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Mã KH", "Tên KH", "SĐT", "Email"});
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setEnabled(false);
        add(scrollPane, BorderLayout.CENTER);

        // 🔹 Nút chức năng
        JPanel panelButtons = new JPanel(new FlowLayout());

        btnThem = new JButton("Thêm");
        btnThem.addActionListener(e -> themKhachHang());
        panelButtons.add(btnThem);

        btnSua = new JButton("Sửa");
        btnSua.addActionListener(e -> suaKhachHang());
        panelButtons.add(btnSua);

        btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaKhachHang());
        panelButtons.add(btnXoa);

        btnCapNhat = new JButton("Cập nhật");
        btnCapNhat.addActionListener(e -> loadData());
        panelButtons.add(btnCapNhat);
        
        btnKhoiTao = new JButton("Khởi tạo");
        btnKhoiTao.addActionListener(e -> resetForm());
        panelButtons.add(btnKhoiTao);

        add(panelButtons, BorderLayout.SOUTH);

        // 🔹 Sự kiện click vào bảng để hiển thị thông tin
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    txtMaKH.setText(model.getValueAt(selectedRow, 0).toString());
                    txtTenKH.setText(model.getValueAt(selectedRow, 1).toString());
                    txtSDT.setText(model.getValueAt(selectedRow, 2).toString());
                    txtEmail.setText(model.getValueAt(selectedRow, 3).toString());
                }
            }
        });

        loadData(); // Load dữ liệu khi mở form
    }

    // 🟢 Hàm lấy dữ liệu từ database
    private void loadData() {
        model.setRowCount(0); // Xóa dữ liệu cũ trước khi load lại
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers")) {
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("customer_id"),
                    rs.getString("customer_name"),
                    rs.getString("phone"),
                    rs.getString("email")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
    }

    // 🟢 Hàm thêm khách hàng
    private void themKhachHang() {
        // Kiểm tra thông tin nhập vào
        if (txtTenKH.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin khách hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO customers (customer_id, customer_name, phone, email) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtMaKH.getText());
            pst.setString(2, txtTenKH.getText());
            pst.setString(3, txtSDT.getText());
            pst.setString(4, txtEmail.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🟢 Hàm sửa thông tin khách hàng
    private void suaKhachHang() {
        if (txtMaKH.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa chọn khách hàng để sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra thông tin nhập vào
        if (txtTenKH.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin khách hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE customers SET customer_name=?, phone=?, email=? WHERE customer_id=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtTenKH.getText());
            pst.setString(2, txtSDT.getText());
            pst.setString(3, txtEmail.getText());
            pst.setString(4, txtMaKH.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sửa khách hàng thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🟢 Hàm xóa khách hàng
    private void xoaKhachHang() {
        if (txtMaKH.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa chọn khách hàng để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection()) {
                String query = "DELETE FROM customers WHERE customer_id=?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, txtMaKH.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
