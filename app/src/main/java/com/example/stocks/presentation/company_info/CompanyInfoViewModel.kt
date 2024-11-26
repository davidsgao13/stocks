package com.example.stocks.presentation.company_info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocks.domain.repository.StockRepository
import com.example.stocks.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    /**
     * @savedStateHandle allows us to retrieve navigation arguments without having to manually pass
     * them to the ViewModel from the UI. We need the symbol from the Listings screen for this
     * ViewModel in order to make a UI request for the company info.
     */
    private val savedStateHandle: SavedStateHandle,
    private val stockRepository: StockRepository
) : ViewModel() {

    private var _state = MutableStateFlow(CompanyInfoState())
    val state = _state.asStateFlow()

    private fun updateState(update: CompanyInfoState.() -> CompanyInfoState) {
        _state.value = _state.value.update()
    }

    init {
        viewModelScope.launch {
            // In the event that "symbol" is a null value, we return@launch to short circuit the
            // coroutine and finish its operation. We use return@launch since we're already inside
            // a lambda in init { } so we need to specify the function we're returning from.
            // This is called a labeled return
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            updateState {
                copy(isLoading = true)
            }
            /**
             * We are making two network calls, one after the other. It's a common coroutine mistake
             * because instead of calling them both simultaneously, we call them in sequence. We can
             * make this operation twice as quickly if we call them both at the same time. By adding
             * an async { } block, we're essentially stating that the network call inside the async
             * block is going to occur outside the scope of the parent coroutine (viewModelScope).
             * We simply add a when expression with an await to return the values.
             */
            val companyInfoResult = async { stockRepository.getCompanyInfo(symbol) }
            val intradayInfoResult = async { stockRepository.getIntradayInfo(symbol) }
            /**
             * Syntactically, companyInfoResult is similar to a "promise" that something will be
             * done in the future. The .await() part means: "pause here until the task is finished,
             * then give me the result when you're done."
             *
             * val result = companyInfoResult.await() will give back some information when the
             * operation is done and .await() has returned something. The when conditional acts
             * as a switchboard. Once the operation has finished, it looks inside the result and
             * chooses the appropriate action based on the content. This is because in Kotlin,
             * assignment is also an expression, meaning they also produce a value. For example,
             * x = 5 would both assign x to 5 and return an evaluation.
             *
             * Remember this paradigm, it's very important.
             */
            when (val result = companyInfoResult.await()) {
                is Resource.Success -> {
                    // If we get a Success Resource, we should update the state of our
                    // view model and assign company to the result, set isLoading to false,
                    // and set our error message to null
                    updateState {
                        copy(
                            company = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    // Otherwise, if we get an Error Resource, we should update the state of our
                    // view model, assign company to null, set isLoading to false, and set our
                    // error message to the message of the Error result
                    updateState {
                        copy(
                            isLoading = false,
                            error = result.message,
                            company = null
                        )
                    }
                }
                else -> Unit
            }
            when (val result = intradayInfoResult.await()) {
                is Resource.Success -> {
                    // We set stockInfos either to result.data or an emptyList(), as stockInfos
                    // is non-nullable
                    updateState {
                        copy(
                            stockInfos = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    updateState {
                        copy(
                            isLoading = false,
                            error = result.message,
                            company = null
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}