package com.example.cloudstoragedemo

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.cloudstoragedemo.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storageRef:StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initStorage()

        binding.imageButton.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        binding.btnUpload.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let{
            storageRef.putFile(it).addOnCompleteListener{task->
                if(task.isSuccessful){
                    storageRef.downloadUrl.addOnSuccessListener {uri->
                        var imageDescription= binding.edtDescription.text.toString()
                        var imageLink =uri.toString()
                        val db = Firebase.firestore
                        Toast.makeText(applicationContext,"SUCCESS!",Toast.LENGTH_SHORT).show()
                        // Create a new user with a first and last name
                        val user = hashMapOf(
                            "imageDescription" to imageDescription,
                            "imageLink" to imageLink)

                        // Add a new document with a generated ID
                        db.collection("image_data")
                            .add(user)
                            .addOnSuccessListener { _ ->
                                Toast.makeText(applicationContext,"SUCCESS!",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(applicationContext,"FAILURE!",Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                else{
                    Toast.makeText(applicationContext,"FAILED!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //function for opening the gallery
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()){
            imageUri= it
            binding.imageButton.setImageURI(it)
        }

    private fun initStorage() {
        //initialize firebase objects
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFirestore = FirebaseFirestore.getInstance()
        Toast.makeText(applicationContext,"Get inside",Toast.LENGTH_SHORT).show()
    }

}