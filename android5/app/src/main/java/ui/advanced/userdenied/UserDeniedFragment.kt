/*
 * This file is part of Blokada.
 *
 * Blokada is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Blokada is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Blokada.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright © 2020 Blocka AB. All rights reserved.
 *
 * @author Karol Gusak (karol@blocka.net)
 */

package ui.advanced.userdenied

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.blokada.R
import ui.StatsViewModel
import ui.app
import ui.stats.StatsFilterFragment
import ui.utils.getColorFromAttr

class UserDeniedFragment : Fragment() {

    private lateinit var vm: StatsViewModel

    private var allowed: Boolean = false

    private lateinit var searchGroup: ViewGroup

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        activity?.let {
            vm = ViewModelProvider(it.app()).get(StatsViewModel::class.java)
        }

        val root = inflater.inflate(R.layout.fragment_userdenied, container, false)

//        searchGroup = root.findViewById(R.id.activity_searchgroup)
//        searchGroup.visibility = View.GONE
//
//        val filter: ImageView = root.findViewById(R.id.activity_filter)
//        filter.setOnClickListener {
//            val fragment = StatsFilterFragment.newInstance()
//            fragment.show(parentFragmentManager, null)
//        }

        val adapter = UserDeniedAdapter(interaction = object : UserDeniedAdapter.Interaction {
            override fun onDelete(item: String) {
                if (allowed) vm.unallow(item)
                else vm.undeny(item)
            }
        })

        val manager = LinearLayoutManager(context)
        val recycler: RecyclerView = root.findViewById(R.id.activity_recyclerview)
        recycler.adapter = adapter
        recycler.layoutManager = manager

        val tabs: TabLayout = root.findViewById(R.id.activity_tabs)

        // Needed for dynamic translation
        tabs.getTabAt(0)?.text = getString(R.string.userdenied_tab_blocked)
        tabs.getTabAt(1)?.text = getString(R.string.userdenied_tab_allowed)

        if (allowed) tabs.selectTab(tabs.getTabAt(1))
        else tabs.selectTab(tabs.getTabAt(0))
        adapter.allowed = allowed

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab) {
                allowed = tab.position == 1
                adapter.allowed = allowed
                vm.refresh()
            }

        })

//        val updateTabsAndFilter = {
//            when (vm.getFilter()) {
//                StatsViewModel.Filter.ALLOWED -> {
//                    tabs.getTabAt(1)?.text = getString(R.string.activity_category_top_allowed)
//                    filter.setColorFilter(requireContext().getColorFromAttr(android.R.attr.colorPrimary))
//                }
//                StatsViewModel.Filter.BLOCKED -> {
//                    tabs.getTabAt(1)?.text = getString(R.string.activity_category_top_blocked)
//                    filter.setColorFilter(requireContext().getColorFromAttr(android.R.attr.colorPrimary))
//                }
//                else -> {
//                    tabs.getTabAt(1)?.text = getString(R.string.activity_category_top)
//                    filter.setColorFilter(null)
//                }
//            }
//        }

//        val search: SearchView = root.findViewById(R.id.activity_search)
//        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//
//            override fun onQueryTextSubmit(term: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(term: String): Boolean {
//                if (term.isNotBlank()) vm.search(term.trim())
//                else vm.search(null)
//                return true
//            }
//
//        })

        val empty: View = root.findViewById(R.id.activity_empty)

        vm.denied.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) empty.visibility = View.GONE
            if (!allowed) {
                adapter.swapData(it.sorted())
                lifecycleScope.launch {
                    delay(400) // Just Android things
                    recycler.scrollToTop()
                }
            }
        })

        vm.allowed.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) empty.visibility = View.GONE
            if (allowed) {
                adapter.swapData(it.sorted())
                lifecycleScope.launch {
                    delay(400) // Just Android things
                    recycler.scrollToTop()
                }
            }
        })

        return root
    }

    private fun RecyclerView.scrollToTop() {
        smoothScrollToPosition(0)
        //scrollToPositionWithOffset(0, 0)
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.stats_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.stats_search -> {
//                if (searchGroup.visibility == View.GONE) {
//                    searchGroup.visibility = View.VISIBLE
//                } else {
//                    searchGroup.visibility = View.GONE
//                }
//                true
//            }
//            else -> false
//        }
//    }
}
