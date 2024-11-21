package com.example.stocks.di

import com.example.stocks.domain.navigation.NavigationCoordinator;
import com.example.stocks.presentation.navigation.NavigationHandler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * Dependency injection, necessary for ViewModels to interact with the domain layer.
 */

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Provides
    @Singleton
    fun provideNavigationHandler(
        navigationCoordinator:NavigationCoordinator
    ): NavigationHandler = NavigationHandler(navigationCoordinator)
}
