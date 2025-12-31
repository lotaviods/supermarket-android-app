package br.com.lotaviods.listadecompras.ui.cart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.lotaviods.listadecompras.databinding.RowItemBinding
import br.com.lotaviods.listadecompras.manager.CurrencyManager
import br.com.lotaviods.listadecompras.helper.PriceHelper
import br.com.lotaviods.listadecompras.model.item.Item


class ItemsAdapter(val onItemClick: (action: Action) -> Unit = {}) :
    RecyclerView.Adapter<ItemsAdapter.ItemsVH>() {
    private val items: MutableList<Item> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsVH {
        return ItemsVH(
            RowItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: List<Item>) {
        this@ItemsAdapter.items.clear()
        this@ItemsAdapter.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemsVH, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    fun removeItem(item: Item) {
        val position = items.indexOf(item)
        try {
            items.removeAt(position)
            notifyItemRemoved(position)
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    class ItemsVH(private val view: RowItemBinding) : ViewHolder(view.root) {
        fun bind(item: Item, onItemClick: (action: Action) -> Unit) {
            view.textDescriptionItem.text = item.name

            val displayUnit = PriceHelper.getLocalizedUnit(view.root.context, item.unit)
            view.textQuantity.text = if (displayUnit.isNotBlank()) {
                "${item.quantity ?: 0} $displayUnit"
            } else {
                "${item.quantity ?: 0}"
            }

            val subtotal =
                PriceHelper.calculateTotalValue(item.quantity ?: 0, item.value, item.unit)

            view.priceItem.text = PriceHelper.formatPrice(
                subtotal.toString(),
                CurrencyManager.getCurrency(view.root.context)
            )

            view.layout.setOnClickListener {
                onItemClick(Action.Edit(item))
            }
            view.imageButtonDelete.setOnClickListener {
                onItemClick(Action.Delete(item))
            }
        }
    }

    sealed class Action {
        class Delete(val item: Item) : Action()
        class Edit(val item: Item) : Action()
    }

}
