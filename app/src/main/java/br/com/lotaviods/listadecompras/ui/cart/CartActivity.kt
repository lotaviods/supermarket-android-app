package br.com.lotaviods.listadecompras.ui.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.core.view.WindowInsetsControllerCompat
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.constantes.Constantes
import br.com.lotaviods.listadecompras.databinding.ActivityCartBinding
import br.com.lotaviods.listadecompras.helper.LanguageHelper
import br.com.lotaviods.listadecompras.helper.PrecoHelper
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.repository.CartRepository
import br.com.lotaviods.listadecompras.repository.ItemRepository
import br.com.lotaviods.listadecompras.ui.cart.adapter.ItensAdapter
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private var itens: ArrayList<Item>? = null
    private val itensRepository by inject<ItemRepository>()
    private val itensAdapter = ItensAdapter(onItemClick = { acao ->
        when (acao) {
            is ItensAdapter.Acao.Deletar -> {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        itensRepository.deleteItem(acao.item)

                        itens?.remove(acao.item)
                        atualizaPrecoTotal()
                        binding.recyclerView.adapter.also { rvAdapter ->
                            withContext(Dispatchers.Main) {
                                (rvAdapter as? ItensAdapter)?.removeItem(acao.item)
                                configuraTelaSemItens(itens)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            is ItensAdapter.Acao.Editar -> {
                try {
                    val data = Intent()
                    data.putExtra("item", acao.item)
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

        inicializaItens()
        configuraRecyclerView()
        configuraTelaSemItens(itens)
        configuraTextNomeLista()
        configuraBotaoLimparCarrinho()
    }

    private fun configuraBotaoLimparCarrinho() {
        binding.buttonLimparTudoCart.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                itensRepository.deletaTodosOsItens()
                withContext(Dispatchers.Main) {
                    itensAdapter.addItens(listOf())
                    itens = ArrayList()
                    configuraTelaSemItens(itens)
                    atualizaPrecoTotal()
                }
            }

        }
    }

    private fun configuraTextNomeLista() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentListId = cartRepository.getCurrentListId()
            val currentList = cartRepository.shoppingListDao.getListById(currentListId)

            withContext(Dispatchers.Main) {
                binding.edtNomeLista.setText(
                    currentList?.name ?: getString(R.string.default_list_name)
                )

                binding.edtNomeLista.doAfterTextChanged { text ->
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

    private fun inicializaItens() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = itensRepository.getAllItems()
            itens = ArrayList(list)
            withContext(Dispatchers.Main) {
                itensAdapter.addItens(itens ?: listOf())
                configuraTelaSemItens(itens)
                atualizaPrecoTotal()
            }
        }
    }

    private fun atualizaPrecoTotal() {
        CoroutineScope(Dispatchers.Default).launch {
            var valorTotal = 0.0
            itens?.forEach {
                val valorUnit = it.valor?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
                val unidade = it.unidade?.lowercase() ?: "unidade"
                val qntd = it.qnt ?: 0
                valorTotal += when (unidade) {
                    "gramas", "ml" -> (qntd / 1000.0) * valorUnit
                    "kg", "litros" -> qntd * valorUnit
                    else -> qntd * valorUnit // unidade, nenhum, or unknown
                }
            }
            withContext(Dispatchers.Main) {
                binding.subTotalCartTextView.text =
                    getString(R.string.total_value, PrecoHelper.formataPreco(valorTotal.toString()))
            }
        }
    }

    private fun configuraRecyclerView() {
        binding.recyclerView.apply {
            adapter = itensAdapter
            layoutManager = LinearLayoutManager(this@CartActivity)
        }

        itens?.let { itensAdapter.addItens(it) }
    }

    private fun configuraTelaSemItens(itens: List<Item>?) {
        binding.semItensView.root.visibility =
            if (itens?.isEmpty() == true) View.VISIBLE else View.GONE
        binding.layoutPrincipal.visibility =
            if (itens?.isEmpty() == true) View.GONE else View.VISIBLE

        binding.semItensView.buttonVoltar.setOnClickListener {
            finish()
        }
        if (itens?.isEmpty() == true) binding.subTotalCartTextView.text = ""
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}