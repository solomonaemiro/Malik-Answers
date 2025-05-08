package com.apps.malikanswers


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AnswerActivity : AppCompatActivity() {
    private lateinit var etPetition: EditText
    private lateinit var etQuestion: EditText
    private lateinit var btnAsk: Button
    private lateinit var tvLanguage: TextView
    private var dotEntered = false
    private val maliksAnswer = charArrayOf('M', 'a', 'l', 'i', 'k',' ', 'p', 'l', 'e', 'a', 's','e',' '
        , 'a','n','s','w','e','r',' ','t','h','e',
        ' ','f','o','l','l','o','w','i','n','g',' ','q','u','e','s','t','i','o','n')

    private val userActualInput = StringBuilder()
    private var preDotText = StringBuilder() // Stores text before dot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_answer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etPetition = findViewById(R.id.etPetition)
        etQuestion = findViewById(R.id.etQuestion)
        btnAsk = findViewById(R.id.btnAsk)
        tvLanguage = findViewById(R.id.tvLanguage)

        etPetition.addTextChangedListener(object: TextWatcher {
            private var previousText = ""
            private var ignoreChanges = false

            override fun beforeTextChanged(char: CharSequence?, start: Int, count: Int, after: Int) {
                if (!ignoreChanges) {
                    previousText = char?.toString() ?: ""
                }
            }

            override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
                if (ignoreChanges) return

                val currentText = char?.toString() ?: ""

                // Handle text deletion
                if (before > 0 && count == 0) { // Backspace was pressed
                    if (dotEntered) {
                        // Check if we're deleting past the dot
                        if (currentText.length <= preDotText.length) {
                            dotEntered = false
                            userActualInput.clear()
                            preDotText = StringBuilder(currentText)
                        } else {
                            // Only remove from userActualInput if we're deleting after-dot characters
                            if (userActualInput.isNotEmpty()) {
                                userActualInput.deleteCharAt(userActualInput.length - 1)
                            }
                        }
                    }
                    return
                }

                // Check if dot was just entered
                if (currentText.endsWith(".") && !previousText.endsWith(".")) {
                    dotEntered = true
                    preDotText = StringBuilder(previousText)
                    userActualInput.clear()
                    // Remove the dot and show first character immediately
                    ignoreChanges = true
                    val newText = previousText + maliksAnswer.first()
                    etPetition.setText(newText)
                    etPetition.setSelection(newText.length)
                    ignoreChanges = false
                    return
                }

                // Handle normal typing before dot
                if (!dotEntered) {
                    preDotText = StringBuilder(currentText)
                    return
                }

                // Handle text after trigger (dot was entered but not shown)
                if (dotEntered) {
                    // Calculate how many new characters were added
                    val addedChars = currentText.length - previousText.length
                    if (addedChars > 0) {
                        // Store the actual characters user is typing
                        val newInput = currentText.substring(previousText.length)
                        userActualInput.append(newInput)

                        // Show corresponding chars from array instead
                        val charsToShow = minOf(userActualInput.length, maliksAnswer.size)
                        val newText = preDotText.toString() +
                                maliksAnswer.take(charsToShow).joinToString("")

                        ignoreChanges = true
                        etPetition.setText(newText)
                        etPetition.setSelection(newText.length)
                        ignoreChanges = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        val dialogDotEntered = AlertDialog.Builder(this)
            .setMessage(userActualInput)
            .setNeutralButton("retry"){ _, _ ->
                etPetition.setText("")
                etQuestion.setText("")
                recreate()
            }.create()

        val dialog = AlertDialog.Builder(this)
            .setMessage("ለማያምነኝ ሰው አልመልስም። I don't give an answer for someone who doesn't believe in me")
            .setNeutralButton("Retry"){ _ ,_ ->
                etPetition.setText("")
                etQuestion.setText("")
                recreate()
            }.create()

        btnAsk.setOnClickListener {
            if (etPetition.toString().isNotEmpty() && etQuestion.toString().isNotEmpty()){

                if (dotEntered){
                    dialogDotEntered.show()
                }
                else{
                    dialog.show()
                }
            }
            else{
                Toast.makeText(this, "Please enter the question and the petition first", Toast.LENGTH_SHORT).show()
            }

        }
    }
}