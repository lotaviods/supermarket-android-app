package br.com.lotaviods.listadecompras.ui.formulario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.lotaviods.listadecompras.databinding.FragmentFormularioBinding
import br.com.lotaviods.listadecompras.helper.PrecoHelper
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.repository.ItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class FormularioFragment : Fragment() {
    private var _binding: FragmentFormularioBinding? = null
    private val binding: FragmentFormularioBinding get() = _binding!!
    private val repository: ItemRepository by inject()
    private val args: FormularioFragmentArgs by navArgs()
    private var quantidadeAtual = 1
    private val quantidadeMin = 1
    private val quantidadeMax = 9999

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormularioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Quantity Stepper Setup ---
        binding.itemQntValue.setText(quantidadeAtual.toString())
        binding.itemQntMinus.setOnClickListener {
            if (quantidadeAtual > quantidadeMin) {
                quantidadeAtual--
                binding.itemQntValue.setText(quantidadeAtual.toString())
                val preco = binding.itemPrecoEditText.text.toString()
                alteraPrecoTextView(quantidadeAtual, preco)
            }
        }
        binding.itemQntPlus.setOnClickListener {
            if (quantidadeAtual < quantidadeMax) {
                quantidadeAtual++
                binding.itemQntValue.setText(quantidadeAtual.toString())
                val preco = binding.itemPrecoEditText.text.toString()
                alteraPrecoTextView(quantidadeAtual, preco)
            }
        }
        // Listen for direct edits
        binding.itemQntValue.addTextChangedListener {
            val value = it?.toString()?.toIntOrNull()
            if (value != null && value in quantidadeMin..quantidadeMax) {
                quantidadeAtual = value
                val preco = binding.itemPrecoEditText.text.toString()
                alteraPrecoTextView(quantidadeAtual, preco)
            } else if (it?.isNotEmpty() == true) {
                // Out of bounds, reset to previous valid value
                binding.itemQntValue.setText(quantidadeAtual.toString())
                binding.itemQntValue.setSelection(binding.itemQntValue.text?.length ?: 0)
            }
        }

        // --- Price Input Setup ---
        binding.itemPrecoEditText.addTextChangedListener {
            val preco = binding.itemPrecoEditText.text.toString()
            alteraPrecoTextView(quantidadeAtual, preco)
            validatePriceInput(preco)
        }

        // --- Unit Dropdown Setup ---
        val units = listOf("unidade", "gramas", "kg", "ml", "litros", "nenhum")
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, units)
        binding.itemUnidadeAutoComplete.setAdapter(unitAdapter)
        binding.itemUnidadeAutoComplete.setText(units[0], false) // Default to 'unidade'
        updatePrecoLabelAndHelper(units[0])
        binding.itemUnidadeAutoComplete.setOnClickListener {
            binding.itemUnidadeAutoComplete.showDropDown()
        }
        binding.itemUnidadeAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selected = units[position]
            updatePrecoLabelAndHelper(selected)
            val preco = binding.itemPrecoEditText.text.toString()
            alteraPrecoTextView(quantidadeAtual, preco)
        }

        // --- Save Button ---
        binding.botaoSalvar.setOnClickListener {
            val preco = binding.itemPrecoEditText.text.toString()
            if (!validatePriceInput(preco)) return@setOnClickListener
            salvar()
        }

        // --- Edit Mode Prefill ---
        if (args.produto != null) {
            args.produto?.qnt?.let {
                quantidadeAtual = it
                binding.itemQntValue.setText(quantidadeAtual.toString())
            }
            args.produto?.nome?.let {
                binding.itemDescricaoEditText.setText(it)
            }
            args.produto?.valor?.let {
                binding.itemPrecoEditText.setText(it)
            }
            args.produto?.unidade?.let {
                if (!it.isNullOrBlank()) binding.itemUnidadeAutoComplete.setText(it, false)
            }
            // Recalculate subtotal with the correct unit after prefill
            val preco = binding.itemPrecoEditText.text.toString()
            alteraPrecoTextView(quantidadeAtual, preco)
        }
    }

    private fun updatePrecoLabelAndHelper(unit: String) {
        val (label, helper) = when (unit.lowercase()) {
            "kg", "gramas" -> "Preço do item (por kg)" to "Informe o preço para 1 kg deste produto."
            "litros", "ml" -> "Preço do item (por litro)" to "Informe o preço para 1 litro deste produto."
            "unidade" -> "Preço do item (por unidade)" to "Informe o preço para 1 unidade deste produto."
            else -> "Preço do item" to "Informe o preço do produto."
        }
        binding.itemPrecoTexView.text = label
        binding.itemPrecoTextInputLayout.helperText = helper
    }

    // --- Helper: Validate and format price input ---
    private fun validatePriceInput(preco: String?): Boolean {
        val priceDouble = preco?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
        return if (preco.isNullOrBlank() || priceDouble <= 0.0) {
            binding.itemPrecoEditText.error = "Digite um preço válido (> 0)"
            false
        } else {
            binding.itemPrecoEditText.error = null
            true
        }
    }

    // --- Helper: Update subtotal text ---
    fun alteraPrecoTextView(quantidade: Int, preco: String?) {
        val unidade = binding.itemUnidadeAutoComplete.text.toString().lowercase()
        val precoDouble = preco?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
        val subtotal = when (unidade) {
            "gramas", "ml" -> (quantidade / 1000.0) * precoDouble
            "kg", "litros" -> quantidade * precoDouble
            else -> quantidade * precoDouble // unidade, nenhum, or unknown
        }
        val numberFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR"))
        binding.subTotalTextView.text = "Total gasto: ${numberFormat.format(subtotal)}"
    }

    private fun salvar() {
        CoroutineScope(Dispatchers.IO).launch {
            criaItem()
            withContext(Dispatchers.Main) {
                findNavController().popBackStack()
            }
        }
    }

    private suspend fun criaItem() {
        val unidadeSelecionada = binding.itemUnidadeAutoComplete.text.toString()
        if (args.produto != null) {
            repository.editaItem(
                Item(
                    uid = args.produto!!.uid,
                    nome = binding.itemDescricaoEditText.text.toString(),
                    valor = binding.itemPrecoEditText.text.toString(),
                    qnt = quantidadeAtual,
                    category = args.categoria,
                    unidade = unidadeSelecionada
                )
            )
            return
        }

        repository.insertItem(
            Item(
                nome = binding.itemDescricaoEditText.text.toString(),
                valor = binding.itemPrecoEditText.text.toString(),
                qnt = quantidadeAtual,
                category = args.categoria,
                unidade = unidadeSelecionada
            )
        )
    }
}