package da.gui;

import da.conn.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class TaiKhoan extends JPanel {
    private JTextField txtMaTK, txtTenTK, txtHienThi, txtMatKhau;
    private JComboBox<String> cbMaNV, cbLoaiTK;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnThem, btnSua, btnXoa, btnCapNhat, btnKhoiTao;

    public TaiKhoan() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel nhập liệu
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Mã tài khoản:", "Tên tài khoản:", "Tên hiển thị:", "Mật khẩu:", "Mã nhân viên:", "Loại tài khoản:"};
        JTextField[] textFields = {txtMaTK = new JTextField(), txtTenTK = new JTextField(), txtHienThi = new JTextField(), txtMatKhau = new JTextField()};
        JComboBox[] comboBoxes = {cbMaNV = new JComboBox<>(), cbLoaiTK = new JComboBox<>()};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panelInput.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            if (i < 4) {
                panelInput.add(textFields[i], gbc);
            } else {
                panelInput.add(comboBoxes[i - 4], gbc);
            }
        }

        add(panelInput, BorderLayout.NORTH);

        // Bảng hiển thị dữ liệu
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Tên TK", "Tên hiển thị", "Mật khẩu", "Loại TK", "Mã NV"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel nút chức năng
        JPanel panelButtons = new JPanel(new GridLayout(1, 5, 10, 10));
        btnThem = new JButton("Thêm");
        btnThem.addActionListener(e -> themTaiKhoan());
        panelButtons.add(btnThem);

        btnSua = new JButton("Sửa");
        btnSua.addActionListener(e -> suaTaiKhoan());
        panelButtons.add(btnSua);

        btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaTaiKhoan());
        panelButtons.add(btnXoa);

        btnCapNhat = new JButton("Cập nhật");
        btnCapNhat.addActionListener(e -> loadData());
        panelButtons.add(btnCapNhat);

        btnKhoiTao = new JButton("Khởi tạo");
        btnKhoiTao.addActionListener(e -> clearFields());
        panelButtons.add(btnKhoiTao);

        add(panelButtons, BorderLayout.SOUTH);
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                	txtMaTK.setText(model.getValueAt(selectedRow, 0).toString());
                	txtTenTK.setText(model.getValueAt(selectedRow, 1).toString());
                    txtHienThi.setText(model.getValueAt(selectedRow, 2).toString());
                    txtMatKhau.setText(model.getValueAt(selectedRow, 3).toString());

                }
            }
        });

        loadData();
        loadComboBoxData();
    }

    private void loadComboBoxData() {
        try (Connection conn = Database.getConnection()) {
            cbMaNV.removeAllItems();
            PreparedStatement pst = conn.prepareStatement("SELECT employee_id FROM employees");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cbMaNV.addItem(rs.getString("employee_id"));
            }
            cbLoaiTK.removeAllItems();
            cbLoaiTK.addItem("Admin");
            cbLoaiTK.addItem("Nhân viên");           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void themTaiKhoan() {
        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO accounts VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(txtMaTK.getText()));
            pst.setString(2, txtTenTK.getText());
            pst.setString(3, txtHienThi.getText());
            pst.setString(4, txtMatKhau.getText());
            pst.setInt(5, cbLoaiTK.getSelectedIndex() + 1);
            pst.setString(6, cbMaNV.getSelectedItem().toString());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void suaTaiKhoan() {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE accounts SET account_name=?, display_name=?, password=?, type_id=?, employee_id=? WHERE account_id=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtTenTK.getText());
            pst.setString(2, txtHienThi.getText());
            pst.setString(3, txtMatKhau.getText());
            pst.setInt(4, cbLoaiTK.getSelectedIndex() + 1);
            pst.setString(5, cbMaNV.getSelectedItem().toString());
            pst.setInt(6, Integer.parseInt(txtMaTK.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xoaTaiKhoan() {
        try (Connection conn = Database.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("DELETE FROM accounts WHERE account_id=?");
            pst.setInt(1, Integer.parseInt(txtMaTK.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM accounts")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), "******", rs.getInt(5), rs.getString(6)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtMaTK.setText("");
        txtTenTK.setText("");
        txtHienThi.setText("");
        txtMatKhau.setText("");
        cbMaNV.setSelectedIndex(0);
        cbLoaiTK.setSelectedIndex(0);
    }
}
