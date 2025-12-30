package br.com.lotaviods.listadecompras.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.constantes.Constants
import br.com.lotaviods.listadecompras.databinding.FragmentCategoriesBinding
import br.com.lotaviods.listadecompras.repository.ItemRepository
import br.com.lotaviods.listadecompras.ui.MainActivity
import br.com.lotaviods.listadecompras.ui.cart.adapter.ItemsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding: FragmentCategoriesBinding get() = _binding!!

    //todo: create viewModel
    private val repository by inject<ItemRepository>()

    private val mAdapter = ItemsAdapter {
        when (it) {
            is ItemsAdapter.Action.Edit -> {
                findNavController().navigate(
                    CategoriesFragmentDirections.actionCategoriesFragmentToFormFragmentWithItem(
                        args.category,
                        it.item
                    )
                )
            }
            is ItemsAdapter.Action.Delete -> {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.deleteItem(it.item)
                    binding.recyclerViewItems.adapter.also { rvAdapter ->
                        withContext(Dispatchers.Main) {
                            (rvAdapter as? ItemsAdapter)?.removeItem(it.item)
                        }
                    }
                }
            }
        }
    }

    private val args: CategoriesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureTitle()
        configureRecyclerView()
        getItems()
        configureAddItemButton()
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            val items = repository.getItemsByCategory(args.category)

            withContext(Dispatchers.Main) {
                mAdapter.addItems(items)
            }
        }
    }

    private fun configureAddItemButton() {
        binding.addItemButton.setOnClickListener {
            findNavController().navigate(
                CategoriesFragmentDirections.actionCategoryFragmentToFormFragment(
                    args.category, null
                )
            )
        }
    }

    private fun getItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val items = repository.getItemsByCategory(args.category)
            withContext(Dispatchers.Main) {
                mAdapter.addItems(items)
            }
        }
    }

    private fun configureRecyclerView() {
        binding.recyclerViewItems.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@CategoriesFragment.context)
        }

    }

    private fun configureTitle() {
        (activity as MainActivity).supportActionBar?.title = when (args.category) {
            Constants.CATEGORY_VEGETABLES -> getString(R.string.category_produce)
            Constants.CATEGORY_CLEANING -> getString(R.string.category_cleaning)
            Constants.CATEGORY_BUTCHER -> getString(R.string.category_butcher)
            Constants.CATEGORY_OTHERS -> getString(R.string.category_others)
            else -> ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}