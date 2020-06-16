package com.abhijai.powerfulandroidapp.ui.main.fragments.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.abhijai.powerfulandroidapp.R
import com.abhijai.powerfulandroidapp.models.AccountPropertiesModel
import com.abhijai.powerfulandroidapp.session.SessionManager
import com.abhijai.powerfulandroidapp.ui.Data
import com.abhijai.powerfulandroidapp.ui.DataState
import com.abhijai.powerfulandroidapp.ui.Event
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.state.AccountStateEvent
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.state.AccountViewState
import kotlinx.android.synthetic.main.fragment_account.*
import javax.inject.Inject

class AccountFragment : BaseAccountFragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        change_password.setOnClickListener {
            Log.d("AccountFragment", "onViewCreated (line 30): CHANGE -> ${findNavController().currentDestination!!.id}")
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        logout_button.setOnClickListener {
            accountViewModel.logout()
        }

        accountViewModel.setStateEvent(AccountStateEvent.GetAccountPropertiesEvent())
        subscribeObservers()
    }

    //observe the dataState and then observe the viewState
    private fun subscribeObservers(){
        accountViewModel.dataState.observe(viewLifecycleOwner, Observer {it: DataState<AccountViewState> ->
            Log.d("AccountFragment", "subscribeObservers Hash : ${it.hashCode()}")
            stateChangeListener.onDataStateChange(it)
            it?.let {dataState:DataState<AccountViewState>->
                dataState.data?.let {data: Data<AccountViewState>->
                    data.data?.let {event : Event<AccountViewState> ->
                        event.getContentIfNotHandled()?.let {accountViewState : AccountViewState->
                            accountViewState.accountPropertiesModel?.let {accountProperties:AccountPropertiesModel->
                                Log.d("AccountFragment", "subscribeObservers (line 50): $accountProperties")
                                accountViewModel.setAccountPropertiesData(accountProperties)
                            }
                        }
                    }
                }
            }
        })

        accountViewModel.viewState.observe(viewLifecycleOwner, Observer {accountViewState : AccountViewState ->
            accountViewState?.let {
                it.accountPropertiesModel?.let {
                    setAccountDataFields(it)
                }
            }
        })
    }

    private fun setAccountDataFields(accountPropertiesModel: AccountPropertiesModel){
        email?.setText(accountPropertiesModel.email)
        username?.setText(accountPropertiesModel.username)
    }

    override fun onResume() {
        super.onResume()
        //accountViewModel.setStateEvent(AccountStateEvent.GetAccountPropertiesEvent())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_view_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit -> {
                Log.d("AccountFragment", "onViewCreated (line 47): EDIT -> ${findNavController().currentDestination!!.id}")
                //if (findNavController().currentDestination?.id == R.id.nav_account)
                findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}