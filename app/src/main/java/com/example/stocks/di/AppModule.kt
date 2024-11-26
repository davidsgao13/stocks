package com.example.stocks.di

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.stocks.BuildConfig
import com.example.stocks.core.Constants
import com.example.stocks.data.local.StockDatabase
import com.example.stocks.data.remote.StockApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

/**
 * Modules in dependency injection work as containers/classes that tell Dagger/Hilt which
 * dependencies we have and how to create them, then provide them to other classes. Think about
 * injection as passing an object as an argument to another object without needing to instantiate
 * it inside the other object (for example, passing StockRepository into CompanyListingsViewModel
 * without having to create a StockRepository instance inside the CompanyListingsViewModel.
 *
 * One of our dependencies is Retrofit. Dagger/Hilt needs to know how to create the object, so we
 * create it in our AppModule.
 *
 * These dependencies are all Singletons, because they exist as a single, reusable instance
 * throughout the app's current lifecycle. This is signified with the SingletonComponent::class
 * that we add as InstallIn and the @Singleton annotation for our dependencies.
 *
 * @Module annotation marks the entire class as a Dagger/Hilt module. It is a class that provides
 * dependencies to other parts of the app, telling Dagger how to create and supply instances of
 * dependencies.
 *
 * @InstallIn(SingletonComponent::class) annotation specifies the Dagger component in which the
 * module will be installed. As previously mentioned, SingletonComponent means that all dependencies
 * provided in the module will live for the entire app lifecycle. This ensures that all dependencies
 * instantiated here are scoped to the application and will not be unnecessarily recreated.
 */

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    /**
     * @Provides tells our AppModule to provide the dependency (StockApi) for other classes to use.
     * @Singleton tells us that the object we're providing will be a Singleton. More specifically,
     * it tells Dagger to create only one instance of this dependency and share it across the
     * entire application. This is singleton scope. Dagger ensures that the same instance is used
     * throughout the entire app.
     */

    @Provides
    @Singleton
    fun providesStockApi() : StockApi {
        Log.d("HiltDebug", "StockApi provided")
        // This tells Retrofit how to create a StockApi which will be used in dependency injection.
        // We start by initializing a new Retrofit instance.
        return Retrofit.Builder()
            // Specify a baseURL; this URl will be used for all network calls made using this
            // Retrofit instance.
            .baseUrl(BuildConfig.BASE_URL)
            // ConverterFactory will automatically transform JSON to Kotlin data classes. It is
            // used to more easily parse network responses and convert them into data classes.
            .addConverterFactory(MoshiConverterFactory.create())
            // Builds a Retrofit instance with the above configurations. .build() validates all the
            // configurations provided and prepares Retrofit to handle HTTPS requests based on
            // the configurations. The object created from .build() represents the the configured
            // API client, but it is not interacting with the application yet. It means an instance
            // of StocksApi with the above configurations is ready to create other API interfaces,
            // but a StocksApi implementation hasn't been created just yet.
            .build()
            // Creates an implementation of the StockApi interface. It reads the annotations of
            // the interface (StockApi) and creates a dynamic implementation of the interface.
            // Whereas .build() configures the Retrofit instance, .create() takes that instance
            // and implements a specific interface with it to be used for actual API calls. Kotlin
            // can make an inference based on the return type of the enclosing function,
            // which is a StockApi as seen in providesStockApi() : StockApi(), to infer that the
            // return value should be of this builder should be a StockApi.
            .create()
    }

    /**
     * Returns a stock database for use in dependency injection. In order to create a Room database,
     * we need a context. But how do we retrieve a context in this instance? Simple: by passing it
     * in as an injected constructor variable. We can use the application as a constructor variable.
     * But how is this possible? Did we ever provide the application class?
     *
     * In fact, we did. We annotated our custom Application class with @HiltAndroidApp, which
     * triggered Hilt's code generation to set up dependency injection for the entire application.
     *
     * Furthermore, Hilt by default also generates several built-in bindings that are available
     * for injection without any additional configuration once you've declared an Application
     * a @HiltAndroidApplication, including Application, Context, and Activity/Fragment in their
     * respective scopes.
     *
     */

    @Provides
    @Singleton
    fun providesStockDatabase(app: Application) : StockDatabase {
        Log.d("HiltDebug", "StockDatabase provided")
        // In Android, a Context is an abstract class that can provide access to application level
        // resources. Application itself is actually a subclass of Context, so it is fine to
        // pass in Application alone as an argument. Note, this is the exact same thing as calling
        // applicationContext, which will return the Application instance of the lifecycle.
        // This makes calling app.applicationContext redundant. Note that a Context is really just
        // a bridge between the app code and the Android system; it provides the tools needed
        // for a developer to access resources, services, and perform system-level operations.
        return Room.databaseBuilder(
            app,
            StockDatabase::class.java,
            Constants.STOCK_DB
        ).build()
    }
}