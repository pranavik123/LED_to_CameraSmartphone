//Authors: Pranavi Kamavarapu, Venkata Naga Sai Tejawsini
package com.example.colorcv

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.colorcv.ui.theme.ColorCVTheme
import com.google.common.util.concurrent.ListenableFuture
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.android.Utils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    lateinit var previewView: PreviewView
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var outputText by mutableStateOf("Waiting for analysis...")
    private var isCapturing = false
    private var frameCount = 0
    private val frameDataList = mutableListOf<String>()
    private val bitDataList = mutableListOf<String>()
    private var frameC = 0
    private var startTime = System.currentTimeMillis()
    private lateinit var baseDirectory: File
    private var currentSecond = 0


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        OpenCVLoader.initDebug()
//
//        setContent {
//            ColorCVTheme {
//                MainContent(outputText, isCapturing) {
////                    captureFrame()
//                    toggleCapture()
//                }
//            }
//        }
//
//        // Validate previewView initialization
//        if (!::previewView.isInitialized || previewView == null) {
//            Log.e("CameraSetup", "PreviewView is not initialized")
//        } else {
//            Log.d("CameraSetup", "PreviewView is ready")
//        }
//
//        // Request camera permissions
//        if (allPermissionsGranted()) {
//            setupCameraProvider()
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                REQUIRED_PERMISSIONS,
//                REQUEST_CODE_PERMISSIONS
//            )
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenCVLoader.initDebug()

        clearDirectory("FrameData")
        clearDirectory("ResizedImages")

        setContent {
            ColorCVTheme {
                MainContent(
                    outputText = outputText,
                    isCapturing = isCapturing,
                    toggleCapture = { toggleCapture() },
                    onPreviewViewReady = { previewViewInstance ->
                        previewView = previewViewInstance // Assign to the lateinit variable
                        Log.d("CameraSetup", "PreviewView is ready")

                        // Request camera permissions and set up camera
                        if (allPermissionsGranted()) {
                            setupCameraProvider() // Call setup after previewView is ready
                        } else {
                            ActivityCompat.requestPermissions(
                                this,
                                REQUIRED_PERMISSIONS,
                                REQUEST_CODE_PERMISSIONS
                            )
                        }
                    }
                )
            }
        }
    }



    private fun toggleCapture() {
        if (isCapturing) {
            isCapturing = false
            outputText = "Transmission stopped."
            saveAllFrameData() // Save any collected data
            saveAllBitsData()
        } else {
            isCapturing = true
            outputText = "Transmission started."
            frameDataList.clear() // Clear previous data
            frameCount = 0
        }
    }

    @OptIn(ExperimentalCamera2Interop::class)
    private fun setupCameraProvider() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val previewBuilder = androidx.camera.core.Preview.Builder()
            val camera2Extender = Camera2Interop.Extender(previewBuilder)


            // Set ISO, Shutter Speed, White Balance, and Focus to Infinity
            camera2Extender.setCaptureRequestOption(
                android.hardware.camera2.CaptureRequest.CONTROL_AE_MODE,
                android.hardware.camera2.CaptureRequest.CONTROL_AE_MODE_OFF
            )
            camera2Extender.setCaptureRequestOption(
                android.hardware.camera2.CaptureRequest.CONTROL_AWB_MODE,
                android.hardware.camera2.CaptureRequest.CONTROL_AWB_MODE_SHADE // White balance: Shade
            )
            camera2Extender.setCaptureRequestOption(
                android.hardware.camera2.CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
                android.util.Range(30, 30) // Setting a fixed FPS range to 30
            )
            camera2Extender.setCaptureRequestOption(
                android.hardware.camera2.CaptureRequest.SENSOR_SENSITIVITY,
                48 // ISO value
            )
            camera2Extender.setCaptureRequestOption(
                android.hardware.camera2.CaptureRequest.SENSOR_EXPOSURE_TIME,
                90_909L // Shutter Speed: 1/10000 second (nanoseconds)
            )
            camera2Extender.setCaptureRequestOption(
                android.hardware.camera2.CaptureRequest.CONTROL_AF_MODE,
                android.hardware.camera2.CaptureRequest.CONTROL_AF_MODE_OFF // Disable Auto-Focus
            )
            camera2Extender.setCaptureRequestOption(
                android.hardware.camera2.CaptureRequest.LENS_FOCUS_DISTANCE,
                0.0f // Focus at infinity
            )

            if (!::previewView.isInitialized || previewView == null) {
                Log.e("CameraSetup", "PreviewView is not initialized yet")
                return@addListener
            }

            val preview = previewBuilder.build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            bindCameraUseCases(cameraProvider, preview)
        }, ContextCompat.getMainExecutor(this))
    }


    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider, preview: androidx.camera.core.Preview) {
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        baseDirectory = File(getExternalFilesDir(null), "FrameData")
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(this)) { image ->

                    if (!validateImageProxy(image)) {
                        Log.e("ImageValidation", "Invalid ImageProxy data, skipping frame.")
                        image.close()
                        return@setAnalyzer
                    }
                    // Increment frame count
                    frameC++

                    // Check if one second has elapsed
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - startTime >= 1000) {
                        currentSecond++
                        Log.d("FPS", "Current Second : ${currentSecond} Frames per second: $frameC")
                        frameC = 0 // Reset the counter
                        startTime = currentTime // Reset the start time
                    }
                    Log.e("CameraResolution", "Height: ${image.height}, Width: ${image.width}")

                    if(isCapturing){
                        val bitmap = imageProxyToBitmap(image)
                        val rotatedBitmap = rotateBitmap(bitmap, 90f)
//                        saveFrame(rotatedBitmap, currentSecond, frameC)
                        processContinuousFrames(rotatedBitmap)
                    }
                    else
                    {
                        Log.d("Start transmission", "Click on start transmission")
                    }

