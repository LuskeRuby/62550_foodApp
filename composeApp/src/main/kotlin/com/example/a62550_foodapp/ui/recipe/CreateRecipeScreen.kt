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
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun CreateRecipeScreen(
    recipeViewModel: RecipeViewModel = koinViewModel(),
    existingRecipeId: Int? = null,
    onRecipeSaved: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    // Replace Int-backed preparationTime with a String so the text field can accept any numeric input
    var preparationTimeText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf<String?>(null) }
    var instructions by remember { mutableStateOf<String?>(null) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var existingImagePath by remember { mutableStateOf<String?>(null) }

    // State for selecting existing item groups
    val allGroups by recipeViewModel.getAllItemGroups().collectAsState(initial = emptyList())
    // search state replaces the previous dropdown/expanded UI
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroupId by remember { mutableStateOf<Int?>(null) }
    var quantityText by remember { mutableStateOf("") }
    var selectedGroups by remember { mutableStateOf(listOf<SelectedItemGroup>()) }

    // If editing, load existing recipe values
    LaunchedEffect(existingRecipeId) {
        existingRecipeId?.let { id ->
            recipeViewModel.getRecipeById(id).collectLatest { recipe ->
                recipe?.let {
                    title = it.title
                    // load preparation time into the text state
                    preparationTimeText = it.preparationTimeMinutes.toString()
                    description = it.description
                    instructions = it.instructions
                    existingImagePath = it.imagePath
                }
            }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImage = uri
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
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
            // Use the string-backed text field with numeric keyboard and digit-only filtering
            OutlinedTextField(
                value = preparationTimeText,
                onValueChange = { input ->
                    // allow only digits
                    val filtered = input.filter { it.isDigit() }
                    preparationTimeText = filtered
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
                    .height(120.dp),
                singleLine = false
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { imagePicker.launch("image/*") }) {
                    Text("Choose image")
                }

                // Show remove image when editing and an existing image is present and no new image selected
                if (existingImagePath != null && selectedImage == null) {
                    Button(onClick = {
                        existingRecipeId?.let { id ->
                            recipeViewModel.removeImage(id)
                            existingImagePath = null
                        }
                    }) {
                        Text("Remove image")
                    }
                }
            }
        }

        item {
            // Show the selected image (or existing image if present)
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

        // UI for selecting existing item groups
        item {
            Text("Item groups (choose from existing)", style = MaterialTheme.typography.titleMedium)
        }

        item {
            if (allGroups.isEmpty()) {
                Text("No item groups available. Add item groups first.")
            } else {
                // Compute matches and the unit type to display
                val matches = if (searchQuery.isNotBlank()) allGroups.filter { it.name.contains(searchQuery, ignoreCase = true) } else emptyList()
                val displayedUnitType = when {
                    selectedGroupId != null -> allGroups.firstOrNull { it.id == selectedGroupId }?.unitType
                    matches.isNotEmpty() -> matches.first().unitType
                    else -> null
                }

                // Row with search field, quantity input, and unit type text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search item groups") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { input ->
                            // allow digits and decimal point
                            val filtered = input.filter { it.isDigit() || it == '.' }
                            quantityText = filtered
                        },
                        label = { Text("Qty") },
                        modifier = Modifier.width(96.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )

                    Text(
                        text = displayedUnitType ?: "",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Suggestions shown when user typed something
                if (searchQuery.isNotBlank()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            if (matches.isEmpty()) {
                                Text("No matches", modifier = Modifier.padding(8.dp))
                            } else {
                                matches.take(8).forEach { g ->
                                    Column(modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedGroupId = g.id
                                            // set searchQuery to the chosen name so user sees selection
                                            searchQuery = g.name
                                        }
                                        .padding(8.dp)) {
                                        Text(g.name)
                                    }
                                    Divider()
                                }
                            }
                        }
                    }
                } else {
                    // when search empty, show currently selected group (if any)
                    val currentLabel = allGroups.firstOrNull { it.id == selectedGroupId }?.name ?: "No group selected"
                    Text("Selected: $currentLabel", modifier = Modifier.fillMaxWidth())
                }


                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        val qty = quantityText.toFloatOrNull() ?: 0f
                        val gid = selectedGroupId
                        if (gid != null) {
                            // avoid duplicates for the same group - replace quantity
                            selectedGroups = (selectedGroups.filter { it.itemGroupId != gid } + SelectedItemGroup(gid, qty))
                            // reset inputs
                            selectedGroupId = null
                            quantityText = ""
                            searchQuery = ""
                        }
                    }) {
                        Text("Add selected group")
                    }

                    Button(onClick = {
                        selectedGroupId = null
                        quantityText = ""
                        searchQuery = ""
                    }) {
                        Text("Clear")
                    }
                }
            }
        }

        // Show the list of selected groups
        item {
            Column {
                selectedGroups.forEachIndexed { idx, sg ->
                    val name = allGroups.firstOrNull { it.id == sg.itemGroupId }?.name ?: "(unknown)"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(name, style = MaterialTheme.typography.bodyLarge)
                            Text("${sg.quantity}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Remove", modifier = Modifier.clickable {
                            selectedGroups = selectedGroups.filterIndexed { i, _ -> i != idx }
                        }, style = MaterialTheme.typography.bodySmall)
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
                            imageUri = selectedImage
                        )
                    } else {
                        // Pass selected existing groups to viewmodel
                        recipeViewModel.createRecipe(
                            title = title,
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
