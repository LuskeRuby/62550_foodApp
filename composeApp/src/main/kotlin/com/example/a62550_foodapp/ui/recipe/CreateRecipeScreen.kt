package com.example.a62550_foodapp.ui.recipe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.shape.CircleShape


@Composable
fun CreateRecipeScreen(
    recipeViewModel: RecipeViewModel,
    existingRecipeId: Long? = null,
    onRecipeSaved: () -> Unit,
    onBack: () -> Unit,
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
                onAddIngredients = { editingIngredients = true },
                onBack = onBack
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
    onBack: () -> Unit,
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

            // ----- IMAGE -----
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

            // ----- DARK OVERLAY -----
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.50f)) // tint på billedet
            )

            // ----- BACK BUTTON (TOP LEFT) -----
            IconButton(
                onClick = {
                    recipeViewModel.resetEditState()
                    onBack()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // ----- EDIT HINT -----
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Rediger billede",
                    tint = themeViewModel.priceTagColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Rediger billede",
                    color = themeViewModel.priceTagColor,
                    fontWeight = FontWeight.Medium
                )
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
            textStyle = MaterialTheme.typography.titleLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = themeViewModel.cardBackgroundColor,
                unfocusedContainerColor = themeViewModel.cardBackgroundColor
            )
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
                .padding(horizontal = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = themeViewModel.cardBackgroundColor,
                unfocusedContainerColor = themeViewModel.cardBackgroundColor
            )
        )

        Spacer(Modifier.height(20.dp))

// ---------- INGREDIENTS ----------
        if (selectedGroups.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = themeViewModel.cardBackgroundColor // lyserøde boks
                )
            ) {
                Column(Modifier.padding(16.dp)) {

                    // ---- HEADER ROW ----
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Ingredienser",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Rediger ingredienser",
                            tint = themeViewModel.priceTagColor,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { onAddIngredients() }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    selectedGroups.forEach { sg ->
                        val group = allGroups.firstOrNull { it.id == sg.itemGroupId }

                        val name = group?.name ?: "(ukendt)"
                        val unit = group?.unitType ?: ""

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                modifier = Modifier.weight(1f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "${sg.quantity} $unit",
                                fontSize = 13.sp,
                                color = themeViewModel.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }


        // ---------- DESCRIPTION ----------
        OutlinedTextField(
            value = description ?: "",
            onValueChange = recipeViewModel::setEditDescription,
            label = { Text("Beskrivelse") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = themeViewModel.cardBackgroundColor,
                unfocusedContainerColor = themeViewModel.cardBackgroundColor
            )
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
                .padding(horizontal = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = themeViewModel.cardBackgroundColor,
                unfocusedContainerColor = themeViewModel.cardBackgroundColor
            )
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
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = themeViewModel.addButtonColor,
                contentColor = themeViewModel.onPrimaryColor,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = themeViewModel.onPrimaryColor.copy(alpha = 0.6f)
            )
        ) {
            Text(if (existingRecipeId != null) "Gem ændringer" else "Gem opskrift")
        }
    }
}
