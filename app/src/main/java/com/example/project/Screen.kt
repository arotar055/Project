package com.example.project

sealed class Screen {
    object List : Screen()
    object Edit : Screen()
}
