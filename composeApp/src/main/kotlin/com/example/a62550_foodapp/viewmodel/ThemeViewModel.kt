package com.example.a62550_foodapp.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

/**
 * A central ViewModel to manage the application's color palette.
 * All colors used throughout the application are centralized here.
 * Colors are chosen to reflect a fresh, organic, and minimalistic food-app aesthetic.
 */
class ThemeViewModel : ViewModel() {

    // ============================================================
    // ## BACKGROUND & SURFACE
    // ============================================================
    val backgroundColor = Color(0xFFF5F5F5)     // Light grey neutral background (main app)
    val surfaceColor = Color(0xFFFFFFFF)        // Pure white for elevated surfaces
    val fadedBackground = Color(0xFFDEDEDE)     // Faded grey for secondary backgrounds

    // ============================================================
    // ## BRAND COLORS
    // ============================================================
    val primaryColor = Color(0xFF4CAF50)        // Fresh Leaf Green (primary actions)
    val onPrimaryColor = Color(0xFFFFFFFF)      // White text on primary
    val secondaryColor = Color(0xFFF5F5F5)      // Soft Off-White/Grey for cards and rows
    val onSecondaryColor = Color(0xFF333333)    // Deep Charcoal for text on secondary

    // ============================================================
    // ## TEXT COLORS
    // ============================================================
    val textPrimary = Color(0xFF212121)         // Near Black (main text)
    val textSecondary = Color(0xFF757575)       // Medium Grey (secondary text)
    val greyedOutColor = Color(0xFF757575)      // Medium Grey for disabled elements
    val grayedOutColor = greyedOutColor                 // Alias for American spelling

    val textWhite = Color.White                          // Pure White text

    // ============================================================
    // ## NAVIGATION & HEADERS
    // ============================================================
    val navBarColor = Color(0xFFFFD59A)         // Creamy Orange (navigation bar background)
    val onNavBarColor = Color(0xFF000000)       // Black text/icons on nav bar

    // ============================================================
    // ## BUTTONS & ACTIONS
    // ============================================================
    val addButtonColor = Color(0xFF4CAF50)      // Green - Matches primary for consistency
    val successColor = Color(0xFF388E3C)        // Dark Green - Success states
    val deleteColor = Color(0xFFD32F2F)         // Red - Delete confirmation
    val cancelButton = Color.LightGray                 // Grey - Cancel actions

    // ============================================================
    // ## PRICES & FINANCIAL
    // ============================================================
    val priceTagColor = Color(0xFFE64A19)       // Vibrant Terracotta/Orange for prices
    val totalPriceColor = Color(0xFF269900)     // Dark Green for total prices

    // ============================================================
    // ## STATUS & ALERTS
    // ============================================================
    val errorColor = Color(0xFFD32F2F)          // Red for error states

    // ============================================================
    // ## RECIPE DETAILS
    // ============================================================
    val cardBackgroundColor = Color(0xFFF3E5F5) // Light Purple for ingredient/instruction cards
    val softFabColor = Color(0xFF4A90E2)        // Light Blue for globe icon on recipe page

    // ============================================================
    // ## SHOPPING LIST CATEGORIES
    // ============================================================
    val dryGoods = Color(0xFF996600)            // Brown - Tørvarer (dry goods)
    val meat = Color(0xFFD32F2F)                // Red - Kød (meat)
    val vegetables = Color(0xFF388E3C)          // Green - Grøntsager (vegetables)
    val dairy = Color(0xFF1976D2)               // Blue - Mejeri (dairy)
    val kolonial = Color(0xFF6A1B9A)            // Purple - Kolonial (colonial/pantry items)
    val other = Color.Gray                      // Grey - Other categories


}
