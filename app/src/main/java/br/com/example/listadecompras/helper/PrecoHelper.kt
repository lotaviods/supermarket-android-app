package br.com.example.listadecompras.helper

import java.text.NumberFormat
import java.util.*

object PrecoHelper {
    fun formataPrecoTotal(quantidade: Int, preco: String?): String? {
        val precoDouble = preco?.replace(',', '.')?.toDoubleOrNull()

        val precoFinal = precoDouble?.let { quantidade.times(it) }

        if (precoFinal != null) {
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            numberFormat.maximumFractionDigits = 2;
            numberFormat.minimumFractionDigits = 2;
            return numberFormat.format(precoFinal)
        }
        return null
    }

    fun formataPreco(preco: String?): String? {
        val precoDouble = preco?.replace(',', '.')?.toDoubleOrNull()
        if (precoDouble != null) {
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            numberFormat.maximumFractionDigits = 2;
            numberFormat.minimumFractionDigits = 2;
            return numberFormat.format(precoDouble)
        }
        return null
    }
}