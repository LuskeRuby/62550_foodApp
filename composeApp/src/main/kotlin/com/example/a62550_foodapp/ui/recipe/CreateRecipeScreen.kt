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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.a62550_foodapp.viewmodel.SelectedItemGroup
import androidx.compose.ui.Alignment
import kotlinx.coroutines.flow.first
import androidx.compose.foundation.background


@Composable
fun CreateRecipeScreen(
    recipeViewModel: com.example.a62550_foodapp.viewmodel.RecipeViewModel,
    existingRecipeId: Int? = null,
    onRecipeSaved: () -> Unit,
    themeViewModel: com.example.a62550_foodapp.viewmodel.ThemeViewModel = org.koin.androidx.compose.koinViewModel()
) {
    var title by remember { mutableStateOf("") }
    var preparationTimeText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf<String?>(null) }
    var instructions by remember { mutableStateOf<String?>(null) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var existingImagePath by remember { mutableStateOf<String?>(null) }

    val allGroups by recipeViewModel.getAllItemGroups().collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroupId by remember { mutableStateOf<Int?>(null) }
    var quantityText by remember { mutableStateOf("") }
    var selectedGroups by remember { mutableStateOf(listOf<SelectedItemGroup>()) }

    LaunchedEffect(existingRecipeId) {
        if (existingRecipeId == null) return@LaunchedEffect

        val recipe = recipeViewModel.getRecipeById(existingRecipeId).first()
        recipe?.let {
            title = it.title
            preparationTimeText = it.preparationTimeMinutes.toString()
            description = it.description
            instructions = it.instructions
            existingImagePath = it.imagePath
        }

        selectedGroups = recipeViewModel.getSelectedGroupsForRecipe(existingRecipeId)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImage = uri
    }

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
                style = MaterialTheme.typography.headlineSmall,
                color = themeViewModel.textPrimary
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
                onValueChange = { input ->
                    preparationTimeText = input.filter { it.isDigit() }
                },
                label = { Text("Preparation time (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
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

                Button(
                    onClick = { imagePicker.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeViewModel.primaryColor,
                        contentColor = themeViewModel.onPrimaryColor
                    )
                ) {
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeViewModel.errorColor,
                            contentColor = themeViewModel.onPrimaryColor
                        )
                    ) {
                        Text("Remove image")
                    }
                }
            }
        }

        item {
            when {
                selectedImage != null -> {
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                existingImagePath != null -> {
                    AsyncImage(
                        model = existingImagePath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = themeViewModel.surfaceColor),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        "Ingredients",
                        style = MaterialTheme.typography.titleMedium,
                        color = themeViewModel.textPrimary
                    )

                    Spacer(Modifier.height(12.dp))

                    val matches =
                        if (searchQuery.isNotBlank()) allGroups.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        } else emptyList()

                    val displayedUnitType = when {
                        selectedGroupId != null -> allGroups.firstOrNull { it.id == selectedGroupId }?.unitType
                        matches.isNotEmpty() -> matches.first().unitType
                        else -> null
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { input ->
                                quantityText = input.filter { it.isDigit() || it == '.' }
                            },
                            label = { Text("Qty") },
                            modifier = Modifier.width(90.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )

                        Text(
                            text = displayedUnitType ?: "",
                            color = themeViewModel.textSecondary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    if (searchQuery.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = themeViewModel.secondaryColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                if (matches.isEmpty()) {
                                    Text(
                                        "No matches",
                                        modifier = Modifier.padding(12.dp),
                                        color = themeViewModel.textSecondary
                                    )
                                } else {
                                    matches.take(6).forEach { g ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedGroupId = g.id
                                                    searchQuery = g.name
                                                }
                                                .padding(12.dp)
                                        ) {
                                            Text(g.name, color = themeViewModel.textPrimary)
                                        }
                                        Divider()
                                    }
                                }
                            }
                        }
                    } else {
                        val currentLabel =
                            allGroups.firstOrNull { it.id == selectedGroupId }?.name ?: "No group selected"

                        Text(
                            "Selected: $currentLabel",
                            color = themeViewModel.textSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        Button(
                            onClick = {
                                val qty = quantityText.toFloatOrNull() ?: 0f
                                val gid = selectedGroupId
                                if (gid != null) {
                                    selectedGroups =
                                        (selectedGroups.filter { it.itemGroupId != gid } +
                                                SelectedItemGroup(gid, qty))
                                    selectedGroupId = null
                                    quantityText = ""
                                    searchQuery = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeViewModel.primaryColor,
                                contentColor = themeViewModel.onPrimaryColor
                            )
                        ) {
                            Text("Add")
                        }

                        OutlinedButton(
                            onClick = {
                                selectedGroupId = null
                                quantityText = ""
                                searchQuery = ""
                            }
                        ) {
                            Text("Clear")
                        }
                    }
                }
            }
        }

        item {
            if (selectedGroups.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = themeViewModel.surfaceColor),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            "Added ingredients",
                            style = MaterialTheme.typography.titleMedium,
                            color = themeViewModel.textPrimary
                        )

                        Spacer(Modifier.height(8.dp))

                        selectedGroups.forEachIndexed { idx, sg ->

                            val name =
                                allGroups.firstOrNull { it.id == sg.itemGroupId }?.name ?: "(unknown)"

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, color = themeViewModel.textPrimary)
                                    Text(
                                        "${sg.quantity}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = themeViewModel.textSecondary
                                    )
                                }

                                Text(
                                    "Remove",
                                    color = themeViewModel.errorColor,
                                    modifier = Modifier.clickable {
                                        selectedGroups =
                                            selectedGroups.filterIndexed { i, _ -> i != idx }
                                    }
                                )
                            }

                            if (idx != selectedGroups.lastIndex) Divider()
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val prepMinutes = preparationTimeText.toIntOrNull() ?: 0
                    if (existingRecipeId != null) {
                        recipeViewModel.updateRecipe(
                            id = existingRecipeId,
                            title = title,
                            preparationTimeMinutes = prepMinutes,
                            description = description,
                            instructions = instructions,
                            imageUri = selectedImage,
                            selectedGroups = selectedGroups
                        )
                    } else {
                        recipeViewModel.createRecipe(
                            title = title,
                            preparationTimeMinutes = prepMinutes,
                            description = description,
                            instructions = instructions,
                            imageUri = selectedImage,
                            selectedGroups = selectedGroups
                        )
                    }
                    onRecipeSaved()
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeViewModel.addButtonColor,
                    contentColor = themeViewModel.onPrimaryColor,
                    disabledContainerColor = themeViewModel.secondaryColor,
                    disabledContentColor = themeViewModel.textSecondary
                )
            ) {
                Text(if (existingRecipeId != null) "Save changes" else "Save recipe")
            }
        }
    }
}
