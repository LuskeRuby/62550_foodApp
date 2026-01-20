package com.example.a62550_foodapp.ui.recipe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.a62550_foodapp.viewmodel.SelectedItemGroup
import androidx.compose.ui.Alignment
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.flow.first
import androidx.compose.foundation.background
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import com.example.a62550_foodapp.ui.components.SearchSelectField
import androidx.compose.foundation.lazy.items
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle

@Composable
fun CreateRecipeScreen(
    recipeViewModel: RecipeViewModel,
    existingRecipeId: Int? = null,
    onRecipeSaved: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    var addIngredientsOverlay by remember { mutableStateOf(false) }

    if (!addIngredientsOverlay) {
        CreateRecipeForm(
            recipeViewModel = recipeViewModel,
            existingRecipeId = existingRecipeId,
            onRecipeSaved = onRecipeSaved,
            onAddIngredients = { addIngredientsOverlay = true }
        )
    } else {
        BackHandler { addIngredientsOverlay = false }
        AddIngredientsOverlay(
            recipeViewModel = recipeViewModel,
            onDone = { addIngredientsOverlay = false },
            onCancel = { addIngredientsOverlay = false }
        )
    }
}

@Composable
private fun CreateRecipeForm(
    recipeViewModel: RecipeViewModel,
    existingRecipeId: Int?,
    onRecipeSaved: () -> Unit,
    onAddIngredients: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    var title by remember { mutableStateOf("") }
    var preparationTimeText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf<String?>(null) }
    var instructions by remember { mutableStateOf<String?>(null) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var existingImagePath by remember { mutableStateOf<String?>(null) }

    val allGroups by recipeViewModel.getAllItemGroups().collectAsState(initial = emptyList())
    val selectedGroups by recipeViewModel.tempGroups.collectAsState()

    var loaded by remember { mutableStateOf(false) }

    // load when editing
    LaunchedEffect(existingRecipeId) {
        if (existingRecipeId == null || loaded) return@LaunchedEffect
        loaded = true

        val recipe = recipeViewModel.getRecipeById(existingRecipeId).first()
        recipe?.let {
            title = it.title
            preparationTimeText = it.preparationTimeMinutes.toString()
            description = it.description
            instructions = it.instructions
            existingImagePath = it.imagePath
        }

        recipeViewModel.setTempGroups(
            recipeViewModel.getSelectedGroupsForRecipe(existingRecipeId)
        )
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImage = uri }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                text = if (existingRecipeId != null) "Edit Recipe" else "Create Recipe",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Recipe title") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = preparationTimeText,
                onValueChange = { preparationTimeText = it.filter(Char::isDigit) },
                label = { Text("Preparation time (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = description ?: "",
                onValueChange = { description = it.ifBlank { null } },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = instructions ?: "",
                onValueChange = { instructions = it.ifBlank { null } },
                label = { Text("Instructions") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                Button(onClick = { imagePicker.launch("image/*") }) {
                    Text("Choose image")
                }

                if (existingImagePath != null && selectedImage == null) {
                    Button(
                        onClick = {
                            existingRecipeId?.let { id ->
                                recipeViewModel.removeImage(id)
                                existingImagePath = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeViewModel.errorColor)
                    ) { Text("Remove image") }
                }
            }
        }

        item {
            when {
                selectedImage != null ->
                    AsyncImage(model = selectedImage, contentDescription = null)

                existingImagePath != null ->
                    AsyncImage(model = existingImagePath, contentDescription = null)
            }
        }

        item {
            if (selectedGroups.isNotEmpty()) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {

                        Text("Added ingredients")

                        selectedGroups.forEach { sg ->
                            val name =
                                allGroups.firstOrNull { it.id == sg.itemGroupId }?.name ?: "(unknown)"

                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("$name — ${sg.quantity}", Modifier.weight(1f))
                                Text(
                                    "Remove",
                                    color = themeViewModel.errorColor,
                                    modifier = Modifier.clickable {
                                        recipeViewModel.removeTempGroup(sg.itemGroupId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onAddIngredients,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add ingredients") }
        }

        item {
            Button(
                onClick = {
                    val prep = preparationTimeText.toIntOrNull() ?: 0
                    if (existingRecipeId != null) {
                        recipeViewModel.updateRecipe(
                            recipeId = existingRecipeId,
                            title = title,
                            preparationTimeMinutes = prep,
                            description = description,
                            instructions = instructions,
                            imageUri = selectedImage,
                            selectedGroups = selectedGroups
                        )
                    } else {
                        recipeViewModel.createRecipe(
                            title = title,
                            preparationTimeMinutes = prep,
                            description = description,
                            instructions = instructions,
                            imageUri = selectedImage,
                            selectedGroups = selectedGroups
                        )
                    }
                    onRecipeSaved()
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (existingRecipeId != null) "Save changes" else "Save recipe")
            }
        }
    }
}

@Composable
private fun AddIngredientsOverlay(
    recipeViewModel: RecipeViewModel,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val allGroups by recipeViewModel.getAllItemGroups().collectAsState(initial = emptyList())
    val tempSelected by recipeViewModel.tempGroups.collectAsState()

    var selectedGroupId by remember { mutableStateOf<Int?>(null) }
    var quantity by remember { mutableStateOf(4) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
            .padding(16.dp)
    ) {

        Text("Add ingredients", style = MaterialTheme.typography.headlineSmall)

        SearchSelectField(
            label = "Search ingredient",
            items = allGroups,
            itemText = { it.name },
            itemUnit = { it.unitType },
            onItemSelected = { selectedGroupId = it.id }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            IconButton(
                onClick = { if (quantity > 1) quantity-- },
                enabled = selectedGroupId != null
            ) {
                Icon(Icons.Default.RemoveCircle, contentDescription = null)
            }

            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(
                onClick = { quantity++ },
                enabled = selectedGroupId != null
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
            }

            Text(
                allGroups.firstOrNull { it.id == selectedGroupId }?.unitType ?: ""
            )

            Spacer(Modifier.weight(1f))

            Button(
                enabled = selectedGroupId != null,
                onClick = {
                    recipeViewModel.addTempGroup(
                        selectedGroupId!!,
                        quantity
                    )
                    selectedGroupId = null
                    quantity = 4   // ⭐ reset default
                }
            ) { Text("Add") }
        }

        LazyColumn(Modifier.weight(1f)) {
            items(tempSelected, key = { it.itemGroupId }) { sg ->
                val name =
                    allGroups.firstOrNull { it.id == sg.itemGroupId }?.name ?: "(unknown)"

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$name — ${sg.quantity}", Modifier.weight(1f))
                    Text(
                        "Remove",
                        color = themeViewModel.errorColor,
                        modifier = Modifier.clickable {
                            recipeViewModel.removeTempGroup(sg.itemGroupId)
                        }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = onDone,
                modifier = Modifier.weight(1f)
            ) {
                Text("Done")
            }
        }
    }
}
