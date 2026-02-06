package com.openclassrooms.vitesseapp

import com.openclassrooms.vitesseapp.ui.DispatcherProvider
import kotlinx.coroutines.test.TestDispatcher

class TestDispatcherProvider(
    testDispatcher: TestDispatcher
) : DispatcherProvider {
    override val main = testDispatcher
    override val io = testDispatcher
}