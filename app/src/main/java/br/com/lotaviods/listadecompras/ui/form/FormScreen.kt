package br.com.lotaviods.listadecompras.ui.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.constantes.Constants
import br.com.lotaviods.listadecompras.helper.PriceHelper
import br.com.lotaviods.listadecompras.manager.CurrencyManager
import br.com.lotaviods.listadecompras.repository.MeasurementPreferences
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    uiState: FormUiState,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onUnitChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val currencyManager = koinInject<CurrencyManager>()

    val useImperial by MeasurementPreferences
        .useImperialSystem(context)
        .collectAsState(initial = false)

    val unitOptions = remember(useImperial) {
        if (useImperial) {
            listOf(
                Constants.UNIT_PIECE,
                Constants.UNIT_OZ,
                Constants.UNIT_LBS,
                Constants.UNIT_GALLONS,
                Constants.UNIT_NONE
            )
        } else {
            listOf(
                Constants.UNIT_PIECE,
                Constants.UNIT_GRAMS,
                Constants.UNIT_KG,
                Constants.UNIT_ML,
                Constants.UNIT_LITERS,
                Constants.UNIT_NONE
            )
        }
    }

    fun getUnitLabel(unit: Int): String =
        PriceHelper.getLocalizedUnit(context, unit)

    var expanded by remember { mutableStateOf(false) }
    var isPriceError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.uid != 0)
                            stringResource(R.string.edit_item)
                        else
                            stringResource(R.string.add_item)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            /** NAME **/
            Text(
                text = stringResource(R.string.item_name_label),
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = uiState.name ?: "",
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                placeholder = { Text(stringResource(R.string.item_name_hint)) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            /** PRICE **/
            val (priceLabel, priceHelper) =
                getPriceLabelAndHelper(uiState.unit, context)

            Text(
                text = priceLabel,
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = uiState.price ?: "",
                onValueChange = {
                    onPriceChange(it)
                    val regex = Regex("""^\d+([.,]\d{0,2})?$""")
                    isPriceError = it.isNotEmpty() && !it.matches(regex)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isPriceError,
                supportingText = {
                    Text(
                        if (isPriceError)
                            stringResource(R.string.invalid_price)
                        else
                            priceHelper
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.quantity_label),
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        uiState.quantity?.let {
                            if (it > 0)
                                onQuantityChange(uiState.quantity - 1)
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("-", fontSize = 24.sp)
                }

                OutlinedTextField(
                    value = uiState.quantity.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { v ->
                            if (v in 0..9999) onQuantityChange(v)
                        }
                    },
                    modifier = Modifier
                        .width(80.dp)
                        .padding(horizontal = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Button(
                    onClick = {
                        uiState.quantity?.let {
                            if (it < 9999)
                                onQuantityChange(uiState.quantity + 1)
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("+", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            ExposedDropdownMenuAnchorType.PrimaryEditable,
                            true
                        ),
                    readOnly = true,
                    value = getUnitLabel(uiState.unit),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.unit_hint)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    unitOptions.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(getUnitLabel(unit)) },
                            onClick = {
                                onUnitChange(unit)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val subtotal = PriceHelper.calculateTotalValue(
                uiState.quantity ?: 0,
                uiState.price,
                uiState.unit
            )

            val currentCurrency by currencyManager
                .getCurrency()
                .collectAsState(
                    initial = CurrencyManager.CurrencyType.BRL
                )

            val formattedTotal = PriceHelper.formatPrice(
                subtotal.toString(),
                currentCurrency
            )

            Text(
                text = stringResource(
                    R.string.total_spent,
                    formattedTotal ?: ""
                ),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            /** SAVE **/
            Button(
                onClick = {
                    if (!isPriceError) onSaveClick()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
fun getPriceLabelAndHelper(unit: Int, context: android.content.Context): Pair<String, String> {
    return when (unit) {
        Constants.UNIT_KG, Constants.UNIT_GRAMS ->
            context.getString(R.string.item_price_per_kg) to context.getString(R.string.price_helper_kg)

        Constants.UNIT_LITERS, Constants.UNIT_ML ->
            context.getString(R.string.item_price_per_liter) to context.getString(R.string.price_helper_liter)

        Constants.UNIT_LBS, Constants.UNIT_OZ ->
            context.getString(R.string.item_price_per_lb) to context.getString(R.string.price_helper_lb)

        Constants.UNIT_GALLONS ->
            context.getString(R.string.item_price_per_gallon) to context.getString(R.string.price_helper_gallon)

        Constants.UNIT_PIECE ->
            context.getString(R.string.item_price_per_unit) to context.getString(R.string.price_helper_unit)

        else -> context.getString(R.string.item_price_label) to context.getString(R.string.price_helper_default)
    }
}
