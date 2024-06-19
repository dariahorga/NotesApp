package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.example.notesapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import android.util.Log
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.android.gms.common.api.Scope
import com.google.api.services.calendar.CalendarScopes
import java.util.*
import com.google.gson.Gson
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var db: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        lateinit var calendarService: Calendar
        var isCalendarServiceInitialized = false
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("910542981394-is2203683tvg308k6q3k9sr8pvnolt8f.apps.googleusercontent.com")
            .requestEmail()
            .requestScopes(Scope(CalendarScopes.CALENDAR))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val notes = db.getAllNotes()
        val folders = db.getAllFolders().associateBy({ it.id }, { it.name })
        notesAdapter = NotesAdapter(notes, folders, this)
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        binding.showTasksButton.setOnClickListener{
            val intent = Intent(this, ShowTasksActivity::class.java)
            startActivity(intent)
        }

        setupEdgeToEdge()
        checkSignInState()
    }

    override fun onResume() {
        super.onResume()
        checkSignInState()
        notesAdapter.refreshData(db.getAllNotes())
    }

    private fun checkSignInState() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            initializeCalendarService(account)
            updateUI(account)
        } else {
            updateUI(null)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_about -> Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show()
            R.id.nav_create_folder -> {
                val intent = Intent(this, AddFolderActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_show_tasks -> {
                val intent = Intent(this, ShowTasksActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_google_sign_in -> {
                val account = GoogleSignIn.getLastSignedInAccount(this)
                if (account == null) {
                    signIn()
                }
            }
            R.id.nav_logout -> {
                logout()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            account?.let {
                initializeCalendarService(it)
            }
            updateUI(account)
            Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: ApiException) {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            updateUI(null)
        }
    }

    private fun initializeCalendarService(account: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(
            this, listOf(CalendarScopes.CALENDAR)
        )
        credential.selectedAccount = account.account
        calendarService = Calendar.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("NotesApp")
            .build()
        isCalendarServiceInitialized = true
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val menu = navigationView.menu
        val signInMenuItem = menu.findItem(R.id.nav_google_sign_in)
        if (account != null) {
            val firstName = account.displayName?.split(" ")?.get(0) ?: "User"
            signInMenuItem.title = "Hello, $firstName!"
        } else {
            signInMenuItem.title = "Sign in with Google"
        }
    }

    private fun logout() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
            isCalendarServiceInitialized = false
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.root.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }
}
