package ec.edu.espe.arguello.ui.creditos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.models.CreditoBanco
import java.text.NumberFormat
import java.util.Locale

class CreditosAdapter(private val onCreditoClick: (CreditoBanco) -> Unit) : RecyclerView.Adapter<CreditosAdapter.CreditoViewHolder>() {
    
    private var creditos = listOf<CreditoBanco>()
    
    fun submitList(newList: List<CreditoBanco>) {
        creditos = newList
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_credito, parent, false)
        return CreditoViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CreditoViewHolder, position: Int) {
        val credito = creditos[position]
        holder.bind(credito)
        holder.itemView.setOnClickListener {
            onCreditoClick(credito)
        }
    }
    
    override fun getItemCount() = creditos.size
    
    class CreditoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val montoTextView: TextView = itemView.findViewById(R.id.montoTextView)
        private val cuotasTextView: TextView = itemView.findViewById(R.id.cuotasTextView)
        private val tasaTextView: TextView = itemView.findViewById(R.id.tasaTextView)
        private val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
        
        fun bind(credito: CreditoBanco) {
            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "EC"))
            montoTextView.text = formatter.format(credito.montoAprobado)
            cuotasTextView.text = "${credito.numeroCuotas} cuotas"
            tasaTextView.text = "Tasa: ${(credito.tasaInteres * 100).toInt()}%"
            
            estadoTextView.text = if (credito.activo) "Activo" else "Inactivo"
            estadoTextView.setTextColor(
                if (credito.activo) 
                    itemView.context.getColor(android.R.color.holo_green_dark)
                else 
                    itemView.context.getColor(android.R.color.holo_red_dark)
            )
        }
    }
}
