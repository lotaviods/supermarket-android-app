package br.com.lotaviods.listadecompras.manager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Currency

class CurrencyManager(private val context: Context) {
    private val KEY_CURRENCY = intPreferencesKey("selected_currency")

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


    suspend fun setCurrency(currency: CurrencyType) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CURRENCY] = currency.id.toInt()
        }
    }

    fun getCurrency(): Flow<CurrencyType> {
        return context.dataStore.data.map { preferences ->
            val id = preferences[KEY_CURRENCY] ?: CurrencyType.BRL.id.toInt()
            CurrencyType.fromId(id.toByte())
        }
    }
}
