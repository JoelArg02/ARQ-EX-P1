package ec.edu.espe.comercializadora.ui.facturar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.comercializadora.R
import java.text.NumberFormat
import java.util.Locale

class CarritoAdapter(
    private val productos: List<ProductoCarrito>,
    private val onCantidadChanged: () -> Unit,
    private val onRemoveClick: (ProductoCarrito) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tv_nombre)
        val tvPrecio: TextView = view.findViewById(R.id.tv_precio)
        val tvCantidad: TextView = view.findViewById(R.id.tv_cantidad)
        val tvSubtotal: TextView = view.findViewById(R.id.tv_subtotal)
        val btnMenos: ImageButton = view.findViewById(R.id.btn_menos)
        val btnMas: ImageButton = view.findViewById(R.id.btn_mas)
        val btnRemove: ImageButton = view.findViewById(R.id.btn_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = productos[position]
        val formato = NumberFormat.getCurrencyInstance(Locale.US)
        
        holder.tvNombre.text = item.producto.nombre
        holder.tvPrecio.text = formato.format(item.producto.precioVenta)
        holder.tvCantidad.text = item.cantidad.toString()
        holder.tvSubtotal.text = formato.format(item.producto.precioVenta * item.cantidad)
        
        holder.btnMenos.setOnClickListener {
            if (item.cantidad > 1) {
                item.cantidad--
                notifyItemChanged(position)
                onCantidadChanged()
            }
        }
        
        holder.btnMas.setOnClickListener {
            item.cantidad++
            notifyItemChanged(position)
            onCantidadChanged()
        }
        
        holder.btnRemove.setOnClickListener {
            onRemoveClick(item)
        }
    }

    override fun getItemCount() = productos.size
}
