package com.example.registrationscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.registrationscreen.classes.ApiClient
import com.example.registrationscreen.classes.SessionManager
import com.example.registrationscreen.data.LoginResponse
import com.example.registrationscreen.data.Users
import com.example.registrationscreen.databinding.ActivityLoginBinding
import com.example.registrationscreen.utils.LoadingActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlinx.coroutines.*



class LoginActivity : AppCompatActivity() {
    private lateinit var  callbackManager : CallbackManager
    private val sessionManager by lazy { SessionManager(this) }
    private var apiClient = ApiClient()
    private lateinit var binding: ActivityLoginBinding
    private val RC_SIGN_IN: Int = 123
    private val TAG = "SignInActivity Tag"
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        if (auth.currentUser != null) startActivity(Intent(this, MainActivity::class.java))
        binding.gBtn.setOnClickListener {
            signIn()
        }
        if (isUserLoggedIn()) startNextActivity()
        callbackManager = CallbackManager.Factory.create()


        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    load()
                    Toast.makeText(this@LoginActivity, "Logged In Successfully!", Toast.LENGTH_SHORT).show()
                    startNextActivity()
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(error: FacebookException) {
                    // App code
                }
            })

        binding.fbBtn.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, listOf("public_profile"))
        }

        binding.registerBtn.setOnClickListener {
            startActivity2()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult: FailedCode=" + e.statusCode)
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                updateUI(firebaseUser)

            }
        }

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {

            val user = Users(firebaseUser.uid,firebaseUser.displayName,firebaseUser.photoUrl.toString())
            val usersDao = UserDao()
            usersDao.addUser(user)

            val mainActivityIntent = Intent(this, MainActivity::class.java)
            load()
            startActivity(mainActivityIntent)
            finish()
        } else {
            binding.gBtn.visibility = View.VISIBLE
        }
    }

    private fun startActivity2() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun isUserLoggedIn(): Boolean = sessionManager.fetchAuthToken().let {
        it != null && it.isNotEmpty()
    }

    private fun initViews() = with(binding) {
        loginBtn.setOnClickListener {
            if (validInput(email.text.toString(), passWord.text.toString())) {
                toggleLoading(true)
                loginWithEmailPass(email.text.toString(), passWord.text.toString())
            } else {
                Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun validInput(email: String, password: String): Boolean {
        return if (email.isEmpty()) {
            binding.email.requestFocus()
            binding.email.error = "Field Cannot be Empty"
            false
        } else
            if (password.length <= 5) {
                binding.passWord.requestFocus()
                binding.passWord.error = "Minimum 6 Characters Required"
                false
            } else {
                true
            }
    }

    private fun load() {
        val loading = LoadingActivity(this@LoginActivity)
        loading.startLoading()

        Handler(Looper.getMainLooper()).postDelayed({
            loading.isDismiss()
        }, 5000)
    }

    private fun unableLoad() {
        val loading = LoadingActivity(this@LoginActivity)
        loading.isDismiss()
    }

    private fun toggleLoading(showLoading : Boolean) {

    }

    private fun loginWithEmailPass(email: String, password: String) {
        apiClient.getApiService()
            .login(email, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val loginResponse = response.body()
                    if (response.isSuccessful && response.body()?.result?.equals("ok") == true) {
                        load()
                        sessionManager.saveAuthToken(loginResponse?.data?.token.toString())
                        Toast.makeText(this@LoginActivity, "Logged in Successfully!", Toast.LENGTH_SHORT).show()
                        startNextActivity()
                    } else {
                        load()
                        Toast.makeText(this@LoginActivity, "Login Failed !", Toast.LENGTH_SHORT).show()
                    }
                    unableLoad()
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    toggleLoading(false)
                    Toast.makeText(this@LoginActivity, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun startNextActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

}