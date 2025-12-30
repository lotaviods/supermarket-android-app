package br.com.lotaviods.listadecompras.helper

import br.com.lotaviods.listadecompras.constantes.Constants
import org.junit.Assert.assertEquals
import org.junit.Test

class PriceHelperTest {

    @Test
    fun formatPrice_validValue_returnsFormattedString() {
        val price = "10.00"
        val formatted = PriceHelper.formatPrice(price)
        // Note: formatPrice uses default locale which might be different on the machine running tests,
        // but the code forces Locale("pt", "BR").
        // "R$ 10,00" or similar depending on non-breaking space.
        // Let's assert based on expected behavior for pt-BR.
        // The space might be a non-breaking space (char 160).
        val expected = "R$ 10,00"
        // Replace non-breaking space with space for comparison if needed, or just compare roughly if issues arise.
        // But let's try exact match first, assuming standard space or just checking the number part.
        
        // Actually, NumberFormat.getCurrencyInstance(Locale("pt", "BR")) usually outputs "R$ 10,00" 
        // possibly with a non-breaking space.
        
        // Let's rely on what the code does.
        // If the code uses a specific locale, the output should be deterministic.
        assertEquals("R$ 10,00".replace(" ", "\u00A0"), formatted)
    }

    @Test
    fun formatPrice_commaValue_returnsFormattedString() {
        val price = "10,50"
        val formatted = PriceHelper.formatPrice(price)
        assertEquals("R$ 10,50".replace(" ", "\u00A0"), formatted)
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
        val quantity = 2 // 2 kg
        val value = "10.00" // price per kg
        val unit = Constants.UNIT_KG
        val total = PriceHelper.calculateTotalValue(quantity, value, unit)
        assertEquals(20.0, total, 0.001)
    }

    @Test
    fun calculateTotalValue_unitGrams_calculatesCorrectly() {
        val quantity = 500 // 500g
        val value = "10.00" // price per kg (implied by logic usually, or price per unit?)
        // The logic is: (quantity / 1000.0) * priceDouble
        // So if I have 500g, and price is 10.00, result is 0.5 * 10.00 = 5.00
        val unit = Constants.UNIT_GRAMS
        val total = PriceHelper.calculateTotalValue(quantity, value, unit)
        assertEquals(5.0, total, 0.001)
    }
    
    @Test
    fun calculateTotalValue_unitMl_calculatesCorrectly() {
        val quantity = 250 // 250ml
        val value = "4.00" 
        val unit = Constants.UNIT_ML
        val total = PriceHelper.calculateTotalValue(quantity, value, unit)
        assertEquals(1.0, total, 0.001)
    }

    @Test
    fun calculateTotalValue_unitOz_calculatesCorrectly() {
        val quantity = 32 // 32 oz
        val value = "10.00" // price per lb (usually?) or per... unit base.
        // The logic is: (quantity / 16.0) * priceDouble
        // 32 / 16 = 2. 
        // 2 * 10.00 = 20.00
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
