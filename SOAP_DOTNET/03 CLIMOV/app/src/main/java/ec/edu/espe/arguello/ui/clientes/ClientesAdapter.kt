package ec.edu.espe.arguello.ui.clientes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.models.ClienteBanco

class ClientesAdapter(private val onClienteClick: (ClienteBanco) -> Unit) : RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder>() {
    
    private var clientes = listOf<ClienteBanco>()
    
    fun submitList(newList: List<ClienteBanco>) {
        clientes = newList
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = clientes[position]
        holder.bind(cliente)
        holder.itemView.setOnClickListener {
            onClienteClick(cliente)
        }
    }
    
    override fun getItemCount() = clientes.size
    
    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        private val cedulaTextView: TextView = itemView.findViewById(R.id.cedulaTextView)
        private val estadoCivilTextView: TextView = itemView.findViewById(R.id.estadoCivilTextView)
        private val creditoActiveTextView: TextView = itemView.findViewById(R.id.creditoActivoTextView)
        
        fun bind(cliente: ClienteBanco) {
            nombreTextView.text = cliente.nombreCompleto
            cedulaTextView.text = "CI: ${cliente.cedula}"
            estadoCivilTextView.text = cliente.estadoCivil
            creditoActiveTextView.text = if (cliente.tieneCreditoActivo) "Crédito Activo" else "Sin Crédito"
            creditoActiveTextView.setTextColor(
                if (cliente.tieneCreditoActivo) 
                    itemView.context.getColor(android.R.color.holo_red_dark)
                else 
                    itemView.context.getColor(android.R.color.holo_green_dark)
            )
        }
    }
}
