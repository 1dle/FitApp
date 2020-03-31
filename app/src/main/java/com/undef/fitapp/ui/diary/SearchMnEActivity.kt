package com.undef.fitapp.ui.diary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.undef.fitapp.R
import com.undef.fitapp.models.Food
import com.undef.fitapp.ui.custom.MEListAdapter
import kotlinx.android.synthetic.main.activity_search_mn_e.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchMnEActivity : AppCompatActivity() {
    //Search Meal and Exercise Activity

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    private lateinit var viewModel: SearchMnEViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_mn_e)

        viewModel = ViewModelProviders.of(this).get(SearchMnEViewModel::class.java)


        viewModel.searchResults.observe(this, Observer { sr ->
            recyclerView = findViewById(R.id.rvResultME)
            viewManager = LinearLayoutManager(this)
            viewAdapter = MEListAdapter(sr)
            recyclerView.layoutManager = viewManager
            recyclerView.adapter = viewAdapter
        })

        btnSearchMnE.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getSearchResult(5, etSearchME.text.toString())
            }

        }


    }
}
