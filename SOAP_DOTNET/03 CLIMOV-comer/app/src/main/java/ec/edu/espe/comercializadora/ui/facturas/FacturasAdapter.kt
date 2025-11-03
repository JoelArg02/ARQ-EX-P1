package ec.edu.espe.comercializadora.ui.facturas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.comercializadora.R
import ec.edu.espe.comercializadora.models.Factura
import java.text.SimpleDateFormat
import java.util.*

class FacturasAdapter(
    private val facturas: List<Factura>
) : RecyclerView.Adapter<FacturasAdapter.FacturaViewHolder>() {

    inner class FacturaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFacturaId: TextView = itemView.findViewById(R.id.tv_factura_id)
        private val tvTipoPago: TextView = itemView.findViewById(R.id.tv_tipo_pago)
        private val tvCliente: TextView = itemView.findViewById(R.id.tv_cliente)
        private val tvFecha: TextView = itemView.findViewById(R.id.tv_fecha)
        private val tvTotal: TextView = itemView.findViewById(R.id.tv_total)

        fun bind(factura: Factura) {
            tvFacturaId.text = "Factura #${factura.id.toString().padStart(3, '0')}"
            
            // Tipo de pago
            tvTipoPago.text = factura.formaPago.uppercase()
            
            tvCliente.text = "${factura.nombreCliente}\nCI: ${factura.cedulaCliente}"
            
            // Formatear fecha
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val displayFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = dateFormat.parse(factura.fechaEmision)
                tvFecha.text = "Fecha: ${displayFormat.format(date)}"
            } catch (e: Exception) {
                tvFecha.text = "Fecha: ${factura.fechaEmision}"
            }
            
            tvTotal.text = String.format("Total: $%.2f\n%d producto(s)", factura.total, factura.cantidadProductos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_factura, parent, false)
        return FacturaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacturaViewHolder, position: Int) {
        holder.bind(facturas[position])
    }

    override fun getItemCount(): Int = facturas.size
}
