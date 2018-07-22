package io.richardking.bound.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.richardking.bound.MainActivity

@Suppress("unused")
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}