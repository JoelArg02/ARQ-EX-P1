package ec.edu.espe.comercializadora.ui.productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.comercializadora.R
import ec.edu.espe.comercializadora.models.Electrodomestico
import java.text.NumberFormat
import java.util.Locale

class ProductosAdapter(
    private val productos: List<Electrodomestico>,
    private val onItemClick: (Electrodomestico) -> Unit,
    private val onDeleteClick: (Electrodomestico) -> Unit
) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tv_nombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tv_descripcion)
        val tvPrecio: TextView = view.findViewById(R.id.tv_precio)
        val tvMarca: TextView = view.findViewById(R.id.tv_marca)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        
        holder.tvNombre.text = producto.nombre
        holder.tvDescripcion.text = producto.descripcion ?: "Sin descripción"
        holder.tvMarca.text = producto.marca ?: "Sin marca"
        
        val formato = NumberFormat.getCurrencyInstance(Locale.US)
        holder.tvPrecio.text = formato.format(producto.precioVenta)
        
        holder.itemView.setOnClickListener {
            onItemClick(producto)
        }
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(producto)
        }
    }

    override fun getItemCount() = productos.size
}
