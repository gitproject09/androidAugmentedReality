package com.sopan.augmented_reality.app

import com.sopan.augmented_reality.helpers.defaultPref
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { defaultPref(androidContext()) }
}