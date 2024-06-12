package br.com.lotaviods.listadecompras.ui.formulario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.itemQuantidadeNumPicker.apply {
            minValue = 1
            maxValue = 99
            setOnValueChangedListener { _, _, newVal ->
                val preco = binding.itemPrecoEditText.text.toString()
                alteraPrecoTextView(newVal, preco)
            }


            binding.itemPrecoEditText.addTextChangedListener {
                val preco = binding.itemPrecoEditText.text.toString()
                val qntd = binding.itemQuantidadeNumPicker.value
                alteraPrecoTextView(qntd, preco)
            }

            binding.botaoSalvar.setOnClickListener {
                salvar()
            }
        }

        if (args.produto != null) {
            args.produto?.qnt?.let {
                binding.itemQuantidadeNumPicker.value = it
            }
            args.produto?.nome?.let {
                binding.itemDescricaoEditText.setText(it)
            }
            args.produto?.valor?.let {
                binding.itemPrecoEditText.setText(it)
            }
        }
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
        if (args.produto != null) {
            repository.editaItem(
                Item(
                    uid = args.produto!!.uid,
                    nome = binding.itemDescricaoEditText.text.toString(),
                    valor = binding.itemPrecoEditText.text.toString(),
                    qnt = binding.itemQuantidadeNumPicker.value,
                    category = args.categoria
                )
            )
            return
        }

        repository.insertItem(
            Item(
                nome = binding.itemDescricaoEditText.text.toString(),
                valor = binding.itemPrecoEditText.text.toString(),
                qnt = binding.itemQuantidadeNumPicker.value,
                category = args.categoria
            )
        )

    }

    fun alteraPrecoTextView(quantidade: Int, preco: String?) {
        val precoFormatado = PrecoHelper.formataPrecoTotal(
            quantidade, preco
        )
        if (precoFormatado != null) {
            binding.subTotalTextView.text = "Total gasto: $precoFormatado"
        }
    }
}