package com.example.registrationscreen

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.EmojiCompatConfigurationView
import com.example.registrationscreen.classes.ApiClient
import com.example.registrationscreen.classes.SessionManager
import com.example.registrationscreen.data.LoginResponse
import com.example.registrationscreen.data.SignupResponse
import com.example.registrationscreen.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {
    private lateinit var Password_Pattern : Pattern
    private lateinit var binding: ActivitySignUpBinding
    private val sessionManager by lazy { SessionManager(this) }
    private var apiClient = ApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Password_Pattern = Pattern.compile("^"
//                        +"(?=.*[0-9])"
//                        + "(?=.*[a-z])"
//                        + "(?=.*[A-Z])"
//                        +"(?=.*[@#\$%^&+=])
//                        +"(?=\\\\S+\$)
//                        +".{4,}
//                        +"\$ ")

        binding.registerBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val name = binding.name.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val password = binding.passWord.text.toString().trim()
        val cPass = binding.confirmPass.text.toString().trim()
        val phone = binding.phoneNo.toString().trim()

        if (name.isEmpty()) {
            binding.name.error = "Enter Name"
            binding.name.requestFocus()
            return
        }
        if (name.length < 3) {
            binding.name.error = "Name Too Short"
            binding.name.requestFocus()
            return
        }
        if (name.length > 30) {
            binding.name.error = "Name is Too Long"
            binding.name.requestFocus()
            return
        }
        if (email.isEmpty()) {
            binding.email.error = "Enter Email"
            binding.email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Enter Valid Email"
            binding.email.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.passWord.error = "Enter Password"
            binding.passWord.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.passWord.error = "Password is too Short"
            binding.passWord.requestFocus()
            return
        }
        if (password.length > 30) {
            binding.passWord.error = "Password is too Long!"
            binding.passWord.requestFocus()
            return
        }
        if (!isValidPassword(password)) {
            binding.passWord.error = "Password is not in correct Format!"
            return
        }
        if (password != cPass){
            binding.confirmPass.error = "Password does not match!"
            binding.confirmPass.requestFocus()
            return
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            binding.phoneNo.error = "Enter Valid Phone Number"
            binding.phoneNo.requestFocus()
            return
        }
        if (phone.isEmpty()) {
            binding.phoneNo.error = "Enter Phone Number!"
            binding.phoneNo.requestFocus()
            return
        }
        if (phone.length > 10) {
            binding.phoneNo.error = "invalid Phone Number!"
            binding.phoneNo.requestFocus()
            return
        }


        doRegister(name, email, password)
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordREGEX = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$"
        );
        return passwordREGEX.matcher(password).matches()
    }

    private fun doRegister(name: String, email: String, password: String) {
        apiClient.getApiService().signUP(name, email, password)
            .enqueue(object : Callback<SignupResponse> {
                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {
                    toggleLoading(false)

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Successfully Registered!",
                            Toast.LENGTH_SHORT
                        ).show()
                        startNextActivity()
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Failed to Register!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    toggleLoading(false)
                }
            })
    }

    private fun toggleLoading(showLoading: Boolean) {
        binding.pBar.visibility = View.VISIBLE
    }

    private fun startNextActivity() {
        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        finish()
    }
}