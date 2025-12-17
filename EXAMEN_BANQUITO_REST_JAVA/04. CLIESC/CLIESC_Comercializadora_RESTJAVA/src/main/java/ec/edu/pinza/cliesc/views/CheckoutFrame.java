package ec.edu.pinza.cliesc.views;

import ec.edu.pinza.cliesc.controllers.CheckoutController;
import ec.edu.pinza.cliesc.managers.SessionManager;
import ec.edu.pinza.cliesc.models.ItemCarrito;
import ec.edu.pinza.cliesc.utils.FormatUtils;
import ec.edu.pinza.cliesc.utils.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Vista de Checkout centrada, sin scroll global y con captura de cédula.
 */
public class CheckoutFrame extends JFrame {

    private final CheckoutController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JRadioButton rbEfectivo;
    private JRadioButton rbCredito;
    private JSpinner spnCuotas;
    private JTextField txtCedula;
    private JLabel lblSubtotal;
    private JLabel lblDescuento;
    private JLabel lblTotal;
    private JPanel panelDescuento;
    private JPanel panelCuotas;

    public CheckoutFrame() {
        this.controller = new CheckoutController(this);
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("COMERCIALIZADORA MONSTER - Checkout");
        setSize(UIConstants.WINDOW_WIDTH_LARGE, UIConstants.WINDOW_HEIGHT_LARGE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.COLOR_BACKGROUND);

        mainPanel.add(createNavbar(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        centerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Finalizar Compra");
        lblTitulo.setFont(UIConstants.FONT_TITLE);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblTitulo);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Cedula
        JPanel cedulaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        cedulaPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        JLabel lblCedula = new JLabel("Cédula del Cliente:");
        lblCedula.setFont(UIConstants.FONT_SUBTITLE);
        lblCedula.setForeground(Color.WHITE);
        cedulaPanel.add(lblCedula);
        txtCedula = new JTextField(12);
        txtCedula.setFont(UIConstants.FONT_NORMAL);
        
        // Configurar cédula según el rol
        SessionManager session = SessionManager.getInstance();
        if (session.getCedula() != null) {
            txtCedula.setText(session.getCedula());
        }
        
        // Si NO es admin, la cédula es de solo lectura (solo puede comprar para sí mismo)
        if (!session.isAdmin()) {
            txtCedula.setEditable(false);
            txtCedula.setBackground(new Color(240, 240, 240));
            txtCedula.setToolTipText("Solo puedes comprar para tu propia cédula");
        } else {
            txtCedula.setToolTipText("Como administrador, puedes vender a cualquier cédula");
        }
        
        cedulaPanel.add(txtCedula);
        centerPanel.add(cedulaPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Tabla productos
        JPanel tablaPanel = new JPanel(new BorderLayout());
        tablaPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        tablaPanel.setPreferredSize(new Dimension(900, 200));
        String[] columnas = {"Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(UIConstants.FONT_NORMAL);
        table.setRowHeight(30);
        table.setBackground(UIConstants.COLOR_CARD);
        table.setForeground(UIConstants.COLOR_TEXT_DARK);
        table.setGridColor(UIConstants.COLOR_CARD_HOVER);
        JTableHeader header = table.getTableHeader();
        header.setFont(UIConstants.FONT_BUTTON);
        header.setBackground(UIConstants.COLOR_PRIMARY_START);
        header.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1));
        tablaPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tablaPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Metodo de pago
        JPanel pagoPanel = new JPanel();
        pagoPanel.setLayout(new BoxLayout(pagoPanel, BoxLayout.Y_AXIS));
        pagoPanel.setBackground(UIConstants.COLOR_CARD);
        pagoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        pagoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMetodoPago = new JLabel("Método de Pago");
        lblMetodoPago.setFont(UIConstants.FONT_SUBTITLE);
        lblMetodoPago.setForeground(UIConstants.COLOR_TEXT_DARK);
        lblMetodoPago.setAlignmentX(Component.CENTER_ALIGNMENT);
        pagoPanel.add(lblMetodoPago);
        pagoPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        ButtonGroup bgMetodoPago = new ButtonGroup();
        JPanel opcionesPago = new JPanel();
        opcionesPago.setLayout(new BoxLayout(opcionesPago, BoxLayout.Y_AXIS));
        opcionesPago.setBackground(UIConstants.COLOR_CARD);
        opcionesPago.setAlignmentX(Component.CENTER_ALIGNMENT);

        rbEfectivo = new JRadioButton("Efectivo (33% de descuento)");
        rbEfectivo.setFont(UIConstants.FONT_NORMAL);
        rbEfectivo.setForeground(UIConstants.COLOR_TEXT_DARK);
        rbEfectivo.setBackground(UIConstants.COLOR_CARD);
        rbEfectivo.setSelected(true);
        rbEfectivo.addActionListener(e -> controller.cambiarMetodoPago());
        bgMetodoPago.add(rbEfectivo);
        opcionesPago.add(rbEfectivo);

        rbCredito = new JRadioButton("Crédito Directo (BanQuito)");
        rbCredito.setFont(UIConstants.FONT_NORMAL);
        rbCredito.setForeground(UIConstants.COLOR_TEXT_DARK);
        rbCredito.setBackground(UIConstants.COLOR_CARD);
        rbCredito.addActionListener(e -> controller.cambiarMetodoPago());
        bgMetodoPago.add(rbCredito);
        opcionesPago.add(rbCredito);

        pagoPanel.add(opcionesPago);
        pagoPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        panelDescuento = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelDescuento.setBackground(UIConstants.COLOR_CARD);
        JLabel lblDesc = new JLabel("Descuento 33%:");
        lblDesc.setFont(UIConstants.FONT_NORMAL);
        lblDesc.setForeground(UIConstants.COLOR_TEXT_SECONDARY);
        panelDescuento.add(lblDesc);
        lblDescuento = new JLabel("$0.00");
        lblDescuento.setFont(UIConstants.FONT_NORMAL);
        lblDescuento.setForeground(UIConstants.COLOR_SUCCESS);
        panelDescuento.add(lblDescuento);
        pagoPanel.add(panelDescuento);

        panelCuotas = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelCuotas.setBackground(UIConstants.COLOR_CARD);
        JLabel lblCuotas = new JLabel("Número de cuotas (3 - 24):");
        lblCuotas.setFont(UIConstants.FONT_NORMAL);
        lblCuotas.setForeground(UIConstants.COLOR_TEXT_SECONDARY);
        panelCuotas.add(lblCuotas);
        SpinnerNumberModel cuotasModel = new SpinnerNumberModel(3, 3, 24, 1);
        spnCuotas = new JSpinner(cuotasModel);
        spnCuotas.setPreferredSize(new Dimension(60, 28));
        panelCuotas.add(spnCuotas);
        panelCuotas.setVisible(false);
        pagoPanel.add(panelCuotas);

        centerPanel.add(pagoPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Resumen
        JPanel resumenPanel = new JPanel();
        resumenPanel.setLayout(new BoxLayout(resumenPanel, BoxLayout.Y_AXIS));
        resumenPanel.setBackground(UIConstants.COLOR_CARD);
        resumenPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));
        resumenPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumenPanel.setPreferredSize(new Dimension(900, 110));

        lblSubtotal = new JLabel("Subtotal: $0.00");
        lblSubtotal.setFont(UIConstants.FONT_NORMAL);
        lblSubtotal.setForeground(UIConstants.COLOR_TEXT_DARK);
        lblSubtotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumenPanel.add(lblSubtotal);

        lblTotal = new JLabel("Total a pagar: $0.00");
        lblTotal.setFont(UIConstants.FONT_TITLE);
        lblTotal.setForeground(UIConstants.COLOR_PRIMARY_START);
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumenPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        resumenPanel.add(lblTotal);

        centerPanel.add(resumenPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        botonesPanel.setBackground(UIConstants.COLOR_BACKGROUND);

        JButton btnVolver = new JButton("Volver al carrito");
        btnVolver.setFont(UIConstants.FONT_BUTTON);
        btnVolver.setBackground(UIConstants.COLOR_TEXT_SECONDARY);
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> dispose());
        botonesPanel.add(btnVolver);

        JButton btnConfirmar = new JButton("Confirmar compra");
        btnConfirmar.setFont(UIConstants.FONT_BUTTON);
        btnConfirmar.setBackground(UIConstants.COLOR_SUCCESS);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmar.addActionListener(e -> controller.confirmarCompra());
        botonesPanel.add(btnConfirmar);

        centerPanel.add(botonesPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createNavbar() {
        JPanel navbar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.COLOR_PRIMARY_START,
                        getWidth(), 0, UIConstants.COLOR_PRIMARY_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        navbar.setPreferredSize(new Dimension(getWidth(), 70));
        navbar.setLayout(new BorderLayout());
        navbar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("COMERCIALIZADORA MONSTER");
        lblTitulo.setFont(UIConstants.FONT_TITLE);
        lblTitulo.setForeground(Color.WHITE);
        navbar.add(lblTitulo, BorderLayout.WEST);

        return navbar;
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<ItemCarrito> carrito = SessionManager.getInstance().getCarrito();
        for (ItemCarrito item : carrito) {
            tableModel.addRow(new Object[]{
                    item.getNombre(),
                    item.getCantidad(),
                    FormatUtils.formatCurrency(item.getPrecio()),
                    FormatUtils.formatCurrency(item.getSubtotal())
            });
        }
        actualizarTotales();
    }

    public void actualizarTotales() {
        BigDecimal subtotal = SessionManager.getInstance().getSubtotalCarrito();
        lblSubtotal.setText("Subtotal: " + FormatUtils.formatCurrency(subtotal));

        if (rbEfectivo.isSelected()) {
            panelDescuento.setVisible(true);
            panelCuotas.setVisible(false);
            BigDecimal descuento = FormatUtils.calcularDescuento33(subtotal);
            BigDecimal total = FormatUtils.aplicarDescuento33(subtotal);
            lblDescuento.setText(FormatUtils.formatCurrency(descuento));
            lblTotal.setText("Total a pagar: " + FormatUtils.formatCurrency(total));
        } else {
            panelDescuento.setVisible(false);
            panelCuotas.setVisible(true);
            lblTotal.setText("Total a pagar: " + FormatUtils.formatCurrency(subtotal));
        }
    }

    public boolean isEfectivo() {
        return rbEfectivo.isSelected();
    }

    public int getCuotas() {
        return (Integer) spnCuotas.getValue();
    }

    public String getCedula() {
        return txtCedula.getText().trim();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}
