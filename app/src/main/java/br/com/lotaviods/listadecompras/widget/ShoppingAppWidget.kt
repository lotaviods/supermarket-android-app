@file:OptIn(ExperimentalGlanceApi::class)

package br.com.lotaviods.listadecompras.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
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
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.ui.MainActivity
import br.com.lotaviods.listadecompras.widget.model.WidgetState
import br.com.lotaviods.listadecompras.widget.repository.ShoppingWidgetRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShoppingAppWidget : GlanceAppWidget(), KoinComponent {
    private val repository: ShoppingWidgetRepository by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                ShoppingCartContent()
            }
        }
    }

    @Composable
    private fun ShoppingCartContent() {
        val state by repository.loadModel().collectAsState(initial = WidgetState.Loading)

        Scaffold {
            Column(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.TopEnd,
                    modifier = GlanceModifier.fillMaxWidth().padding(12.dp)
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.baseline_refresh_24),
                        modifier = GlanceModifier.clickable {
                            actionRunCallback<ReloadAction>()
                        },
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                        contentDescription = "Atualizar"
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())
                ) {
                    when (state) {
                        is WidgetState.Loaded -> {
                            val loadedState = state as WidgetState.Loaded
                            val items = loadedState.items

                            Text(
                                text = loadedState.listName,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = GlanceTheme.colors.onSurface
                                ),
                                modifier = GlanceModifier.padding(vertical = 8.dp)
                            )

                            LazyColumn(
                                modifier = GlanceModifier.fillMaxSize()
                            ) {
                                items(items) { item ->
                                    ShoppingCartItemRow(item = item)
                                    Spacer(modifier = GlanceModifier.height(16.dp))
                                }

                                item {
                                    Column {
                                        Spacer(modifier = GlanceModifier.height(16.dp))
                                        TotalSummary(items)
                                    }
                                }
                            }
                        }

                        is WidgetState.Empty -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Sem itens no carrinho",
                                    style = TextStyle(
                                        fontStyle = FontStyle.Italic,
                                        color = GlanceTheme.colors.onSurface
                                    ),
                                    modifier = GlanceModifier.padding(16.dp)
                                )
                                Image(
                                    provider = ImageProvider(R.drawable.baseline_refresh_24),
                                    modifier = GlanceModifier
                                        .padding(8.dp)
                                        .clickable { actionRunCallback<ReloadAction>() },
                                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                                    contentDescription = "Atualizar"
                                )
                            }
                        }

                        is WidgetState.Loading -> {
                            Text(
                                text = "Carregando...",
                                modifier = GlanceModifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalSummary(items: List<Item>) {
    val totalValue = items.sumOf {
        val unidade = it.unidade?.trim()?.lowercase() ?: "unidade"
        val valorUnit = it.valor?.replace(',', '.')?.toDoubleOrNull()
        val qntd = it.qnt ?: 0
        if (valorUnit != null && qntd > 0) {
            when (unidade) {
                "gramas", "ml" -> (qntd / 1000.0) * valorUnit
                "kg", "litros" -> qntd * valorUnit
                else -> qntd * valorUnit // unidade, nenhum, or unknown
            }
        } else 0.0
    }

    Column {
        Spacer(modifier = GlanceModifier.height(16.dp))
        Text(
            text = "Quantidade de items no carrinho: ${items.size}",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GlanceTheme.colors.onSurface
            ),
        )
        Text(
            text = "Total: R$ %.2f".format(totalValue),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GlanceTheme.colors.onSurface
            ),
        )
    }
}

@Composable
private fun ShoppingCartItemRow(item: Item) {
    val unidade = item.unidade?.trim()?.lowercase() ?: "unidade"
    val valorUnit = item.valor?.replace(',', '.')?.toDoubleOrNull()
    val qntd = item.qnt ?: 0
    val subtotal = if (valorUnit != null && qntd > 0) {
        when (unidade) {
            "gramas", "ml" -> (qntd / 1000.0) * valorUnit
            "kg", "litros" -> qntd * valorUnit
            else -> qntd * valorUnit // unidade, nenhum, or unknown
        }
    } else null

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.padding(horizontal = 8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = GlanceModifier.padding(8.dp).defaultWeight()
        ) {
            Text(
                text = "${item.qnt} $unidade - ${item.nome}",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onSurface
                ),
            )
            item.valor?.let {
                Text(
                    text = "Preço: " + (valorUnit?.let { v -> "R$ %.2f".format(v) } ?: "N/A"),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        color = GlanceTheme.colors.onSurface
                    )
                )
            }
        }

        Box(
            modifier = GlanceModifier
                .fillMaxHeight()
                .width(1.dp)
                .padding(horizontal = 8.dp)
                .background(Color.Gray),
            content = {}
        )

        Text(
            text = "Subtotal: " + (subtotal?.let { "R$ %.2f".format(it) } ?: "N/A"),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface
            ),
            modifier = GlanceModifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = GlanceModifier.width(16.dp))
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
