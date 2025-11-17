package ec.edu.pinza.cliesc.views;

import ec.edu.pinza.cliesc.models.ProductoDTO;
import ec.edu.pinza.cliesc.services.ComercializadoraRestClient;
import ec.edu.pinza.cliesc.utils.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

/**
 * Frame de administración de productos con soporte para imágenes
 */
public class AdminProductosFrame extends JFrame {
    
    private final ComercializadoraRestClient restClient;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtDescripcion, txtPrecio, txtStock;
    private JLabel lblImagenPreview;
    private String imagenBase64;
    private ProductoDTO productoSeleccionado;
    
    public AdminProductosFrame() {
        this.restClient = new ComercializadoraRestClient();
        initComponents();
        cargarProductos();
    }
    
    private void initComponents() {
        setTitle("Administración de Productos");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(UIConstants.COLOR_BACKGROUND);
        
        // Panel superior: Tabla
        JPanel panelTabla = crearPanelTabla();
        mainPanel.add(panelTabla, BorderLayout.CENTER);
        
        // Panel derecho: Formulario
        JPanel panelFormulario = crearPanelFormulario();
        mainPanel.add(panelFormulario, BorderLayout.EAST);
        
        add(mainPanel);
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIConstants.COLOR_BACKGROUND);
        
        JLabel titulo = new JLabel("Lista de Productos");
        titulo.setFont(UIConstants.FONT_SUBTITLE);
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, BorderLayout.NORTH);
        
        String[] columnas = {"ID", "Nombre", "Descripción", "Precio", "Stock", "Imagen"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarProductoSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(UIConstants.COLOR_BACKGROUND);
        
        JButton btnNuevo = new JButton("Nuevo");
        btnNuevo.addActionListener(e -> limpiarFormulario());
        panelBotones.add(btnNuevo);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarProducto());
        panelBotones.add(btnEliminar);
        
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarProductos());
        panelBotones.add(btnRefrescar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBackground(UIConstants.COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.COLOR_PRIMARY_START, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titulo = new JLabel("Formulario de Producto");
        titulo.setFont(UIConstants.FONT_SUBTITLE);
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, BorderLayout.NORTH);
        
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(UIConstants.COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(Color.WHITE);
        campos.add(lblNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        txtNombre = new JTextField(20);
        campos.add(txtNombre, gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setForeground(Color.WHITE);
        campos.add(lblDescripcion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        txtDescripcion = new JTextField(20);
        campos.add(txtDescripcion, gbc);
        
        // Precio
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setForeground(Color.WHITE);
        campos.add(lblPrecio, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        txtPrecio = new JTextField(20);
        campos.add(txtPrecio, gbc);
        
        // Stock
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblStock = new JLabel("Stock:");
        lblStock.setForeground(Color.WHITE);
        campos.add(lblStock, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        txtStock = new JTextField(20);
        campos.add(txtStock, gbc);
        
        // Imagen
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel lblImagen = new JLabel("Imagen:");
        lblImagen.setForeground(Color.WHITE);
        campos.add(lblImagen, gbc);
        
        gbc.gridx = 0; gbc.gridy = 9;
        lblImagenPreview = new JLabel();
        lblImagenPreview.setPreferredSize(new Dimension(200, 150));
        lblImagenPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblImagenPreview.setHorizontalAlignment(JLabel.CENTER);
        lblImagenPreview.setText("Sin imagen");
        lblImagenPreview.setForeground(Color.WHITE);
        campos.add(lblImagenPreview, gbc);
        
        gbc.gridx = 0; gbc.gridy = 10;
        JPanel panelBotonesImagen = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelBotonesImagen.setBackground(UIConstants.COLOR_BACKGROUND);
        
        JButton btnCargarImagen = new JButton("Cargar Imagen");
        btnCargarImagen.addActionListener(e -> cargarImagen());
        panelBotonesImagen.add(btnCargarImagen);
        
        JButton btnQuitarImagen = new JButton("Quitar");
        btnQuitarImagen.addActionListener(e -> quitarImagen());
        panelBotonesImagen.add(btnQuitarImagen);
        
        campos.add(panelBotonesImagen, gbc);
        
        panel.add(campos, BorderLayout.CENTER);
        
        // Botones de acción
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelAcciones.setBackground(UIConstants.COLOR_BACKGROUND);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(UIConstants.COLOR_SUCCESS);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarProducto());
        panelAcciones.add(btnGuardar);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> limpiarFormulario());
        panelAcciones.add(btnCancelar);
        
        panel.add(panelAcciones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void cargarProductos() {
        try {
            List<ProductoDTO> productos = restClient.obtenerProductos();
            modeloTabla.setRowCount(0);
            
            for (ProductoDTO p : productos) {
                modeloTabla.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getNombre(),
                    p.getDescripcion(),
                    String.format("$%.2f", p.getPrecio()),
                    p.getStock(),
                    p.getImagen() != null ? "Sí" : "No"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar productos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            Long id = (Long) modeloTabla.getValueAt(fila, 0);
            try {
                productoSeleccionado = restClient.obtenerProductoPorId(id);
                
                txtNombre.setText(productoSeleccionado.getNombre());
                txtDescripcion.setText(productoSeleccionado.getDescripcion());
                txtPrecio.setText(productoSeleccionado.getPrecio().toString());
                txtStock.setText(productoSeleccionado.getStock().toString());
                
                imagenBase64 = productoSeleccionado.getImagen();
                if (imagenBase64 != null && !imagenBase64.isEmpty()) {
                    mostrarImagenPreview(imagenBase64);
                } else {
                    lblImagenPreview.setIcon(null);
                    lblImagenPreview.setText("Sin imagen");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cargarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Imágenes (*.jpg, *.jpeg, *.png, *.gif)", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                byte[] fileContent = Files.readAllBytes(file.toPath());
                imagenBase64 = Base64.getEncoder().encodeToString(fileContent);
                
                mostrarImagenPreview(imagenBase64);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar imagen: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarImagenPreview(String base64) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64);
            ImageIcon icon = new ImageIcon(imageBytes);
            Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            lblImagenPreview.setIcon(new ImageIcon(img));
            lblImagenPreview.setText(null);
        } catch (Exception e) {
            lblImagenPreview.setIcon(null);
            lblImagenPreview.setText("Error al cargar");
        }
    }
    
    private void quitarImagen() {
        imagenBase64 = null;
        lblImagenPreview.setIcon(null);
        lblImagenPreview.setText("Sin imagen");
    }
    
    private void guardarProducto() {
        try {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio", 
                    "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            ProductoDTO producto = productoSeleccionado != null ? 
                productoSeleccionado : new ProductoDTO();
            
            producto.setNombre(txtNombre.getText().trim());
            producto.setDescripcion(txtDescripcion.getText().trim());
            producto.setPrecio(new BigDecimal(txtPrecio.getText().trim()));
            producto.setStock(Integer.parseInt(txtStock.getText().trim()));
            producto.setImagen(imagenBase64);
            
            if (productoSeleccionado == null) {
                restClient.crearProducto(producto);
                JOptionPane.showMessageDialog(this, "Producto creado exitosamente");
            } else {
                restClient.actualizarProducto(producto.getIdProducto(), producto);
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente");
            }
            
            limpiarFormulario();
            cargarProductos();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Precio y stock deben ser valores numéricos válidos",
                "Error de validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar producto: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar");
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el producto: " + productoSeleccionado.getNombre() + "?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                restClient.eliminarProducto(productoSeleccionado.getIdProducto());
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente");
                limpiarFormulario();
                cargarProductos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarFormulario() {
        productoSeleccionado = null;
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        quitarImagen();
        tablaProductos.clearSelection();
    }
}
