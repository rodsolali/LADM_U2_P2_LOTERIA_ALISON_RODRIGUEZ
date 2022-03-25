package mx.tecnm.tepic.ladm_u2_p2_loteria_alison_rodriguez

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.*
import mx.tecnm.tepic.ladm_u2_p2_loteria_alison_rodriguez.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var started = false
    var revisar = false
    lateinit var mediaPlayer: MediaPlayer
    var cartas: MutableList<Carta> = mutableListOf<Carta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        asigacion()

        var verCartaHilo = verCartaHilo(binding,this)
        verCartaHilo.start()

        binding.btnver.isVisible  = false
        binding.btnpausa.isVisible  = false

        binding.btnbarajear.setOnClickListener {
            cartas.shuffle()
            mediaPlayer = MediaPlayer.create(this,R.raw.barajeason)
            mediaPlayer.start()
        }
        binding.btnplay.setOnClickListener{
            started = true
            binding.btnpausa.isVisible  = true
            binding.btnbarajear.isVisible  = false
            binding.btnplay.isVisible  = false
        }
        binding.btnpausa.setOnClickListener{
            started = false
            binding.btnver.isVisible  = true
            binding.btnplay.isVisible = false
            binding.btnbarajear.isVisible = false
            binding.btnpausa.isVisible = false

        }
        binding.btnver.setOnClickListener {
            revisar = true
            binding.btnver.isVisible = false
        }


//-------------------------------------------------------------------------------------------------
        val corrutina = GlobalScope.launch(Dispatchers.IO){
            while (true){
                withContext(Dispatchers.Main) {
                    if (started){
                        binding.imageView2.setImageResource(cartas[0].imagen)
                        mediaPlayer = MediaPlayer.create(applicationContext,cartas[0].audio)
                        mediaPlayer.start()
                        cartas.removeAt(0)
                        delay(4000)
                        }
                    }
            }
        }
//-------------------------------------------------------------------------------------------------
    }
    fun asigacion(){
        cartas.clear()
        for(i in 1..54) {
            val imagenId: Int = this.getResources()
                .getIdentifier("carta$i", "drawable", this.getPackageName())

            val audioId: Int = this.getResources()
                .getIdentifier("carta$i", "raw", this.getPackageName())

            cartas.add(Carta(imagenId, audioId))
        }
        cartas.shuffle()
    }
}
//--------------------------------------------------------------------------------------------------
class verCartaHilo(binding:ActivityMainBinding,main:MainActivity):Thread(){

    var binding = binding
    var main = main


    override fun run() {
        super.run()
        while (true) {
                if (main.revisar) {
                    main.runOnUiThread(){
                        binding.imageView2.setImageResource(main.cartas[0].imagen)
                    }
                    main.mediaPlayer= MediaPlayer.create(main,main.cartas[0].audio)
                    main.mediaPlayer.start()
                    main.cartas.removeAt(0)
                    sleep(3000)
                }
                if (main.cartas.size==0){
                    main.revisar = false
                    main.runOnUiThread() {
                        binding.btnver.isVisible  = false
                        binding.btnplay.isVisible = true
                        binding.btnbarajear.isVisible = true
                    }
                    main.asigacion()
                }
            }
        }

    }