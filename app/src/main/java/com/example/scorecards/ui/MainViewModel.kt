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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import zechs.codeforcesapi.data.model.Contest
import zechs.codeforcesapi.data.model.UserRating
import zechs.codeforcesapi.data.model.User
import zechs.codeforcesapi.repository.CodeforcesRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CodeforcesRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<Resource<User>>(Resource.Loading())
    val userState: StateFlow<Resource<User>> = _userState

    private val _contestState = MutableStateFlow<Resource<List<Contest>>>(Resource.Loading())
    val contestState: StateFlow<Resource<List<Contest>>> = _contestState

    private val _ratedUserState = MutableStateFlow<Resource<List<UserRating>>>(Resource.Loading())
    val ratedUserState: StateFlow<Resource<List<UserRating>>> = _ratedUserState


    private val friendList = mutableListOf<Friend>()

    private val _friends = MutableStateFlow<List<Friend>>(friendList.toList())
    val friends: StateFlow<List<Friend>> = _friends

    private val _gradient = MutableStateFlow(GradientDrawable())
    val gradient: StateFlow<GradientDrawable> = _gradient

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userUid = currentUser?.uid
    private val friendsRef: DatabaseReference? = userUid?.let {
        database.getReference("users").child(it).child("friends")
    }

    fun getUser(handle: String) = viewModelScope.launch(Dispatchers.IO) {
        _userState.value = Resource.Loading()
        val response = repository.getUser(handle)
        _userState.value = when (response) {
            is zechs.codeforcesapi.utils.Resource.Error -> Resource.Error(response.message)
            is zechs.codeforcesapi.utils.Resource.Success -> Resource.Success(response.data)
        }
    }

    fun getContest() = viewModelScope.launch ( Dispatchers.IO) {
        _contestState.value = Resource.Loading()
        val response = repository.getContests()
        _contestState.value = when(response){
            is zechs.codeforcesapi.utils.Resource.Error -> Resource.Error(response.message)
            is zechs.codeforcesapi.utils.Resource.Success -> Resource.Success(response.data)
        }
    }

    fun getUserRating(handle: String) = viewModelScope.launch ( Dispatchers.IO) {
        _ratedUserState.value = Resource.Loading()
        val response = repository.getUserRating(handle)
        _ratedUserState.value = when(response){
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

//    fun addFriend(handle: String) = viewModelScope.launch(Dispatchers.IO) {
//        if (friendList.any { it.friendHandle == handle }) return@launch
//        val response = repository.getUser(handle)
//        if (response  is zechs.codeforcesapi.utils.Resource.Success) {
//                val user = response.data
//                val friend = Friend(
//                    friendHandle = user.handle,
//                    friendRating = user.rating.toString(),
//                    friendAvatar = user.avatar
//                )
//                println("HEREEEEEE = ${friend}")
//                friendList.add(friend)
//                _friends.value = friendList.toList()
//            }
//    }

    fun addFriend(handle: String) = viewModelScope.launch(Dispatchers.IO) {
        val response = repository.getUser(handle)
        if (response is zechs.codeforcesapi.utils.Resource.Success) {
            val user = response.data

            val friendUid = user.handle
            val friendData = mapOf(
                "friendHandle" to user.handle,
                "friendRating" to user.rating.toString(),
                "friendAvatar" to user.avatar
            )
            friendsRef?.child(friendUid)?.setValue(friendData)
        }
    }

//    fun removeFriend(friend: Friend) {
//        friendList.remove(friend)
//        _friends.value = friendList.toList()
//    }

    fun removeFriend(handle: String) = viewModelScope.launch(Dispatchers.IO) {
            friendsRef?.child(handle)?.removeValue()
    }
}