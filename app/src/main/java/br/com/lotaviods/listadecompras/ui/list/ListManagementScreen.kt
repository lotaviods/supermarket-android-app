package br.com.lotaviods.listadecompras.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.model.list.ShoppingList

@Composable
fun ListManagementDialog(
    lists: List<ShoppingList>,
    currentListId: Int,
    onDismissRequest: () -> Unit,
    onListClick: (ShoppingList) -> Unit,
    onDeleteListClick: (ShoppingList) -> Unit,
    onCreateListClick: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ListManagementContent(
            lists = lists,
            currentListId = currentListId,
            onDismissRequest = onDismissRequest,
            onListClick = onListClick,
            onDeleteListClick = onDeleteListClick,
            onCreateListClick = onCreateListClick
        )
    }
}

@Composable
fun ListManagementContent(
    lists: List<ShoppingList>,
    currentListId: Int,
    onDismissRequest: () -> Unit,
    onListClick: (ShoppingList) -> Unit,
    onDeleteListClick: (ShoppingList) -> Unit,
    onCreateListClick: (String) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.manage_lists),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .fillMaxWidth()
            ) {
                items(lists) { list ->
                    ListItemRow(
                        list = list,
                        isCurrent = list.id == currentListId,
                        onClick = { onListClick(list) },
                        onDelete = { onDeleteListClick(list) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            var newListName by remember { mutableStateOf("") }

            OutlinedTextField(
                value = newListName,
                onValueChange = { newListName = it },
                label = { Text(stringResource(R.string.new_list_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (newListName.isNotBlank()) {
                            onCreateListClick(newListName)
                            newListName = ""
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.create_list))
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    }
}

@Composable
fun ListItemRow(
    list: ShoppingList,
    isCurrent: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = list.name,
                fontSize = 18.sp
            )
            if (isCurrent) {
                Text(
                    text = stringResource(R.string.cart_activity_title), // Using as "Current" indicator
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (list.id != 1) { // Default list cannot be deleted
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
