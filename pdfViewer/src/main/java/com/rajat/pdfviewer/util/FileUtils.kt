package com.rajat.pdfviewer.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
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
            val destPath: String? = context.getExternalFilesDir(null)?.absolutePath

            val dirPath = "${destPath}/${filePath}"
            val outFile = File(dirPath)
            //Create New File if not present
            if (!outFile.exists()) {
                outFile.mkdirs()
            }
            val outFile1 = File(dirPath, "/$fileName.pdf")
            val localPdf = File(assetName)

            var ins: InputStream = localPdf.inputStream()
            copy(ins, outFile1)
//            val uri = Uri.fromFile(outFile1)
            val toast = Toast.makeText(context, "Successfully Save PDF To Download", 3000)
            toast.show()
            val externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            val uri = Uri.parse(externalFilesDir) // a directory
            intent.setDataAndType(uri, "file/*");
            context.startActivity(Intent.createChooser(intent, "Open folder"));
        } catch(e: Exception) {
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            val exceptionAsString = sw.toString()
            Log.d(">>>>>", exceptionAsString);
        }
    }
}
