package com.example.myhabits3

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myhabits3.model.Util
import kotlinx.android.synthetic.main.color_picker_dialog_fragment.*
import kotlinx.android.synthetic.main.color_picker_dialog_fragment.view.*

class ColorPickerDialogFragment : DialogFragment() {

    var color: Int = 0

    companion object {

        const val TAG = "ColorPicker"
        const val COLOR_NUMBER_BUNDLE_ARG = "curColorNumber"
        const val DEFAULT_COLOR = 16
        const val VISIBLE = View.VISIBLE
        const val INVISIBLE = View.INVISIBLE


        fun newInstance(curColorNumber: Int): ColorPickerDialogFragment {

            val args = Bundle().apply {
                putInt(COLOR_NUMBER_BUNDLE_ARG, curColorNumber)
            }
            val fragment = ColorPickerDialogFragment()
            fragment.arguments = args
            return fragment
        }

    }

    interface IChangeColor {
        fun onChangeColor(newColor: Int, newColorNumber: Int)
    }

    private lateinit var listOfRgb: Array<String>
    private lateinit var listOfHsv: Array<String>

    private var colors = Util.intColors

    private lateinit var iChangeColor: IChangeColor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        iChangeColor = activity as IChangeColor

        return inflater.inflate(R.layout.color_picker_dialog_fragment, container, false)
    }


    override fun onStart() {

        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window?.setLayout(width, height)
        }


        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        listOfRgb = activity!!.resources.getStringArray(R.array.rgbs)
        listOfHsv = activity!!.resources.getStringArray(R.array.hsvs)

        val curColorNumber = arguments!!.getInt(COLOR_NUMBER_BUNDLE_ARG)

        if (curColorNumber != DEFAULT_COLOR) {
            choseColor(curColorNumber)
        } else {
            choseColor(DEFAULT_COLOR)
        }

        defaultColorButton.setOnClickListener {
            choseColor(DEFAULT_COLOR)
        }

        view.apply {
            colorCard1.setOnClickListener {
                choseColor(0)
            }
            colorCard2.setOnClickListener {
                choseColor(1)
            }
            colorCard3.setOnClickListener {
                choseColor(2)
            }
            colorCard4.setOnClickListener {
                choseColor(3)
            }
            colorCard5.setOnClickListener {
                choseColor(4)
            }
            colorCard6.setOnClickListener {
                choseColor(5)
            }
            colorCard7.setOnClickListener {
                choseColor(6)
            }
            colorCard8.setOnClickListener {
                choseColor(7)
            }
            colorCard9.setOnClickListener {
                choseColor(8)
            }
            colorCard10.setOnClickListener {
                choseColor(9)
            }
            colorCard11.setOnClickListener {
                choseColor(10)
            }
            colorCard12.setOnClickListener {
                choseColor(11)
            }
            colorCard13.setOnClickListener {
                choseColor(12)
            }
            colorCard14.setOnClickListener {
                choseColor(13)
            }
            colorCard15.setOnClickListener {
                choseColor(14)
            }
            colorCard16.setOnClickListener {
                choseColor(15)
            }

            applyColorButton.setOnClickListener {
                iChangeColor.onChangeColor(
                    curColorForCard,
                    this@ColorPickerDialogFragment.curColorNumber
                )
                dismiss()
            }

        }

        super.onViewCreated(view, savedInstanceState)
    }

    private var curColorForCard = colors[DEFAULT_COLOR]!!
    private var curColorNumber = DEFAULT_COLOR

    private fun choseColor(colorNumber: Int) {

        curColorForCard = colors[colorNumber]!!
        curColorNumber = colorNumber

        chosenColorCard.setCardForegroundColor(ColorStateList.valueOf(curColorForCard))

        rgbColorTextView.text =
            requireContext().getString(R.string.RGB,listOfRgb[colorNumber])

        hsvColorTextView.text =
            requireContext().getString(R.string.HSV,listOfHsv[colorNumber])

        val checkedColors = arrayOf(checkedColor1, checkedColor2, checkedColor3,
            checkedColor4, checkedColor5, checkedColor6, checkedColor7,
            checkedColor8, checkedColor9, checkedColor10, checkedColor11,
            checkedColor12, checkedColor13, checkedColor14, checkedColor15,
            checkedColor16,
        )

        checkedColors.forEach { it.visibility = INVISIBLE }
        if (colorNumber != 16) {
            checkedColors[colorNumber].visibility = VISIBLE
        }

        when (colorNumber) {
            0 -> checkedColor1.visibility = VISIBLE
            1 -> checkedColor2.visibility = VISIBLE
            2 -> checkedColor3.visibility = VISIBLE
            3 -> checkedColor4.visibility = VISIBLE
            4 -> checkedColor5.visibility = VISIBLE
            5 -> checkedColor6.visibility = VISIBLE
            6 -> checkedColor7.visibility = VISIBLE
            7 -> checkedColor8.visibility = VISIBLE
            8 -> checkedColor9.visibility = VISIBLE
            9 -> checkedColor10.visibility = VISIBLE
            10 -> checkedColor11.visibility = VISIBLE
            11 -> checkedColor12.visibility = VISIBLE
            12 -> checkedColor13.visibility = VISIBLE
            13 -> checkedColor14.visibility = VISIBLE
            14 -> checkedColor15.visibility = VISIBLE
            15 -> checkedColor16.visibility = VISIBLE
        }

    }

}