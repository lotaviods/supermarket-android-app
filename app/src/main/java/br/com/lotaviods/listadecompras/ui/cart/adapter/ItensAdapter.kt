package br.com.lotaviods.listadecompras.ui.cart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.lotaviods.listadecompras.databinding.RowItemBinding
import br.com.lotaviods.listadecompras.helper.PrecoHelper
import br.com.lotaviods.listadecompras.model.item.Item


class ItensAdapter(val onItemClick: (acao: Acao) -> Unit = {}) :
    RecyclerView.Adapter<ItensAdapter.ItensVH>() {
    private val mItens: MutableList<Item> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItensVH {
        return ItensVH(
            RowItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItens(itens: List<Item>) {
        mItens.clear()
        mItens.addAll(itens)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mItens.size
    }

    override fun onBindViewHolder(holder: ItensVH, position: Int) {
        holder.bind(mItens[position], onItemClick)
    }

    fun removeItem(item: Item) {
        val position = mItens.indexOf(item)
        try {
            mItens.removeAt(position)
            notifyItemRemoved(position)
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    class ItensVH(private val view: RowItemBinding) : ViewHolder(view.root) {
        fun bind(item: Item, onItemClick: (acao: Acao) -> Unit) {
            view.textDescricaoItem.text = item.nome
            view.txtQnt.text = item.qnt.toString()
            view.precoItem.text = PrecoHelper.formataPreco(item.valor)

            view.layout.setOnClickListener {
                onItemClick(Acao.Editar(item))
            }
            view.imageButtonDelete.setOnClickListener {
                onItemClick(Acao.Deletar(item))
            }
        }
    }

    sealed class Acao {
        class Deletar(val item: Item) : Acao()
        class Editar(val item: Item) : Acao()
    }

}

