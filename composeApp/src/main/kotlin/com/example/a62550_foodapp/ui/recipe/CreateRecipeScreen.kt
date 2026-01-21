package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.background
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import com.example.a62550_foodapp.ui.components.LocalImage
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.first
import com.example.a62550_foodapp.ui.shoppingList.AddItemGroupToShoppingListPage

@Composable
fun CreateRecipeScreen(
    recipeViewModel: RecipeViewModel,
    existingRecipeId: Long? = null,
    onRecipeSaved: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    var showIngredientEditor by remember { mutableStateOf(false) }

    if (showIngredientEditor && existingRecipeId != null) {

        AddItemGroupToShoppingListPage(
            shoppingListId = existingRecipeId,
            disableItemOverlay = { showIngredientEditor = false }
        )

    } else {

        CreateRecipeForm(
            recipeViewModel = recipeViewModel,
            existingRecipeId = existingRecipeId,
            onRecipeSaved = onRecipeSaved,
            onAddIngredients = { showIngredientEditor = true }
        )
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
    var title by remember { mutableStateOf("") }
    var preparationTimeText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf<String?>(null) }
    var instructions by remember { mutableStateOf<String?>(null) }

    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var existingImagePath by remember { mutableStateOf<String?>(null) }

    val allGroups by recipeViewModel.getAllItemGroups().collectAsState(initial = emptyList())
    val selectedGroups by recipeViewModel.tempGroups.collectAsState()

    var loaded by remember { mutableStateOf(false) }

    // ---- LOAD EXISTING RECIPE WHEN EDITING ----
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

    // ---- IMAGE PICKER ----
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImage = uri }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(themeViewModel.backgroundColor)
    ) {

        // ---------- IMAGE (CLICK TO CHANGE) ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clickable { imagePicker.launch("image/*") }
        ) {
            when {
                selectedImage != null -> {
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                existingImagePath != null -> {
                    LocalImage(
                        imagePath = existingImagePath,
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
            onValueChange = { title = it },
            label = { Text("Titel") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textStyle = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        // ---------- TIME ----------
        OutlinedTextField(
            value = preparationTimeText,
            onValueChange = { preparationTimeText = it.filter(Char::isDigit) },
            label = { Text("Tid (minutter)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            onValueChange = { description = it.ifBlank { null } },
            label = { Text("Beskrivelse") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        // ---------- INSTRUCTIONS ----------
        OutlinedTextField(
            value = instructions ?: "",
            onValueChange = { instructions = it.ifBlank { null } },
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(if (existingRecipeId != null) "Gem ændringer" else "Gem opskrift")
        }

        Spacer(Modifier.height(32.dp))
    }
}
