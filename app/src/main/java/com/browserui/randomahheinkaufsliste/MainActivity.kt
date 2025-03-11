package com.browserui.randomahheinkaufsliste

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


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var lvtodoList: ListView
    private lateinit var fab: FloatingActionButton
    private lateinit var shopitems: ArrayList<String>
    private lateinit var itemAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lvtodoList = findViewById(R.id.lvtodoList)
        fab = findViewById(R.id.floatingActionButton)
        shopitems = ArrayList()

        itemAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, shopitems)
        lvtodoList.adapter = itemAdapter

        lvtodoList.setOnItemLongClickListener(OnItemLongClickListener { arg0, arg1, pos, id ->
            shopitems.removeAt(pos)
            itemAdapter.notifyDataSetChanged()
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

            builder.setPositiveButton("Add"){
                dialog, which ->
                val item = input.text.toString()
                shopitems.add(item)
                itemAdapter.notifyDataSetChanged()
                Toast.makeText(applicationContext, "Item added to list.", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Cancel") {
                dialog, which ->
                Toast.makeText(applicationContext, "Canceled", Toast.LENGTH_SHORT).show()
            }

            builder.show()
        }




        }
}
