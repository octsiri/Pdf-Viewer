package com.rajat.pdfviewer.util
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.*



object FileUtils {
    @Throws(IOException::class)
    fun fileFromAsset(context: Context, assetName: String): File {
        val outFile = File(context.cacheDir, "$assetName")
        if (assetName.contains("/")) {
            outFile.parentFile.mkdirs()
        }
        copy(context.assets.open(assetName), outFile)
        return outFile
    }

    fun copy(inputStream: InputStream?, output: File?) {
        var outputStream: OutputStream? = null
        try {
                outputStream = FileOutputStream(output)
            var read = 0
            val bytes = ByteArray(1024)
            while (inputStream!!.read(bytes).also { read = it } != -1) {
                outputStream.write(bytes, 0, read)
            }
        } finally {
            try {
                inputStream?.close()
            } finally {
                outputStream?.close()
            }
        }
    }

    fun downloadFile(context: Context, assetName: String, filePath: String, fileName: String?){
        try {
            val dirPath = "${Environment.getExternalStorageDirectory()}/${filePath}"
            val outFile = File(dirPath)
            //Create New File if not present
            if (!outFile.exists()) {
                outFile.mkdirs()
            }
            val outFile1 = File(dirPath, "/$fileName.pdf")
            val localPdf = File(assetName)
            var ins: InputStream = localPdf.inputStream()
            copy(ins, outFile1)

            val myDir = Uri.parse(dirPath)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setDataAndType(myDir,  "application/pdf")

            if (intent.resolveActivityInfo(context.packageManager, 0) != null)
            {
                context.startActivity(Intent.createChooser(intent, "Open folder"))
                val toast = Toast.makeText(context, "Successfully Save PDF To Download: /${dirPath}", Toast.LENGTH_LONG)
                toast.show()
            }
            else
            {
                // if you reach this place, it means there is no any file
                // explorer app installed on your device
                val toast = Toast.makeText(context, "Failed Save PDF To Download", Toast.LENGTH_LONG)
                toast.show()
            }
        } catch(e: Exception) {
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            val exceptionAsString = sw.toString()
            Log.d(">>>>>", exceptionAsString);
            val toast = Toast.makeText(context, "Failed Save PDF To Download: /${exceptionAsString}", Toast.LENGTH_LONG)
            toast.show()
        }
    }
}
