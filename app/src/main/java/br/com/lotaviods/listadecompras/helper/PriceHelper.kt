package br.com.lotaviods.listadecompras.helper

import android.content.Context
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.constantes.Constants
import java.text.NumberFormat
import java.util.*

object PriceHelper {
    fun formatPrice(price: String?): String? {
        val priceDouble = price?.replace(',', '.')?.toDoubleOrNull()
        if (priceDouble != null) {
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            numberFormat.maximumFractionDigits = 2
            numberFormat.minimumFractionDigits = 2
            return numberFormat.format(priceDouble)
        }
        return null
    }

    fun calculateTotalValue(quantity: Int, value: String?, unit: Int): Double {
        val priceDouble = value?.replace(',', '.')?.toDoubleOrNull() ?: 0.0

        return when (unit) {
            Constants.UNIT_GRAMS, Constants.UNIT_ML -> (quantity / 1000.0) * priceDouble
            Constants.UNIT_KG, Constants.UNIT_LITERS -> quantity * priceDouble
            else -> quantity * priceDouble
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
