package br.com.lotaviods.listadecompras.repository

import android.content.Context
import androidx.core.content.edit

class CartRepository(context: Context) {
    private val preferences = context.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)

    fun salvaNomeLista(nome: String){
        preferences.edit {
            putString(KEY_NOME_LISTA, nome)
        }
    }

    fun getNomeLista(): String? {
        return preferences.getString(KEY_NOME_LISTA, "")
    }

    companion object {
        private const val KEY_PREFERENCES = "cart"
        private const val KEY_NOME_LISTA = "cart"
    }
}