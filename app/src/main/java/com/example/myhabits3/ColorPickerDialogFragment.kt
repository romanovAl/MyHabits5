package com.example.myhabits3

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.color_picker_dialog_fragment.*
import kotlinx.android.synthetic.main.color_picker_dialog_fragment.view.*

class ColorPickerDialogFragment : DialogFragment() {

    var color: Int = 0

    companion object {

        const val TAG = "SimpleDialog"
        const val DEFAULT_COLOR = 16

        fun newInstance(): ColorPickerDialogFragment {
            val args = Bundle()
            val fragment = ColorPickerDialogFragment()
            fragment.arguments = args
            return fragment
        }

    }

    interface IChangeColor{
        fun onChangeColor(newColor : Int)
    }

    lateinit var listOfColors: Array<Int>
    lateinit var listOfRgb: Array<String>
    lateinit var listOfHsv: Array<String>

    lateinit var iChangeColor: IChangeColor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        iChangeColor = activity as IChangeColor

        return inflater.inflate(R.layout.color_picker_dialog_fragment, container, false)
    }


    override fun onStart() {

        val dialog = dialog;
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT;
            val height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.window?.setLayout(width, height);
        }

        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val colorCardsList: MutableList<View> = mutableListOf()

        listOfColors = activity!!.resources.getIntArray(R.array.cardsColors).toTypedArray()
        listOfRgb = activity!!.resources.getStringArray(R.array.rgbs)
        listOfHsv = activity!!.resources.getStringArray(R.array.hsvs)

        choseColor(DEFAULT_COLOR)

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

        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun choseColor(colorNumber: Int) {

        chosenColorCard.setCardForegroundColor(ColorStateList.valueOf(listOfColors[colorNumber]))

        rgbColorTextView.text = "RGB - ${listOfRgb[colorNumber]}"

        hsvColorTextView.text = "HSV - ${listOfHsv[colorNumber]}"

        iChangeColor.onChangeColor(listOfColors[colorNumber])

    }

}