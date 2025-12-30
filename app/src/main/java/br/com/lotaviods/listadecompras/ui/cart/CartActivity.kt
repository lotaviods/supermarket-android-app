package br.com.lotaviods.listadecompras.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.core.view.WindowInsetsControllerCompat
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.databinding.ActivityCartBinding
import br.com.lotaviods.listadecompras.helper.LanguageHelper
import br.com.lotaviods.listadecompras.helper.PriceHelper
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.repository.CartRepository
import br.com.lotaviods.listadecompras.repository.ItemRepository
import br.com.lotaviods.listadecompras.ui.cart.adapter.ItemsAdapter
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private var items: ArrayList<Item>? = null
    private val itemsRepository by inject<ItemRepository>()
    private val itemsAdapter = ItemsAdapter(onItemClick = { action ->
        when (action) {
            is ItemsAdapter.Action.Delete -> {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        itemsRepository.deleteItem(action.item)

                        items?.remove(action.item)
                        updateTotalPrice()
                        binding.recyclerView.adapter.also { rvAdapter ->
                            withContext(Dispatchers.Main) {
                                (rvAdapter as? ItemsAdapter)?.removeItem(action.item)
                                configureEmptyItemsScreen(items)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            is ItemsAdapter.Action.Edit -> {
                try {
                    val data = Intent()
                    data.putExtra("item", action.item)
                    setResult(RESULT_OK, data)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    })

    private val cartRepository by inject<CartRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.applyLanguage(this)

        enableEdgeToEdge()
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        binding = ActivityCartBinding.inflate(layoutInflater)
        title = getString(R.string.cart_activity_title)

        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        initializeItems()
        configureRecyclerView()
        configureEmptyItemsScreen(items)
        configureListNameText()
        configureClearCartButton()
    }

    private fun configureClearCartButton() {
        binding.buttonClearAllCart.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                itemsRepository.deleteAllItems()
                withContext(Dispatchers.Main) {
                    itemsAdapter.addItems(listOf())
                    items = ArrayList()
                    configureEmptyItemsScreen(items)
                    updateTotalPrice()
                }
            }

        }
    }

    private fun configureListNameText() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentListId = cartRepository.getCurrentListId()
            val currentList = cartRepository.shoppingListDao.getListById(currentListId)

            withContext(Dispatchers.Main) {
                binding.editListName.setText(
                    currentList?.name ?: getString(R.string.default_list_name)
                )

                binding.editListName.doAfterTextChanged { text ->
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(1000)
                        ensureActive()
                        currentList?.let { list ->
                            cartRepository.shoppingListDao.update(list.copy(name = text.toString()))
                        }
                    }
                }
            }
        }
    }

    private fun initializeItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = itemsRepository.getAllItems()
            items = ArrayList(list)
            withContext(Dispatchers.Main) {
                itemsAdapter.addItems(items ?: listOf())
                configureEmptyItemsScreen(items)
                updateTotalPrice()
            }
        }
    }

    private fun updateTotalPrice() {
        CoroutineScope(Dispatchers.Default).launch {
            var totalValue = 0.0
            items?.forEach {
                totalValue += PriceHelper.calculateTotalValue(it.quantity ?: 0, it.value, it.unit)
            }
            withContext(Dispatchers.Main) {
                binding.subtotalCartTextView.text =
                    getString(R.string.total_value, PriceHelper.formatPrice(totalValue.toString()))
            }
        }
    }

    private fun configureRecyclerView() {
        binding.recyclerView.apply {
            adapter = itemsAdapter
            layoutManager = LinearLayoutManager(this@CartActivity)
        }

        items?.let { itemsAdapter.addItems(it) }
    }

    private fun configureEmptyItemsScreen(items: List<Item>?) {
        binding.emptyItemsView.root.visibility =
            if (items?.isEmpty() == true) View.VISIBLE else View.GONE
        binding.mainLayout.visibility =
            if (items?.isEmpty() == true) View.GONE else View.VISIBLE

        binding.emptyItemsView.backButton.setOnClickListener {
            finish()
        }
        if (items?.isEmpty() == true) binding.subtotalCartTextView.text = ""
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
