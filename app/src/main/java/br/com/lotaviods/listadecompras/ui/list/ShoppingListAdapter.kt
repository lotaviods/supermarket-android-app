package br.com.lotaviods.listadecompras.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.lotaviods.listadecompras.databinding.ItemShoppingListBinding
import br.com.lotaviods.listadecompras.model.list.ShoppingList

class ShoppingListAdapter(
    private val onListClick: (ShoppingList) -> Unit,
    private val onDeleteClick: (ShoppingList) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

    private val lists = mutableListOf<ShoppingList>()
    private var currentListId: Int = 1

    fun updateLists(newLists: List<ShoppingList>, currentId: Int = 1) {
        lists.clear()
        lists.addAll(newLists)
        currentListId = currentId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position])
    }

    override fun getItemCount() = lists.size

    inner class ViewHolder(private val binding: ItemShoppingListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: ShoppingList) {
            binding.textListName.text = list.name
            binding.textCurrentIndicator.visibility = if (list.id == currentListId) android.view.View.VISIBLE else android.view.View.GONE
            binding.root.setOnClickListener { onListClick(list) }
            binding.buttonDeleteList.setOnClickListener { onDeleteClick(list) }
            binding.buttonDeleteList.visibility = if (list.id == 1) android.view.View.GONE else android.view.View.VISIBLE
        }
    }
}