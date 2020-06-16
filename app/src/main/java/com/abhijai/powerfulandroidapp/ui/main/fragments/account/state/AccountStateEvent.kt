package com.abhijai.powerfulandroidapp.ui.main.fragments.account.state

sealed class AccountStateEvent {

    class GetAccountPropertiesEvent():AccountStateEvent()

    /**
     * Q -> Why this is data class ?
     * Ans -> Because we want parameters to update them to the server
     * if user wants.
     */
    data class UpdateAccountPropertiesEvent(
        var email : String,
        var userName : String
    ) : AccountStateEvent()


    data class ChangeAccountPropertiesEvent(
        var currentPassword : String,
        var newPassword : String,
        var confirmPassword : String
    ): AccountStateEvent()

    /**
     * it can be used to resetting things,
     * sometimes this is nice to have.
     */
    class None : AccountStateEvent()

}