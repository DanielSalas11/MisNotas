package salas.daniel.misnotas

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
import salas.daniel.misnotas.databinding.ActivityAgregarNotaBinding
import salas.daniel.misnotas.databinding.ActivityMainBinding
//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.nota_layout.view.*
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {
    var notas = ArrayList<Nota>()
    lateinit var adaptador: AdaptadorNotas
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)


        binding.fab.setOnClickListener{
            var intent = Intent(this, AgregarNotaActivity::class.java)
            startActivityForResult(intent,123)
        }

        leerNotas()

        adaptador = AdaptadorNotas(this, notas)
        binding.listview.adapter = adaptador
    }

    fun leerNotas(){
        notas.clear()
        var carpeta = File(ubicacion().absolutePath)

        if(carpeta.exists()){
            var archivos =carpeta.listFiles()
            if(archivos != null){
                for(archivo in archivos){
                    leerArchivo(archivo)
                }
            }
        }
    }

    fun leerArchivo(archivo: File){
        val fis = FileInputStream(archivo)
        val di = DataInputStream(fis)
        val br = BufferedReader(InputStreamReader(di))
        var strLine: String? = br.readLine()
        var myData = ""

        while(strLine != null){
            myData = myData + strLine
            strLine = br.readLine()
        }
        br.close()
        di.close()
        fis.close()

        var nombre = archivo.name.substring(0, archivo.name.length-4)

        var nota = Nota(nombre,myData)
        notas.add(nota)
    }

    private fun ubicacion(): File{
        val folder =File(getExternalFilesDir(null), "notas")
        if(!folder.exists()){
            folder.mkdir()
        }
        return folder
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode,resultCode,data)

        if (requestCode == 123){
            leerNotas()
            adaptador.notifyDataSetChanged()
        }
    }

    class AdaptadorNotas : BaseAdapter {
        var context: Context
        var notas = ArrayList<Nota>()


        constructor(context: Context, notas: ArrayList<Nota>) {
            this.context = context
            this.notas = notas
        }

        override fun getCount(): Int {
            return notas.size
        }

        override fun getItem(p0: Int): Any {
            return notas[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var inflador = LayoutInflater.from(context)
            var vista = inflador.inflate(R.layout.nota_layout, null)
            var nota = notas[p0]
/*
            val tv_titulo_det: TextView= vista.findViewById(R.id.tv_titulo_det)
            val tv_contenido_det: TextView = vista.findViewById(R.id.tv_contenido_det)
            val btn_borrar: Button = vista.findViewById(R.id.btn_borrar)
            tv_titulo_det.text = nota.titulo
            tv_contenido_det.text = nota.contenido

            btn_borrar.setOnClickListener{
                eliminar(nota.titulo)
                notas.remove(nota)
                this.notifyDataSetChanged()

            }
*/
            return vista
        }

        private fun eliminar(titulo: String){
            if(titulo == ""){
                Toast.makeText(context,"Error: título vacío", Toast.LENGTH_SHORT).show()
            }else{
                try{
                    val archivo = File(ubicacion(), titulo+".txt")
                    archivo.delete()

                    Toast.makeText(context,"Se eliminó el archivo", Toast.LENGTH_SHORT).show()
                } catch(e: Exception){
                    Toast.makeText(context,"Error al eliminar el archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun ubicacion(): String{
            val album = File(context?.getExternalFilesDir(null),"notas")
            if(!album.exists()){
                album.mkdir()
            }
            return album.absolutePath
        }
    }
}