package com.undef.fitapp.ui.diary

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
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
import com.undef.fitapp.models.Food
import com.undef.fitapp.ui.custom.MEListAdapter
import com.undef.fitapp.ui.custom.MEListAdapter.OnMEListItemClickListener
import kotlinx.android.synthetic.main.activity_search_mn_e.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchMnEActivity() : AppCompatActivity(), OnMEListItemClickListener {
    //Search Meal and Exercise Activity

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    private lateinit var viewModel: SearchMnEViewModel

    constructor(parcel: Parcel) : this() {

    }

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
            viewAdapter = MEListAdapter(sr, this)
            recyclerView.layoutManager = viewManager
            recyclerView.adapter = viewAdapter
        })

        btnSearchMnE.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getSearchResult(5, etSearchME.text.toString())
            }

        }


    }

    override fun onMEListItemClick(position: Int) {
        if(viewModel.searchResults.value!= null){
            val f = viewModel.searchResults.value!![position]
            Toast.makeText(this, f.name, Toast.LENGTH_SHORT).show()

/*
            val intent = Intent(this, EditMealActivity::class.java)
            startActivity(intent)*/
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
