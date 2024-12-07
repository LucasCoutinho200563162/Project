package com.example.project

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project.databinding.ActivityDifficultyLevelsBinding


private lateinit var binding: ActivityDifficultyLevelsBinding
class DifficultyLevels : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDifficultyLevelsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Setting buttons with intents to open the page where the image will be displayed

        binding.btnEasyDifficulty.setOnClickListener {
            val openDifficultySelectedIntent = Intent(this, DifficultySelected::class.java )
            // adding string to be read by the activity where image will be displayed
            openDifficultySelectedIntent.putExtra("selected_diff", "easy")
            startActivity(openDifficultySelectedIntent)
        }



        binding.btnMediumDifficulty.setOnClickListener {
            val openDifficultySelectedIntent = Intent(this, DifficultySelected::class.java )
            openDifficultySelectedIntent.putExtra("selected_diff", "medium")
            startActivity(openDifficultySelectedIntent)
        }



        binding.btnAdvancedDifficulty.setOnClickListener {
            val openDifficultySelectedIntent = Intent(this, DifficultySelected::class.java )
            openDifficultySelectedIntent.putExtra("selected_diff", "advanced")
            startActivity(openDifficultySelectedIntent)
        }



        binding.btnIntermediateDifficulty.setOnClickListener {
            val openDifficultySelectedIntent = Intent(this, DifficultySelected::class.java )
            openDifficultySelectedIntent.putExtra("selected_diff", "intermediate")
            startActivity(openDifficultySelectedIntent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}