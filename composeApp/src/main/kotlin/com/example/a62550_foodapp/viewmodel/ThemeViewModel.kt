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
    val backgroundColorTwo = Color(0xFFF0F0F0)   // Slightly darker grey for contrast areas
    val surfaceColor = Color(0xFFFFFFFF)        // Pure white for elevated surfaces
    val fadedBackground = Color(0xFFDEDEDE)     // Faded grey for secondary backgrounds

    // ============================================================
    // ## OUTLINE COLORS
    // ============================================================

    val greyOutline = Color(0xFFBDBDBD)       // Medium Grey for borders and outlines
    val blackOutline = Color(0xFF9E9E9E) // Darker Grey for stronger borders

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

    val textWhite = Color.White                          // Pure White text

    // ============================================================
    // ## NAVIGATION & HEADERS
    // ============================================================
    val navBarColor = Color(0xFFFFD59A)         // Creamy Orange (navigation bar background)
    val onNavBarColor = Color(0xFF000000)       // Black text/icons on nav bar
    val softDivide = Color(0xFF252525)          // Light Grey for subtle dividers

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
    // Updated to more matte / desaturated variants
    val dryGoods = Color(0xFF8F6F3B)            // Brown - Tørvarer (dry goods) (matte)
    val meat = Color(0xFFB23B3B)                // Red - Kød (meat) (matte)
    val fish = Color(0xFF3B90A8)                // Light Blue - Fisk (fish) (matte)
    val vegetables = Color(0xFF4A7A4A)          // Green - Grøntsager (vegetables) (matte)
    val dairy = Color(0xFF2E7BB3)               // Blue - Mejeri (dairy) (matte)
    val spices = Color(0xFFCC7A19)              // Amber/Orange - Krydderier (spices) (matte)
    val kolonial = Color(0xFF6C3B85)            // Purple - Kolonial (colonial/pantry items) (matte)
    val bread = Color(0xFF7E635B)               // Warm Brown - Brød (bread) (matte)
    val unavailable = Color(0xFF6E6E6E)         // Grey - Utilgængelige varer (unavailable items) (matte)
    val other = Color(0xFF9E9E9E)               // Grey - Other categories (matte)

    // ============================================================
    // ## SUPER MARKET CATEGORIES
    // ============================================================

    val nettoColor = Color(0xFFFFCC00)          // Yellow (Netto brand color)
    val kvicklyColor =Color(0xFFFF1E31)         // Bright Red (Kvickly brand color)
    val fotexColor = Color(0xFF3257A1)               // Green (Føtex brand color)
    val menyColor = Color(0xFF86180C)           // Darker Red (Meny brand color)
    val bilkaColor = Color(0xFF3B8DDC)          // Darker blue (Bilka brand color)
    val rema1000Color = Color(0xFF6B95B6)       // Light blue (Rema 1000 brand color)
    val otherSuperMarkets = Color(0xFF252525)   // Black fallback
    val textOtherSuperMarkets = Color.White            // White text for dark backgrounds


}
