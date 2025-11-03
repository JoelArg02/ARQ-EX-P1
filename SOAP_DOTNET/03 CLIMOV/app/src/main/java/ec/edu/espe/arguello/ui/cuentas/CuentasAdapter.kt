package ec.edu.espe.arguello.ui.cuentas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.models.Cuenta
import java.text.NumberFormat
import java.util.Locale

class CuentasAdapter(private val onCuentaClick: (Cuenta) -> Unit) : RecyclerView.Adapter<CuentasAdapter.CuentaViewHolder>() {
    
    private var cuentas = listOf<Cuenta>()
    
    fun submitList(newList: List<Cuenta>) {
        cuentas = newList
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuentaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cuenta, parent, false)
        return CuentaViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CuentaViewHolder, position: Int) {
        val cuenta = cuentas[position]
        holder.bind(cuenta)
        holder.itemView.setOnClickListener {
            onCuentaClick(cuenta)
        }
    }
    
    override fun getItemCount() = cuentas.size
    
    class CuentaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numeroCuentaTextView: TextView = itemView.findViewById(R.id.numeroCuentaTextView)
        private val tipoCuentaTextView: TextView = itemView.findViewById(R.id.tipoCuentaTextView)
        private val saldoTextView: TextView = itemView.findViewById(R.id.saldoTextView)
        
        fun bind(cuenta: Cuenta) {
            numeroCuentaTextView.text = cuenta.numeroCuenta
            tipoCuentaTextView.text = cuenta.tipoCuenta
            
            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "EC"))
            saldoTextView.text = formatter.format(cuenta.saldo)
        }
    }
}
