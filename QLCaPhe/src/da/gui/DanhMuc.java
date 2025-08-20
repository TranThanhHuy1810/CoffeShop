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

public class DanhMuc extends JPanel {
    private JTextField txtMaDM, txtTenDM;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnThem, btnSua, btnXoa, btnCapNhat, btnKhoiTao;

    public DanhMuc() {
        setLayout(new BorderLayout());

        // Panel nhập liệu
        JPanel panelInput = new JPanel(new GridLayout(2, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Thông tin danh mục"));

        panelInput.add(new JLabel("Mã danh mục:"));
        txtMaDM = new JTextField();
        panelInput.add(txtMaDM);

        panelInput.add(new JLabel("Tên danh mục:"));
        txtTenDM = new JTextField();
        panelInput.add(txtTenDM);

        add(panelInput, BorderLayout.NORTH);

        // Bảng hiển thị dữ liệu
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Mã danh mục", "Tên danh mục"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel nút chức năng
        JPanel panelButtons = new JPanel(new FlowLayout());

        btnThem = new JButton("Thêm");
        btnThem.addActionListener(e -> themDanhMuc());
        panelButtons.add(btnThem);

        btnSua = new JButton("Sửa");
        btnSua.addActionListener(e -> suaDanhMuc());
        panelButtons.add(btnSua);

        btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaDanhMuc());
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
                	txtMaDM.setText(model.getValueAt(selectedRow, 0).toString());
                	txtTenDM.setText(model.getValueAt(selectedRow, 1).toString());                 
                }
            }
        });
        loadData();
    }

    private void themDanhMuc() {
        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO categoryfood VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtMaDM.getText());
            pst.setString(2, txtTenDM.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm danh mục thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void suaDanhMuc() {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE categoryfood SET name=? WHERE CategoryFood_id=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtTenDM.getText());
            pst.setString(2, txtMaDM.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cập nhật danh mục thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xoaDanhMuc() {
        try (Connection conn = Database.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("DELETE FROM categoryfood WHERE CategoryFood_id=?");
            pst.setString(1, txtMaDM.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa danh mục thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM categoryfood")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtMaDM.setText("");
        txtTenDM.setText("");
    }
}
