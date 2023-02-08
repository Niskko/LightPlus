package fr.loicpp.lightplus

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.time.toDuration

class MainActivity : AppCompatActivity() {
    var flashLightStatus: Boolean = false
    var isBlink: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    private fun blinkLight(Delay: Long = 100) {
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
