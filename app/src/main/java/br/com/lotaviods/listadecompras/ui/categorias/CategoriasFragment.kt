package br.com.lotaviods.listadecompras.ui.categorias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lotaviods.listadecompras.constantes.Constantes
import br.com.lotaviods.listadecompras.databinding.FragmentCategoriasBinding
import br.com.lotaviods.listadecompras.repository.ItemRepository
import br.com.lotaviods.listadecompras.ui.MainActivity
import br.com.lotaviods.listadecompras.ui.cart.adapter.ItensAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class CategoriasFragment : Fragment() {
    private var _binding: FragmentCategoriasBinding? = null
    private val binding: FragmentCategoriasBinding get() = _binding!!

    //todo: criar viewModel
    private val repository by inject<ItemRepository>()

    private val mAdapter = ItensAdapter {
        when (it) {
            is ItensAdapter.Acao.Editar -> {
                findNavController().navigate(
                    CategoriasFragmentDirections.actionCategoriasFragmentToFormularioFragmentWithItem(
                        args.categoria,
                        it.item
                    )
                )
            }
            is ItensAdapter.Acao.Deletar -> {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.deleteItem(it.item)
                    binding.rvItem.adapter.also { rvAdapter ->
                        withContext(Dispatchers.Main) {
                            (rvAdapter as? ItensAdapter)?.removeItem(it.item)
                        }
                    }
                }
            }
        }
    }

    private val args: CategoriasFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configuraTitulo()
        configuraRv()
        getItems()
        configuraBotaoAddItem()
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            val itens = repository.getItensPelaCategoria(args.categoria)

            withContext(Dispatchers.Main) {
                mAdapter.addItens(itens)
            }
        }
    }

    private fun configuraBotaoAddItem() {
        binding.addItemButton.setOnClickListener {
            findNavController().navigate(
                CategoriasFragmentDirections.actionCategoriasFragmentToFormularioFragment(
                    args.categoria, null
                )
            )
        }
    }

    private fun getItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val itens = repository.getItensPelaCategoria(args.categoria)
            withContext(Dispatchers.Main) {
                mAdapter.addItens(itens)
            }
        }
    }

    private fun configuraRv() {
        binding.rvItem.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@CategoriasFragment.context)
        }

    }

    private fun configuraTitulo() {
        (activity as MainActivity).supportActionBar?.title = when (args.categoria) {
            Constantes.CATEGORIA_LEGUME -> "Hortifruti"
            Constantes.CATEGORIA_LIMPEZA -> "Limpeza"
            Constantes.CATEGORIA_ACOUGUE -> "Açougue"
            Constantes.CATEGORIA_OUTROS -> "Outros"
            else -> ""

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}