package br.com.lotaviods.listadecompras.ui.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.constantes.Constantes
import br.com.lotaviods.listadecompras.databinding.ActivityCartBinding
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
                    setResult(Activity.RESULT_OK, data)
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
        binding = ActivityCartBinding.inflate(layoutInflater)
        title = getString(R.string.cart_activity_title)

        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        binding.edtNomeLista.apply {
            setText(cartRepository.getNomeLista())
            doAfterTextChanged { text ->
                CoroutineScope(Dispatchers.Default).launch {
                    delay(1000)
                    ensureActive()
                    cartRepository.salvaNomeLista(text.toString())
                }
            }
        }
    }

    private fun inicializaItens() {
        intent.extras?.getParcelableArrayList<Item>(Constantes.CART_BUNDLE_ITENS)?.let {
            itens = it
        }

        atualizaPrecoTotal()
    }

    private fun atualizaPrecoTotal() {
        CoroutineScope(Dispatchers.Default).launch {
            var valorTotal = 0.0
            itens?.forEach {
                val valorUnit = it.valor?.replace(',', '.')?.toDoubleOrNull()
                if (valorUnit != null) {
                     it.qnt?.let { qntd ->
                         valorTotal += (qntd * valorUnit)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                binding.subTotalCartTextView.text =
                    "Valor total: ${PrecoHelper.formataPreco(valorTotal.toString())}"
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