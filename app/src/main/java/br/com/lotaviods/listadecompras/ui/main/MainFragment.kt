package br.com.lotaviods.listadecompras.ui.main

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.broadcast.PinWidgetReceiver
import br.com.lotaviods.listadecompras.constantes.Constants
import br.com.lotaviods.listadecompras.databinding.FragmentMainBinding
import br.com.lotaviods.listadecompras.widget.ShoppingListWidgetReceiver

class MainFragment : Fragment(R.layout.fragment_main) {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    private val mAppWidgetManager: AppWidgetManager by lazy {
        requireContext().getSystemService(
            AppWidgetManager::class.java
        )
    }

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
        configurePinWidgetButton()
    }

    private fun configurePinWidgetButton() {
        binding.addWidgetButton.setOnClickListener {
            val myProvider = ComponentName(requireContext(), ShoppingListWidgetReceiver::class.java)

            val successCallback = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, PinWidgetReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mAppWidgetManager.isRequestPinAppWidgetSupported) {
                mAppWidgetManager.requestPinAppWidget(
                    myProvider, null,
                    successCallback
                )
            }
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || !mAppWidgetManager.isRequestPinAppWidgetSupported) {
            binding.addWidgetButton.visibility = View.GONE
        }

        val myProvider = ComponentName(requireContext(), ShoppingListWidgetReceiver::class.java)
        val widgetIds = mAppWidgetManager.getAppWidgetIds(myProvider)

        if (widgetIds.isNotEmpty()) {
            binding.addWidgetButton.visibility = View.GONE
        }
    }

    private fun configuraClicksCards() {
        binding.legumesMaterialCardView.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCategoriasFragment(
                    Constants.CATEGORIA_LEGUME
                )
            )
        }
        binding.limpezaMaterialCardView.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCategoriasFragment(
                    Constants.CATEGORIA_LIMPEZA
                )
            )
        }
        binding.acougueMaterialCardView.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCategoriasFragment(
                    Constants.CATEGORIA_ACOUGUE
                )
            )
        }
        binding.outrasComprasMaterialCardView.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCategoriasFragment(
                    Constants.CATEGORIA_OUTROS
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}