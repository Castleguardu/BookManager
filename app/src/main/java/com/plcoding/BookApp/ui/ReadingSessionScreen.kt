package com.plcoding.material3expressiveguide.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.plcoding.material3expressiveguide.data.Note
import com.plcoding.material3expressiveguide.ui.NoteDetailDialog
import com.plcoding.material3expressiveguide.viewmodel.BookViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingSessionScreen(
    bookId: Int,
    viewModel: BookViewModel,
    onBackClick: () -> Unit
) {
    var showCamera by remember { mutableStateOf(false) }
    var detectedText by remember { mutableStateOf("") }
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    val notes by viewModel.notes.collectAsState()
    val serviceConnected by viewModel.serviceConnected.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    LaunchedEffect(bookId, serviceConnected) {
        if (serviceConnected) {
            viewModel.fetchNotes(bookId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reading Session") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCamera = true }) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Scan Text")
            }
        }
    ) { padding ->
        if (showCamera) {
            CameraOCRView(
                onTextDetected = { text ->
                    detectedText = text
                    showCamera = false
                },
                onClose = { showCamera = false }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (detectedText.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Scanned Text:", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(detectedText)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Button(
                                        onClick = {
                                            viewModel.addNote(bookId, detectedText)
                                            detectedText = ""
                                        }
                                    ) {
                                        Text("Save as Note")
                                    }
                                    OutlinedButton(onClick = { detectedText = "" }) {
                                        Text("Clear")
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text("My Notes", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                if (notes.isEmpty()) {
                    item {
                        Text(
                            text = "还没有笔记，先用相机识别一段文字并保存吧。",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(notes) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp),
                            onClick = { selectedNote = note }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = note.content,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = dateFormatter.format(note.timestamp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Full note dialog (separate file, scrollable)
    selectedNote?.let { note ->
        NoteDetailDialog(
            note = note,
            formattedTime = dateFormatter.format(note.timestamp),
            onClose = { selectedNote = null }
        )
    }
}

@Composable
fun CameraOCRView(
    onTextDetected: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Single-shot capture executor (avoid continuous OCR from ImageAnalysis)
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Always cleanup camera resources when this composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraProvider?.unbindAll()
            } catch (_: Exception) {
            }
            cameraExecutor.shutdown()
        }
    }

    if (hasPermission) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    cameraProviderFuture.addListener({
                        val provider = cameraProviderFuture.get()
                        cameraProvider = provider
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = capture

                        try {
                            provider.unbindAll()
                            provider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                capture
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraOCR", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            )
            
            Button(
                onClick = onClose,
                modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)
            ) {
                Text("Cancel")
            }

            // Capture + OCR (single-shot)
            Button(
                onClick = {
                    if (isCapturing) return@Button
                    isCapturing = true

                    val capture = imageCapture
                    if (capture == null) {
                        isCapturing = false
                        Toast.makeText(context, "相机尚未就绪", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    capture.takePicture(
                        cameraExecutor,
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                processImageProxy(imageProxy) { text ->
                                    onTextDetected(text)
                                    try {
                                        cameraProvider?.unbindAll()
                                    } catch (_: Exception) {
                                    }
                                }
                                isCapturing = false
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraOCR", "Capture failed", exception)
                                isCapturing = false
                                Toast.makeText(context, "拍照失败：${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
                enabled = !isCapturing,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 96.dp)
            ) {
                Text(if (isCapturing) "识别中…" else "拍照识别")
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission required")
        }
    }
}

private fun processImageProxy(
    imageProxy: ImageProxy,
    onTextDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Only return if text is long enough to be a note
                if (visionText.text.length > 20) {
                     onTextDetected(visionText.text)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

