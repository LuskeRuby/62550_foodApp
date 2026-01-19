package com.example.a62550_foodapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a62550_foodapp.model.ShoppingList
import com.example.a62550_foodapp.ui.shoppingList.ShoppingListDetailsPage
import com.example.a62550_foodapp.viewmodel.ShoppingListViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShoppingListPage(
    themeViewModel: ThemeViewModel = koinViewModel()
) {

    val shoppingListViewModel: ShoppingListViewModel = koinViewModel ()
    val shoppingList by shoppingListViewModel.shoppingLists.collectAsState()

    // onClick booleans
    var newListOverlay by remember { mutableStateOf(false) }
    var editListNameOverlay by remember { mutableStateOf(false) }
    var deleteListOverlay by remember { mutableStateOf(false) }
    var selectListPage by remember { mutableStateOf(false) }

    var selectedShoppingList by remember { mutableStateOf<ShoppingList?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(shoppingList) { list ->
                    ShoppingListPageRow(
                        list,
                        themeViewModel = themeViewModel,
                        editClick = { clicked ->
                            selectedShoppingList = clicked
                            editListNameOverlay = true
                        },
                        deleteClick = { clicked ->
                            selectedShoppingList = clicked
                            deleteListOverlay = true
                        },
                        selectClick = { clicked ->
                            selectedShoppingList = clicked
                            selectListPage = true
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { newListOverlay = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            containerColor = themeViewModel.addButtonColor,
            contentColor = themeViewModel.onPrimaryColor
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Shopping List")
        }

        if (selectListPage) {
            BackHandler { selectListPage = false }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(themeViewModel.backgroundColor)
            ) {
                selectedShoppingList?.let {
                    ShoppingListDetailsPage(
                        shoppingListId = it.id
                    )
                }
            }
        } else if (newListOverlay) {
            BackHandler { newListOverlay = false }
            NewShoppingListFormOverlay(
                onDismiss = { newListOverlay = false },
                onCreate = { name: String ->
                    shoppingListViewModel.createShoppingList( name )
                    newListOverlay = false
                }
            )
        } else if (editListNameOverlay) {
            BackHandler { editListNameOverlay = false }
            EditShoppingListFormOverlay(
                onDismiss = {editListNameOverlay = false},
                onEdit = { id: Int, name: String ->
                    shoppingListViewModel.editShoppingList(
                        id = id,
                        name = name
                    )
                    editListNameOverlay = false
                },
                selectedShoppingList = selectedShoppingList
            )
        } else if (deleteListOverlay) {
            BackHandler { deleteListOverlay = false }
            DeleteShoppingListFormOverlay(
                onDismiss = {deleteListOverlay = false},
                onDelete = { id: Int, name: String ->
                    shoppingListViewModel.deleteShoppingList(
                        id = id,
                        name = name
                    )
                    deleteListOverlay = false
                },
                selectedShoppingList = selectedShoppingList
            )
        }

    }
}

@Composable
private fun ShoppingListPageRow(
    shoppingList: ShoppingList,
    editClick: (shoppingList: ShoppingList) -> Unit,
    deleteClick: (shoppingList: ShoppingList) -> Unit,
    selectClick: (shoppingList: ShoppingList) -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {

    val interactionSource = remember { MutableInteractionSource() }

    Card(
        onClick = { selectClick(shoppingList) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = themeViewModel.surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = shoppingList.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = themeViewModel.textPrimary,
                modifier = Modifier
                    .weight(1f)
            )

            Icon(
                imageVector = Icons.Default.BorderColor,
                contentDescription = "Edit",
                tint = themeViewModel.textSecondary,
                modifier = Modifier
                    .size(24.dp)
                   .clickable(
                       indication = LocalIndication.current,
                       interactionSource = interactionSource
                    ) { editClick(shoppingList) }
            )

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = themeViewModel.textSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        indication = LocalIndication.current,
                        interactionSource = interactionSource
                    ) { deleteClick(shoppingList) }
            )
        }
    }
}

@Composable
private fun NewShoppingListFormOverlay(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { },
        contentAlignment = Alignment.Center
    ) {

        var newShoppingListName by remember { mutableStateOf("") }

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(340.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text(
                    text = "New Shopping List",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.size(16.dp))

                OutlinedTextField(
                    value = newShoppingListName,
                    onValueChange = { newShoppingListName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            onCreate(newShoppingListName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Create")
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Cancel", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditShoppingListFormOverlay(
    onDismiss: () -> Unit,
    onEdit: (Int, String) -> Unit,
    selectedShoppingList: ShoppingList?
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { },
        contentAlignment = Alignment.Center
    ) {

        var newName by remember { mutableStateOf(selectedShoppingList?.name?: "") }

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(340.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text(
                    text = "Edit Shopping List",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.size(16.dp))

                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            selectedShoppingList?.let {onEdit(it.id, newName)}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Edit")
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Cancel", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteShoppingListFormOverlay(
    onDismiss: () -> Unit,
    onDelete: (Int, String) -> Unit,
    selectedShoppingList: ShoppingList?,
    themeViewModel : ThemeViewModel = koinViewModel()
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { },
        contentAlignment = Alignment.Center
    ) {

        var newName by remember { mutableStateOf(selectedShoppingList?.name?: "") }

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(340.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text(
                    text = "Deleting: \n${selectedShoppingList?.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.size(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            selectedShoppingList?.let {onDelete(it.id, newName)}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeViewModel.confirmDelete)
                    ) {
                        Text("Delete")
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = themeViewModel.cancelButton)
                    ) {
                        Text("Cancel", color = Color.Black)
                    }
                }
            }
        }
    }
}
