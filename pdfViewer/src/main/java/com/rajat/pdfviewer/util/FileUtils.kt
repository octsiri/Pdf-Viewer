package com.rajat.pdfviewer.util
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
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

    private fun copy(inputStream: InputStream?, output: File?) {
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

    @SuppressLint("WrongConstant")
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
            val uri = Uri.parse(dirPath)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf");
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
            val toast = Toast.makeText(context, "Successfully Save PDF To Download", Toast.LENGTH_LONG)
            toast.show()
            context.startActivity(Intent.createChooser(intent, "Open Folder"));
        } catch(e: Exception) {
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            val exceptionAsString = sw.toString()
            Log.d(">>>>>", exceptionAsString);
        }
    }
}
