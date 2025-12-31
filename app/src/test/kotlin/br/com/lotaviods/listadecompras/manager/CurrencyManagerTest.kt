package br.com.lotaviods.listadecompras.manager

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class CurrencyManagerTest {

    @Test
    fun setCurrency_savesCurrencyIdToSharedPreferences() {
        val context = Mockito.mock(Context::class.java)
        val sharedPreferences = Mockito.mock(SharedPreferences::class.java)
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)

        Mockito.`when`(context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)).thenReturn(sharedPreferences)
        Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)
        Mockito.`when`(editor.putInt(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(editor)

        CurrencyManager.setCurrency(context, CurrencyManager.CurrencyType.USD)

        Mockito.verify(editor).putInt("selected_currency", CurrencyManager.CurrencyType.USD.id.toInt())
        Mockito.verify(editor).apply()
    }

    @Test
    fun getCurrency_returnsCurrencyFromSharedPreferences() {
        val context = Mockito.mock(Context::class.java)
        val sharedPreferences = Mockito.mock(SharedPreferences::class.java)

        Mockito.`when`(context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)).thenReturn(sharedPreferences)
        Mockito.`when`(
            sharedPreferences.getInt(
                "selected_currency",
                CurrencyManager.CurrencyType.BRL.id.toInt()
            )
        )
            .thenReturn(CurrencyManager.CurrencyType.EUR.id.toInt())

        val currency = CurrencyManager.getCurrency(context)

        Assert.assertEquals(CurrencyManager.CurrencyType.EUR, currency)
    }

    @Test
    fun getCurrency_returnsDefaultWhenNoPreference() {
        val context = Mockito.mock(Context::class.java)
        val sharedPreferences = Mockito.mock(SharedPreferences::class.java)

        Mockito.`when`(context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)).thenReturn(sharedPreferences)
        Mockito.`when`(
            sharedPreferences.getInt(
                "selected_currency",
                CurrencyManager.CurrencyType.BRL.id.toInt()
            )
        )
            .thenReturn(CurrencyManager.CurrencyType.BRL.id.toInt())

        val currency = CurrencyManager.getCurrency(context)

        Assert.assertEquals(CurrencyManager.CurrencyType.BRL, currency)
    }

    @Test
    fun currencyType_fromId_returnsCorrectType() {
        Assert.assertEquals(
            CurrencyManager.CurrencyType.BRL,
            CurrencyManager.CurrencyType.fromId(0x01)
        )
        Assert.assertEquals(
            CurrencyManager.CurrencyType.USD,
            CurrencyManager.CurrencyType.fromId(0x02)
        )
        Assert.assertEquals(
            CurrencyManager.CurrencyType.EUR,
            CurrencyManager.CurrencyType.fromId(0x03)
        )
    }

    @Test
    fun currencyType_fromId_returnsDefaultForUnknownId() {
        Assert.assertEquals(
            CurrencyManager.CurrencyType.BRL,
            CurrencyManager.CurrencyType.fromId(0x99.toByte())
        )
    }

    @Test
    fun currencyType_toCurrency_returnsCorrectCurrency() {
        Assert.assertEquals("BRL", CurrencyManager.CurrencyType.BRL.toCurrency().currencyCode)
        Assert.assertEquals("USD", CurrencyManager.CurrencyType.USD.toCurrency().currencyCode)
        Assert.assertEquals("EUR", CurrencyManager.CurrencyType.EUR.toCurrency().currencyCode)
    }
}