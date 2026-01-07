package com.example.a62550_foodapp.ui.recipe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import com.example.a62550_foodapp.viewmodel.RecipeViewModel


@Composable
fun CreateRecipeScreen(
    recipeViewModel: RecipeViewModel,
    onRecipeSaved: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImage = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Create Recipe",
            style = MaterialTheme.typography.headlineSmall
        )

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Recipe title") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { imagePicker.launch("image/*") }) {
            Text("Choose image")
        }

        selectedImage?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Button(
            onClick = {
                recipeViewModel.createRecipe(
                    title = title,
                    description = null,
                    instructions = null,
                    imageUri = selectedImage
                )
                onRecipeSaved()
            },
            enabled = title.isNotBlank()
        ) {
            Text("Save recipe")
        }
    }
}
