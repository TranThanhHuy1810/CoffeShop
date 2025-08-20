package da.gui;

import da.conn.Database;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class NhanVien extends JPanel {
    private JTextField txtMaNV, txtTenNV, txtNgaySinh, txtChucVu, txtNgayThue, txtLuong, txtSDT, txtDiaChi, txtEmail;
    private JComboBox<String> cbGioiTinh;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnThem, btnSua, btnXoa, btnCapNhat, btnKhoiTao;

    public NhanVien() {
        setLayout(new BorderLayout());

        JPanel panelInput = new JPanel(new GridLayout(10, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));

        panelInput.add(new JLabel("Mã NV:"));
        txtMaNV = new JTextField();
        panelInput.add(txtMaNV);

        panelInput.add(new JLabel("Tên NV:"));
        txtTenNV = new JTextField();
        panelInput.add(txtTenNV);

        panelInput.add(new JLabel("Ngày sinh:"));
        txtNgaySinh = new JTextField();
        panelInput.add(txtNgaySinh);

        panelInput.add(new JLabel("Giới tính:"));
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        panelInput.add(cbGioiTinh);

        panelInput.add(new JLabel("Chức vụ:"));
        txtChucVu = new JTextField();
        panelInput.add(txtChucVu);

        panelInput.add(new JLabel("Ngày thuê:"));
        txtNgayThue = new JTextField();
        panelInput.add(txtNgayThue);

        panelInput.add(new JLabel("Lương:"));
        txtLuong = new JTextField();
        panelInput.add(txtLuong);

        panelInput.add(new JLabel("SĐT:"));
        txtSDT = new JTextField();
        panelInput.add(txtSDT);

        panelInput.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        panelInput.add(txtDiaChi);

        panelInput.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panelInput.add(txtEmail);

        add(panelInput, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Mã NV", "Tên NV", "Ngày sinh", "Giới tính", "Chức vụ", "Ngày thuê", "Lương", "SĐT", "Địa chỉ", "Email"});
        table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(model.getValueAt(row, 0).toString());
                    txtTenNV.setText(model.getValueAt(row, 1).toString());
                    txtNgaySinh.setText(model.getValueAt(row, 2).toString());
                    cbGioiTinh.setSelectedItem(model.getValueAt(row, 3).toString());
                    txtChucVu.setText(model.getValueAt(row, 4).toString());
                    txtNgayThue.setText(model.getValueAt(row, 5).toString());
                    txtLuong.setText(model.getValueAt(row, 6).toString());
                    txtSDT.setText(model.getValueAt(row, 7).toString());
                    txtDiaChi.setText(model.getValueAt(row, 8).toString());
                    txtEmail.setText(model.getValueAt(row, 9).toString());
                }
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panelButtons = new JPanel(new FlowLayout());
        btnThem = new JButton("Thêm");
        btnThem.addActionListener(e -> themNhanVien());
        panelButtons.add(btnThem);

        btnSua = new JButton("Sửa");
        btnSua.addActionListener(e -> suaNhanVien());
        panelButtons.add(btnSua);

        btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaNhanVien());
        panelButtons.add(btnXoa);

        btnCapNhat = new JButton("Cập nhật");
        btnCapNhat.addActionListener(e -> loadData());
        panelButtons.add(btnCapNhat);
        
        btnKhoiTao = new JButton("Khởi tạo");
        btnKhoiTao.addActionListener(e -> khoiTaoForm());
        panelButtons.add(btnKhoiTao);

        add(panelButtons, BorderLayout.SOUTH);

        loadData();
    }

    private void khoiTaoForm() {
        txtMaNV.setText("");
        txtTenNV.setText("");
        txtNgaySinh.setText("");
        cbGioiTinh.setSelectedIndex(0);
        txtChucVu.setText("");
        txtNgayThue.setText("");
        txtLuong.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtEmail.setText("");
    }

    private boolean validateInput() {
        if (txtMaNV.getText().trim().isEmpty() || txtTenNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên và tên nhân viên không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(txtLuong.getText());  // Kiểm tra lương là số
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lương phải là một số hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Thêm các kiểm tra cho các trường khác nếu cần
        return true;
    }

    private void themNhanVien() {
        if (!validateInput()) {
            return;
        }
        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO employees VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtMaNV.getText());
            pst.setString(2, txtTenNV.getText());
            pst.setString(3, txtNgaySinh.getText());
            pst.setString(4, cbGioiTinh.getSelectedItem().toString());
            pst.setString(5, txtChucVu.getText());
            pst.setString(6, txtNgayThue.getText());
            pst.setInt(7, Integer.parseInt(txtLuong.getText()));
            pst.setString(8, txtSDT.getText());
            pst.setString(9, txtDiaChi.getText());
            pst.setString(10, txtEmail.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void suaNhanVien() {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE employees SET employee_name=?, dateofbirth=?, Gender=?, position=?, hire_date=?, salary=?, phone=?, address=?, email=? WHERE employee_id=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtTenNV.getText());
            pst.setString(2, txtNgaySinh.getText());
            pst.setString(3, cbGioiTinh.getSelectedItem().toString());
            pst.setString(4, txtChucVu.getText());
            pst.setString(5, txtNgayThue.getText());
            pst.setInt(6, Integer.parseInt(txtLuong.getText()));
            pst.setString(7, txtSDT.getText());
            pst.setString(8, txtDiaChi.getText());
            pst.setString(9, txtEmail.getText());
            pst.setString(10, txtMaNV.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xoaNhanVien() {
        int response = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa nhân viên này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection()) {
                PreparedStatement pst = conn.prepareStatement("DELETE FROM employees WHERE employee_id=?");
                pst.setString(1, txtMaNV.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM employees")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getString(10)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
