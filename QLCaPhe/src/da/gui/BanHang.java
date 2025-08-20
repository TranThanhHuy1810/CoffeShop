package da.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;

import org.eclipse.ui.internal.forms.widgets.Paragraph;

import com.ibm.icu.text.SimpleDateFormat;
import java.util.Date;
import java.awt.*;
import java.sql.*;
import da.conn.Database;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.io.IOException;
import java.awt.Image;
import java.awt.Graphics2D;

//import com.itextpdf.text.Paragraph;


//import com.itextpdf.text.*;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.FileWriter;

public class BanHang extends JPanel {
    private JTextField txtBillID, txtNote, txtTotalQuantity, txtTotal, txtDiscount;
    private JComboBox<String> cbEmployee, cbCustomer, cbTable;
    private JFormattedTextField txtCheckIn, txtCheckOut;
    private JTable tableOrder;
    private DefaultTableModel modelOrder;
    private JPanel panelFoods;
    private JButton btnAddCustomer, btnSave, btnPrint;
    private JButton btnRemove, btnDecrease, btnClear;
    

    public BanHang() {
    	
        setLayout(new BorderLayout());
        setBackground(new Color(255, 204, 153)); // Nền cam nhạt

        // Panel trên cùng chứa tìm kiếm và tổng tiền
        JPanel panelTop = new JPanel(new BorderLayout());
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(560, 30)); // Ô tìm kiếm dài hơn

        // Tạo icon kích thước nhỏ
        ImageIcon icon = new ImageIcon("src/images/search_icon.jpg");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Resize icon
        ImageIcon smallIcon = new ImageIcon(img);

        JButton btnSearch = new JButton("Tìm kiếm", smallIcon);
        btnSearch.setPreferredSize(new Dimension(120, 30)); // Nút dài hơn
        btnSearch.setHorizontalTextPosition(SwingConstants.RIGHT); // Đưa chữ sang phải icon
        btnSearch.setIconTextGap(5); // Khoảng cách giữa icon và chữ

        // Thêm vào panel
        panelSearch.add(txtSearch);
        panelSearch.add(btnSearch);

        txtTotal = new JTextField("0.00");
        txtTotal.setEditable(false);
        txtTotal.setFont(new Font("Arial", Font.BOLD, 20));
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setPreferredSize(new Dimension(150, 40)); 

        panelTop.add(panelSearch, BorderLayout.WEST);
        panelTop.add(txtTotal, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);

        // Panel chính
        JPanel panelMain = new JPanel(new GridLayout(1, 2, 10, 10));

        // Panel danh sách món
        panelFoods = new JPanel(new GridLayout(0, 3, 10, 10));
        panelFoods.setBackground(new Color(255, 218, 185));
        JScrollPane scrollFoods = new JScrollPane(panelFoods);
        panelMain.add(scrollFoods);

        // Panel hóa đơn
        JPanel panelOrderInfo = new JPanel(null);

        JLabel lblBillID = new JLabel("Mã hóa đơn:");
        lblBillID.setBounds(20, 20, 100, 30);
        panelOrderInfo.add(lblBillID);
        txtBillID = new JTextField();
        txtBillID.setBounds(130, 20, 150, 30);
        panelOrderInfo.add(txtBillID);

        JLabel lblEmployee = new JLabel("Nhân viên:");
        lblEmployee.setBounds(20, 60, 100, 30);
        panelOrderInfo.add(lblEmployee);
        cbEmployee = new JComboBox<>();
        cbEmployee.setBounds(130, 60, 150, 30);
        panelOrderInfo.add(cbEmployee);

        JLabel lblCustomer = new JLabel("Khách hàng:");
        lblCustomer.setBounds(20, 100, 100, 30);
        panelOrderInfo.add(lblCustomer);
        cbCustomer = new JComboBox<>();
        cbCustomer.setBounds(130, 100, 150, 30);
        panelOrderInfo.add(cbCustomer);
        btnAddCustomer = new JButton("+");
        btnAddCustomer.setBounds(290, 100, 50, 30);
        panelOrderInfo.add(btnAddCustomer);

