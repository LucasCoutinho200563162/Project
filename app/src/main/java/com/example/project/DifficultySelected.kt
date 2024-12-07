package com.example.project

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project.databinding.ActivityDifficultySelectedBinding

private lateinit var binding: ActivityDifficultySelectedBinding
class DifficultySelected : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDifficultySelectedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Reading the string sent by the difficulty level selector
        val difficulty_selected = intent.getStringExtra("selected_diff")

        // setting text on textfield based on difficult selected
        binding.textLevelReached.setText("You reached ${difficulty_selected} level.")


        // Displaying image based on difficulty level selected
        if (difficulty_selected == "easy") {
            binding.imageDifficultLevel.setImageResource(R.drawable.easy)
        } else if (difficulty_selected == "medium") {
            binding.imageDifficultLevel.setImageResource(R.drawable.medium)
        } else if (difficulty_selected == "intermediate") {
            binding.imageDifficultLevel.setImageResource(R.drawable.intermediate)
        } else if (difficulty_selected == "advanced") {
            binding.imageDifficultLevel.setImageResource(R.drawable.advanced)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}