package com.example.myhabits3.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myhabits3.viewModels.MainViewModel
import com.example.myhabits3.R
import com.example.myhabits3.adapters.ViewPagerAdapter
import com.example.myhabits3.model.Habit
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.bottom_sheet_main_fragment.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main) {

    private val navController: NavController by lazy {
        Navigation.findNavController(requireView())
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, defaultViewModelProviderFactory).get(MainViewModel::class.java)
    }

    private val filterTypes by lazy {
        resources.getStringArray(R.array.filterTypes)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        val adapterFilterTypes = context?.let { ArrayAdapter(it, R.layout.list_item, filterTypes) }
        (filterInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapterFilterTypes)
        filterTypeSpinner.keyListener = null

        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.findAndSort) {
            if (bottomSheetMainFragment != null) {
                val behavior = BottomSheetBehavior.from(bottomSheetMainFragment)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return true
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewPager2.adapter = ViewPagerAdapter(this)
        val tabNames =
            listOf(getString(R.string.view_pager_good), getString(R.string.view_pager_bad))

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = tabNames[position]
        }.attach()

        fabAddHabit.setOnClickListener {
            val action =
                MainFragmentDirections.actionFragmentMainToFragmentAddEdit(getString(R.string.label_add))
            navController.navigate(action)
        }


        var sortedHabits: List<Habit>? = null
        var currentSortedHabits: List<Habit>? = null

        viewModel.habits.observe(viewLifecycleOwner, {
            sortedHabits = it
            currentSortedHabits = it
        })

        filterFind.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {

                if (text != null) {

                    if (text.isEmpty()) {
                        currentSortedHabits = sortedHabits

                        if (currentSortedHabits != null) {
                            viewModel.setSortedHabits(currentSortedHabits!!)
                        }
                    } else {
                        currentSortedHabits = sortedHabits?.filter {
                            it.title.contains(text.toString(), ignoreCase = true)
                        }

                        if (currentSortedHabits != null) {
                            viewModel.setSortedHabits(currentSortedHabits!!)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        filterSortUp.setOnClickListener { //Сортировка вверх

            filterSortUp.setImageResource(R.drawable.ic_baseline_arrow_upward_24_activated)
            filterSortDown.setImageResource(R.drawable.ic_baseline_arrow_downward_24)

            when (filterTypeSpinner.text.toString()) {

                filterTypes[0] -> { //По приоритету

                    currentSortedHabits = currentSortedHabits?.sortedByDescending { it.priority }
                    if (currentSortedHabits != null) {
                        viewModel.setSortedHabits(currentSortedHabits!!)
                    }

                }

                filterTypes[1] -> { //По периоду
                    currentSortedHabits = currentSortedHabits?.sortedByDescending { it.frequency }

                    if (currentSortedHabits != null) {
                        viewModel.setSortedHabits(currentSortedHabits!!)
                    }
                }

                filterTypes[2] -> { //По количеству раз
                    currentSortedHabits = currentSortedHabits?.sortedByDescending { it.count }

                    if (currentSortedHabits != null) {
                        viewModel.setSortedHabits(currentSortedHabits!!)
                    }

                }

                filterTypes[3] -> {//По дате
                    currentSortedHabits = currentSortedHabits?.sortedByDescending { it.date }

                    if (currentSortedHabits != null) {
                        viewModel.setSortedHabits(currentSortedHabits!!)
                    }
                }

            }
        }

        filterSortDown.setOnClickListener {

            filterSortDown.setImageResource(R.drawable.ic_baseline_arrow_downward_24_activated)
            filterSortUp.setImageResource(R.drawable.ic_baseline_arrow_upward_24)

            when (filterTypeSpinner.text.toString()) {

                filterTypes[0] -> { //По приоритету

                    currentSortedHabits = currentSortedHabits?.sortedBy { it.priority }

                    if (currentSortedHabits != null) {
                        viewModel.setSortedHabits(currentSortedHabits!!)
                    }

                }

                filterTypes[1] -> { //По периоду
                    currentSortedHabits = currentSortedHabits?.sortedBy { it.frequency }
                    if (currentSortedHabits != null) {
                        viewModel.setSortedHabits(currentSortedHabits!!)
                    }
                }

                filterTypes[2] -> { //По количеству раз
                    currentSortedHabits = currentSortedHabits?.sortedBy { it.count }
                    if (currentSortedHabits != null) {
                        viewModel.setSortedHabits(currentSortedHabits!!)
                    }
                }

                filterTypes[3] -> {//По дате
                    currentSortedHabits = currentSortedHabits?.sortedBy { it.date }
                    if (currentSortedHabits != null) {
                        viewModel.setSortedHabits(currentSortedHabits!!)
                    }
                }

            }
        }

        filterTypeSpinner.onItemClickListener = AdapterView.OnItemClickListener()
        { _, _, p, _ ->

            if (p == 4) {

                filterSortUp.setImageResource(R.drawable.ic_baseline_arrow_upward_24)
                filterSortDown.setImageResource(R.drawable.ic_baseline_arrow_downward_24)


                viewModel.setSortedHabits(emptyList())
                //TODO сделать поле неактивным

            }

        }

        val behavior = BottomSheetBehavior.from(bottomSheetMainFragment)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        behavior.peekHeight = 0


        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                fabAddHabit.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset)
                    .setDuration(0).start()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheet.hideKeyboard()
                }
                fabAddHabit?.isClickable = newState == BottomSheetBehavior.STATE_COLLAPSED
            }
        })

        bottomSheetTitleCard.setOnClickListener {
            if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }


        super.onViewCreated(view, savedInstanceState)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}