        JLabel lblTable = new JLabel("Bàn:");
        lblTable.setBounds(20, 140, 100, 30);
        panelOrderInfo.add(lblTable);
        cbTable = new JComboBox<>();
        cbTable.setBounds(130, 140, 150, 30);
        panelOrderInfo.add(cbTable);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        JLabel lblCheckIn = new JLabel("Ngày vào:");
        lblCheckIn.setBounds(20, 180, 100, 30);
        panelOrderInfo.add(lblCheckIn);
        txtCheckIn = new JFormattedTextField(currentDateTime);
        txtCheckIn.setBounds(130, 181, 150, 30);
        panelOrderInfo.add(txtCheckIn);

        JLabel lblCheckOut = new JLabel("Ngày ra:");
        lblCheckOut.setBounds(353, 60, 100, 30);
        panelOrderInfo.add(lblCheckOut);
        txtCheckOut = new JFormattedTextField(currentDateTime);
        txtCheckOut.setBounds(463, 60, 150, 30);
        panelOrderInfo.add(txtCheckOut);
        
        JLabel lblDiscount = new JLabel("Giảm giá:");
        lblDiscount.setBounds(353, 100, 100, 30);
        panelOrderInfo.add(lblDiscount);
        txtDiscount = new JTextField("0");
        txtDiscount.setBounds(463, 100, 150, 30);
        panelOrderInfo.add(txtDiscount);

        JLabel lblNote = new JLabel("Ghi chú:");
        lblNote.setBounds(353, 140, 100, 30);
        panelOrderInfo.add(lblNote);
        txtNote = new JTextField();
        txtNote.setBounds(463, 140, 150, 30);
        panelOrderInfo.add(txtNote);

        JLabel lblTotalQuantity = new JLabel("Tổng số lượng:");
        lblTotalQuantity.setBounds(353, 180, 100, 30);
        panelOrderInfo.add(lblTotalQuantity);
        txtTotalQuantity = new JTextField();
        txtTotalQuantity.setBounds(463, 180, 150, 30);
        panelOrderInfo.add(txtTotalQuantity);

        // Bảng hóa đơn
        String[] columns = {"STT", "Tên món", "Giá tiền", "Số lượng", "Thành tiền"};
        modelOrder = new DefaultTableModel(columns, 0);
        tableOrder = new JTable(modelOrder);
        JScrollPane scrollOrder = new JScrollPane(tableOrder);
        scrollOrder.setBounds(20, 273, 593, 200);
        panelOrderInfo.add(scrollOrder);

        panelMain.add(panelOrderInfo);
        
        JButton btnTable = new JButton("+");
        btnTable.setBounds(290, 140, 50, 30);
        panelOrderInfo.add(btnTable);
        add(panelMain, BorderLayout.CENTER);

        // Panel chứa nút điều khiển
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDecrease = new JButton("Giảm số lượng");
        btnRemove = new JButton("Xóa món");
        btnClear = new JButton("Xóa tất cả");
        btnPrint = new JButton("In hóa đơn");
        btnSave = new JButton("Lưu");

        panelButtons.add(btnDecrease);
        panelButtons.add(btnRemove);
        panelButtons.add(btnClear);
        panelButtons.add(btnSave);
        panelButtons.add(btnPrint);
        add(panelButtons, BorderLayout.SOUTH);

        // Sự kiện
        btnAddCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Tạo một cửa sổ JDialog mới để hiển thị form khách hàng
                JDialog customerFormDialog = new JDialog((Frame) null, "Quản lý khách hàng", true); // true để cửa sổ là modal
                KhachHang customerForm = new KhachHang();
                
                customerFormDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Đóng cửa sổ khi người dùng thoát
                customerFormDialog.getContentPane().add(customerForm); // Thêm JPanel (form) vào trong JDialog
                customerFormDialog.setSize(600, 400); // Thiết lập kích thước cho cửa sổ
                customerFormDialog.setLocationRelativeTo(null); // Đặt cửa sổ vào giữa màn hình
                customerFormDialog.setVisible(true); // Hiển thị cửa sổ
            }
        });

        btnTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Tạo một cửa sổ JDialog mới để hiển thị form bàn
                JDialog tableFormDialog = new JDialog((Frame) null, "Quản lý Bàn", true); // true để cửa sổ là modal
                Ban tableForm = new Ban();
                
                tableFormDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Đóng cửa sổ khi người dùng thoát
                tableFormDialog.getContentPane().add(tableForm); // Thêm JPanel (form Bàn) vào trong JDialog
                tableFormDialog.setSize(600, 400); // Thiết lập kích thước cho cửa sổ
                tableFormDialog.setLocationRelativeTo(null); // Đặt cửa sổ vào giữa màn hình
                tableFormDialog.setVisible(true); // Hiển thị cửa sổ
            }
        });



        btnDecrease.addActionListener(e -> decreaseQuantity());
        btnRemove.addActionListener(e -> removeSelectedItem());
        btnClear.addActionListener(e -> clearAllItems());
        btnSave.addActionListener(e -> saveBill());
        btnPrint.addActionListener(e -> printBill());
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = txtSearch.getText().trim();
                if (!searchTerm.isEmpty()) {
                    searchFoods(searchTerm);
                } else {
                    // Nếu không có từ khóa tìm kiếm, có thể hiển thị tất cả món ăn
                    loadFoods(); // Hoặc gọi phương thức loadFoods() để tải lại tất cả món ăn
                }
            }
        });
        
        cbTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTable = (String) cbTable.getSelectedItem();
                if (selectedTable != null) {
                    loadTableData(selectedTable);
                }
            }
        });
        
        // Load dữ liệu
        loadComboBoxData();
        loadFoods();
    }
    
    private void loadTableData(String tableName) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT bi.billInfo_id, bi.check_in, bi.check_out, bi.total_price FROM billinfo bi JOIN tables t ON bi.table_id = t.table_id WHERE t.table_name = ? AND t.status = 'Có người' ORDER BY bi.billInfo_id DESC LIMIT 1")) {

            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Lấy thông tin hóa đơn
                int billInfoID = rs.getInt("billInfo_id");
                Timestamp checkIn = rs.getTimestamp("check_in");
                double totalPrice = rs.getDouble("total_price"); // Sử dụng double cho tổng tiền

                // Hiển thị lên giao diện
                txtBillID.setText(String.valueOf(billInfoID));
                //txtCheckIn.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(checkIn));
                txtTotal.setText(String.format("%.2f", totalPrice)); // Định dạng tổng tiền

                // Load danh sách món ăn của hóa đơn này
                loadOrderedFoods(billInfoID);

                // Vô hiệu hóa nút thêm món khi bàn có người
                //btnAddCustomer.setEnabled(false);
                //btnAddFood.setEnabled(false); // Giả sử bạn có nút thêm món
            } else {
                // Nếu bàn không có người -> Xóa thông tin cũ
                txtBillID.setText("");
                //txtCheckIn.setText("");
                txtTotal.setText("");
                clearAllItems();

                // Cho phép thêm khách và thêm món
                //btnAddCustomer.setEnabled(true);
                //btnAddFood.setEnabled(true); // Giả sử bạn có nút thêm món
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadOrderedFoods(int billInfoID) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT b.food_name, b.quantity, b.price, b.total_price FROM bill b JOIN food f ON b.food_id = f.food_id WHERE b.billInfo_id = ?")) {

            stmt.setInt(1, billInfoID);
            ResultSet rs = stmt.executeQuery();

            modelOrder.setRowCount(0); // Xóa danh sách cũ trước khi tải mới

            while (rs.next()) {
                String foodName = rs.getString("food_name");
                int quantity = rs.getInt("quantity");
                int price = rs.getInt("price");

                modelOrder.addRow(new Object[]{
                    modelOrder.getRowCount() + 1, foodName, price, quantity, price * quantity
                });
            }

            updateTotal(); // Cập nhật tổng tiền
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void printBill() {
        String billID = txtBillID.getText();
        String employee = cbEmployee.getSelectedItem() != null ? cbEmployee.getSelectedItem().toString() : "N/A";
        String customer = cbCustomer.getSelectedItem() != null ? cbCustomer.getSelectedItem().toString() : "N/A";
        String checkIn = txtCheckIn.getText();
        String checkOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String discount = txtDiscount.getText();
        String total = txtTotal.getText();
        String tableName = cbTable.getSelectedItem() != null ? cbTable.getSelectedItem().toString() : "Không xác định";

        if (billID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có hóa đơn để in!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
                if (pageIndex > 0) return NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());

                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("HÓA ĐƠN THANH TOÁN", 100, 20);

                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString("Mã hóa đơn: " + billID, 20, 50);
                g2d.drawString("Nhân viên: " + employee, 20, 70);
                g2d.drawString("Khách hàng: " + customer, 20, 90);
                g2d.drawString("Bàn: " + tableName, 20, 110);
                g2d.drawString("Thời gian vào: " + checkIn, 20, 130);
                g2d.drawString("Thời gian ra: " + checkOut, 20, 150);
                g2d.drawString("--------------------------------------", 20, 170);

                g2d.drawString("STT", 20, 190);
                g2d.drawString("Tên món", 50, 190);
                g2d.drawString("SL", 200, 190);
                g2d.drawString("Giá", 250, 190);

                int y = 210;
                for (int i = 0; i < modelOrder.getRowCount(); i++) {
                    g2d.drawString(String.valueOf(i + 1), 20, y);
                    g2d.drawString(modelOrder.getValueAt(i, 1).toString(), 50, y);
                    g2d.drawString(modelOrder.getValueAt(i, 3).toString(), 200, y);
                    g2d.drawString(modelOrder.getValueAt(i, 4).toString(), 250, y);
                    y += 20;
                }

                y += 20;
                g2d.drawString("--------------------------------------", 20, y);
                g2d.drawString("Giảm giá: " + discount + " %", 20, y + 20);
                g2d.drawString("Tổng tiền: " + total + " VND", 20, y + 40);
                g2d.drawString("Cảm ơn quý khách!", 100, y + 60);

                return PAGE_EXISTS;
            }
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try (Connection conn = Database.getConnection()) {
                job.print();
                updateCheckoutTime(conn, billID, checkOut);
                updateTableStatus(conn, tableName, "Trống");
                
            } catch (PrinterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi in hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật database: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTableStatus(Connection conn, String tableName, String status) throws SQLException {
        String sql = "UPDATE tables SET status = ? WHERE table_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, tableName);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Không tìm thấy bàn: " + tableName);
            }
        }
    }

    private void updateCheckoutTime(Connection conn, String billID, String checkOut) throws SQLException {
        String sql = "UPDATE billinfo SET check_out = ? WHERE billinfo_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkOut);
            stmt.setString(2, billID);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Không tìm thấy hóa đơn ID: " + billID);
            }
        }
    }



    private void searchFoods(String searchTerm) {
        SwingUtilities.invokeLater(() -> {  // Đảm bảo cập nhật UI trên EDT
            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT food_name, price, image FROM food WHERE food_name LIKE ?")) {

                // Sử dụng LIKE với ký tự đại diện
                stmt.setString(1, "%" + searchTerm + "%");

                ResultSet rs = stmt.executeQuery();

                panelFoods.removeAll(); // Xóa các món cũ trước khi tải mới
                panelFoods.setLayout(new GridLayout(0, 3, 10, 10)); // 3 món trên hàng, khoảng cách nhỏ

                // Kiểm tra nếu không có món nào
                if (!rs.isBeforeFirst()) {
                    JLabel lblNoFood = new JLabel("Không có món ăn nào", SwingConstants.CENTER);
                    lblNoFood.setFont(new Font("Arial", Font.BOLD, 16));
                    lblNoFood.setForeground(Color.RED);
                    panelFoods.add(lblNoFood);
                } else {
                    while (rs.next()) {
                        String foodName = rs.getString("food_name");
                        int price = rs.getInt("price");
                        byte[] imageData = rs.getBytes("image");

                        // Xử lý hình ảnh món ăn
                        ImageIcon icon = new ImageIcon("default.jpg"); // Hình mặc định
                        if (imageData != null) {
                            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
                            if (img != null) {
                                img = resizeImage(img, 120, 120); // Đảm bảo hình vuông
                                icon = new ImageIcon(img);
                            }
                        }

                        // Tạo Panel cho mỗi món ăn
                        JPanel panelFood = new JPanel();
                        panelFood.setLayout(new BoxLayout(panelFood, BoxLayout.Y_AXIS));
                        panelFood.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Viền nhẹ giúp nổi bật
                        panelFood.setBackground(Color.WHITE); // Nền trắng giúp hình ảnh rõ hơn
                        panelFood.setAlignmentX(Component.CENTER_ALIGNMENT);

                        // Đặt kích thước bằng hình ảnh + tiêu đề
                        panelFood.setPreferredSize(new Dimension(120, 170));
                        panelFood.setMaximumSize(new Dimension(120, 170));
                        panelFood.setMinimumSize(new Dimension(120, 170));

                        // Hiển thị giá (nền đỏ, chữ trắng)
                        JLabel lblPrice = new JLabel(price + "đ", SwingConstants.CENTER);
                        lblPrice.setOpaque(true);
                        lblPrice.setBackground(Color.RED);
                        lblPrice.setForeground(Color.WHITE);
                        lblPrice.setFont(new Font("Arial", Font.BOLD, 14));
                        lblPrice.setPreferredSize(new Dimension(100, 25));
                        lblPrice.setMaximumSize(new Dimension(100, 25));
                        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

                        // Hiển thị hình ảnh món ăn (căn giữa, có viền nhẹ)
                        JLabel lblImage = new JLabel(icon);
                        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
                        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
                        lblImage.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // Viền mỏng quanh ảnh

                        // Hiển thị tên món (nền xanh, chữ trắng)
                        JLabel lblName = new JLabel(foodName, SwingConstants.CENTER);
                        lblName.setOpaque(true);
                        lblName.setBackground(Color.BLUE);
                        lblName.setForeground(Color.WHITE);
                        lblName.setFont(new Font("Arial", Font.BOLD, 12));
                        lblName.setPreferredSize(new Dimension(100, 25));
                        lblName.setMaximumSize(new Dimension(100, 25));
                        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

                        // Thêm các thành phần vào panel món ăn
                        panelFood.add(lblPrice);
                        panelFood.add(Box.createVerticalStrut(5)); // Khoảng cách nhỏ
                        panelFood.add(lblImage);
                        panelFood.add(Box.createVerticalStrut(5)); // Khoảng cách nhỏ
                        panelFood.add(lblName);

                        // Gán sự kiện click vào món ăn
                        panelFood.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                addFoodToOrder(foodName, price);
                            }
                        });

                        // Thêm vào danh sách món
                        panelFoods.add(panelFood);
                    }
                }

                panelFoods.revalidate();
                panelFoods.repaint();

            } catch (SQLException | IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu món ăn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void decreaseQuantity() {
        int selectedRow = tableOrder.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món để giảm số lượng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantity = (int) modelOrder.getValueAt(selectedRow, 3);
        if (quantity > 1) {
            int response = JOptionPane.showConfirmDialog(this, 
                "Bạn chắc chắn muốn giảm số lượng món này?", 
                "Xác nhận giảm số lượng", 
                JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                modelOrder.setValueAt(quantity - 1, selectedRow, 3);
                updateRowTotal(selectedRow);
                updateTotal(); // Cập nhật tổng tiền và tổng số lượng
            }
        } else {
            int response = JOptionPane.showConfirmDialog(this, 
                "Số lượng món này đã là 1. Bạn có muốn xóa món không?", 
                "Xác nhận xóa món", 
                JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                modelOrder.removeRow(selectedRow);
                updateSTT(); // Cập nhật lại STT sau khi xóa
                updateTotal(); // Cập nhật tổng tiền và tổng số lượng
            }
        }
    }


    private void removeSelectedItem() {
        int selectedRow = tableOrder.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int response = JOptionPane.showConfirmDialog(this, 
            "Bạn chắc chắn muốn xóa món này khỏi đơn hàng?", 
            "Xác nhận xóa món", 
            JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            modelOrder.removeRow(selectedRow);
            updateSTT(); // Cập nhật STT
            updateTotal(); // Cập nhật tổng số lượng và tổng tiền
        }
    }


    private void clearAllItems() {
//    	int selectedRow = tableOrder.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "Không có món để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
        modelOrder.setRowCount(0);
        updateTotal(); // Cập nhật tổng số lượng và tổng tiền
    }



    private void updateSTT() {
        for (int i = 0; i < modelOrder.getRowCount(); i++) {
            modelOrder.setValueAt(i + 1, i, 0); // Cột STT luôn là số thứ tự bắt đầu từ 1
        }
    }

    private void updateRowTotal(int row) {
        int price = (int) modelOrder.getValueAt(row, 2);
        int quantity = (int) modelOrder.getValueAt(row, 3);
        modelOrder.setValueAt(price * quantity, row, 4);
    }
    
    private void loadComboBoxData() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            if (conn == null) return;

            ResultSet rs;

            // Load danh sách bàn
            rs = stmt.executeQuery("SELECT table_id, table_name FROM tables");
            cbTable.removeAllItems();
            while (rs.next()) {
                cbTable.addItem(rs.getString("table_name"));
            }
            rs.close();

            // Load danh sách nhân viên
            rs = stmt.executeQuery("SELECT employee_id, employee_name FROM employees");
            cbEmployee.removeAllItems();
            while (rs.next()) {
                cbEmployee.addItem(rs.getString("employee_name"));
            }
            rs.close();

            // Load danh sách khách hàng
            rs = stmt.executeQuery("SELECT customer_id, customer_name FROM customers");
            cbCustomer.removeAllItems();
            while (rs.next()) {
                cbCustomer.addItem(rs.getString("customer_name"));
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFoods() {
        SwingUtilities.invokeLater(() -> {  // Đảm bảo cập nhật UI trên EDT
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT food_name, price, image FROM food")) {

                panelFoods.removeAll(); // Xóa các món cũ trước khi tải mới
                panelFoods.setLayout(new GridLayout(0, 3, 10, 10)); // 3 món trên hàng, khoảng cách nhỏ

                // Kiểm tra nếu không có món nào
                if (!rs.isBeforeFirst()) {
                    JLabel lblNoFood = new JLabel("Không có món ăn nào", SwingConstants.CENTER);
                    lblNoFood.setFont(new Font("Arial", Font.BOLD, 16));
                    lblNoFood.setForeground(Color.RED);
                    panelFoods.add(lblNoFood);
                } else {
                    while (rs.next()) {
                        String foodName = rs.getString("food_name");
                        int price = rs.getInt("price");
                        byte[] imageData = rs.getBytes("image");

                        // Xử lý hình ảnh món ăn
                        ImageIcon icon = new ImageIcon("default.jpg"); // Hình mặc định
                        if (imageData != null) {
                            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
                            if (img != null) {
                                img = resizeImage(img, 120, 120); // Đảm bảo hình vuông
                                icon = new ImageIcon(img);
                            }
                        }

                        // Tạo Panel cho mỗi món ăn
                        JPanel panelFood = new JPanel();
                        panelFood.setLayout(new BoxLayout(panelFood, BoxLayout.Y_AXIS));
                        panelFood.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Viền nhẹ giúp nổi bật
                        panelFood.setBackground(Color.WHITE); // Nền trắng giúp hình ảnh rõ hơn
                        panelFood.setAlignmentX(Component.CENTER_ALIGNMENT);

                        // Đặt kích thước bằng hình ảnh + tiêu đề
                        panelFood.setPreferredSize(new Dimension(120, 170));
                        panelFood.setMaximumSize(new Dimension(120, 170));
                        panelFood.setMinimumSize(new Dimension(120, 170));

                        // Hiển thị giá (nền đỏ, chữ trắng)
                        JLabel lblPrice = new JLabel(price + "đ", SwingConstants.CENTER);
                        lblPrice.setOpaque(true);
                        lblPrice.setBackground(Color.RED);
                        lblPrice.setForeground(Color.WHITE);
                        lblPrice.setFont(new Font("Arial", Font.BOLD, 14));
                        lblPrice.setPreferredSize(new Dimension(100, 25));
                        lblPrice.setMaximumSize(new Dimension(100, 25));
                        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

                        // Hiển thị hình ảnh món ăn (căn giữa, có viền nhẹ)
                        JLabel lblImage = new JLabel(icon);
                        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
                        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
                        lblImage.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // Viền mỏng quanh ảnh

                        // Hiển thị tên món (nền xanh, chữ trắng)
                        JLabel lblName = new JLabel(foodName, SwingConstants.CENTER);
                        lblName.setOpaque(true);
                        lblName.setBackground(Color.BLUE);
                        lblName.setForeground(Color.WHITE);
                        lblName.setFont(new Font("Arial", Font.BOLD, 12));
                        lblName.setPreferredSize(new Dimension(100, 25));
                        lblName.setMaximumSize(new Dimension(100, 25));
                        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

                        // Thêm các thành phần vào panel món ăn
                        panelFood.add(lblPrice);
                        panelFood.add(Box.createVerticalStrut(5)); // Khoảng cách nhỏ
                        panelFood.add(lblImage);
                        panelFood.add(Box.createVerticalStrut(5)); // Khoảng cách nhỏ
                        panelFood.add(lblName);

                        // Gán sự kiện click vào món ăn
                        panelFood.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                addFoodToOrder(foodName, price);
                            }
                        });

                        // Thêm vào danh sách món
                        panelFoods.add(panelFood);
                    }
                }

                panelFoods.revalidate();
                panelFoods.repaint();

            } catch (SQLException | IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu món ăn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Thêm món ăn vào bảng
    private void addFoodToOrder(String foodName, int price) {
        // Kiểm tra nếu chưa chọn bàn
        if (cbTable.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước khi thêm món!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra trạng thái bàn
        String selectedTable = cbTable.getSelectedItem().toString();
        if (isTableOccupied(selectedTable)) {
            JOptionPane.showMessageDialog(this, "Bàn này đã có người, vui lòng chọn bàn khác!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exists = false;
        for (int i = 0; i < modelOrder.getRowCount(); i++) {
            if (modelOrder.getValueAt(i, 1).equals(foodName)) {
                int quantity = Integer.parseInt(modelOrder.getValueAt(i, 3).toString());
                modelOrder.setValueAt(quantity + 1, i, 3); // Tăng số lượng
                updateRowTotal(i);
                exists = true;
                break;
            }
        }

        if (!exists) {
            modelOrder.addRow(new Object[]{modelOrder.getRowCount() + 1, foodName, price, 1, price});
        }

        updateTotal(); // Cập nhật tổng số lượng và tổng tiền
    }

    // Kiểm tra trạng thái bàn
    private boolean isTableOccupied(String tableName) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT status FROM tables WHERE table_name = ?")) {

            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                return "Có người".equals(status); // Trả về true nếu bàn có người
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Nếu không tìm thấy hoặc lỗi, trả về false
    }


    private void updateTableStatus(String tableName, String status) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE tables SET status = ? WHERE table_name = ?")) {
            stmt.setString(1, status);
            stmt.setString(2, tableName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật tổng số lượng và tổng tiền
    private void updateTotal() {
        int totalQuantity = 0;
        double total = 0.0;

        for (int i = 0; i < modelOrder.getRowCount(); i++) {
            int quantity = Integer.parseInt(modelOrder.getValueAt(i, 3).toString()); // Lấy số lượng
            double thanhTien = Double.parseDouble(modelOrder.getValueAt(i, 4).toString()); // Lấy thành tiền

            totalQuantity += quantity; // Cộng dồn số lượng
            total += thanhTien; // Cộng dồn thành tiền
        }

        txtTotalQuantity.setText(String.valueOf(totalQuantity)); // Hiển thị tổng số lượng
        txtTotal.setText(String.format("%.2f", total)); // Hiển thị tổng tiền
    }

    // Phương thức resizeImage
    public BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        Image tmp = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose(); // Giải phóng tài nguyên
        return resizedImage;
    }

    private void saveBill() {
        System.out.println("Bắt đầu lưu hóa đơn...");

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            conn.setAutoCommit(false); // Bắt đầu transaction
            System.out.println("Kết nối CSDL thành công!");

            // Lấy dữ liệu từ giao diện
            String employeeName = (String) cbEmployee.getSelectedItem();
            String customerName = (String) cbCustomer.getSelectedItem();
            String tableName = (String) cbTable.getSelectedItem();
            String checkIn = txtCheckIn.getText();
            String checkOut = txtCheckOut.getText();
            String note = txtNote.getText();

            // Kiểm tra đầu vào hợp lệ
            int totalQuantity;
            double discount;
            try {
                totalQuantity = Integer.parseInt(txtTotalQuantity.getText().trim());
                discount = Double.parseDouble(txtDiscount.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Lỗi định dạng số! Kiểm tra lại dữ liệu nhập vào.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy ID từ tên
            int employeeID = getIDByName(conn, "employees", "employee_id", "employee_name", employeeName);
            int customerID = getIDByName(conn, "customers", "customer_id", "customer_name", customerName);
            int tableID = getIDByName(conn, "tables", "table_id", "table_name", tableName);

            if (employeeID == -1 || customerID == -1 || tableID == -1) {
                JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy dữ liệu từ danh sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Lấy ID thành công: EmployeeID=" + employeeID + ", CustomerID=" + customerID + ", TableID=" + tableID);

            // Kiểm tra nếu không có món ăn nào trong hóa đơn
            if (modelOrder.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có món ăn nào trong hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tính tổng tiền
            double total = 0;
            for (int i = 0; i < modelOrder.getRowCount(); i++) {
                double thanhTien = Double.parseDouble(modelOrder.getValueAt(i, 4).toString());
                total += thanhTien;
            }
            total = total - (total * discount / 100);
            txtTotal.setText(String.format("%.2f", total));

            System.out.println("Tổng tiền: " + total);

            // Lưu vào bảng billinfo
            String insertBillInfoSQL = "INSERT INTO billinfo (customer_id, table_id, employee_id, check_in, check_out, total_quantity, total_price, discount) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            int billInfoID = -1;

            try (PreparedStatement stmtBillInfo = conn.prepareStatement(insertBillInfoSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmtBillInfo.setInt(1, customerID);
                stmtBillInfo.setInt(2, tableID);
                stmtBillInfo.setInt(3, employeeID);
                stmtBillInfo.setString(4, checkIn);
                stmtBillInfo.setString(5, checkOut);
                stmtBillInfo.setInt(6, totalQuantity);
                stmtBillInfo.setDouble(7, total);
                stmtBillInfo.setDouble(8, discount);

                int affectedRows = stmtBillInfo.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Không thể lưu thông tin hóa đơn!");
                }

                try (ResultSet generatedKeys = stmtBillInfo.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        billInfoID = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Không thể lấy billInfo ID!");
                    }
                }
            }

            System.out.println("Lưu billinfo thành công, ID: " + billInfoID);

            // Lưu từng món vào bảng bill
            String insertBillSQL = "INSERT INTO bill (billInfo_id , food_id, food_name, quantity, price, total_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmtBill = conn.prepareStatement(insertBillSQL)) {
                for (int i = 0; i < modelOrder.getRowCount(); i++) {
                    String foodName = (String) modelOrder.getValueAt(i, 1);
                    int price = Integer.parseInt(modelOrder.getValueAt(i, 2).toString());
                    int quantity = Integer.parseInt(modelOrder.getValueAt(i, 3).toString());
                    double thanhTien = Double.parseDouble(modelOrder.getValueAt(i, 4).toString());

                    int foodID = getIDByName(conn, "food", "food_id", "food_name", foodName);
                    if (foodID == -1) {
                        System.err.println("Không tìm thấy ID của món: " + foodName);
                        continue;
                    }

                    stmtBill.setInt(1, billInfoID);      // billInfo_id
                    stmtBill.setInt(2, foodID);          // food_id
                    stmtBill.setString(3, foodName);     // food_name
                    stmtBill.setInt(4, quantity);        // quantity
                    stmtBill.setInt(5, price);           // price
                    stmtBill.setDouble(6, thanhTien);    // total_price
                    stmtBill.setString(7, "Đã thanh toán"); // status
                    stmtBill.addBatch();
                }
                stmtBill.executeBatch();
            }

            // Cập nhật trạng thái bàn thành "Có người"
            String updateTableStatusSQL = "UPDATE tables SET status = 'Có người' WHERE table_id = ?";
            try (PreparedStatement stmtUpdateTable = conn.prepareStatement(updateTableStatusSQL)) {
                stmtUpdateTable.setInt(1, tableID);
                stmtUpdateTable.executeUpdate();
            }

            // Commit giao dịch
            conn.commit();

            JOptionPane.showMessageDialog(this, "Lưu hóa đơn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            txtBillID.setText(String.valueOf(billInfoID)); // Cập nhật mã hóa đơn vào txtBillID

            // Xóa dữ liệu sau khi lưu
            // clearAllItems();
            // txtDiscount.setText("0");
            // txtTotal.setText("0.00");
            // txtTotalQuantity.setText("0");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            try (Connection conn = Database.getConnection()) {
                if (conn != null) {
                    conn.rollback(); // Quay lại trạng thái trước khi xảy ra lỗi
                    System.out.println("Đã rollback giao dịch.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    /**
     * Lấy ID từ tên
     */
    private int getIDByName(Connection conn, String tableName, String idColumn, String nameColumn, String name) throws SQLException {
        String sql = "SELECT " + idColumn + " FROM " + tableName + " WHERE " + nameColumn + " = ? LIMIT 1"; // Thêm `LIMIT 1` để tối ưu truy vấn
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                System.err.println("Không tìm thấy " + name + " trong bảng " + tableName);
                return -1; // Trả về -1 thay vì ném ngoại lệ
            }
        }
    }

}