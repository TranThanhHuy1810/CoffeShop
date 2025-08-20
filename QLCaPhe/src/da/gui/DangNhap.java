package da.gui;

import da.conn.Database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DangNhap extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnExit;
    private JLabel lblShowPass, lblHidePass;

    public DangNhap() {
        setTitle("Đăng nhập");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 🎨 Panel chính
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 🔥 Tiêu đề
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;  
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lblTitle, gbc);

        // 🧑‍💻 Tên đăng nhập
        gbc.gridy = 1; 
        gbc.gridx = 0;
        gbc.gridwidth = 1; 
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);

        txtUser = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtUser, gbc);

        // 🔑 Mật khẩu
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Mật khẩu:"), gbc);

        txtPass = new JPasswordField(20);

        // 👀 Hiển thị / Ẩn mật khẩu
        lblShowPass = createResizedLabel("src/images/show.jpg", 24, 24);
        lblHidePass = createResizedLabel("src/images/hide.jpg", 24, 24);
        lblShowPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblHidePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblHidePass.setVisible(false);

        // 📌 Panel chứa mật khẩu + icon
        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.add(txtPass, BorderLayout.CENTER);
        passPanel.add(lblShowPass, BorderLayout.EAST);

        gbc.gridx = 1;
        panel.add(passPanel, gbc);

        // 🎭 Sự kiện ẩn/hiện mật khẩu
        lblShowPass.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                txtPass.setEchoChar((char) 0);
                lblShowPass.setVisible(false);
                passPanel.add(lblHidePass, BorderLayout.EAST);
                lblHidePass.setVisible(true);
            }
        });

        lblHidePass.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                txtPass.setEchoChar('•');
                lblHidePass.setVisible(false);
                passPanel.add(lblShowPass, BorderLayout.EAST);
                lblShowPass.setVisible(true);
            }
        });

        // ✅ Nút đăng nhập
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel(""), gbc); // Thêm khoảng trống

        gbc.gridx = 0;
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(Color.GREEN);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(btnLogin, gbc);

        // ❌ Nút thoát
        gbc.gridx = 1;
        btnExit = new JButton("Thoát");
        btnExit.setBackground(Color.RED);
        btnExit.setForeground(Color.WHITE);
        btnExit.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(btnExit, gbc);

        add(panel);

        // 🎯 Xử lý sự kiện
        btnLogin.addActionListener(e -> loginAction());
        btnExit.addActionListener(e -> System.exit(0));

        // 🔥 Nhấn Enter để đăng nhập
        txtPass.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginAction();
                }
            }
        });
    }

    // 📌 Hàm thay đổi kích thước icon và tạo JLabel
    private JLabel createResizedLabel(String imagePath, int width, int height) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(img));
    }

    // 🔑 Xử lý đăng nhập
    private void loginAction() {
        String username = txtUser .getText().trim();
        String password = new String(txtPass.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT * FROM accounts WHERE account_name = ? AND password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int typeId = rs.getInt("type_id"); // Lấy giá trị type_id từ kết quả truy vấn

                this.dispose(); // Đóng form đăng nhập ngay lập tức

                // Kiểm tra giá trị type_id để quyết định form nào sẽ mở
                if (typeId == 1) {
                    SwingUtilities.invokeLater(() -> new TrangChu(username).setVisible(true)); // Mở form Trang Chủ
                } else {
                    // Mở form bán hàng
                    SwingUtilities.invokeLater(() -> {
                        JFrame frame = new JFrame("Bán Hàng");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.setSize(800, 600);  // Kích thước cửa sổ
                        frame.add(new BanHang());  // Thêm JPanel BanHang vào JFrame
                        frame.setLocationRelativeTo(null);  // Đặt cửa sổ vào giữa màn hình
                        frame.setVisible(true);  // Hiển thị cửa sổ
                    });
                }

            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DangNhap().setVisible(true));
    }
}
