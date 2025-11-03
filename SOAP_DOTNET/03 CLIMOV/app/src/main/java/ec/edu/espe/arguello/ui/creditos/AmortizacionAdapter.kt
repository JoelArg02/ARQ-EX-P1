package ec.edu.espe.arguello.ui.creditos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.models.AmortizacionCredito
import java.text.NumberFormat
import java.util.Locale

class AmortizacionAdapter : RecyclerView.Adapter<AmortizacionAdapter.AmortizacionViewHolder>() {
    
    private var amortizaciones = listOf<AmortizacionCredito>()
    
    fun submitList(newList: List<AmortizacionCredito>) {
        amortizaciones = newList
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmortizacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_amortizacion, parent, false)
        return AmortizacionViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: AmortizacionViewHolder, position: Int) {
        holder.bind(amortizaciones[position])
    }
    
    override fun getItemCount() = amortizaciones.size
    
    class AmortizacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numeroCuotaTextView: TextView = itemView.findViewById(R.id.numeroCuotaTextView)
        private val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
        private val montoCuotaTextView: TextView = itemView.findViewById(R.id.montoCuotaTextView)
        private val capitalTextView: TextView = itemView.findViewById(R.id.capitalTextView)
        private val interesTextView: TextView = itemView.findViewById(R.id.interesTextView)
        private val saldoPendienteTextView: TextView = itemView.findViewById(R.id.saldoPendienteTextView)
        private val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
        
        fun bind(amortizacion: AmortizacionCredito) {
            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "EC"))
            
            numeroCuotaTextView.text = "Cuota #${amortizacion.numeroCuota}"
            fechaTextView.text = amortizacion.fechaVencimiento.substringBefore("T")
            montoCuotaTextView.text = formatter.format(amortizacion.montoCuota)
            capitalTextView.text = formatter.format(amortizacion.capital)
            interesTextView.text = formatter.format(amortizacion.interes)
            saldoPendienteTextView.text = formatter.format(amortizacion.saldoPendiente)
            
            estadoTextView.text = if (amortizacion.pagada) "Pagada" else "Pendiente"
            estadoTextView.setTextColor(
                if (amortizacion.pagada)
                    itemView.context.getColor(android.R.color.holo_green_dark)
                else
                    itemView.context.getColor(android.R.color.holo_orange_dark)
            )
        }
    }
}
