package com.example.a62550_foodapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a62550_foodapp.model.ShoppingList
import com.example.a62550_foodapp.ui.shoppingList.ShoppingListDetailsPage
import com.example.a62550_foodapp.viewmodel.ShoppingListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShoppingListPage() {

    val shoppingListViewModel: ShoppingListViewModel = koinViewModel ()
    val shoppingList by shoppingListViewModel.shoppingLists.collectAsState()

    // onClick booleans
    var newListOverlay by remember { mutableStateOf(false) }
    var editListNameOverlay by remember { mutableStateOf(false) }
    var selectListPage by remember { mutableStateOf(false) }

    var selectedShoppingList by remember { mutableStateOf<ShoppingList?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White).padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(shoppingList) { list ->
                    ShoppingListPageRow(
                        list,
                        editClick = { clicked ->
                            selectedShoppingList = clicked
                            editListNameOverlay = true
                        },
                        selectClick = { clicked ->
                            selectedShoppingList = clicked
                            selectListPage = true
                        }
                    )
                }
            }

            Button(
                onClick = { newListOverlay = true },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ){
                Text(
                    text = "+",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(10.dp)
                )
            }
        }

        // trigger overlay
        if (selectListPage) {
            BackHandler { selectListPage = false }

            // only call when non-null
            selectedShoppingList?.let {
                ShoppingListDetailsPage(selectedShoppingList = it)
            }

        }
        else if (newListOverlay) {
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
        }

    }
}

@Composable
fun ShoppingListPageRow(
    shoppingList: ShoppingList,
    editClick: (shoppingList: ShoppingList) -> Unit,
    selectClick: (shoppingList: ShoppingList) -> Unit
) {

    // program crashes without it
    val interactionSource = remember { MutableInteractionSource() }

    Button(
        onClick = {selectClick(shoppingList)},
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = shoppingList.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 32.dp, end = 8.dp)
                    .padding(vertical = 16.dp)
            )

            Icon(
                imageVector = Icons.Default.BorderColor,
                contentDescription = "Edit",
                tint = Color.Gray,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
                   .clickable(
                       // quick fix to avoid crash
                       indication = LocalIndication.current,
                       interactionSource = interactionSource
                    ) { editClick(shoppingList) }
            )
        }
    }
}

@Composable
fun NewShoppingListFormOverlay(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {

    // Backdrop
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {

        var newShoppingListName by remember { mutableStateOf("") }

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(340.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "New Shopping List",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.size(8.dp))

                OutlinedTextField(
                    value = newShoppingListName,
                    onValueChange = { newShoppingListName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            onCreate(newShoppingListName)
                        }
                    ) {
                        Text("Create")
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun EditShoppingListFormOverlay(
    onDismiss: () -> Unit,
    onEdit: (Int, String) -> Unit,
    selectedShoppingList: ShoppingList?
) {

    // Backdrop
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {

        var newName by remember { mutableStateOf(selectedShoppingList?.name?: "") }

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(340.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Edit Shopping List",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.size(8.dp))

                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            selectedShoppingList?.let {onEdit(it.id, newName)}
                        }
                    ) {
                        Text("Edit")
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
