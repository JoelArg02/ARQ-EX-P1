package ec.edu.pinza.cliesc.views;

import ec.edu.pinza.cliesc.controllers.CarritoController;
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
 * Vista del Carrito/Factura con diseño Monsters Inc.
 */
public class CarritoFrame extends JFrame {

    private final CarritoController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;

    public CarritoFrame() {
        this.controller = new CarritoController(this);
        initComponents();
        cargarCarrito();
    }

    private void initComponents() {
        setTitle("COMERCIALIZADORA MONSTER - Mi Factura");
        setSize(UIConstants.WINDOW_WIDTH_LARGE, UIConstants.WINDOW_HEIGHT_MEDIUM);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        mainPanel.add(createNavbar(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        centerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel tituloPanel = new JPanel();
        tituloPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        JLabel lblTitulo = new JLabel("Mi Factura");
        lblTitulo.setFont(UIConstants.FONT_TITLE);
        lblTitulo.setForeground(Color.WHITE);
        tituloPanel.add(lblTitulo);
        centerPanel.add(tituloPanel, BorderLayout.NORTH);

        String[] columnas = {"Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(UIConstants.FONT_NORMAL);
        table.setRowHeight(40);
        table.setBackground(UIConstants.COLOR_CARD);
        table.setForeground(UIConstants.COLOR_TEXT_DARK);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(UIConstants.COLOR_CARD_HOVER);
        table.setSelectionForeground(UIConstants.COLOR_TEXT_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(UIConstants.FONT_BUTTON);
        header.setBackground(UIConstants.COLOR_PRIMARY_START);
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        bottomPanel.setBorder(new EmptyBorder(0, 30, 30, 30));

        JPanel totalPanel = new JPanel() {
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
        totalPanel.setPreferredSize(new Dimension(getWidth(), 60));
        totalPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 15));

        JLabel lblTotalLabel = new JLabel("TOTAL:");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotalLabel.setForeground(Color.WHITE);
        totalPanel.add(lblTotalLabel);

        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotal.setForeground(Color.WHITE);
        totalPanel.add(lblTotal);

        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        botonesPanel.setBackground(UIConstants.COLOR_BACKGROUND);

        JButton btnEliminar = new JButton("Eliminar seleccionado");
        btnEliminar.setFont(UIConstants.FONT_BUTTON);
        btnEliminar.setBackground(UIConstants.COLOR_WARNING);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(e -> controller.eliminarItem(table.getSelectedRow()));
        botonesPanel.add(btnEliminar);

        JButton btnLimpiar = new JButton("Limpiar factura");
        btnLimpiar.setFont(UIConstants.FONT_BUTTON);
        btnLimpiar.setBackground(UIConstants.COLOR_ERROR);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(e -> controller.limpiarCarrito());
        botonesPanel.add(btnLimpiar);

        JButton btnVolver = new JButton("Seguir comprando");
        btnVolver.setFont(UIConstants.FONT_BUTTON);
        btnVolver.setBackground(UIConstants.COLOR_TEXT_SECONDARY);
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new ProductosFrame().setVisible(true));
        });
        botonesPanel.add(btnVolver);

        JButton btnCheckout = new JButton("Proceder al pago");
        btnCheckout.setFont(UIConstants.FONT_BUTTON);
        btnCheckout.setBackground(UIConstants.COLOR_SUCCESS);
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.addActionListener(e -> controller.irACheckout());
        botonesPanel.add(btnCheckout);

        bottomPanel.add(botonesPanel, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

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

    public void cargarCarrito() {
        tableModel.setRowCount(0);
        List<ItemCarrito> items = SessionManager.getInstance().getCarrito();

        for (ItemCarrito item : items) {
            tableModel.addRow(new Object[]{
                    item.getNombre(),
                    FormatUtils.formatCurrency(item.getPrecio()),
                    item.getCantidad(),
                    FormatUtils.formatCurrency(item.getSubtotal())
            });
        }

        actualizarTotal();
    }

    public void actualizarTotal() {
        BigDecimal total = SessionManager.getInstance().getSubtotalCarrito();
        lblTotal.setText(FormatUtils.formatCurrency(total));
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarConfirmacion(String mensaje, Runnable onConfirm) {
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            onConfirm.run();
        }
    }
}
