package com.example.a62550_foodapp.ui.recipe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.a62550_foodapp.ui.components.LocalImage
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateRecipeScreen(
    recipeViewModel: RecipeViewModel,
    existingRecipeId: Long? = null,
    onRecipeSaved: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    var editingIngredients by remember { mutableStateOf(false) }

    // ---- LOAD DATA FOR EDIT ONCE ----
    LaunchedEffect(existingRecipeId) {
        existingRecipeId?.let {
            recipeViewModel.loadRecipeForEdit(it)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!editingIngredients) {
            CreateRecipeForm(
                recipeViewModel = recipeViewModel,
                existingRecipeId = existingRecipeId,
                onRecipeSaved = onRecipeSaved,
                onAddIngredients = { editingIngredients = true }
            )
        } else {
            AddItemGroupToRecipePage(
                recipeViewModel = recipeViewModel,
                onDone = { editingIngredients = false }
            )
        }
    }
}

@Composable
private fun CreateRecipeForm(
    recipeViewModel: RecipeViewModel,
    existingRecipeId: Long?,
    onRecipeSaved: () -> Unit,
    onAddIngredients: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    // ---- UI STATE FROM VIEWMODEL ----
    val title by recipeViewModel.editTitle.collectAsState()
    val time by recipeViewModel.editTime.collectAsState()
    val description by recipeViewModel.editDescription.collectAsState()
    val instructions by recipeViewModel.editInstructions.collectAsState()
    val imagePath by recipeViewModel.editImagePath.collectAsState()
    val imageUri by recipeViewModel.editImageUri.collectAsState()

    val selectedGroups by recipeViewModel.tempGroups.collectAsState()
    val allGroups by recipeViewModel.getAllItemGroups().collectAsState(initial = emptyList())

    // ---- IMAGE PICKER ----
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        recipeViewModel.setEditImageUri(uri)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(themeViewModel.backgroundColor)
    ) {

        // ---------- IMAGE ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clickable { imagePicker.launch("image/*") }
        ) {
            when {
                imageUri != null -> {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                imagePath != null -> {
                    LocalImage(
                        imagePath = imagePath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tryk for at vælge billede")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---------- TITLE ----------
        OutlinedTextField(
            value = title,
            onValueChange = recipeViewModel::setEditTitle,
            label = { Text("Titel") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textStyle = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        // ---------- TIME ----------
        OutlinedTextField(
            value = time,
            onValueChange = { recipeViewModel.setEditTime(it.filter(Char::isDigit)) },
            label = { Text("Tid (minutter)") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(20.dp))

        // ---------- INGREDIENTS ----------
        if (selectedGroups.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Ingredienser", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    selectedGroups.forEach { sg ->
                        val name =
                            allGroups.firstOrNull { it.id == sg.itemGroupId }?.name ?: "(ukendt)"

                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$name — ${sg.quantity}", Modifier.weight(1f))
                            Text(
                                "Fjern",
                                color = themeViewModel.errorColor,
                                modifier = Modifier.clickable {
                                    recipeViewModel.removeTempGroup(sg.itemGroupId)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        Button(
            onClick = onAddIngredients,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Tilføj ingredienser")
        }

        Spacer(Modifier.height(20.dp))

        // ---------- DESCRIPTION ----------
        OutlinedTextField(
            value = description ?: "",
            onValueChange = recipeViewModel::setEditDescription,
            label = { Text("Beskrivelse") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        // ---------- INSTRUCTIONS ----------
        OutlinedTextField(
            value = instructions ?: "",
            onValueChange = recipeViewModel::setEditInstructions,
            label = { Text("Fremgangsmåde") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(24.dp))

        // ---------- SAVE ----------
        Button(
            onClick = {
                val prep = time.toIntOrNull() ?: 0

                if (existingRecipeId != null) {
                    recipeViewModel.updateRecipe(
                        recipeId = existingRecipeId,
                        title = title,
                        preparationTimeMinutes = prep,
                        description = description,
                        instructions = instructions,
                        imageUri = imageUri,
                        selectedGroups = selectedGroups
                    )
                } else {
                    recipeViewModel.createRecipe(
                        title = title,
                        preparationTimeMinutes = prep,
                        description = description,
                        instructions = instructions,
                        imageUri = imageUri,
                        selectedGroups = selectedGroups
                    )
                }

                recipeViewModel.resetEditState()
                onRecipeSaved()
            },
            enabled = title.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(if (existingRecipeId != null) "Gem ændringer" else "Gem opskrift")
        }

        Spacer(Modifier.height(32.dp))
    }
}
