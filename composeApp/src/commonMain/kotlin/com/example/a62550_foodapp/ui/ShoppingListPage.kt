package com.example.a62550_foodapp.ui

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
import com.example.a62550_foodapp.ui.icons.PencilIcon


data class ShoppingItem1(
    val id: Int,
    val name: String
)


@Composable
fun ShoppingListPage() {

                                val items = listOf(
                                    ShoppingItem1(1, "Spaghetti Carbonara"),
                                    ShoppingItem1(2, "Lasagna"),
                                    ShoppingItem1(3, "Butter Chicken"),
                                    ShoppingItem1(4, "Tacos al Pastor"),
                                    ShoppingItem1(5, "Pad Thai"),
                                    ShoppingItem1(6, "Chicken Tikka Masala"),
                                    ShoppingItem1(7, "Beef Stroganoff"),
                                    ShoppingItem1(8, "Fish and Chips"),
                                    ShoppingItem1(9, "Shepherd's Pie"),
                                    ShoppingItem1(10, "Ratatouille"),
                                    ShoppingItem1(11, "Ramen"),
                                    ShoppingItem1(12, "Margherita Pizza"),
                                    ShoppingItem1(13, "Sushi Rolls"),
                                    ShoppingItem1(14, "Caesar Salad"),
                                    ShoppingItem1(15, "Bruschetta"),
                                    ShoppingItem1(16, "Enchiladas"),
                                    ShoppingItem1(17, "Paella"),
                                    ShoppingItem1(18, "Poke Bowl")
                                )

    var newListOverlay by remember { mutableStateOf(false) }
    var editListNameOverlay by remember { mutableStateOf(false) }
    

    Box(
        modifier = Modifier.fillMaxSize()
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
                items(items) { item ->
                    ShoppingListRow(item, onClick = {editListNameOverlay = true})
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

        // trigger invocation of overlay
        if (newListOverlay) {
            NewShoppingListFormOverlay(onDismiss = { newListOverlay = false })
        } else if (editListNameOverlay) {
            EditShoppingListFormOverlay(onDismiss = {editListNameOverlay = false})
        }

    }
}

@Composable
fun ShoppingListRow(item: ShoppingItem1, onClick: () -> Unit) {

    // remember the source
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(Color.Gray,shape = RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
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
                imageVector = Icons.Default.BorderColor, //PencilIcon,
                contentDescription = "Edit",
                tint = Color.Black,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
                   .clickable(
                       indication = LocalIndication.current,           // explicitly pass current indication
                       interactionSource = interactionSource           // and the remembered interaction source
                    ) { onClick() }
            )
        }
    }
}

@Composable
fun NewShoppingListFormOverlay(onDismiss: () -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        var name by remember { mutableStateOf("") }

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
                    value = name,
                    onValueChange = { name = it },
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
                            /* TODO: create action */
                            onDismiss() }
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
fun EditShoppingListFormOverlay(onDismiss: () -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        var name by remember { mutableStateOf("") }

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
                    value = name,
                    onValueChange = { name = it },
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
                            /* TODO: create action */
                            onDismiss() }
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