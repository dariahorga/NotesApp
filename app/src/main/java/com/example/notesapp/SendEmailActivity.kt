package com.example.notesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SendEmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)
        val noteId = intent.getIntExtra("noteId", -1)
    }
}
