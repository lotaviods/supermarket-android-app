package br.com.lotaviods.listadecompras.helper

import android.content.Context
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.constantes.Constants
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

    fun calcularValorTotal(quantidade: Int, valor: String?, unidade: Int): Double {
        val precoDouble = valor?.replace(',', '.')?.toDoubleOrNull() ?: 0.0

        return when (unidade) {
            Constants.UNIT_GRAMS, Constants.UNIT_ML -> (quantidade / 1000.0) * precoDouble
            Constants.UNIT_KG, Constants.UNIT_LITERS -> quantidade * precoDouble
            else -> quantidade * precoDouble
        }
    }

    fun getLocalizedUnit(context: Context, unit: Int): String {
        return when (unit) {
            Constants.UNIT_GRAMS -> context.getString(R.string.unit_grams)
            Constants.UNIT_KG -> context.getString(R.string.unit_kg)
            Constants.UNIT_ML -> context.getString(R.string.unit_ml)
            Constants.UNIT_LITERS -> context.getString(R.string.unit_liters)
            Constants.UNIT_PIECE -> context.getString(R.string.unit_piece)
            Constants.UNIT_NONE -> ""
            else -> context.getString(R.string.unit_piece)
        }
    }
}
