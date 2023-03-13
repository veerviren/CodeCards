package com.example.scorecards.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scorecards.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import zechs.codeforcesapi.data.model.User
import zechs.codeforcesapi.repository.CodeforcesRepository
import java.util.logging.Handler
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CodeforcesRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<Resource<User>>(Resource.Loading())
    val userState: StateFlow<Resource<User>> = _userState

    fun getUser(handle: String) = viewModelScope.launch(Dispatchers.IO) {
        _userState.value = Resource.Loading()
        val response = repository.getUser(handle)
        _userState.value = when (response) {
            is zechs.codeforcesapi.utils.Resource.Error -> Resource.Error(response.message)
            is zechs.codeforcesapi.utils.Resource.Success -> Resource.Success(response.data)
        }
    }
}