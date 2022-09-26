package com.example.registrationscreen

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.registrationscreen.data.News
import com.example.registrationscreen.databinding.ActivityMainBinding
import com.example.registrationscreen.utils.RetrofitInstance
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), NewsItemClicked {
    private lateinit var mAdapter : NewsListAdapter
    private lateinit var auth : FirebaseAuth
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        fetchDataRetrofit()
        mAdapter = NewsListAdapter(this)
        binding.recyclerView.adapter = mAdapter

//        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                Toast.makeText(this@MainActivity, "You Selected ${adapterView?.getItemAtPosition(position).toString()}", Toast.LENGTH_LONG).show()
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//
//            }
//        }

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
//        binding.logout.setOnClickListener {
//            LoginManager.getInstance().logOut()
//            signOut()
//            startNextActivity()
//        }
    }

    private fun fetchDataRetrofit() {
        lifecycleScope.launch(Dispatchers.IO) {
            val news = RetrofitInstance.newsApi.getHeadline()
            withContext(Dispatchers.Main) {
                mAdapter.updateNews(news.articles)
            }
        }
    }

//    private fun signOut() {
//        signOut()
//        startNextActivity()
//    }

//    private fun startNextActivity() {
//        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//    }

    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))

    }
}