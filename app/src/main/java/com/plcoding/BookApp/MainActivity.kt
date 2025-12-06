package com.plcoding.material3expressiveguide

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plcoding.material3expressiveguide.data.Book
import com.plcoding.material3expressiveguide.ui.SecondActivity
import com.plcoding.material3expressiveguide.viewmodel.BookViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apple Style Theme Overrides
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF007AFF), // iOS Blue
                    background = Color(0xFFF2F2F7), // iOS System Gray 6
                    surface = Color.White,
                    onPrimary = Color.White,
                    onSurface = Color.Black
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookApp()
                }
            }
        }
    }
}

@Composable
fun BookApp(isSecondInstance: Boolean = false) {
    val viewModel: BookViewModel = viewModel()
    val isAdmin by viewModel.isAdmin.collectAsState()
    
    if (isAdmin == null) {
        LoginScreen(
            onLogin = viewModel::login,
            isSecondInstance = isSecondInstance
        )
    } else {
        BookListScreen(
            viewModel = viewModel,
            isAdmin = isAdmin!!,
            onLogout = viewModel::logout
        )
    }
}

@Composable
fun LoginScreen(
    onLogin: (Boolean) -> Unit,
    isSecondInstance: Boolean
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSecondInstance) "Client 2 (Process)" else "Books Library",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        AppleStyleButton(text = "Login as Admin") {
            onLogin(true)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AppleStyleButton(
            text = "Login as User",
            containerColor = Color.White,
            contentColor = Color(0xFF007AFF)
        ) {
            onLogin(false)
        }

        if (!isSecondInstance) {
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton(
                onClick = {
                    val intent = Intent(context, SecondActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Launch Client 2 (Separate Process)")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    viewModel: BookViewModel,
    isAdmin: Boolean,
    onLogout: () -> Unit
) {
    val books by viewModel.books.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        "Library",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0xFFF2F2F7).copy(alpha = 0.9f),
                    scrolledContainerColor = Color(0xFFF2F2F7)
                )
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF007AFF),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Book")
                }
            }
        },
        containerColor = Color(0xFFF2F2F7)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(books) { book ->
                BookItem(book, isAdmin) {
                    viewModel.deleteBook(book)
                }
            }
            item { 
                Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for FAB
            }
        }

        if (showAddDialog) {
            AddBookDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { title, author, price ->
                    viewModel.addBook(title, author, price.toDoubleOrNull() ?: 0.0)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun BookItem(book: Book, isAdmin: Boolean, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.author,
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }
            
            Text(
                text = "$${book.price}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF007AFF)
            )
            
            if (isAdmin) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun AddBookDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Book") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppleStyleTextField(value = title, onValueChange = { title = it }, placeholder = "Title")
                AppleStyleTextField(value = author, onValueChange = { author = it }, placeholder = "Author")
                AppleStyleTextField(value = price, onValueChange = { price = it }, placeholder = "Price")
            }
        },
        confirmButton = {
            TextButton(onClick = { onAdd(title, author, price) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Red)
            }
        },
        containerColor = Color.White,
        tonalElevation = 0.dp
    )
}

@Composable
fun AppleStyleButton(
    text: String,
    containerColor: Color = Color(0xFF007AFF),
    contentColor: Color = Color.White,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text = text, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppleStyleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF2F2F7),
            unfocusedContainerColor = Color(0xFFF2F2F7),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

