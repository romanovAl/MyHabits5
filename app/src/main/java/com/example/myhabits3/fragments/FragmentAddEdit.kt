package com.example.myhabits3.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myhabits3.R
import com.example.myhabits3.data.FakeDatabase
import com.example.myhabits3.model.Habit
import com.example.myhabits3.model.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_add_edit.*
import java.util.*

class FragmentAddEdit : Fragment(R.layout.fragment_add_edit) {

    private var curColor: Int = Util.intColors[16]!!
    private var curColorNumber = ColorPickerDialogFragment.DEFAULT_COLOR

    private val priorities: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.priorities)
    }
    private val periods: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.periods)
    }
    private val habitToEdit: Habit? by lazy {
        FragmentAddEditArgs.fromBundle(requireArguments()).habitToEdit
    }
    private val navController: NavController by lazy {
        Navigation.findNavController(requireView())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.are_you_sure_you_want_to_exit)
                .setMessage(R.string.habit_will_not_be_saved)
                .setPositiveButton(R.string.yes) { _, _ ->
                    navController.popBackStack()
                }
                .setNegativeButton(R.string.no) { _, _ -> }
                .setCancelable(true)
                .show()
        }



        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.saveHabit -> {
            saveHabit()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putInt(COLOR_CONFIG_CHANGE_CODE, curColor)
        outState.putInt(COLOR_NUM_CONFIG_CHANGE_CODE, curColorNumber)

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {

        savedInstanceState?.let { it ->
            curColor = it.getInt(COLOR_CONFIG_CHANGE_CODE)
            curColorNumber = it.getInt(COLOR_NUM_CONFIG_CHANGE_CODE)
            curColorImageView.setColorFilter(curColor)
        }

        super.onViewStateRestored(savedInstanceState)
    }


    private fun saveHabit() {
        if (canAddOrEdit()) {

            val newPriority = if (habitPriorityAddAndEdit.text.toString().isNotEmpty()) {
                priorities.indexOf(habitPriorityAddAndEdit.text.toString())
            } else {
                0
            }
            val newCount = if (habitDoneAddEdit.text.toString() == "") {
                0
            } else {
                habitDoneAddEdit.text.toString().toInt()
            }
            val newFrequency = if (habitPeriodAddAndEdit.text.toString().isNotEmpty()) {
                periods.indexOf(habitPeriodAddAndEdit.text.toString())
            } else {
                0
            }
            val newDate = if (habitToEdit != null) {
                habitToEdit!!.date
            } else {
                Calendar.getInstance().time.time
            }

            val newHabit = Habit(
                title = habitNameAddAndEdit.text.toString(),
                description = habitDescriptionAddAndEdit.text.toString(),
                priority = newPriority,
                type = radioButtonGood.isChecked.toInt(),
                count = newCount,
                frequency = newFrequency,
                color = curColor,
                date = newDate,
                doneDates = mutableListOf()
            )

            if (habitToEdit != null) {
                FakeDatabase.replaceHabit(habitToEdit!!, newHabit)
            } else {
                FakeDatabase.addHabit(newHabit)
            }

            requireView().hideKeyboard()

            navController.popBackStack()
        }
    }

    private fun canAddOrEdit(): Boolean {
        var isAble = true
        if (habitNameAddAndEdit.text?.isEmpty() == true) {
            habitNameInputLayout.error = resources.getString(R.string.Field_must_not_be_empty)
            isAble = false
        } else {
            habitNameInputLayout.error = ""
        }
        if (habitDescriptionAddAndEdit.text?.isEmpty() == true) {
            habitDescriptionInputLayout.error =
                resources.getString(R.string.Field_must_not_be_empty)
            isAble = false
        } else {
            habitDescriptionInputLayout.error = ""
        }
        if (habitPeriodAddAndEdit.text?.isEmpty() == true) {
            habitPeriodInputLayout.error = resources.getString(R.string.Field_must_not_be_empty)
            isAble = false
        } else {
            habitPeriodInputLayout.error = ""
        }
        return isAble
    }


    private fun init() {

        habitToEdit?.let{
            habitNameAddAndEdit.setText(it.title)
            habitDescriptionAddAndEdit.setText((it.description))

            if (it.type.toBoolean()) {
                radioButtonGood.isChecked = true
            } else {
                radioButtonBad.isChecked = true
            }

            habitDoneAddEdit.setText(it.count.toString())

            habitPriorityAddAndEdit.setText(priorities[it.priority])
            habitPeriodAddAndEdit.setText(periods[it.frequency])

            curColor = it.color

            curColorImageView.setColorFilter(it.color)
        }


        val adapterPriority = ArrayAdapter(requireContext(), R.layout.list_item, priorities)
        (habitPriorityInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapterPriority)
        habitPriorityAddAndEdit.keyListener = null

        val adapterPeriod = ArrayAdapter(requireContext(), R.layout.list_item, periods)
        (habitPeriodInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapterPeriod)
        habitPeriodAddAndEdit.keyListener = null

        colorPickerButton.setOnClickListener {
            colorPickersOnClick()
        }
        curColorImageView.setOnClickListener {
            colorPickersOnClick()
        }

        curColorImageView.setColorFilter(curColor)
    }

    private fun colorPickersOnClick() {

        ColorPickerDialogFragment.newInstance(Util.getColorNumberByColor(curColor)) { newColor, newColorNumber ->

            curColor = newColor
            curColorNumber = newColorNumber

            curColorImageView.setColorFilter(newColor)
        }
            .show(parentFragmentManager, ColorPickerDialogFragment.TAG)

    }

    private fun Boolean.toInt() = if (this) 1 else 0

    private fun Int.toBoolean(): Boolean = this == 1


    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    companion object {
        const val COLOR_CONFIG_CHANGE_CODE = "current color"
        const val COLOR_NUM_CONFIG_CHANGE_CODE = "current color number"
    }

}