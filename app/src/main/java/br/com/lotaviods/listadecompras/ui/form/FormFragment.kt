package br.com.lotaviods.listadecompras.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.constantes.Constants
import br.com.lotaviods.listadecompras.databinding.FragmentFormBinding
import br.com.lotaviods.listadecompras.helper.MeasurementHelper
import br.com.lotaviods.listadecompras.helper.PriceHelper
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.repository.ItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class FormFragment : Fragment() {
    private var _binding: FragmentFormBinding? = null
    private val binding: FragmentFormBinding get() = _binding!!
    private val repository: ItemRepository by inject()
    private val args: FormFragmentArgs by navArgs()
    private var currentQuantity = 1
    private val minQuantity = 0
    private val maxQuantity = 9999

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Quantity Stepper Setup ---
        binding.itemQuantityValue.setText(currentQuantity.toString())
        binding.itemQuantityMinus.setOnClickListener {
            if (currentQuantity > minQuantity) {
                currentQuantity--
                binding.itemQuantityValue.setText(currentQuantity.toString())
                val price = binding.itemPriceEditText.text.toString()
                updatePriceTextView(currentQuantity, price)
            }
        }
        binding.itemQuantityPlus.setOnClickListener {
            if (currentQuantity < maxQuantity) {
                currentQuantity++
                binding.itemQuantityValue.setText(currentQuantity.toString())
                val price = binding.itemPriceEditText.text.toString()
                updatePriceTextView(currentQuantity, price)
            }
        }
        // Listen for direct edits
        binding.itemQuantityValue.addTextChangedListener {
            val value = it?.toString()?.toIntOrNull()
            if (value != null && value in minQuantity..maxQuantity) {
                currentQuantity = value
                val price = binding.itemPriceEditText.text.toString()
                updatePriceTextView(currentQuantity, price)
            } else if (it?.isNotEmpty() == true) {
                // Out of bounds, reset to previous valid value
                binding.itemQuantityValue.setText(currentQuantity.toString())
                binding.itemQuantityValue.setSelection(binding.itemQuantityValue.text?.length ?: 0)
            }
        }

        // --- Price Input Setup ---
        binding.itemPriceEditText.addTextChangedListener {
            val price = binding.itemPriceEditText.text.toString()
            updatePriceTextView(currentQuantity, price)
            validatePriceInput(price)
        }

        // --- Unit Dropdown Setup ---
        val useImperial = MeasurementHelper.useImperialSystem(requireContext())
        val units = if (useImperial) {
            listOf(
                getString(R.string.unit_piece),
                getString(R.string.unit_oz),
                getString(R.string.unit_lbs),
                getString(R.string.unit_gallons),
                getString(R.string.unit_none)
            )
        } else {
            listOf(
                getString(R.string.unit_piece),
                getString(R.string.unit_grams),
                getString(R.string.unit_kg),
                getString(R.string.unit_ml),
                getString(R.string.unit_liters),
                getString(R.string.unit_none)
            )
        }
        
        val unitAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, units)
        binding.itemUnitAutoComplete.setAdapter(unitAdapter)
        binding.itemUnitAutoComplete.setText(units[0], false) // Default to 'unit'
        updatePriceLabelAndHelper(units[0])
        binding.itemUnitAutoComplete.setOnClickListener {
            binding.itemUnitAutoComplete.showDropDown()
        }
        binding.itemUnitAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selected = binding.itemUnitAutoComplete.adapter.getItem(position) as String
            updatePriceLabelAndHelper(selected)
            val price = binding.itemPriceEditText.text.toString()
            updatePriceTextView(currentQuantity, price)
        }

        // --- Save Button ---
        binding.buttonSave.setOnClickListener {
            val price = binding.itemPriceEditText.text.toString()
            if (!validatePriceInput(price)) return@setOnClickListener
            save()
        }

        // --- Edit Mode Prefill ---
        args.product?.apply {
            quantity?.let {
                currentQuantity = it
                binding.itemQuantityValue.setText(currentQuantity.toString())
            }
            name?.let {
                binding.itemDescriptionEditText.setText(it)
            }
            unit.let { unitInt ->
                val displayUnit = PriceHelper.getLocalizedUnit(requireContext(), unitInt)
                if (displayUnit.isNotBlank()) {
                    binding.itemUnitAutoComplete.setText(displayUnit, false)
                    updatePriceLabelAndHelper(displayUnit)
                }
            }

            val price = binding.itemPriceEditText.text.toString()
            updatePriceTextView(currentQuantity, price)
        }
    }

    private fun updatePriceLabelAndHelper(unit: String) {
        val unitConstant = getUnitConstant(unit)
        val (label, helper) = when (unitConstant) {
            Constants.UNIT_KG, Constants.UNIT_GRAMS ->
                getString(R.string.item_price_per_kg) to getString(R.string.price_helper_kg)

            Constants.UNIT_LITERS, Constants.UNIT_ML ->
                getString(R.string.item_price_per_liter) to getString(R.string.price_helper_liter)
            
            Constants.UNIT_LBS, Constants.UNIT_OZ ->
                getString(R.string.item_price_per_lb) to getString(R.string.price_helper_lb)
            
            Constants.UNIT_GALLONS ->
                getString(R.string.item_price_per_gallon) to getString(R.string.price_helper_gallon)

            Constants.UNIT_PIECE ->
                getString(R.string.item_price_per_unit) to getString(R.string.price_helper_unit)

            else -> getString(R.string.item_price_label) to getString(R.string.price_helper_default)
        }
        binding.itemPriceTextView.text = label
        binding.itemPriceTextInputLayout.helperText = helper
    }

    // --- Helper: Validate and format price input ---
    private fun validatePriceInput(price: String?): Boolean {
        if (price.isNullOrEmpty()) {
            binding.itemPriceTextInputLayout.error = null
            return true
        }
        val regex = Regex("""^\d+([.,]\d{0,2})?$""")
        return if (price.matches(regex)) {
            binding.itemPriceTextInputLayout.error = null
            true
        } else {
            binding.itemPriceTextInputLayout.error = getString(R.string.invalid_price)
            false
        }
    }

    fun updatePriceTextView(quantity: Int, price: String?) {
        val unit = binding.itemUnitAutoComplete.text.toString()
        val unitConstant = getUnitConstant(unit)
        val total = PriceHelper.calculateTotalValue(quantity, price, unitConstant)
        val numberFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR"))
        binding.subtotalTextView.text = getString(R.string.total_spent, numberFormat.format(total))
    }

    private fun save() {
        CoroutineScope(Dispatchers.IO).launch {
            createItem()
            withContext(Dispatchers.Main) {
                findNavController().popBackStack()
            }
        }
    }

    private suspend fun createItem() {
        val selectedUnit = getUnitConstant(binding.itemUnitAutoComplete.text.toString())
        if (args.product != null) {
            repository.editItem(
                Item(
                    uid = args.product!!.uid,
                    name = binding.itemDescriptionEditText.text.toString(),
                    value = binding.itemPriceEditText.text.toString(),
                    quantity = currentQuantity,
                    category = args.category,
                    unit = selectedUnit
                )
            )
            return
        }

        repository.insertItem(
            Item(
                name = binding.itemDescriptionEditText.text.toString(),
                value = binding.itemPriceEditText.text.toString(),
                quantity = currentQuantity,
                category = args.category,
                unit = selectedUnit
            )
        )
    }

    private fun getUnitConstant(displayedUnit: String): Int {
        return when (displayedUnit.lowercase()) {
            getString(R.string.unit_grams).lowercase() -> Constants.UNIT_GRAMS
            getString(R.string.unit_kg).lowercase() -> Constants.UNIT_KG
            getString(R.string.unit_ml).lowercase() -> Constants.UNIT_ML
            getString(R.string.unit_liters).lowercase() -> Constants.UNIT_LITERS
            getString(R.string.unit_piece).lowercase() -> Constants.UNIT_PIECE
            getString(R.string.unit_lbs).lowercase() -> Constants.UNIT_LBS
            getString(R.string.unit_oz).lowercase() -> Constants.UNIT_OZ
            getString(R.string.unit_gallons).lowercase() -> Constants.UNIT_GALLONS
            getString(R.string.unit_none).lowercase() -> Constants.UNIT_NONE
            else -> Constants.UNIT_PIECE
        }
    }
}
