package ec.edu.pinza.cliesc.views;

import ec.edu.pinza.cliesc.models.CreditoDTO;
import ec.edu.pinza.cliesc.services.BanquitoRestClient;
import ec.edu.pinza.cliesc.utils.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConsultaCreditoFrame extends JFrame {

    private final BanquitoRestClient banquitoClient;
    private JTextField txtCedula;
    private JTextArea txtResultado;

    public ConsultaCreditoFrame() {
        this.banquitoClient = new BanquitoRestClient();
        initComponents();
    }

    private void initComponents() {
        setTitle("COMERCIALIZADORA MONSTER - Consulta de Crédito");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.COLOR_BACKGROUND);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblTitulo = new JLabel("Consulta de Crédito BanQuito");
        lblTitulo.setFont(UIConstants.FONT_TITLE);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblTitulo);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel cedulaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        cedulaPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        JLabel lblCedula = new JLabel("Cédula del Cliente:");
        lblCedula.setFont(UIConstants.FONT_SUBTITLE);
        lblCedula.setForeground(Color.WHITE);
        cedulaPanel.add(lblCedula);
        txtCedula = new JTextField(15);
        txtCedula.setFont(UIConstants.FONT_NORMAL);
        txtCedula.setBackground(Color.WHITE);
        txtCedula.setForeground(UIConstants.COLOR_TEXT_DARK);
        cedulaPanel.add(txtCedula);
        contentPanel.add(cedulaPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        botonesPanel.setBackground(UIConstants.COLOR_BACKGROUND);

        JButton btnValidar = new JButton("Validar Sujeto de Crédito");
        btnValidar.setFont(UIConstants.FONT_BUTTON);
        btnValidar.setBackground(UIConstants.COLOR_PRIMARY_START);
        btnValidar.setForeground(Color.WHITE);
        btnValidar.setFocusPainted(false);
        btnValidar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnValidar.addActionListener(e -> validarSujetoCredito());
        botonesPanel.add(btnValidar);

        JButton btnMontoMaximo = new JButton("Consultar Monto Máximo");
        btnMontoMaximo.setFont(UIConstants.FONT_BUTTON);
        btnMontoMaximo.setBackground(UIConstants.COLOR_SUCCESS);
        btnMontoMaximo.setForeground(Color.WHITE);
        btnMontoMaximo.setFocusPainted(false);
        btnMontoMaximo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMontoMaximo.addActionListener(e -> consultarMontoMaximo());
        botonesPanel.add(btnMontoMaximo);

        contentPanel.add(botonesPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblResultado = new JLabel("Resultado:");
        lblResultado.setFont(UIConstants.FONT_SUBTITLE);
        lblResultado.setForeground(Color.WHITE);
        lblResultado.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblResultado);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        txtResultado = new JTextArea(12, 50);
        txtResultado.setFont(UIConstants.FONT_NORMAL);
        txtResultado.setEditable(false);
        txtResultado.setBackground(UIConstants.COLOR_CARD);
        txtResultado.setForeground(UIConstants.COLOR_TEXT_DARK);
        txtResultado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(txtResultado);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(UIConstants.FONT_BUTTON);
        btnCerrar.setBackground(UIConstants.COLOR_ERROR);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCerrar.addActionListener(e -> dispose());
        contentPanel.add(btnCerrar);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void validarSujetoCredito() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese una cédula",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CreditoDTO response = banquitoClient.validarSujetoCredito(cedula);
            StringBuilder resultado = new StringBuilder();
            resultado.append("=== VALIDACIÓN DE SUJETO DE CRÉDITO ===\n\n");
            resultado.append("Cédula: ").append(cedula).append("\n");
            
            Boolean esSujeto = response.isEsSujetoCredito();
            if (esSujeto == null) esSujeto = false;
            
            resultado.append("Es sujeto de crédito: ").append(esSujeto ? "SÍ" : "NO").append("\n");
            resultado.append("Mensaje: ").append(response.getMotivo() != null ? response.getMotivo() : "Sin información").append("\n");
            txtResultado.setText(resultado.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al validar sujeto de crédito:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultarMontoMaximo() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese una cédula",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CreditoDTO response = banquitoClient.obtenerMontoMaximo(cedula);
            StringBuilder resultado = new StringBuilder();
            resultado.append("=== CONSULTA DE MONTO MÁXIMO ===\n\n");
            resultado.append("Cédula: ").append(cedula).append("\n");
            resultado.append("Monto máximo: $").append(response.getMontoMaximo()).append("\n");
            resultado.append("Mensaje: ").append(response.getMensaje()).append("\n");
            txtResultado.setText(resultado.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al consultar monto máximo:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
