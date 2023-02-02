package com.fowlplay.parrot

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.fowlplay.parrot.model.ParrotDataStore
import com.fowlplay.parrot.model.ParrotDatabase
import com.fowlplay.parrot.model.ParrotModel
import com.fowlplay.parrot.viewmodel.ParrotViewModel
import dagger.*
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

@Component
interface MainComponent {
    fun activityViewModelComponentBuilder(): ActivityViewModelComponent.Builder
}

@ActivityScope
@Subcomponent(modules = [ActivityViewModelModule::class, ParrotModelModule::class, ContextModule::class])
interface ActivityViewModelComponent {

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun componentActivity(activity: ComponentActivity): Builder
        fun build(): ActivityViewModelComponent
    }

    fun inject(activity: ParrotActivity)
}


class ParrotApplication: Application() {

    val appComponent: MainComponent = DaggerMainComponent.create()

}

@Module
class ActivityViewModelModule {
    @Provides
    @ActivityScope
    fun provideParrotViewModel(activity: ComponentActivity, parrotModel: ParrotModel):
            ParrotViewModel {
        return ViewModelProvider(
            activity.viewModelStore,
            ParrotViewModelFactory(activity, parrotModel, activity.intent.extras)
        )[ParrotViewModel::class.java]
    }
}

class ParrotViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val parrotModel: ParrotModel,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String, modelClass: Class<T>, handle: SavedStateHandle
    ): T {
        return ParrotViewModel(
            parrotModel
        ) as T
    }
}

@Module
class ParrotModelModule {

    @Provides
    @ActivityScope
    fun provideParrotModel(context: Context): ParrotModel =
        ParrotModel(
            context,
            ParrotDatabase.getDatabase(context),
            ParrotDataStore(context),
            R.raw.db_2_2k
        )
}

@Module
class ContextModule {

    @Provides
    @ActivityScope
    fun provideContext(activity: ComponentActivity): Context = activity.applicationContext
}