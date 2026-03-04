package com.example.ubercloneapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UberCloneUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun button_is_displayed() {
        composeRule.setContent {
            Button(onClick = {}) {
                Text("Iniciar Sesión")
            }
        }

        composeRule
            .onNodeWithText("Iniciar Sesión")
            .assertIsDisplayed()
    }

    @Test
    fun can_type_in_text_field() {
        composeRule.setContent {
            var text by remember { mutableStateOf("") }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Email") }
            )
        }

        composeRule
            .onNodeWithText("Email")
            .performTextInput("usuario@correo.com")

        composeRule
            .onNodeWithText("usuario@correo.com")
            .assertExists()
    }

    @Test
    fun click_changes_state() {
        composeRule.setContent {
            var count by remember { mutableStateOf(0) }
            Column {
                Text("Viajes: $count")
                Button(onClick = { count++ }) {
                    Text("Añadir viaje")
                }
            }
        }

        composeRule
            .onNodeWithText("Viajes: 0")
            .assertExists()

        composeRule
            .onNodeWithText("Añadir viaje")
            .performClick()
        composeRule
            .onNodeWithText("Viajes: 1")
            .assertExists()
    }
    @Test
    fun error_message_hidden_initially() {
        composeRule.setContent {
            val error: String? = null
            Column {
                Text("Bienvenido")
                if (error != null) {
                    Text(error)
                }
            }
        }

        composeRule
            .onNodeWithText("Bienvenido")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Error")
            .assertDoesNotExist()
    }
}