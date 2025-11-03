package ec.edu.espe.comercializadora.ui.clientes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.comercializadora.R
import ec.edu.espe.comercializadora.models.Cliente

class ClientesAdapter(
    private val clientes: List<Cliente>,
    private val onItemClick: (Cliente) -> Unit
) : RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tv_nombre)
        val tvCedula: TextView = view.findViewById(R.id.tv_cedula)
        val tvContacto: TextView = view.findViewById(R.id.tv_contacto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = clientes[position]
        
        holder.tvNombre.text = cliente.nombreCompleto
        holder.tvCedula.text = "CI: ${cliente.cedula}"
        
        val contacto = buildString {
            cliente.telefono?.let { append("Tel: $it") }
            if (cliente.telefono != null && cliente.correo != null) append(" • ")
            cliente.correo?.let { append(it) }
        }
        holder.tvContacto.text = contacto.ifEmpty { "Sin contacto" }
        
        holder.itemView.setOnClickListener {
            onItemClick(cliente)
        }
    }

    override fun getItemCount() = clientes.size
}
