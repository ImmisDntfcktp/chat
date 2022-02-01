package com.example.android.mychat

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.mychat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        auth = Firebase.auth
        setUpActionBar()

        binding.apply {
            buttonSend.setOnClickListener {
                myRef.child(myRef.push().key ?: getString(R.string.hint_message))
                    .setValue(User(auth.currentUser?.displayName, editTextMessage.text.toString()))
                hideMyKeyboard()
                editTextMessage.setText(R.string.empty)
            }
        }
        getChangeDB(myRef)
        initRecyclerView()
    }

    private fun getChangeDB(databaseReference: DatabaseReference) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfMessage = ArrayList<User>()
                for (item in snapshot.children) {
                    val user = item.getValue(User::class.java)
                    if (user != null) listOfMessage.add(user)
                }
                adapter.submitList(listOfMessage)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun setUpActionBar() {
        val actionBar = supportActionBar
        CoroutineScope(Dispatchers.Main).launch {
            val dIcon = bMap()
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setHomeAsUpIndicator(dIcon)
            actionBar?.title = auth.currentUser?.displayName
        }
    }

    private suspend fun bMap() = withContext(Dispatchers.IO) {
        val bMap = Picasso.get().load(auth.currentUser?.photoUrl).get()
        return@withContext BitmapDrawable(resources, bMap)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.signOut) {
            auth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() = with(binding) {
        adapter = RecyclerAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.adapter = adapter
    }

    fun hideMyKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val hideMy = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideMy.hideSoftInputFromWindow(view.windowToken, 0)
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        }
    }
}