//                    processFrame(image)
                    image.close()
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        } catch (exc: Exception) {
            Log.e("CameraX", "Binding failed", exc)
        }
    }

    private fun saveFrame(frameBitmap: Bitmap, second: Int, frameNumber: Int) {

        // Create directory for the current second
        val secondDir = File(baseDirectory, "$second")
        if (!secondDir.exists()) secondDir.mkdirs()

        // Save the frame
        val fileName = "frame_$frameNumber.png"
        val file = File(secondDir, fileName)

        try {
            val outputStream = FileOutputStream(file)
            frameBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d("FrameSave", "Saved frame: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("FrameSave", "Failed to save frame", e)
        }
    }


    private fun validateImageProxy(image: ImageProxy): Boolean {
        if (image == null) {
            Log.e("ImageProxyValidation", "ImageProxy is null")
            return false
        }

        if (image.planes == null || image.planes.isEmpty()) {
            Log.e("ImageProxyValidation", "ImageProxy planes are invalid")
            return false
        }

        for ((index, plane) in image.planes.withIndex()) {
            if (plane.buffer == null || plane.buffer.remaining() == 0) {
                Log.e("ImageProxyValidation", "Plane $index buffer is invalid or empty")
                return false
            }
        }

        if (image.format != android.graphics.ImageFormat.YUV_420_888) {
            Log.e("ImageProxyValidation", "Unsupported image format: ${image.format}")
            return false
        }
        else{
            Log.d("ImageFormat", "image format=${image.format}")
        }

        if (image.width <= 0 || image.height <= 0) {
            Log.e("ImageProxyValidation", "Invalid image dimensions: width=${image.width}, height=${image.height}")
            return false
        }

        Log.d("ImageProxyValidation", "ImageProxy is valid: width=${image.width}, height=${image.height}")
        return true
    }


//    private fun captureFrame() {
//        outputText = "Capturing frame..."
//        previewView.bitmap?.let { bitmap ->
//            analyzeImage(bitmap)
//        }
//    }


    private fun processContinuousFrames(bitmap: Bitmap) {
        if (!isCapturing) {
            // Check for preamble pattern
            val f = detectPreamblePattern(bitmap)
            Log.d("Preamble", "preamble detection=$f")
            if (f) {
                isCapturing = true
                outputText = "Preamble detected. Starting capture..."
                frameCount = 0
            }
        } else {
            // Process and detect colors in the current frame
            frameCount++
            val detectedColors = analyzeImage(bitmap)
            Log.d("FrameCapture", "Second: $currentSecond Frame #$frameC: $detectedColors")
            frameDataList.add("Second: $currentSecond Frame #$frameC: $detectedColors")
            var bitData=""
            for(color in detectedColors) {
                val bits= convertColorToBit(color)
                if(bits!=""){
                    bitData+=bits
                }
            }
            bitDataList.add("Second: $currentSecond Frame #$frameC: $bitData")

            // Check for end pattern
            if (detectEndPattern(bitmap)) {
                isCapturing = false
                outputText = "End pattern detected. Transmission complete."
                saveAllFrameData() // Save data to file
            }
        }
    }

    private fun convertColorToBit(color: String): String {
        return when (color.lowercase()) {
            "magenta" -> "100" // Binary representation for Magenta
            "blue" -> "010"    // Binary representation for Blue
            "green" -> "001"   // Binary representation for Green
            "black" -> "111"   // Binary representation for Black
            "cyan" -> "011"    // Binary representation for Cyan
            "white" -> "110"   // Binary representation for White
            "yellow" -> "101"  // Binary representation for Yellow
            "red" -> "000"     // Binary representation for Red
            else -> ""
        }
    }

    private fun detectPreamblePattern(bitmap: Bitmap): Boolean {
        val colors = analyzeImage(bitmap)
        return colors.contains("Red") && colors.contains("Blue") // Example pattern: Red followed by Blue
    }

    private fun detectEndPattern(bitmap: Bitmap): Boolean {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        val resizedMat = Mat()
        Imgproc.resize(mat, resizedMat, Size(10.0, 1.0))
        val colors = detectColors(resizedMat)
        mat.release()
        resizedMat.release()
        return colors.contains("Green") && colors.contains("Yellow") // Example end pattern
    }

    private fun saveAllFrameData() {
        val directory = File(getExternalFilesDir(null), "FrameData")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, "FrameData_${System.currentTimeMillis()}.txt")
        try {
            val fileWriter = FileWriter(file)
            frameDataList.forEach { data ->
                fileWriter.append("$data\n")
            }
            fileWriter.close()
            Log.d("FrameData", "All frame data saved to: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("FrameData", "Failed to save frame data", e)
        }
    }

    private fun saveAllBitsData() {
        val directory = File(getExternalFilesDir(null), "FrameData")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, "BitsData_${System.currentTimeMillis()}.txt")
        try {
            val fileWriter = FileWriter(file)
            bitDataList.forEach { data ->
                fileWriter.append("$data\n")
            }
            fileWriter.close()
            Log.d("BitsData", "All BitsData saved to: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("BitsData", "Failed to save BitsData", e)
        }
    }

//    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
//        val buffer = image.planes[0].buffer
//        val bytes = ByteArray(buffer.remaining())
//        buffer.get(bytes)
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//    }


    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val yuvMat = imageToMat(image) // Convert ImageProxy to a Mat
        val rgbMat = Mat()

        // Convert YUV to RGB
        Imgproc.cvtColor(yuvMat, rgbMat, Imgproc.COLOR_YUV2RGB_NV21)

        // Create Bitmap from Mat
        val bitmap = Bitmap.createBitmap(rgbMat.cols(), rgbMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(rgbMat, bitmap)

        // Release Mats
        yuvMat.release()
        rgbMat.release()

        return bitmap
    }

    private fun imageToMat(image: ImageProxy): Mat {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvMat = Mat(image.height + image.height / 2, image.width, CvType.CV_8UC1)
        yuvMat.put(0, 0, nv21)

        return yuvMat
    }


    private fun captureFrame() {
        outputText = "Capturing frame..."
        clearRegionImages();
        previewView.bitmap?.let { bitmap ->
            val savedFilePath = saveBitmapToDirectory(bitmap)
            if (savedFilePath != null) {
                saveImageToGallery(bitmap)
                outputText = "Analyzing saved frame..."
                analyzeImage(bitmap)
            } else {
                outputText = "Failed to save frame."
            }
        }
    }

    private fun analyzeImage(bitmap: Bitmap): List<String> {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // Resize the frame for simpler processing
        val resizedMat = Mat()
        Imgproc.resize(mat, resizedMat, Size(100.0, 1.0)) // Resize to 100x1 pixels
        saveResizedImage(resizedMat)
        // Log RGB values of each pixel
        for (x in 0 until resizedMat.cols()) {
            val rgb = resizedMat.get(0, x) // Extract RGB values
            val red = rgb[0].toInt()
            val green = rgb[1].toInt()
            val blue = rgb[2].toInt()

//            Log.d("RGBValues", "NthSecond: ${currentSecond} Frame ${frameC} Pixel $x: R=$red, G=$green, B=$blue")
        }

        val detectedColors = detectColors(resizedMat)
        outputText = "Detected Colors: $detectedColors"

        mat.release()
        resizedMat.release()
        return detectedColors
    }


    private fun detectColors(resizedMat: Mat): List<String> {
        val detectedColors = mutableListOf<String>()
        val pixelData = mutableListOf<Scalar>()

        for (x in 0 until resizedMat.cols()) {
            val rgb = resizedMat.get(0, x) // Extract RGB values
            pixelData.add(Scalar(rgb[0], rgb[1], rgb[2]))
        }

        val data = Mat(pixelData.size, 3, CvType.CV_32F)
        for (i in pixelData.indices) {
            data.put(i, 0, floatArrayOf(pixelData[i].`val`[0].toFloat(), pixelData[i].`val`[1].toFloat(), pixelData[i].`val`[2].toFloat()))
        }

        // K-Means parameters
        val clusterCount = 8 // Number of clusters
        val labels = Mat()
        val centers = Mat()
        val termCriteria = TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 100, 1.0)

        // Perform K-Means clustering
        Core.kmeans(data, clusterCount, labels, termCriteria, 3, Core.KMEANS_PP_CENTERS, centers)


        // Map cluster centroids to color categories
        val colorMapping = mutableMapOf<Int, String>()
        for (i in 0 until centers.rows()) {
            val centroid = FloatArray(3)
            centers.get(i, 0, centroid)
            val red = centroid[0].toInt()
            val green = centroid[1].toInt()
            val blue = centroid[2].toInt()

            // Log the centroid RGB values
//            Log.d("ClusterMapping", "Frame ${frameC} Cluster $i: Centroid RGB = R=$red, G=$green, B=$blue")

            // Assign cluster index to a color based on centroid RGB
            colorMapping[i] = categorizeColor(red, green, blue)
        }

        // Detect colors based on consecutive appearance count
        var lastColor: String? = null
        var consecutiveCount = 0

        for (i in 0 until labels.rows()) {
            val clusterIndex = labels[i, 0][0].toInt()
            val currentColor = colorMapping[clusterIndex]

            if (currentColor == lastColor) {
                // Increment the count for consecutive appearances
                consecutiveCount++
            } else {
                // Check if the previous color qualifies
                if (consecutiveCount > 4 && lastColor != null) {
                    detectedColors.add(lastColor)
                }

                // Reset for the new color
                lastColor = currentColor
                consecutiveCount = 1
            }

            // Log pixel-to-cluster mapping
            val pixelRGB = pixelData[i]
            Log.d(
                "Pixels",
                "Second:$currentSecond Frame:$frameC  Pixel $i: R=${pixelRGB.`val`[0].toInt()}, G=${pixelRGB.`val`[1].toInt()}, B=${pixelRGB.`val`[2].toInt()} -> Cluster $clusterIndex ($currentColor)"
            )
        }

        // Add the last color if it qualifies
        if (consecutiveCount > 2 && lastColor != null) {
            detectedColors.add(lastColor)
        }

        // Log the final detected colors in order
//        Log.d("FinalColors", "Colors in order: $detectedColors")

        return detectedColors
    }

    private fun categorizeColor(r: Int, g: Int, b: Int): String {
        return when {
            r > 100 && g < 30 && b < 30 -> "Red"
            r < 20 && g > 110 && b < 40 -> "Green"
            r < 20 && g < 40 && b > 200 -> "Blue"
            r > 80 && g > 120 && b < 40 -> "Yellow"
            r < 20 && g > 110 && b > 200 -> "Cyan"
            r > 110 && g < 50 && b > 200 -> "Magenta"
            r < 30 && g < 30 && b < 30 -> "Black"
            r > 70 && g > 120 && b > 200 -> "White"
            else -> "Unknown"
        }
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }


    private fun saveImageToGallery(bitmap: Bitmap) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "CapturedFrame_$timestamp.png"

        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ColorCV") // Save to a folder in the gallery
        }

        val resolver = contentResolver
        val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    Log.d("GallerySave", "Image saved to gallery: $uri")
                }
            } catch (e: Exception) {
                Log.e("GallerySave", "Error saving image to gallery", e)
            }
        } else {
            Log.e("GallerySave", "Failed to create URI for gallery")
        }
    }


    private fun saveBitmapToDirectory(bitmap: Bitmap): String? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "CapturedFrame_$timestamp.png"
        val directory = File(getExternalFilesDir(null), "CapturedFrames")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, fileName)
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d("FrameCapture", "Frame saved: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            Log.e("FrameCapture", "Failed to save frame", e)
            null
        }
    }


    private fun analyzeImageFromDirectory(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            outputText = "File not found."
            return
        }

        val mat = Mat()
        val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
        Utils.bitmapToMat(bitmap, mat)

        val detectedColors = detectColorsInRegions(mat)
        outputText = "Detected Colors: $detectedColors\nImage Path: $filePath"
        mat.release()
    }


    private fun detectColorsInRegions(mat: Mat): List<String> {
        val bands = 11 // Number of vertical bands
        val bandWidth = mat.cols() / bands // Width of each band
        val detectedColors = mutableListOf<String>()

        for (i in 0 until bands) {
            val bandStart = i * bandWidth
            val bandEnd = if (i == bands - 1) mat.cols() else (i + 1) * bandWidth // Handle the last band
            val bandMat = mat.submat(0, mat.rows(), bandStart, bandEnd)

            val bandColor = detectDominantColor(bandMat)
            detectedColors.add("Band ${i + 1}: $bandColor")

            saveBandImage(bandMat, i + 1) // Save the band image for each region

            // Log HSV values
            val hsvMat = Mat()
            Imgproc.cvtColor(bandMat, hsvMat, Imgproc.COLOR_RGB2HSV)
            val meanHSV = Core.mean(hsvMat) // Mean HSV values of the band
            Log.d("HSVValues", "Band ${i + 1}: Mean HSV: H=${meanHSV.`val`[0]}, S=${meanHSV.`val`[1]}, V=${meanHSV.`val`[2]}")

            hsvMat.release()
            bandMat.release()
        }
        return detectedColors
    }


    private fun saveBandImage(region: Mat, regionIndex: Int) {
        val bitmap = Bitmap.createBitmap(region.cols(), region.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(region, bitmap)

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Region_${regionIndex}_$timestamp.png"
        val directory = File(getExternalFilesDir(null), "RegionImages")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, fileName)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d("RegionSave", "Region $regionIndex saved: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("RegionSave", "Failed to save region $regionIndex", e)
        }
    }


    private fun detectDominantColor(mat: Mat): String {
        val hsvMat = Mat()
        Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV)

