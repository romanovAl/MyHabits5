package com.example.myhabits3.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.res.ResourcesCompat
import com.example.myhabits3.ColorPickerDialogFragment
import com.example.myhabits3.model.Habit
import com.example.myhabits3.R
import com.example.myhabits3.model.Util
import kotlinx.android.synthetic.main.activity_add_and_edit.*

class AddAndEditActivity : AppCompatActivity(), ColorPickerDialogFragment.IChangeColor {

    private var curColor : Int = Util.intColors[16]!!
    private var curColorNumber = ColorPickerDialogFragment.DEFAULT_COLOR

    private lateinit var priorities : Array<String>
    private lateinit var periods : Array<String>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_edit)

        priorities = applicationContext.resources.getStringArray(R.array.priorities)
        periods = applicationContext.resources.getStringArray(R.array.periods)

        if(intent.extras == null){
            initAdding()
        }else{
            initEditing()
        }

    }

    private fun init(){

        val adapterPriority = ArrayAdapter(this, R.layout.list_item,priorities)
        (habitPriorityInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapterPriority)
        habitPriorityAddAndEdit.keyListener = null

        val adapterPeriod = ArrayAdapter(this, R.layout.list_item, periods)
        (habitPeriodInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapterPeriod)
        habitPeriodAddAndEdit.keyListener = null

        colorPickerButton.setOnClickListener {

            println(curColor)
            println(Util.getColorNumberByColor(curColor))

            ColorPickerDialogFragment.newInstance(Util.getColorNumberByColor(curColor))
                .show(supportFragmentManager, ColorPickerDialogFragment.TAG)


        }

    }

    private fun initAdding(){
        init()

        addAndEditFab.setOnClickListener {

            if(canAddOrEdit()){

                val hab = Habit(habitNameAddAndEdit.text.toString(),habitDescriptionAddAndEdit.text.toString(),
                    habitPriorityAddAndEdit.text.toString(), radioButtonGood.isActivated, habitPeriodAddAndEdit.text.toString(),
                    curColor, habitDoneAddEdit.text?.toString()?.toInt())


                println(curColor)
                println(Util.intColors[16])

                val intent = Intent(this, MainActivity::class.java).run {
                    putExtra("new", hab)
                }

                it.hideKeyboard()
                setResult(Activity.RESULT_OK, intent)
                finish()

            }

        }
    }

    private fun initEditing() {

        init()

        addAndEditFab.setImageResource(R.drawable.ic_baseline_check_24)

        val hab = intent.getSerializableExtra("object") as Habit
        val pos = intent.getIntExtra("position", 0) as Int

        habitPriorityAddAndEdit.setText(hab.priority)
        habitPeriodAddAndEdit.setText(hab.period)

        val adapterPriority = ArrayAdapter(this, R.layout.list_item, priorities)
        (habitPriorityInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapterPriority)

        val adapterPeriod = ArrayAdapter(this, R.layout.list_item, periods)
        (habitPeriodInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapterPeriod)

        habitNameAddAndEdit.setText(hab.name)
        habitDescriptionAddAndEdit.setText((hab.description))

        curColor = hab.color

        colorPickerButton.setTextColor(hab.color)

        if (hab.type) {
            radioButtonGood.isChecked = true
        } else {
            radioButtonBad.isChecked = true
        }
        habitDoneAddEdit.setText(hab.times?.toString())

        addAndEditFab.setImageResource(R.drawable.ic_baseline_check_24)

        addAndEditFab.setOnClickListener {

            if(canAddOrEdit()){
                val habit = Habit(
                    habitNameAddAndEdit.text.toString(),
                    habitDescriptionAddAndEdit.text.toString(),
                    habitPriorityAddAndEdit.text.toString(),
                    radioButtonGood.isChecked,
                    habitPeriodAddAndEdit.text.toString(),
                    curColor,
                    habitDoneAddEdit.text?.toString()?.toInt()
                )

                val intent = Intent(this, MainActivity::class.java).run {
                    putExtra("new", habit)
                    putExtra("position", pos)
                }

                it.hideKeyboard()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }


        }
    }

    private fun canAddOrEdit(): Boolean {
        var isAble = true
        if (habitNameAddAndEdit.text?.length?.compareTo(0) == 0) {
            habitNameInputLayout.error = resources.getString(R.string.Field_must_not_be_empty)
            isAble = false
        } else {
            habitNameInputLayout.error = ""
        }
        if (habitDescriptionAddAndEdit.text?.length?.compareTo(0) == 0) {
            habitDescriptionInputLayout.error =
                resources.getString(R.string.Field_must_not_be_empty)
            isAble = false
        } else {
            habitDescriptionInputLayout.error = ""
        }
        if (habitPeriodAddAndEdit.text?.length?.compareTo(0) == 0) {
            habitPeriodInputLayout.error = resources.getString(R.string.Field_must_not_be_empty)
            isAble = false
        } else {
            habitPeriodInputLayout.error = ""
        }
        return isAble
    }



    override fun onChangeColor(newColor: Int, newColorNumber : Int) {
        curColor = newColor
        curColorNumber = newColorNumber

        colorPickerButton.setTextColor(newColor)

        colorPickerButton.setTextColor(newColor)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}