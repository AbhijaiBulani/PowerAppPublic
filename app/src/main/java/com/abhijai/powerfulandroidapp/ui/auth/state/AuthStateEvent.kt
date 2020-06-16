package com.abhijai.powerfulandroidapp.ui.auth.state

sealed class AuthStateEvent
{
    data class LoginAttemptEvent(
        var email:String,
        var password : String
    ):AuthStateEvent()

    data class RegisterAttemptEvent(
        var email : String,
        var userName:String,
        var password:String,
        var confirmPassword:String
    ):AuthStateEvent()

    class CheckPreviousAuthEvent:AuthStateEvent()
}