package com.example.stocks.presentation.company_info

import com.example.stocks.domain.model.CompanyInfo
import com.example.stocks.domain.model.IntradayInfo

/**
 *  * The State for our CompanyInfoViewModel; it represents everything that belongs to the
 *  * ViewModel on our CompanyInfoScreen.
 *
 *  @stockInfos is a list of IntradayInfo that we'll use to populate our chart graphic
 *  @company is the current company we're examining in the info screen
 *  @isLoading tells us whether or not the data is loading
 *  @error is the error we'll show in the event one occurs while trying to load the screen
 */

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)