//        val colorRanges = mapOf(
//            "Red" to listOf(Scalar(0.0, 150.0, 70.0), Scalar(10.0, 255.0, 255.0)),
//            "Green" to listOf(Scalar(60.0, 200.0, 90.0), Scalar(70.0, 255.0, 255.0)),
//            "Blue" to listOf(Scalar(115.0, 200.0, 150.0), Scalar(125.0, 255.0, 255.0))
//        )
//        val colorRanges = mapOf(
//            "Red" to listOf(Scalar(0.0, 150.0, 70.0), Scalar(10.0, 255.0, 255.0)),
//            "Green" to listOf(Scalar(60.0, 200.0, 90.0), Scalar(70.0, 255.0, 255.0)),
//            "Blue" to listOf(Scalar(115.0, 200.0, 150.0), Scalar(125.0, 255.0, 255.0)),
//            "Cyan" to listOf(Scalar(85.0, 50.0, 50.0), Scalar(100.0, 255.0, 255.0)),
//            "Magenta" to listOf(Scalar(140.0, 50.0, 50.0), Scalar(160.0, 255.0, 255.0)),
//            "Yellow" to listOf(Scalar(20.0, 50.0, 50.0), Scalar(30.0, 255.0, 255.0)),
//            "Orange" to listOf(Scalar(10.0, 50.0, 50.0), Scalar(20.0, 255.0, 255.0)),
//            "Purple" to listOf(Scalar(130.0, 50.0, 50.0), Scalar(145.0, 255.0, 255.0))
//        )

        val colorRanges = mapOf(
            "Red" to listOf(Scalar(0.0, 150.0, 50.0), Scalar(10.0, 255.0, 255.0)),
            "Yellow" to listOf(Scalar(20.0, 150.0, 100.0), Scalar(30.0, 255.0, 255.0)),
            "Green" to listOf(Scalar(35.0, 150.0, 100.0), Scalar(85.0, 255.0, 255.0)),
            "White" to listOf(Scalar(0.0, 0.0, 200.0), Scalar(180.0, 30.0, 255.0)),
            "Black" to listOf(Scalar(0.0, 0.0, 0.0), Scalar(180.0, 255.0, 50.0)),
            "Blue" to listOf(Scalar(100.0, 150.0, 50.0), Scalar(130.0, 255.0, 255.0)),
            "Magenta" to listOf(Scalar(140.0, 150.0, 50.0), Scalar(160.0, 255.0, 255.0)),
            "Cyan" to listOf(Scalar(85.0, 150.0, 50.0), Scalar(95.0, 255.0, 255.0))
        )

        val detectedColors = mutableListOf<String>()

        colorRanges.forEach { (color, range) ->
            val mask = Mat()
            Core.inRange(hsvMat, range[0], range[1], mask)
            val area = Core.countNonZero(mask)
            mask.release()

            if (area > 0) detectedColors.add(color)
        }

        hsvMat.release()
        return detectedColors.maxByOrNull { detectedColors.count { it == it } } ?: "Unknown"
    }


    private fun clearRegionImages() {
        val directory = File(getExternalFilesDir(null), "RegionImages")
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.isFile) {
                    file.delete()
                    Log.d("RegionImages", "Deleted: ${file.absolutePath}")
                }
            }
        } else {
            Log.d("RegionImages", "Directory does not exist.")
        }
    }

    private fun saveResizedImage(resizedMat: Mat) {
        val resizedBitmap = Bitmap.createBitmap(resizedMat.cols(), resizedMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resizedMat, resizedBitmap)

        val directory = File(getExternalFilesDir(null), "ResizedImages/$currentSecond")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val fileName = "frame_${frameC}.png"
        val file = File(directory, fileName)

        try {
            val outputStream = FileOutputStream(file)
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d("ResizedImageSave", "Saved resized image: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("ResizedImageSave", "Failed to save resized image", e)
        }
    }


    private fun clearDirectory(dirname: String) {
        val directory = File(getExternalFilesDir(null), dirname)
        if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                if (file.isFile) {
                    file.delete()
                } else if (file.isDirectory) {
                    file.deleteRecursively()
                }
            }
            Log.d("Directory Cleanup", "$dirname directory cleared.")
        } else {
            Log.d("Directory Cleanup", "$dirname directory does not exist or is not a directory.")
        }
    }


