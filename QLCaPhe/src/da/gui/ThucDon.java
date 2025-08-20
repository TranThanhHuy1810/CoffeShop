package da.gui;

import da.conn.Database;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class ThucDon extends JPanel {
    private JTextField txtMaMon, txtTenMon, txtGia;
    private JComboBox<String> cbDanhMuc;
    private JLabel lblHinhAnh;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnThem, btnSua, btnXoa, btnCapNhat, btnChonAnh;
    private byte[] imageBytes;

    public ThucDon() {
        setLayout(new BorderLayout());

        // Panel nhập liệu
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBorder(new TitledBorder("Thông tin món ăn"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Mã món ăn:", "Tên món:", "Danh mục:", "Giá:", "Hình ảnh:"};
        txtMaMon = new JTextField(); txtTenMon = new JTextField(); txtGia = new JTextField();
        cbDanhMuc = new JComboBox<>();
        lblHinhAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblHinhAnh.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblHinhAnh.setPreferredSize(new Dimension(120, 120));

        JComponent[] components = {txtMaMon, txtTenMon, cbDanhMuc, txtGia, lblHinhAnh};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            panelInput.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            panelInput.add(components[i], gbc);
        }
        add(panelInput, BorderLayout.NORTH);

        // Bảng hiển thị danh sách món ăn
        model = new DefaultTableModel(new String[]{"Mã món", "Tên món", "Danh mục", "Giá", "Hình ảnh"}, 0);
        table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaMon.setText(model.getValueAt(row, 0).toString());
                    txtTenMon.setText(model.getValueAt(row, 1).toString());
                    cbDanhMuc.setSelectedItem(model.getValueAt(row, 2).toString());
                    txtGia.setText(model.getValueAt(row, 3).toString());
                    loadHinhAnh(row);
                }
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel chứa các nút bấm
        JPanel panelButtons = new JPanel(new FlowLayout());
        btnThem = new JButton("Thêm"); btnThem.addActionListener(e -> themMonAn());
        btnSua = new JButton("Sửa"); btnSua.addActionListener(e -> suaMonAn());
        btnXoa = new JButton("Xóa"); btnXoa.addActionListener(e -> xoaMonAn());
        btnCapNhat = new JButton("Cập nhật"); btnCapNhat.addActionListener(e -> loadData());
        btnChonAnh = new JButton("Chọn ảnh"); btnChonAnh.addActionListener(e -> chonAnh());

        panelButtons.add(btnThem); panelButtons.add(btnSua); panelButtons.add(btnXoa);
        panelButtons.add(btnCapNhat); panelButtons.add(btnChonAnh);
        add(panelButtons, BorderLayout.SOUTH);

        loadData();
        loadDanhMuc();
    }

    private void loadDanhMuc() {
        try (Connection conn = Database.getConnection()) {
            cbDanhMuc.removeAllItems();
            PreparedStatement pst = conn.prepareStatement("SELECT CategoryFood_id FROM categoryfood");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cbDanhMuc.addItem(rs.getString("CategoryFood_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void themMonAn() {
        if (txtMaMon.getText().isEmpty() || txtTenMon.getText().isEmpty() || txtGia.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO food (food_id, CategoryFood_id, food_name, price, image) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtMaMon.getText());
            pst.setString(2, cbDanhMuc.getSelectedItem().toString());
            pst.setString(3, txtTenMon.getText());
            pst.setInt(4, Integer.parseInt(txtGia.getText()));
            pst.setBytes(5, imageBytes != null ? imageBytes : null);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm món ăn thành công!");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void suaMonAn() {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE food SET CategoryFood_id=?, food_name=?, price=?, image=? WHERE food_id=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, cbDanhMuc.getSelectedItem().toString());
            pst.setString(2, txtTenMon.getText());
            pst.setInt(3, Integer.parseInt(txtGia.getText()));
            pst.setBytes(4, imageBytes);
            pst.setString(5, txtMaMon.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cập nhật món ăn thành công!");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void xoaMonAn() {
        try (Connection conn = Database.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("DELETE FROM food WHERE food_id=?");
            pst.setString(1, txtMaMon.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa món ăn thành công!");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "png", "jpeg"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                imageBytes = new FileInputStream(file).readAllBytes();
                lblHinhAnh.setIcon(new ImageIcon(new ImageIcon(imageBytes).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadHinhAnh(int row) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT image FROM food WHERE food_id=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, model.getValueAt(row, 0).toString());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                byte[] imgData = rs.getBytes("image");
                if (imgData != null && imgData.length > 0) {
                    imageBytes = imgData; // Cập nhật ảnh vào biến toàn cục
                    ImageIcon icon = new ImageIcon(new ImageIcon(imgData).getImage()
                            .getScaledInstance(120, 120, Image.SCALE_SMOOTH));
                    lblHinhAnh.setIcon(icon);
                } else {
                    lblHinhAnh.setIcon(null); // Nếu không có ảnh, đặt lại về null
                    lblHinhAnh.setText("Chưa có ảnh");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM food")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(3), rs.getString(2), rs.getInt(4), "Ảnh"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
