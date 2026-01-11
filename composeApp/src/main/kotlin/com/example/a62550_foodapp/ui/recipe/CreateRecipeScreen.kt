package com.example.a62550_foodapp.ui.recipe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest


@Composable
fun CreateRecipeScreen(
    recipeViewModel: com.example.a62550_foodapp.viewmodel.RecipeViewModel,
    existingRecipeId: Int? = null,
    onRecipeSaved: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var preparationTime by remember { mutableStateOf(30) }
    var description by remember { mutableStateOf<String?>(null) }
    var instructions by remember { mutableStateOf<String?>(null) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var existingImagePath by remember { mutableStateOf<String?>(null) }

    // If editing, load existing recipe values
    LaunchedEffect(existingRecipeId) {
        existingRecipeId?.let { id ->
            recipeViewModel.getRecipeById(id).collectLatest { recipe ->
                recipe?.let {
                    title = it.title
                    preparationTime = it.preparationTimeMinutes
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
            OutlinedTextField(
                value = if (preparationTime == 0) "" else preparationTime.toString(),
                onValueChange = {
                    preparationTime = it.toIntOrNull() ?: 0
                },
                label = { Text("Preparation time (minutes)") },
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

        item {
            Button(
                onClick = {
                    if (existingRecipeId != null) {
                        recipeViewModel.updateRecipe(
                            id = existingRecipeId,
                            title = title,
                            preparationTimeMinutes = preparationTime,
                            description = description,
                            instructions = instructions,
                            imageUri = selectedImage
                        )
                    } else {
                        recipeViewModel.createRecipe(
                            title = title,
                            description = description,
                            instructions = instructions,
                            imageUri = selectedImage
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
