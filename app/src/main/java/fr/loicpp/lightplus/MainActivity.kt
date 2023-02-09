package fr.loicpp.lightplus

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    var flashLightStatus: Boolean = false
    var isBlink: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonTEST.setOnClickListener(View.OnClickListener {
            inputDialog("Morse Code","Veuillez entrer votre phrase en Morse")
        })
        btn1.setOnClickListener(View.OnClickListener {
            Log.d("DEBUG","Click !")
            if(isBlink) {
                isBlink=false
            }
            else {
                isBlink=true
                blinkLight(100)
            }
        })
    }

    val dictionnary = mapOf(
        "A" to ".-",
        "B" to "_...",
        "C" to "-.-.",
        "D" to "-..",
        "E" to ".",
        "F" to "..-.",
        "G" to "--.",
        "H" to "....",
        "I" to "..",
        "J" to ".---",
        "K" to "-.-",
        "L" to ".-..",
        "M" to "--",
        "N" to "-.",
        "O" to "---",
        "P" to ".--.",
        "Q" to "--.-",
        "R" to ".-.",
        "S" to "...",
        "T" to "-",
        "U" to "..-",
        "V" to "...-",
        "W" to ".--",
        "X" to "-..-",
        "Y" to "-.--",
        "Z" to "--..",
        "1" to ".----",
        "2" to "..---",
        "3" to "...--",
        "4" to "....-",
        "5" to ".....",
        "6" to "-....",
        "7" to "--...",
        "8" to "---..",
        "9" to "----.",
        "0" to "-----",
        " " to " "
    )

    private fun toMorse(message: String) {
        val pattern = Regex("^[A-Za-z\\d\\s]+\$")
        if (pattern.containsMatchIn(message)) {
            for (letter in message) {
                Log.d("DEBUGMORSE", "${dictionnary[letter.uppercaseChar().toString()]}")
            }
        }
        else {
            errorDialog("Regex not match")
        }
    }

    private fun errorDialog(message: String) {
        Log.e("ERROR",message)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }
    private fun inputDialog(title: String, message: String) {
        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setView(editText)
            .setPositiveButton(
                "OK"
            ) { dialogInterface: DialogInterface?, i: Int ->
                val editTextInput = editText.text.toString()
                toMorse(editTextInput)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }
    private fun blinkLight(Delay: Long = 1000) {
        if (this.isBlink) {
            openFlashLight(null)
            Timer().schedule(Delay.toLong()) {
                openFlashLight(null)
                Timer().schedule(Delay.toLong()) {
                    blinkLight()
                }
            }
        }
    }
    private fun openFlashLight(turnInto: Boolean?) {
        if (turnInto!=null) {
            if (turnInto) {
                turnFLashLight(true)
            }
            else {
                turnFLashLight(false)
            }
        }
        else {
            if (!flashLightStatus) {
                turnFLashLight(true)
            } else {
                turnFLashLight(false)
            }
        }
    }
    private fun turnFLashLight(turnInto: Boolean) {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        try {
            cameraManager.setTorchMode(cameraId, turnInto)
            flashLightStatus = turnInto

        } catch (e: CameraAccessException) {
        }
    }
}
