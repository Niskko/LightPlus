package fr.loicpp.lightplus

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    var flashLightStatus: Boolean = false
    var isBlink: Boolean = false
    var job: Job? = null
    var isCoroutine: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnMorse.setOnClickListener(View.OnClickListener {
            btnMorse.setBackgroundColor(ContextCompat.getColor(this, R.color.pink))
            lightMorse()
        })
        btnStopAll.setOnClickListener(View.OnClickListener {
            isCoroutine=false
            Log.d("DEBUG", "Stop it")
        })
        btn1.setOnClickListener(View.OnClickListener {
            Log.d("DEBUG","Click !")
            if(isBlink) {
                isBlink=false
            }
            else {
                isBlink=true
                blinkLight(Companion.timeBlink)
            }
        })
    }

    private val charToMorseDictionary = mapOf(
        "A" to ".-/",
        "B" to "_.../",
        "C" to "-.-./",
        "D" to "-../",
        "E" to "./",
        "F" to "..-./",
        "G" to "--./",
        "H" to "..../",
        "I" to "../",
        "J" to ".---/",
        "K" to "-.-/",
        "L" to ".-../",
        "M" to "--/",
        "N" to "-./",
        "O" to "---/",
        "P" to ".--./",
        "Q" to "--.-/",
        "R" to ".-./",
        "S" to ".../",
        "T" to "-/",
        "U" to "..-/",
        "V" to "...-/",
        "W" to ".--/",
        "X" to "-..-/",
        "Y" to "-.--/",
        "Z" to "--../",
        "1" to ".----/",
        "2" to "..---/",
        "3" to "...--/",
        "4" to "....-/",
        "5" to "...../",
        "6" to "-..../",
        "7" to "--.../",
        "8" to "---../",
        "9" to "----./",
        "0" to "-----/",
        " " to " "
    )
    private fun lightMorse() {
        if (!isCoroutine) {
            inputDialog("Morse Code","Veuillez entrer votre phrase en Morse") { result ->
                val morseCode: String = stringToMorse(result)
                isCoroutine = true
                job = GlobalScope.launch {
                    for (char in morseCode) {
                        if (isCoroutine) {
                            morseToLight(char)
                        } else {
                            return@launch
                        }
                    }
                }
            }
        }
        else {
            isCoroutine=false
        }
    }
    private fun stringToMorse(message: String): String {
        val pattern = Regex("^[A-Za-z\\d\\s]+\$")
        var morseCode: String = ""
        if (pattern.containsMatchIn(message)) {
            for (letter in message) {
                morseCode += charToMorseDictionary[letter.uppercaseChar().toString()]
            }
        }
        else {
            errorDialog("Regex not match")
        }
        return morseCode
    }

    //1 tiret: 3 points
    //blanc même lettre: 1 points
    //blanc 2 lettre: 3 points
    //space: 7 points
    private fun morseToLight(char: Char) {
        when (char.toString()) {
            "." -> {
                openFlashLight(true)
                Thread.sleep(Companion.timeMorsePoint)
                openFlashLight(false)
            }
            "-" -> {
                openFlashLight(true)
                Thread.sleep(Companion.timeMorsePoint*3)
                openFlashLight(false)
            }
            " " -> {
                openFlashLight(false)
                Thread.sleep(Companion.timeMorsePoint*7)
            }
            "/" -> {
                openFlashLight(false)
                Thread.sleep(Companion.timeMorsePoint*3)
            }
            else -> {
                Log.e("ERROR", "Unknown character")
            }
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
    private fun inputDialog(title: String, message: String, callback: (result: String) -> Unit) {
        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setView(editText)
            .setPositiveButton(
                "OK"
            ) { dialogInterface: DialogInterface?, i: Int ->
                callback(editText.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()

    }
    private fun blinkLight(Delay: Long = Companion.timeBlink) {
        if (isBlink) {
            openFlashLight(true)
            Timer().schedule(Delay.toLong()) {
                openFlashLight(false)
                Timer().schedule(Delay.toLong()) {
                    blinkLight()
                }
            }
        }
        else {
            openFlashLight(false)
        }
    }
    private fun openFlashLight(turnInto: Boolean?= null) {
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

    companion object {
        const val timeBlink: Long = 100
        const val timeMorsePoint: Long = 250
    }
}
