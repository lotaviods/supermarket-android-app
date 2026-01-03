package br.com.lotaviods.listadecompras.ui.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.constantes.Constants
import br.com.lotaviods.listadecompras.helper.PriceHelper
import br.com.lotaviods.listadecompras.manager.CurrencyManager
import br.com.lotaviods.listadecompras.model.item.Item
import org.koin.compose.koinInject

@Composable
fun CartItemRow(
    item: Item,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val currencyManager = koinInject<CurrencyManager>()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayUnit = remember(item.unit) {
                PriceHelper.getLocalizedUnit(context, item.unit)
            }
            val quantityText = if (item.unit != Constants.UNIT_NONE && displayUnit.isNotBlank()) {
                "${item.quantity ?: 0} $displayUnit"
            } else {
                "${item.quantity ?: 0}"
            }

            Text(
                text = quantityText,
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 10.dp)
            )

            Text(
                text = item.name ?: "",
                fontSize = 18.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            val subtotal = remember(item.quantity, item.value, item.unit) {
                PriceHelper.calculateTotalValue(item.quantity ?: 0, item.value, item.unit)
            }
            
            val currentCurrency by currencyManager.getCurrency().collectAsState(initial = CurrencyManager.CurrencyType.BRL)
            
            val priceText = remember(subtotal, currentCurrency) {
                PriceHelper.formatPrice(
                    subtotal.toString(),
                    currentCurrency
                ) ?: ""
            }

            Text(
                text = priceText,
                modifier = Modifier.padding(horizontal = 5.dp)
            )

            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
