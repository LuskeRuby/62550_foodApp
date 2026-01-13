package com.example.a62550_foodapp.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

/**
 * A central ViewModel to manage the application's color palette.
 * Colors are chosen to reflect a fresh, organic, and minimalistic food-app aesthetic.
 */
class ThemeViewModel : ViewModel() {
    // Core Brand Colors
    val primaryColor = Color(0xFF4CAF50)    // Fresh Leaf Green
    val onPrimaryColor = Color(0xFFFFFFFF)  // White text on primary
    
    val secondaryColor = Color(0xFFF5F5F5)  // Soft Off-White/Grey for cards and rows
    val onSecondaryColor = Color(0xFF333333) // Deep Charcoal for text on secondary
    
    // Background and Surface
    val backgroundColor = Color(0xFFFAFAFA) // Very light neutral background
    val surfaceColor = Color(0xFFFFFFFF)    // Pure white for elevated surfaces
    
    // Nav Bar / Header Color (Matching the user's preferred creamy orange)
    val navBarColor = Color(0xFFFFD59A)
    val onNavBarColor = Color(0xFF000000)
    
    // Text Colors
    val textPrimary = Color(0xFF212121)    // Near Black
    val textSecondary = Color(0xFF757575)  // Medium Grey
    
    // Functional Colors
    val priceTagColor = Color(0xFFE64A19)   // Vibrant Terracotta/Orange for prices
    val addButtonColor = Color(0xFF4CAF50)  // Matches primary for consistency
    
    // Status Colors
    val successColor = Color(0xFF388E3C)
    val errorColor = Color(0xFFD32F2F)

    val greyedOutColor = Color(0xFF757575) // Medium Grey for disabled elements
    val grayedOutColor = greyedOutColor

}
