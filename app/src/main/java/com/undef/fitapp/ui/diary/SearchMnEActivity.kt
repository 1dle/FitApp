package com.undef.fitapp.ui.diary

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.undef.fitapp.R
import com.undef.fitapp.ui.custom.MEListAdapter
import kotlinx.android.synthetic.main.activity_search_mn_e.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchMnEActivity : AppCompatActivity() {
    //Search Meal and Exercise Activity

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    private lateinit var viewModel: SearchMnEViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_mn_e)
        val toolbars: Toolbar = findViewById(R.id.toolbarOnSearch)
        setSupportActionBar(toolbars)
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }



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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
