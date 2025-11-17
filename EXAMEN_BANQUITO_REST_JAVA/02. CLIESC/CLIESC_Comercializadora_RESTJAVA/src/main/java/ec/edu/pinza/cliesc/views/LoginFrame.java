package ec.edu.pinza.cliesc.views;

import ec.edu.pinza.cliesc.controllers.LoginController;
import ec.edu.pinza.cliesc.utils.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Vista de Login con diseño Monsters Inc.
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JLabel lblError;
    private final LoginController controller;

    public LoginFrame() {
        this.controller = new LoginController(this);
        initComponents();
    }

    private void initComponents() {
        setTitle("COMERCIALIZADORA MONSTER - Login");
        setSize(UIConstants.WINDOW_WIDTH_SMALL, UIConstants.WINDOW_HEIGHT_SMALL + 80);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.COLOR_PRIMARY_START,
                        getWidth(), getHeight(), UIConstants.COLOR_PRIMARY_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(UIConstants.COLOR_CARD);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1),
                new EmptyBorder(25, 35, 25, 35)
        ));

        JLabel lblTitulo = new JLabel("COMERCIALIZADORA MONSTER");
        lblTitulo.setFont(UIConstants.FONT_TITLE);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(lblTitulo);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Logo
        try {
            ImageIcon icon = new ImageIcon("assets/Sullyvan.png");
            Image scaled = icon.getImage().getScaledInstance(121, 121, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(scaled));
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginPanel.add(lblLogo);
            loginPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        } catch (Exception ignored) {
            loginPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        JLabel lblSubtitulo = new JLabel("Iniciar Sesión");
        lblSubtitulo.setFont(UIConstants.FONT_SUBTITLE);
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(lblSubtitulo);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(UIConstants.FONT_NORMAL);
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(lblUsuario);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        txtUsuario = new JTextField();
        txtUsuario.setFont(UIConstants.FONT_NORMAL);
        txtUsuario.setMaximumSize(new Dimension(300, 35));
        txtUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtUsuario.setBackground(UIConstants.COLOR_BACKGROUND);
        txtUsuario.setForeground(Color.WHITE);
        txtUsuario.setCaretColor(Color.WHITE);
        txtUsuario.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER));
        loginPanel.add(txtUsuario);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(UIConstants.FONT_NORMAL);
        lblContrasena.setForeground(Color.WHITE);
        lblContrasena.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(lblContrasena);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        txtContrasena = new JPasswordField();
        txtContrasena.setFont(UIConstants.FONT_NORMAL);
        txtContrasena.setMaximumSize(new Dimension(300, 35));
        txtContrasena.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtContrasena.setBackground(UIConstants.COLOR_BACKGROUND);
        txtContrasena.setForeground(Color.WHITE);
        txtContrasena.setCaretColor(Color.WHITE);
        txtContrasena.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER));
        loginPanel.add(txtContrasena);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        lblError = new JLabel(" ");
        lblError.setFont(UIConstants.FONT_SMALL);
        lblError.setForeground(UIConstants.COLOR_ERROR);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(lblError);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(UIConstants.FONT_BUTTON);
        btnIngresar.setBackground(UIConstants.COLOR_PRIMARY_START);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setMaximumSize(new Dimension(280, 40));
        btnIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.addActionListener(e -> controller.iniciarSesion());
        loginPanel.add(btnIngresar);

        txtContrasena.addActionListener(e -> controller.iniciarSesion());

        mainPanel.add(loginPanel);
        add(mainPanel);
    }

    public String getCorreo() {
        return txtUsuario.getText().trim();
    }

    public String getContrasena() {
        return new String(txtContrasena.getPassword());
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }

    public void limpiarError() {
        lblError.setText(" ");
    }

    public void limpiarCampos() {
        txtUsuario.setText("");
        txtContrasena.setText("");
        limpiarError();
    }
}
