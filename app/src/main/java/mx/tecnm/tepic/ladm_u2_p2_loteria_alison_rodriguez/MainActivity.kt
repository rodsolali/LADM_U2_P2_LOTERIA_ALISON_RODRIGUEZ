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

        binding.btnbarajear.setOnClickListener {//Botón para barajear las cartas.
            cartas.shuffle()
            mediaPlayer = MediaPlayer.create(this,R.raw.barajeason)//sonido
            mediaPlayer.start()
        }
        binding.btnplay.setOnClickListener{// Botón para iniciar el juego (donde se tiran una a una).
            started = true
            binding.btnpausa.isVisible  = true
            binding.btnbarajear.isVisible  = false
            binding.btnplay.isVisible  = false
        }
        binding.btnpausa.setOnClickListener{//Botón para cuando alguien grite LOTERÍA! Y se suspenda el juego.
            started = false
            mediaPlayer = MediaPlayer.create(this,R.raw.ganar)//sonido
            mediaPlayer.start()
            binding.btnver.isVisible  = true
            binding.btnplay.isVisible = false
            binding.btnbarajear.isVisible = false
            binding.btnpausa.isVisible = false

        }
        binding.btnver.setOnClickListener { //Botón para verificar la CARTA del ganador.
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
        //La corrutina es la encargada de tirar las cartas, las suelta.
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
//El metodo asignacion crea las cartas de imagenes con audios.
//--------------------------------------------------------------------------------------------------
class verCartaHilo(binding:ActivityMainBinding,main:MainActivity):Thread(){
//El hilo se encarga de mostrar las cartas faltantes mediante el botón revisar (es un icono de ojito).

    var binding = binding
    var main = main


    override fun run() {
        super.run()
        while (true) {
                if (main.revisar) {
                    main.runOnUiThread(){
                        binding.imageView2.setImageResource(main.cartas[0].imagen)//Saca la carta siguiente,cambiando la imagen por la siguiente.
                    }
                    main.mediaPlayer= MediaPlayer.create(main,main.cartas[0].audio)//Saca el siguiente audio.
                    main.mediaPlayer.start()
                    main.cartas.removeAt(0)
                    sleep(2200)
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