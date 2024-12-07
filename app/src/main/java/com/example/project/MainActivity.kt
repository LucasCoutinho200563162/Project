package com.example.project

import android.content.Intent
import android.os.Bundle

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project.databinding.ActivityDifficultyLevelsBinding
import com.example.project.databinding.ActivityMainBinding

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FacebookAuthProvider

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

import com.google.android.gms.common.api.ApiException

class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var binding: ActivityMainBinding
    private val google_sign_in_code = 103



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FacebookSdk.sdkInitialize(applicationContext)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Facebook SDK and Callback Manager
        callbackManager = CallbackManager.Factory.create()

        // Google Sign-In Options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your actual web client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google Sign-In Button
        binding.btnSignInWithGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Facebook Sign-In Button
        binding.btnSignInWithFacebook.setOnClickListener {
            signInWithFacebook()
        }

        // Email and password Sign-In Button
        binding.btnCreateAccount.setOnClickListener {
            val email = binding.textFieldEmailAddress.text.toString()
            val password = binding.textFieldPassword.text.toString()
            signInOrSignUpWithEmail(email, password)
        }

        // Adjust for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Google Sign-In
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, google_sign_in_code)
    }

    // Facebook Sign-In
    private fun signInWithFacebook() {
        // requesting access to scopes email and public profile
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        // trying to log in with inner class FacebookCallback
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // if it works
                // get token
                val token = result.accessToken
                val credential = FacebookAuthProvider.getCredential(token.token)
                //retrieve credentials
                auth.signInWithCredential(credential).addOnCompleteListener(this@MainActivity) { task ->
                    if (task.isSuccessful) {
                        // print user
                        val user = auth.currentUser
                        // using this@MainActivity to refer to the outer class
                        Toast.makeText(this@MainActivity, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
                        val difficultyLevelIntent = Intent(this@MainActivity, DifficultyLevels::class.java)
                        startActivity(difficultyLevelIntent)
                    } else {
                        Toast.makeText(this@MainActivity, "Facebook Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancel() {
                Toast.makeText(this@MainActivity, "Facebook Login Canceled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@MainActivity, "Facebook Login Failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun signInOrSignUpWithEmail(email: String, password: String) {
        // Checking if the email or password fields are empty
        if (email.isNullOrBlank() or password.isNullOrBlank()) {
            Toast.makeText(this, "Please insert an email and password", Toast.LENGTH_SHORT).show()
        } else {
            // send the login credentials to signInTask
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { signInTask ->
                // Check if received confirmation of login
                if (signInTask.isSuccessful) {
                    // get user
                    val user = auth.currentUser
                    Toast.makeText(this, "Hello, ${user?.email}", Toast.LENGTH_SHORT).show()
                    val difficultyLevelIntent = Intent(this, DifficultyLevels::class.java)
                    startActivity(difficultyLevelIntent)
                } else {
                    // if failed try to create the account sending credentials to signUpTask
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { signUpTask ->
                        if (signUpTask.isSuccessful) {
                            // if it was possible to create the account then proceed
                            val newUser = auth.currentUser
                            Toast.makeText(this, "Account created successfully! Welcome, ${newUser?.email}", Toast.LENGTH_SHORT).show()
                        } else {
                            // If it was not possible to create the account then send error from signup
                            Toast.makeText(this, "Error: ${signUpTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    // Handle the result of the Facebook login
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == google_sign_in_code) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                // Firebase Sign-In with Google credentials
                auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(this, "Hello ${user?.displayName}", Toast.LENGTH_SHORT).show()
                        val difficultyLevelIntent = Intent(this, DifficultyLevels::class.java)
                        startActivity(difficultyLevelIntent)
                    } else {
                        Toast.makeText(this, "Sign-In Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
