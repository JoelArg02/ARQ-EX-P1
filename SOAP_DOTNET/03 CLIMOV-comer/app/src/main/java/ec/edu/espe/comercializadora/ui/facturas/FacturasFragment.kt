package ec.edu.espe.comercializadora.ui.facturas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ec.edu.espe.comercializadora.databinding.FragmentFacturasBinding
import ec.edu.espe.comercializadora.models.Factura
import ec.edu.espe.comercializadora.repository.FacturaRepository
import kotlinx.coroutines.launch

class FacturasFragment : Fragment() {

    private var _binding: FragmentFacturasBinding? = null
    private val binding get() = _binding!!
    private val facturaRepository = FacturaRepository()
    private lateinit var facturasAdapter: FacturasAdapter
    private val facturas = mutableListOf<Factura>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacturasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        loadFacturas()
    }

    private fun setupRecyclerView() {
        facturasAdapter = FacturasAdapter(facturas)
        
        binding.recyclerViewFacturas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = facturasAdapter
        }
    }

    private fun loadFacturas() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val result = facturaRepository.getAllFacturas()
                facturas.clear()
                facturas.addAll(result)
                facturasAdapter.notifyDataSetChanged()
                
                binding.progressBar.visibility = View.GONE
                
                if (facturas.isEmpty()) {
                    binding.textViewEmpty.visibility = View.VISIBLE
                } else {
                    binding.textViewEmpty.visibility = View.GONE
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error al cargar facturas: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
