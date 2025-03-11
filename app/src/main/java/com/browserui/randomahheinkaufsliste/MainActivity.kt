package com.browserui.randomahheinkaufsliste

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.browserui.randomahheinkaufsliste.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.lifecycle.lifecycleScope

// Correct: Declare dataStore as a top-level property delegate
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("shopping_list")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var lvtodoList: ListView
    private lateinit var fab: FloatingActionButton
    private lateinit var shopitems: ArrayList<String>
    private lateinit var itemAdapter: ArrayAdapter<String>

    private val SHOPPING_LIST_KEY = stringPreferencesKey("shopping_list_items")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lvtodoList = findViewById(R.id.lvtodoList)
        fab = findViewById(R.id.floatingActionButton)
        shopitems = ArrayList()

        itemAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, shopitems)
        lvtodoList.adapter = itemAdapter

        // Load the list when the activity is created
        lifecycleScope.launch {
            loadList().collect { loadedList ->
                shopitems.clear()
                shopitems.addAll(loadedList)
                itemAdapter.notifyDataSetChanged()
            }
        }

        lvtodoList.setOnItemLongClickListener(OnItemLongClickListener { arg0, arg1, pos, id ->
            shopitems.removeAt(pos)
            itemAdapter.notifyDataSetChanged()
            saveList(shopitems) // Save the list after removing an item
            Toast.makeText(applicationContext, "Item removed from list.", Toast.LENGTH_SHORT).show()
            true
        })

        fab.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add Item")

            var input = EditText(this)
            input.hint = "Enter Item"
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton("Add") { dialog, which ->
                val item = input.text.toString()
                shopitems.add(item)
                itemAdapter.notifyDataSetChanged()
                saveList(shopitems) // Save the list after adding an item
                Toast.makeText(applicationContext, "Item added to list.", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                Toast.makeText(applicationContext, "Canceled", Toast.LENGTH_SHORT).show()
            }

            builder.show()
        }
    }

    private fun saveList(list: List<String>) {
        lifecycleScope.launch {
            val jsonString = Json.encodeToString(list)
            // Correct: Use dataStore directly (it's now a top-level property)
            dataStore.edit { preferences ->
                preferences[SHOPPING_LIST_KEY] = jsonString
            }
        }
    }

    private fun loadList(): Flow<List<String>> {
        return dataStore.data.map { preferences ->
            val jsonString = preferences[SHOPPING_LIST_KEY] ?: "[]" // Default to empty list
            Json.decodeFromString<List<String>>(jsonString)
        }
    }
}