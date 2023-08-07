package com.example.task_2.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.task_2.R
import com.example.task_2.databinding.ActivityContactsBinding
import com.example.task_2.domain.model.User
import com.example.task_2.repository.UserItemClickListener
import com.example.task_2.ui.contactAdapter.RecyclerViewAdapter
import com.example.task_2.ui.fragments.DialogFragment
import com.example.task_2.utils.Constants
import com.example.task_2.utils.ext.animateVisibility
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), UserItemClickListener {

    private lateinit var binding: ActivityContactsBinding
    private lateinit var adapter: RecyclerViewAdapter

    private var userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecyclerViewAdapter()
        initialRecyclerview()
        showAddContactsDialog()
        setNavigationUpListeners()
    }

    private fun initialRecyclerview() {
        setTouchRecycleItemListener()
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val layoutManager = LinearLayoutManager(this)
        adapter.setUserItemClickListener(this)
        binding.recyclerViewContacts.layoutManager = layoutManager
        binding.recyclerViewContacts.adapter = adapter
        adapter.updateUsers(userViewModel.getUserList())
    }

    private fun showAddContactsDialog() {
        binding.textViewAddContacts.setOnClickListener {
            val dialogFragment = DialogFragment()
            dialogFragment.setViewModel(userViewModel)
            dialogFragment.setAdapter(adapter)
            dialogFragment.show(supportFragmentManager, Constants.DIALOG_TAG)
        }
    }

    private fun setNavigationUpListeners() {
        binding.imageViewNavigationUp.viewTreeObserver.addOnScrollChangedListener {
            checkForDisplayUpNavigationButton()
        }
        binding.imageViewNavigationUp.setOnClickListener {
            binding.recyclerViewContacts.smoothScrollToPosition(0)
        }
    }

    private fun checkForDisplayUpNavigationButton() {
        val visibleItemCount = binding.recyclerViewContacts.childCount
        val layoutManager = binding.recyclerViewContacts.layoutManager as LinearLayoutManager
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        binding.imageViewNavigationUp.animateVisibility(
            if (lastVisibleItemPosition >= visibleItemCount) View.VISIBLE else View.GONE
        )
    }

    private fun setTouchRecycleItemListener() {
        val itemTouchCallback = setTouchCallBackListener()
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(binding.recyclerViewContacts)
    }

    private fun setTouchCallBackListener(): ItemTouchHelper.Callback {
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteUserWithRestore(
                    userViewModel.getUserList()[viewHolder.adapterPosition],
                    viewHolder.adapterPosition
                )
            }
        }
    }

    override fun onUserDelete(user: User, position: Int) {
        deleteUserWithRestore(user, position)
    }

    fun deleteUserWithRestore(user: User, position: Int) {
        if (userViewModel.deleteUser(user)) {
            adapter.notifyItemRemoved(position)
            adapter.updateUsers(userViewModel.getUserList())
            Snackbar.make(
                binding.recyclerViewContacts,
                getString(R.string.s_has_been_removed).format(user.name),
                Snackbar.LENGTH_LONG
            )
                .setAction(getString(R.string.restore)) {
                    if (userViewModel.addUser(user, position)) {
                        adapter.notifyItemInserted(position)
                        adapter.updateUsers(userViewModel.getUserList())
                    }
                }.show()
        }
    }
}