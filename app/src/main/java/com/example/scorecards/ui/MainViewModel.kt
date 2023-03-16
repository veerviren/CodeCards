package com.example.scorecards.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.scorecards.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import zechs.codeforcesapi.data.model.User
import zechs.codeforcesapi.repository.CodeforcesRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CodeforcesRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<Resource<User>>(Resource.Loading())
    val userState: StateFlow<Resource<User>> = _userState

    private val _gradient = MutableStateFlow(GradientDrawable())
    val gradient: StateFlow<GradientDrawable> = _gradient

    fun getUser(handle: String) = viewModelScope.launch(Dispatchers.IO) {
        _userState.value = Resource.Loading()
        val response = repository.getUser(handle)
        _userState.value = when (response) {
            is zechs.codeforcesapi.utils.Resource.Error -> Resource.Error(response.message)
            is zechs.codeforcesapi.utils.Resource.Success -> Resource.Success(response.data)
        }
    }

    fun setImageDrawable(
        drawable: Drawable
    ) = viewModelScope.launch(Dispatchers.Default) {
        val bmp = (drawable as BitmapDrawable)
            .bitmap
            .copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palette ->
            val defaultColor = 0x000000
            val swatches = listOf(
                palette?.vibrantSwatch,
                palette?.darkVibrantSwatch,
                palette?.lightVibrantSwatch,
                palette?.mutedSwatch,
                palette?.darkMutedSwatch
            )
            val colorGradient = intArrayOf(
                Color.BLACK,
                *swatches.map { it?.rgb ?: defaultColor }.toIntArray()
            )
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                colorGradient
            )
            _gradient.value = gradientDrawable
        }
    }
}