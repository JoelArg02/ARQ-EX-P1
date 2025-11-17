package ec.edu.pinza.cliesc.views;

import ec.edu.pinza.cliesc.controllers.ProductosController;
import ec.edu.pinza.cliesc.managers.SessionManager;
import ec.edu.pinza.cliesc.models.ProductoDTO;
import ec.edu.pinza.cliesc.utils.FormatUtils;
import ec.edu.pinza.cliesc.utils.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Vista de Productos con diseño Monsters Inc.
 */
public class ProductosFrame extends JFrame {

    private final ProductosController controller;
    private JPanel productosPanel;
    private JLabel lblCarritoBadge;
    private JButton btnCarrito;

    public ProductosFrame() {
        this.controller = new ProductosController(this);
        initComponents();
        controller.cargarProductos();
    }

    private void initComponents() {
        setTitle("COMERCIALIZADORA MONSTER - Productos");
        setSize(UIConstants.WINDOW_WIDTH_LARGE, UIConstants.WINDOW_HEIGHT_LARGE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.COLOR_BACKGROUND);

        mainPanel.add(createNavbar(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        productosPanel = new JPanel();
        productosPanel.setLayout(new GridLayout(0, 3, 20, 20));
        productosPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        productosPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        scrollPane.setViewportView(productosPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        botonesPanel.setOpaque(false);

        JButton btnProductos = new JButton("Productos");
        btnProductos.setFont(UIConstants.FONT_NORMAL);
        btnProductos.setForeground(Color.WHITE);
        btnProductos.setContentAreaFilled(false);
        btnProductos.setBorderPainted(false);
        btnProductos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonesPanel.add(btnProductos);

        JPanel carritoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        carritoPanel.setOpaque(false);

        btnCarrito = new JButton("Factura");
        btnCarrito.setFont(UIConstants.FONT_NORMAL);
        btnCarrito.setForeground(Color.WHITE);
        btnCarrito.setContentAreaFilled(false);
        btnCarrito.setBorderPainted(false);
        btnCarrito.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCarrito.addActionListener(e -> controller.irACarrito());

        lblCarritoBadge = new JLabel("0");
        lblCarritoBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCarritoBadge.setForeground(Color.WHITE);
        lblCarritoBadge.setBackground(UIConstants.COLOR_ERROR);
        lblCarritoBadge.setOpaque(true);
        lblCarritoBadge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        lblCarritoBadge.setVisible(false);

        carritoPanel.add(btnCarrito);
        carritoPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        carritoPanel.add(lblCarritoBadge);
        botonesPanel.add(carritoPanel);

        JButton btnVentas = new JButton("Mis Ventas");
        btnVentas.setFont(UIConstants.FONT_NORMAL);
        btnVentas.setForeground(Color.WHITE);
        btnVentas.setContentAreaFilled(false);
        btnVentas.setBorderPainted(false);
        btnVentas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVentas.addActionListener(e -> controller.irAVentas());
        botonesPanel.add(btnVentas);

        JButton btnSalir = new JButton("Salir");
        btnSalir.setFont(UIConstants.FONT_NORMAL);
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setBackground(UIConstants.COLOR_ERROR);
        btnSalir.setOpaque(true);
        btnSalir.setBorderPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> controller.cerrarSesion());
        botonesPanel.add(btnSalir);

        navbar.add(botonesPanel, BorderLayout.EAST);

        return navbar;
    }

    public void mostrarProductos(List<ProductoDTO> productos) {
        productosPanel.removeAll();

        if (productos == null || productos.isEmpty()) {
            JLabel lblNoData = new JLabel("No hay productos disponibles");
            lblNoData.setForeground(Color.WHITE);
            lblNoData.setFont(UIConstants.FONT_NORMAL);
            productosPanel.add(lblNoData);
        } else {
            for (ProductoDTO producto : productos) {
                productosPanel.add(crearProductoCard(producto));
            }
        }

        productosPanel.revalidate();
        productosPanel.repaint();
        actualizarBadgeCarrito();
    }

    private JPanel crearProductoCard(ProductoDTO producto) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_CARD_HOVER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblNombre = new JLabel(producto.getNombre());
        lblNombre.setFont(UIConstants.FONT_SUBTITLE);
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblNombre);

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea txtDescripcion = new JTextArea(producto.getDescripcion());
        txtDescripcion.setFont(UIConstants.FONT_SMALL);
        txtDescripcion.setForeground(UIConstants.COLOR_TEXT_SECONDARY);
        txtDescripcion.setBackground(UIConstants.COLOR_CARD);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setMaximumSize(new Dimension(300, 60));
        card.add(txtDescripcion);

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblPrecio = new JLabel(FormatUtils.formatCurrency(producto.getPrecio()));
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPrecio.setForeground(UIConstants.COLOR_PRIMARY_START);
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblPrecio);

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblStock = new JLabel("Stock: " + producto.getStock());
        lblStock.setFont(UIConstants.FONT_SMALL);
        lblStock.setForeground(producto.hayStock() ? UIConstants.COLOR_SUCCESS : UIConstants.COLOR_ERROR);
        lblStock.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblStock);

        card.add(Box.createRigidArea(new Dimension(0, 15)));

        if (producto.hayStock()) {
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            formPanel.setBackground(UIConstants.COLOR_CARD);
            formPanel.setMaximumSize(new Dimension(300, 40));

            JLabel lblCantidad = new JLabel("Cantidad:");
            lblCantidad.setFont(UIConstants.FONT_SMALL);
            lblCantidad.setForeground(Color.WHITE);
            formPanel.add(lblCantidad);

            SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
                    Integer.valueOf(1),
                    Integer.valueOf(1),
                    Integer.valueOf(producto.getStock()),
                    Integer.valueOf(1)
            );
            JSpinner spnCantidad = new JSpinner(spinnerModel);
            spnCantidad.setPreferredSize(new Dimension(60, 30));
            formPanel.add(spnCantidad);

            JButton btnAgregar = new JButton("Agregar a Factura");
            btnAgregar.setFont(UIConstants.FONT_SMALL);
            btnAgregar.setBackground(UIConstants.COLOR_PRIMARY_START);
            btnAgregar.setForeground(Color.WHITE);
            btnAgregar.setFocusPainted(false);
            btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnAgregar.addActionListener(e -> {
                int cantidad = (Integer) spnCantidad.getValue();
                controller.agregarAlCarrito(producto, cantidad);
            });
            formPanel.add(btnAgregar);

            card.add(formPanel);
        } else {
            JButton btnNoDisponible = new JButton("No Disponible");
            btnNoDisponible.setFont(UIConstants.FONT_SMALL);
            btnNoDisponible.setBackground(UIConstants.COLOR_TEXT_SECONDARY);
            btnNoDisponible.setForeground(Color.WHITE);
            btnNoDisponible.setEnabled(false);
            btnNoDisponible.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(btnNoDisponible);
        }

        return card;
    }

    public void actualizarBadgeCarrito() {
        int totalItems = SessionManager.getInstance().getTotalItems();
        lblCarritoBadge.setText(String.valueOf(totalItems));
        lblCarritoBadge.setVisible(totalItems > 0);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}
