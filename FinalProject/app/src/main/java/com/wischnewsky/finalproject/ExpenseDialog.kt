package com.wischnewsky.finalproject

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.common.api.GoogleApiClient
import com.wischnewsky.finalproject.data.Expense
import kotlinx.android.synthetic.main.new_expense_dialog.*
import kotlinx.android.synthetic.main.new_expense_dialog.view.*
import java.lang.RuntimeException
import java.util.*
import android.R.attr.data
import android.location.LocationManager
import android.net.Uri
import android.os.Environment
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.content.FileProvider
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat


class ExpenseDialog : DialogFragment() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
        private const val CAMERA_REQUEST_CODE = 102
        private const val PLACE_PICKER_REQUEST = 103
        private val REQUEST_TAKE_PHOTO = 1
    }

    var uploadBitmap: Bitmap? = null
    var pictureFilePath: String = ""


    interface ExpenseHandler {
        fun expenseCreated(item: Expense)
        fun expenseUpdated(item: Expense)
    }

    private lateinit var expenseHandler: ExpenseHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is ExpenseHandler) {
            expenseHandler = context
        } else {
            throw RuntimeException(
                "The activity does not implement the TodoHandlerInterface"
            )
        }
    }

    private lateinit var etExpenseName: EditText
    private lateinit var etExpenseDate: EditText
    private lateinit var etExpenseCost: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var imgAttach: ImageView
    private lateinit var etPlaceName: EditText

    lateinit var rootV: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())


        builder.setTitle("New Expense")

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_expense_dialog, null
        )
        rootV = rootView

        //etTodoDate = rootView.findViewById(R.id.etTodoText)

        val categoriesAdapter = ArrayAdapter.createFromResource(
            context,
            R.array.array_categories, android.R.layout.simple_spinner_item
        )
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rootView.categorySpinner.adapter = categoriesAdapter

        spinnerCategory = rootView.categorySpinner
        etExpenseName = rootView.etExpense
        etExpenseDate = rootView.etDate
        etExpenseCost = rootView.etCost
        imgAttach = rootView.imgAttach
        etPlaceName = rootView.etPlaceName

        builder.setView(rootView)


        val arguments = this.arguments

        // IF I AM IN EDIT MODE
        if (arguments != null && arguments.containsKey(
                ScrollingActivity.KEY_ITEM_TO_EDIT
            )
        ) {
            val expenseItem = arguments.getSerializable(
                ScrollingActivity.KEY_ITEM_TO_EDIT
            ) as Expense

            etExpenseName.setText(expenseItem.itemName)
            etExpenseDate.setText(expenseItem.purchaseDate)
            etExpenseCost.setText(expenseItem.cost.toString())
            etPlaceName.setText(expenseItem.locationName)
            pictureFilePath = expenseItem.photofile

            builder.setTitle("Edit Expense")
        }

        Log.d("CAMERA", "permission grated")


        builder.setPositiveButton("OK") { dialog, witch ->
            // empty
        }

        requestNeededPermission()

        rootView.btnAttach.setOnClickListener {
            dispatchTakePictureIntent()
        }

        val countries = resources.getStringArray(R.array.cities_array)
        // Create the adapter and set it to the AutoCompleteTextView
        val adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, countries)
        rootView.etPlaceName.setAdapter(adapter)


        return builder.create()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context?.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    return
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "com.wischnewsky.finalproject.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    var currentPhotoPath: String = ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            pictureFilePath = absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uploadBitmap = data!!.extras.get("data") as Bitmap
            rootV.imgAttach.setImageBitmap(uploadBitmap)
            rootV.imgAttach.visibility = View.VISIBLE
        }else if ( requestCode == PLACE_PICKER_REQUEST && resultCode ==Activity.RESULT_OK){
            val place = PlacePicker.getPlace(context, data)
            var addressText = place.name.toString()
            addressText += "\n" + place.address.toString()
            val latLong = place.latLng
        }

    }


    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.activity as Activity,
                    android.Manifest.permission.CAMERA
                )
            ) {
                Log.d("CAMERA", "permission grated")
            }

            ActivityCompat.requestPermissions(
                this.activity as Activity,
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // we already have permission
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("CAMERA", "permission grated")
                } else {
                    Log.d("CAMERA", "permission not grated")
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etExpenseName.text.isNotEmpty()) {
                if (etExpenseCost.text.isNotEmpty()) {


                    val arguments = this.arguments
                    // IF EDIT MODE
                    if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
                        handleTodoEdit()
                    } else {
                        handleTodoCreate()
                    }

                    dialog.dismiss()
                } else {
                    etExpenseCost.error = getString(R.string.not_empty)
                }
            } else {
                etExpenseName.error = getString(R.string.not_empty)
            }
        }
    }

    private fun handleTodoCreate() {
        imgAttach.buildDrawingCache()

        expenseHandler.expenseCreated(
            Expense(
                null,
                etExpenseName.text.toString(),
                etExpenseDate.text.toString(),
                etExpenseCost.text.toString().toLong(),
                spinnerCategory.selectedItem.toString(),
                pictureFilePath,
                etPlaceName.text.toString()

            )
        )
    }

    private fun handleTodoEdit() {
        val expenseToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM_TO_EDIT
        ) as Expense

        imgAttach.buildDrawingCache()

        expenseToEdit.itemName = etExpenseName.text.toString()
        expenseToEdit.purchaseDate = etExpenseDate.text.toString()
        expenseToEdit.cost = etExpenseCost.text.toString().toLong()
        expenseToEdit.category = spinnerCategory.selectedItem.toString()
        expenseToEdit.locationName = etPlaceName.text.toString()
        expenseToEdit.photofile = pictureFilePath


        expenseHandler.expenseUpdated(expenseToEdit)
    }

}