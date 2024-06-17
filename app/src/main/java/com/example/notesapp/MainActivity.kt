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
import com.example.notesapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var db: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var googleSignInClient: GoogleSignInClient

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

        setupEdgeToEdge()
        checkSignInState()
    }

    override fun onResume() {
        super.onResume()
        checkSignInState()
        notesAdapter.refreshData(db.getAllNotes())
    }

    private fun checkSignInState() {
        val user = FirebaseAuth.getInstance().currentUser
        updateUI(user)
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
    companion object {
        private const val RC_SIGN_IN = 9001
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
            firebaseAuthWithGoogle(account)
            Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: ApiException) {
            Log.w("SignInActivity", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Firebase sign in success, now you can get the Firebase User
                    val user = FirebaseAuth.getInstance().currentUser
                    updateUserInFirestore(user)
                    updateUI(user)  // Update UI with the Firebase user
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MainActivity", "Firebase authentication failed", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUserInFirestore(user: FirebaseUser?) {
        user?.let {
            val userData = hashMapOf(
                "email" to user.email,
                "first_name" to user.displayName?.split(" ")?.get(0),  // Assuming the name is splitable
                "last_name" to user.displayName?.split(" ")?.get(1)    // Assuming the name is splitable
            )

            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .set(userData)
                .addOnSuccessListener {
                    Log.d("Firestore", "User data successfully written to Firestore.")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error writing document", e)
                }
        } ?: Log.w("Firestore", "FirebaseAuth user is null, cannot update Firestore")
    }




    private fun updateUI(user: FirebaseUser?) {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val menu = navigationView.menu
        val signInMenuItem = menu.findItem(R.id.nav_google_sign_in)

        if (user != null) {
            val displayName = user.displayName
            val firstName = displayName?.split(" ")?.get(0) ?: "User"
            signInMenuItem.title = "Hello, $firstName"

        } else {
            signInMenuItem.title = "Sign in with Google"
        }
    }


    private fun logout() {

        googleSignInClient.signOut().addOnCompleteListener(this) {

            updateUI(null)
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
