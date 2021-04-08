package com.example.myhabits3.ui

import android.view.View
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.edit.KTextInputLayout
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView
import com.example.myhabits3.R
import org.hamcrest.Matcher

class AddScreen : Screen<AddScreen>() {

    val addButtonMain =KButton{withId(R.id.fabAddHabit)}

    val addOrEditButton = KView {withId(R.id.saveHabit)}

    val nameInput = KTextInputLayout{withId(R.id.habitNameInputLayout)}

    val descriptionInput = KTextInputLayout{ withId(R.id.habitDescriptionInputLayout)}

    val priorityInput = KTextInputLayout{ withId(R.id.habitPriorityInputLayout)}

    val periodInput = KTextInputLayout{withId(R.id.habitPeriodInputLayout)}

    val goodButton = KButton{withId(R.id.radioButtonGood)}

    val badButton = KButton{withId(R.id.radioButtonBad)}

    val countView = KEditText{withId(R.id.habitDoneAddEdit)}

    class Item(parent : Matcher<View>) : KRecyclerItem<Item>(parent){
        val title = KTextView(parent) {withId(R.id.habitNameRecyclerElement)}
        val description = KTextView(parent) {withId(R.id.habitDescriptionRecyclerElement)}
        val period = KTextView(parent) {withId(R.id.habitPeriodRecyclerElement)}
        val colorDivider = KView(parent) {withId(R.id.habitColorDivider)}
        val priority = KTextView(parent) {withId(R.id.habitPriorityRecyclerElement)}
        val doneButton = KButton(parent) {withId(R.id.buttonDone)}
    }

    val recycler = KRecyclerView({
        withId(R.id.recyclerHabits)
    }, itemTypeBuilder = {
        itemType(::Item)
    })

}