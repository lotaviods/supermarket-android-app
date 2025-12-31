package br.com.lotaviods.listadecompras.helper

import br.com.lotaviods.listadecompras.constantes.Constants
import br.com.lotaviods.listadecompras.manager.CurrencyManager.CurrencyType
import org.junit.Assert.assertEquals
import org.junit.Test

class PriceHelperTest {

    @Test
    fun formatPrice_validValue_returnsFormattedString() {
        val price = "10.00"
        val formatted = PriceHelper.formatPrice(price)
        val expected = "R$10.00"
        assertEquals(expected.replace(" ", "\u00A0"), formatted)
    }

    @Test
    fun formatPrice_commaValue_returnsFormattedString() {
        val price = "10,50"
        val formatted = PriceHelper.formatPrice(price)
        assertEquals("R$10.50".replace(" ", "\u00A0"), formatted)
    }

    @Test
    fun formatPrice_brlValue_returnsFormattedString() {
        val price = "10.00"
        val formatted = PriceHelper.formatPrice(price, CurrencyType.BRL)
        val expected = "R$10.00"
        assertEquals(expected.replace(" ", "\u00A0"), formatted)
    }

    @Test
    fun formatPrice_usdValue_returnsFormattedString() {
        val price = "10.00"
        val formatted = PriceHelper.formatPrice(price, CurrencyType.USD)
        val expected = "$10.00"
        assertEquals(expected.replace(" ", "\u00A0"), formatted)
    }

    @Test
    fun formatPrice_eurValue_returnsFormattedString() {
        val price = "10.00"
        val formatted = PriceHelper.formatPrice(price, CurrencyType.EUR)
        val expected = "€10.00"
        assertEquals(expected.replace(" ", "\u00A0"), formatted)
    }

    @Test
    fun formatPrice_invalidValue_returnsNull() {
        val price = "abc"
        val formatted = PriceHelper.formatPrice(price)
        assertEquals(null, formatted)
    }

    @Test
    fun calculateTotalValue_unitPiece_calculatesCorrectly() {
        val quantity = 2
        val value = "10.00"
        val unit = Constants.UNIT_PIECE
        val total = PriceHelper.calculateTotalValue(quantity, value, unit)
        assertEquals(20.0, total, 0.001)
    }

    @Test
    fun calculateTotalValue_unitKg_calculatesCorrectly() {
        val quantity = 2
        val value = "10.00"
        val unit = Constants.UNIT_KG
        val total = PriceHelper.calculateTotalValue(quantity, value, unit)
        assertEquals(20.0, total, 0.001)
    }

    @Test
    fun calculateTotalValue_unitGrams_calculatesCorrectly() {
        val quantity = 500
        val value = "10.00"
        val unit = Constants.UNIT_GRAMS
        val total = PriceHelper.calculateTotalValue(quantity, value, unit)
        assertEquals(5.0, total, 0.001)
    }
    
    @Test
    fun calculateTotalValue_unitMl_calculatesCorrectly() {
        val quantity = 250
        val value = "4.00"
        val unit = Constants.UNIT_ML
        val total = PriceHelper.calculateTotalValue(quantity, value, unit)
        assertEquals(1.0, total, 0.001)
    }

    @Test
    fun calculateTotalValue_unitOz_calculatesCorrectly() {
        val quantity = 32
        val value = "10.00"
        val unit = Constants.UNIT_OZ
        val total = PriceHelper.calculateTotalValue(quantity, value, unit)
        assertEquals(20.0, total, 0.001)
    }

    @Test
    fun calculateTotalValue_unitLbs_calculatesCorrectly() {
        val quantity = 2
        val value = "5.00"
        val unit = Constants.UNIT_LBS
        val total = PriceHelper.calculateTotalValue(quantity, value, unit)
        assertEquals(10.0, total, 0.001)
    }

    @Test
    fun calculateTotalValue_unitGallons_calculatesCorrectly() {
        val quantity = 1
        val value = "3.50"
        val unit = Constants.UNIT_GALLONS
        val total = PriceHelper.calculateTotalValue(quantity, value, unit)
        assertEquals(3.50, total, 0.001)
    }
}
