package ec.edu.pinza.cliesc.views;

import ec.edu.pinza.cliesc.controllers.VentasController;
import ec.edu.pinza.cliesc.models.FacturaResponseDTO;
import ec.edu.pinza.cliesc.utils.FormatUtils;
import ec.edu.pinza.cliesc.utils.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.math.BigDecimal;
import ec.edu.pinza.cliesc.views.ProductosFrame;

/**
 * Vista de facturas (equivalente a Mis Ventas del cliente web).
 */
public class VentasFrame extends JFrame {

    private final VentasController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<FacturaResponseDTO> facturas;

    public VentasFrame() {
        this.controller = new VentasController(this);
        initComponents();
        controller.cargarFacturas();
    }

    private void initComponents() {
        setTitle("COMERCIALIZADORA MONSTER - Mis Ventas");
        setSize(UIConstants.WINDOW_WIDTH_LARGE, UIConstants.WINDOW_HEIGHT_LARGE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.COLOR_BACKGROUND);

        mainPanel.add(createNavbar(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        centerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitulo = new JLabel("Mis Ventas");
        lblTitulo.setFont(UIConstants.FONT_TITLE);
        lblTitulo.setForeground(Color.WHITE);
        centerPanel.add(lblTitulo, BorderLayout.NORTH);

        String[] columnas = {"# Factura", "Fecha", "Total", "Forma Pago", "Estado"};
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
        table.setForeground(Color.WHITE);
        table.setGridColor(UIConstants.COLOR_CARD_HOVER);
        table.setSelectionBackground(UIConstants.COLOR_PRIMARY_START);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        controller.verDetalle(row);
                    }
                }
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setFont(UIConstants.FONT_BUTTON);
        header.setBackground(UIConstants.COLOR_PRIMARY_START);
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        bottomPanel.setBackground(UIConstants.COLOR_BACKGROUND);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(UIConstants.FONT_BUTTON);
        btnVolver.setBackground(UIConstants.COLOR_TEXT_SECONDARY);
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new ProductosFrame().setVisible(true));
        });
        bottomPanel.add(btnVolver);

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

    // ========== Bindings ==========

    public void mostrarFacturas(List<FacturaResponseDTO> facturas) {
        this.facturas = facturas;
        tableModel.setRowCount(0);

        if (facturas == null || facturas.isEmpty()) {
            tableModel.addRow(new Object[]{"-", "-", "-", "-", "-"});
            return;
        }

        for (FacturaResponseDTO factura : facturas) {
            String estado = (factura.getInfoCredito() != null && factura.getInfoCredito().getEstado() != null)
                    ? factura.getInfoCredito().getEstado()
                    : "-";
            tableModel.addRow(new Object[]{
                    factura.getIdFactura(),
                    FormatUtils.formatDate(factura.getFecha()),
                    FormatUtils.formatCurrency(factura.getTotal()),
                    factura.getFormaPago(),
                    estado
            });
        }
    }

    public void mostrarDetalleFactura(FacturaResponseDTO factura) {
        if (factura == null) {
            mostrarError("Factura no encontrada");
            return;
        }

        JDialog dialog = new JDialog(this, "Detalle de factura #" + factura.getIdFactura(), true);
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("Factura #" + factura.getIdFactura());
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UIConstants.COLOR_BACKGROUND);

        JPanel grid = new JPanel(new GridLayout(2, 2, 15, 15));
        grid.setBackground(UIConstants.COLOR_BACKGROUND);
        grid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        grid.add(createInfoBox("Fecha", FormatUtils.formatDate(factura.getFecha())));
        grid.add(createInfoBox("Cliente", factura.getNombreCliente()));
        grid.add(createInfoBox("Forma de Pago", factura.getFormaPago()));
        grid.add(createInfoBox("Total", FormatUtils.formatCurrency(factura.getTotal())));
        content.add(grid);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        BigDecimal subtotalProductos = BigDecimal.ZERO;
        if (factura.getDetalles() != null) {
            for (var det : factura.getDetalles()) {
                subtotalProductos = subtotalProductos.add(det.getSubtotal());
            }
        }
        BigDecimal descuento = "EFECTIVO".equalsIgnoreCase(factura.getFormaPago())
                ? FormatUtils.calcularDescuento33(subtotalProductos)
                : BigDecimal.ZERO;
        content.add(createResumenFinanciero(subtotalProductos, descuento, factura.getTotal(),
                factura.getFormaPago()));

        if (factura.getInfoCredito() != null) {
            content.add(Box.createRigidArea(new Dimension(0, 15)));
            content.add(createCreditoPanel(factura));
        }

        content.add(Box.createRigidArea(new Dimension(0, 15)));
        content.add(createProductosTable(factura));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(UIConstants.COLOR_BACKGROUND);
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(UIConstants.FONT_BUTTON);
        btnCerrar.setBackground(UIConstants.COLOR_TEXT_SECONDARY);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dialog.dispose());
        footer.add(btnCerrar);
        mainPanel.add(footer, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public void mostrarTablaAmortizacion(List<FacturaResponseDTO.CuotaAmortizacionDTO> amortizacion,
                                         Integer idFactura) {
        JDialog dialog = new JDialog(this, "Tabla de amortización - Factura #" + idFactura, true);
        dialog.setSize(850, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Tabla de amortización - Factura #" + idFactura);
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Cuota", "Valor Cuota", "Interés", "Capital", "Saldo"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (amortizacion != null) {
            for (FacturaResponseDTO.CuotaAmortizacionDTO cuota : amortizacion) {
                model.addRow(new Object[]{
                        cuota.getNumeroCuota(),
                        FormatUtils.formatCurrency(cuota.getValorCuota()),
                        FormatUtils.formatCurrency(cuota.getInteresPagado()),
                        FormatUtils.formatCurrency(cuota.getCapitalPagado()),
                        FormatUtils.formatCurrency(cuota.getSaldoRestante())
                });
            }
        }

        JTable tableAmort = new JTable(model);
        tableAmort.setFont(UIConstants.FONT_NORMAL);
        tableAmort.setRowHeight(32);
        tableAmort.setBackground(UIConstants.COLOR_CARD);
        tableAmort.setForeground(Color.WHITE);
        tableAmort.setGridColor(UIConstants.COLOR_CARD_HOVER);
        JTableHeader header = tableAmort.getTableHeader();
        header.setFont(UIConstants.FONT_BUTTON);
        header.setBackground(UIConstants.COLOR_PRIMARY_START);
        header.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tableAmort);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1));
        mainPanel.add(scroll, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(UIConstants.FONT_BUTTON);
        btnCerrar.setBackground(UIConstants.COLOR_TEXT_SECONDARY);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dialog.dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(UIConstants.COLOR_BACKGROUND);
        footer.add(btnCerrar);
        mainPanel.add(footer, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // Helpers
    private JPanel createInfoBox(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(UIConstants.FONT_SMALL);
        lblLabel.setForeground(UIConstants.COLOR_TEXT_SECONDARY);
        panel.add(lblLabel, BorderLayout.NORTH);

        JLabel lblValue = new JLabel(value != null ? value : "-");
        lblValue.setFont(UIConstants.FONT_SUBTITLE);
        lblValue.setForeground(Color.WHITE);
        panel.add(lblValue, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProductosTable(FacturaResponseDTO factura) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.COLOR_BACKGROUND);
        String[] columnas = {"Código", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (factura.getDetalles() != null) {
            for (FacturaResponseDTO.DetalleFacturaDTO det : factura.getDetalles()) {
                model.addRow(new Object[]{
                        det.getCodigoProducto(),
                        det.getNombreProducto(),
                        det.getCantidad(),
                        FormatUtils.formatCurrency(det.getPrecioUnitario()),
                        FormatUtils.formatCurrency(det.getSubtotal())
                });
            }
        }

        JTable tbl = new JTable(model);
        tbl.setFont(UIConstants.FONT_NORMAL);
        tbl.setRowHeight(32);
        tbl.setBackground(UIConstants.COLOR_CARD);
        tbl.setForeground(Color.WHITE);
        tbl.setGridColor(UIConstants.COLOR_CARD_HOVER);
        JTableHeader header = tbl.getTableHeader();
        header.setFont(UIConstants.FONT_BUTTON);
        header.setBackground(UIConstants.COLOR_PRIMARY_START);
        header.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createResumenFinanciero(BigDecimal subtotal, BigDecimal descuento, BigDecimal total, String formaPago) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(520, Integer.MAX_VALUE));

        JLabel lblTitle = new JLabel("Resumen financiero");
        lblTitle.setFont(UIConstants.FONT_SUBTITLE);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitle);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setBackground(UIConstants.COLOR_CARD);
        rows.setAlignmentX(Component.CENTER_ALIGNMENT);
        rows.add(createInfoRow("Subtotal productos", FormatUtils.formatCurrency(subtotal)));
        if ("EFECTIVO".equalsIgnoreCase(formaPago)) {
            rows.add(createInfoRow("Descuento 33%", "-" + FormatUtils.formatCurrency(descuento)));
        }
        rows.add(createInfoRow("Total", FormatUtils.formatCurrency(total)));
        panel.add(rows);

        return panel;
    }

    private JPanel createCreditoPanel(FacturaResponseDTO factura) {
    FacturaResponseDTO.InfoCreditoDTO credito = factura.getInfoCredito();

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(UIConstants.COLOR_CARD);
    panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1),
            new EmptyBorder(15, 15, 15, 15)
    ));
    panel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.setMaximumSize(new Dimension(520, Integer.MAX_VALUE));

    JLabel lblTitle = new JLabel("Información del crédito");
    lblTitle.setFont(UIConstants.FONT_SUBTITLE);
    lblTitle.setForeground(Color.WHITE);
    lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(lblTitle);

    panel.add(Box.createRigidArea(new Dimension(0, 10)));

    JPanel rows = new JPanel();
    rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
    rows.setBackground(UIConstants.COLOR_CARD);
    rows.setAlignmentX(Component.CENTER_ALIGNMENT);
    rows.add(createInfoRow("ID Crédito", "#" + credito.getIdCredito()));
    rows.add(createInfoRow("Monto", FormatUtils.formatCurrency(credito.getMontoCredito())));
    rows.add(createInfoRow("Cuotas", credito.getNumeroCuotas() + " meses"));
    rows.add(createInfoRow("Valor cuota", FormatUtils.formatCurrency(credito.getValorCuota())));
    rows.add(createInfoRow("Tasa interés", credito.getTasaInteres() + "%"));
    rows.add(createInfoRow("Estado", credito.getEstado()));
    panel.add(rows);

    panel.add(Box.createRigidArea(new Dimension(0, 10)));

    JButton btnAmort = new JButton(
            credito.getTablaAmortizacion() != null && !credito.getTablaAmortizacion().isEmpty()
                    ? "Ver tabla de amortización"
                    : "Solicitar tabla de amortización"
    );
    btnAmort.setFont(UIConstants.FONT_BUTTON);
    btnAmort.setBackground(UIConstants.COLOR_INFO);
    btnAmort.setForeground(Color.WHITE);
    btnAmort.setFocusPainted(false);
    btnAmort.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnAmort.setAlignmentX(Component.CENTER_ALIGNMENT);  // Cambiado de LEFT_ALIGNMENT a CENTER_ALIGNMENT
    btnAmort.addActionListener(e -> {
        if (credito.getTablaAmortizacion() != null && !credito.getTablaAmortizacion().isEmpty()) {
            mostrarTablaAmortizacion(credito.getTablaAmortizacion(), factura.getIdFactura());
        } else {
            controller.verTablaAmortizacion(factura.getIdFactura());
        }
    });
    panel.add(btnAmort);

    return panel;
}

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        row.setBackground(UIConstants.COLOR_CARD);
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(UIConstants.FONT_NORMAL);
        lbl.setForeground(UIConstants.COLOR_TEXT_SECONDARY);
        row.add(lbl);

        JLabel val = new JLabel(value != null ? value : "-");
        val.setFont(UIConstants.FONT_NORMAL);
        val.setForeground(Color.WHITE);
        row.add(val);
        return row;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