//    private fun detectDominantColor(mat: Mat): String {
//        val hsvMat = Mat()
//        Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV)
//
//        val colorRanges = mapOf(
//            "Red" to listOf(Scalar(0.0, 50.0, 50.0), Scalar(10.0, 255.0, 255.0)),
//            "Green" to listOf(Scalar(35.0, 50.0, 50.0), Scalar(85.0, 255.0, 255.0)),
//            "Blue" to listOf(Scalar(100.0, 50.0, 50.0), Scalar(130.0, 255.0, 255.0)),
//            "Cyan" to listOf(Scalar(85.0, 50.0, 50.0), Scalar(100.0, 255.0, 255.0)),
//            "Magenta" to listOf(Scalar(140.0, 50.0, 50.0), Scalar(160.0, 255.0, 255.0)),
//            "Yellow" to listOf(Scalar(20.0, 50.0, 50.0), Scalar(30.0, 255.0, 255.0)),
//            "Orange" to listOf(Scalar(10.0, 50.0, 50.0), Scalar(20.0, 255.0, 255.0)),
//            "Purple" to listOf(Scalar(130.0, 50.0, 50.0), Scalar(145.0, 255.0, 255.0))
//        )
//
//        val detectedColors = mutableListOf<String>()
//
//        colorRanges.forEach { (color, range) ->
//            val mask = Mat()
//            Core.inRange(hsvMat, range[0], range[1], mask)
//            val area = Core.countNonZero(mask)
//            mask.release()
//
//            if (area > 0) detectedColors.add(color)
//        }
//
//        hsvMat.release()
//        return detectedColors.maxByOrNull { detectedColors.count { it == it } } ?: "Unknown"
//    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

@Composable
fun MainContent(
    outputText: String,
    isCapturing: Boolean,
    toggleCapture: () -> Unit,
    onPreviewViewReady: (PreviewView) -> Unit
//    captureFrame: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AndroidView(
                factory = {
                    PreviewView(context).apply {
//                        (context as? MainActivity)?.previewView = this
                        onPreviewViewReady(this)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )

            Button(
//                onClick = { captureFrame() },
                onClick = { toggleCapture() },
                modifier = Modifier.padding(16.dp)
            ) {
//                Text("Capture Frame")
                Text(if (isCapturing) "Stop Transmission" else "Start Transmission")
            }

            Text(
                text = outputText,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

