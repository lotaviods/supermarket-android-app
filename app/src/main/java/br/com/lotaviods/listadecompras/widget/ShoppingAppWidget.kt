@file:OptIn(ExperimentalGlanceApi::class)

package br.com.lotaviods.listadecompras.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.helper.PriceHelper
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.ui.MainActivity
import br.com.lotaviods.listadecompras.ui.cart.CartActivity
import br.com.lotaviods.listadecompras.widget.model.WidgetState
import br.com.lotaviods.listadecompras.widget.repository.ShoppingWidgetRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShoppingAppWidget : GlanceAppWidget(), KoinComponent {
    private val repository: ShoppingWidgetRepository by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                ShoppingCartContent(context)
            }
        }
    }

    @Composable
    private fun ShoppingCartContent(context: Context) {
        val state by repository.loadModel().collectAsState(initial = WidgetState.Loading)

        Scaffold {
            when (state) {
                is WidgetState.Loaded -> {
                    val loadedState = state as WidgetState.Loaded
                    val items = loadedState.items
                    
                    Row(
                        modifier = GlanceModifier.fillMaxSize().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side - List info and items count
                        Column(
                            modifier = GlanceModifier.defaultWeight().padding(end = 8.dp)
                                .clickable(actionStartActivity<MainActivity>()),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = loadedState.listName,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = GlanceTheme.colors.onSurface
                                )
                            )
                            Text(
                                text = context.getString(R.string.widget_items_count, items.size),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = GlanceTheme.colors.onSurface
                                )
                            )
                            // Show first few items
                            val itemsToShow = items.take(2)
                            itemsToShow.forEach { item ->
                                Text(
                                    text = "• ${item.name}",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = GlanceTheme.colors.onSurface
                                    )
                                )
                            }
                            if (items.size > 2) {
                                Text(
                                    text = context.getString(R.string.widget_and_more, items.size - 2),
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = GlanceTheme.colors.onSurface
                                    )
                                )
                            }
                            if (items.isNotEmpty()) {
                                val totalValue = calculateTotal(items)
                                Text(
                                    text = "R$ %.2f".format(totalValue),
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = GlanceTheme.colors.primary
                                    )
                                )
                            }
                        }
                        
                        // Right side - Action buttons
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Image(
                                provider = ImageProvider(R.drawable.ic_shopping_cart),
                                modifier = GlanceModifier
                                    .clickable(actionStartActivity<CartActivity>())
                                    .padding(4.dp),
                                colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
                                contentDescription = context.getString(R.string.widget_open_app)
                            )
                            Image(
                                provider = ImageProvider(R.drawable.baseline_refresh_24),
                                modifier = GlanceModifier
                                    .clickable { actionRunCallback<ReloadAction>() }
                                    .padding(4.dp),
                                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                                contentDescription = context.getString(R.string.widget_refresh)
                            )
                        }
                    }
                }
                
                is WidgetState.Empty -> {
                    Row(
                        modifier = GlanceModifier.fillMaxSize().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = GlanceModifier.defaultWeight()
                                .clickable(actionStartActivity<MainActivity>()),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = context.getString(R.string.widget_empty_title),
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = GlanceTheme.colors.onSurface
                                )
                            )
                            Text(
                                text = context.getString(R.string.widget_empty_subtitle),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = GlanceTheme.colors.onSurface
                                )
                            )
                        }
                        
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Image(
                                provider = ImageProvider(R.drawable.ic_shopping_cart),
                                modifier = GlanceModifier
                                    .clickable(actionStartActivity<CartActivity>())
                                    .padding(4.dp),
                                colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
                                contentDescription = context.getString(R.string.widget_open_app)
                            )
                            Image(
                                provider = ImageProvider(R.drawable.baseline_refresh_24),
                                modifier = GlanceModifier
                                    .clickable { actionRunCallback<ReloadAction>() }
                                    .padding(4.dp),
                                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                                contentDescription = context.getString(R.string.widget_refresh)
                            )
                        }
                    }
                }
                
                is WidgetState.Loading -> {
                    Row(
                        modifier = GlanceModifier.fillMaxSize().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = context.getString(R.string.widget_loading),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = GlanceTheme.colors.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
    
    private fun calculateTotal(items: List<Item>): Double {
        return items.sumOf {
            PriceHelper.calculateTotalValue(it.quantity ?: 0, it.value, it.unit)
        }
    }
}

class ReloadAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val widget = ShoppingAppWidget()
        widget.update(context, glanceId)
    }
}
