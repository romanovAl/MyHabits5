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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myhabits3.viewModels.MainViewModel
import com.example.myhabits3.R
import com.example.myhabits3.adapters.ViewPagerAdapter
import com.example.myhabits3.utils.FilterTypes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.bottom_sheet_main_fragment.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragment : Fragment(R.layout.fragment_main) {

    private val navController: NavController by lazy {
        Navigation.findNavController(requireView())
    }

    private val viewModel: MainViewModel by activityViewModels()

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

        val behavior = BottomSheetBehavior.from(bottomSheetMainFragment)
        if (behavior.state != BottomSheetBehavior.STATE_COLLAPSED) {
            fabAddHabit.animate().scaleX(0F).scaleY(0f)
                .setDuration(0).start()
        }

        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.api_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.uploadToServer -> {
                viewModel.insertHabitsIntoApi()
                true
            }
            else -> {
                viewModel.downloadHabitsFromApi()
                true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewPager2.adapter = ViewPagerAdapter(this)
        val tabNames =
            listOf(getString(R.string.view_pager_good), getString(R.string.view_pager_bad))

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = tabNames[position]
        }.attach()

        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->

            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                mainFragmentConstraint.visibility = View.INVISIBLE
            } else {
                progressBar.visibility = View.INVISIBLE
                mainFragmentConstraint.visibility = View.VISIBLE
            }

        })

        fabAddHabit.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.cleanHabitsFilter()
            }

            val action =
                MainFragmentDirections.actionFragmentMainToFragmentAddEdit(getString(R.string.label_add))
            navController.navigate(action)
        }


        filterFind.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                if (text != null) {
                    viewModel.sortHabits(text.toString())
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.cleanHabitsFilter()
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
                    viewModel.sortHabits(FilterTypes.ByPriority, true)
                }

                filterTypes[1] -> { //По периоду
                    viewModel.sortHabits(FilterTypes.ByPeriod, true)
                }

                filterTypes[2] -> { //По количеству раз
                    viewModel.sortHabits(FilterTypes.ByCount, true)
                }

                filterTypes[3] -> {//По дате
                    viewModel.sortHabits(FilterTypes.ByDate, true)
                }

            }
        }

        filterSortDown.setOnClickListener {

            filterSortDown.setImageResource(R.drawable.ic_baseline_arrow_downward_24_activated)
            filterSortUp.setImageResource(R.drawable.ic_baseline_arrow_upward_24)

            when (filterTypeSpinner.text.toString()) {

                filterTypes[0] -> { //По приоритету
                    viewModel.sortHabits(FilterTypes.ByPriority, false)
                }

                filterTypes[1] -> { //По периоду
                    viewModel.sortHabits(FilterTypes.ByPeriod, false)
                }

                filterTypes[2] -> { //По количеству раз
                    viewModel.sortHabits(FilterTypes.ByCount, false)
                }

                filterTypes[3] -> {//По дате
                    viewModel.sortHabits(FilterTypes.ByDate, false)
                }

            }
        }

        filterTypeSpinner.onItemClickListener =
            AdapterView.OnItemClickListener() { _, _, position, _ ->

                if (position == 4) { //Без фильтра

                    filterSortUp.setImageResource(R.drawable.ic_baseline_arrow_upward_24)
                    filterSortDown.setImageResource(R.drawable.ic_baseline_arrow_downward_24)

                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.cleanHabitsFilter()
                    }

                }

            }

        val behavior = BottomSheetBehavior.from(bottomSheetMainFragment)


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
            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
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