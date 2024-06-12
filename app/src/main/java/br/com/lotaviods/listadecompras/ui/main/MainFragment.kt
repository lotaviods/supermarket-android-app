package br.com.lotaviods.listadecompras.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.constantes.Constantes
import br.com.lotaviods.listadecompras.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configuraClicksCards()
    }

    private fun configuraClicksCards() {
        binding.legumesMaterialCardView.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCategoriasFragment(
                    Constantes.CATEGORIA_LEGUME
                )
            )
        }
        binding.limpezaMaterialCardView.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCategoriasFragment(
                    Constantes.CATEGORIA_LIMPEZA
                )
            )
        }
        binding.acougueMaterialCardView.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCategoriasFragment(
                    Constantes.CATEGORIA_ACOUGUE
                )
            )
        }
        binding.outrasComprasMaterialCardView.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCategoriasFragment(
                    Constantes.CATEGORIA_OUTROS
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}