package com.example.task_2.ui.fragments

import android.app.Dialog
import android.os.Bundle

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.task_2.R
import com.example.task_2.domain.model.User
import com.example.task_2.ui.activities.UserViewModel
import com.example.task_2.ui.contactAdapter.RecyclerViewAdapter
import com.example.task_2.utils.Constants
import com.google.android.material.textfield.TextInputEditText

class DialogFragment : AppCompatDialogFragment() {

    private var userViewModel = UserViewModel()
    fun setViewModel(userViewModel: UserViewModel) {
        this.userViewModel = userViewModel
    }

    private var adapter = RecyclerViewAdapter()
    fun setAdapter(recyclerViewAdapter: RecyclerViewAdapter) {
        adapter = recyclerViewAdapter
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_add_user, null)
        builder.setView(dialogView)
            .setPositiveButton(Constants.DIALOG_POSITIVE_BUTTON) { _, _ ->
                userViewModel.addUser(
                    User(
                        dialogView.findViewById<TextInputEditText>(R.id.textInputEditTextFullName).text.toString(),
                        dialogView.findViewById<TextInputEditText>(R.id.textInputEditTextCareer).text.toString(),
                        ""
                    ), userViewModel.getUserList().size
                )
                adapter.updateUsers(userViewModel.getUserList())
                adapter.notifyItemInserted(userViewModel.getUserList().size - 1)
            }.setNegativeButton(Constants.DIALOG_NEGATIVE_BUTTON) { _, _ ->
                dismiss()
            }
        return builder.create()
    }
}