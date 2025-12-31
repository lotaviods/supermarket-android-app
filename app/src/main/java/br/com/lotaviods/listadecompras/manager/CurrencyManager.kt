package br.com.lotaviods.listadecompras.manager

import android.content.Context
import androidx.core.content.edit
import java.util.Currency

object CurrencyManager {
    private const val PREFS_NAME = "currency_prefs"
    private const val KEY_CURRENCY = "selected_currency"

    enum class CurrencyType(val id: Byte) {
        BRL(0x01),
        USD(0x02),
        EUR(0x03);

        companion object {
            private val lookup = entries.associateBy { it.id }

            fun fromId(id: Byte): CurrencyType =
                lookup[id] ?: BRL
        }

        fun toCurrency(): Currency = when (this) {
            BRL -> Currency.getInstance("BRL")
            USD -> Currency.getInstance("USD")
            EUR -> Currency.getInstance("EUR")
        }
    }


    fun setCurrency(context: Context, currency: CurrencyType) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putInt(KEY_CURRENCY, currency.id.toInt())
            }
    }

    fun getCurrency(context: Context): CurrencyType {
        val id = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_CURRENCY, CurrencyType.BRL.id.toInt())
            .toByte()

        return CurrencyType.fromId(id)
    }
}