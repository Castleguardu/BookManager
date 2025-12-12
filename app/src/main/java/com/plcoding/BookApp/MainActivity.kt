package com.plcoding.material3expressiveguide

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import com.plcoding.material3expressiveguide.data.Book
import com.plcoding.material3expressiveguide.navigation.Screen
import com.plcoding.material3expressiveguide.ui.BookDetailScreen
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
fun BookApp() {
    val viewModel: BookViewModel = viewModel()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val navController = rememberNavController()
    
    if (isAdmin == null) {
        LoginScreen(onLogin = viewModel::login)
    } else {
        // Navigation Host
        NavHost(navController = navController, startDestination = Screen.BookList) {
            composable<Screen.BookList> {
                BookListScreen(
                    viewModel = viewModel,
                    isAdmin = isAdmin!!,
                    onLogout = viewModel::logout,
                    onBookClick = { book ->
                        navController.navigate(Screen.BookDetail(book.id))
                    }
                )
            }
            composable<Screen.BookDetail> { backStackEntry ->
                val detail: Screen.BookDetail = backStackEntry.toRoute()
                BookDetailScreen(
                    bookId = detail.bookId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (Boolean) -> Unit) {
    var username by remember { mutableStateOf("user") }
    var password by remember { mutableStateOf("123456") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Books Library",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        AppleStyleTextField(
            value = username, 
            onValueChange = { username = it }, 
            placeholder = "Username"
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF2F2F7),
                unfocusedContainerColor = Color(0xFFF2F2F7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error!!, color = Color.Red)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        AppleStyleButton(text = "Login") {
            if (username == "user" && password == "123456") {
                onLogin(false) // Always login as user
            } else {
                error = "Invalid username or password"
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    viewModel: BookViewModel,
    isAdmin: Boolean,
    onLogout: () -> Unit,
    onBookClick: (Book) -> Unit
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
                    IconButton(onClick = { viewModel.fetchDoubanBooks() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync Books")
                    }
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
                BookItem(book, isAdmin, 
                    onClick = { onBookClick(book) },
                    onDelete = { viewModel.deleteBook(book) }
                )
            }
            item { 
                Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for FAB
            }
        }

        if (showAddDialog) {
            AddBookDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { title, author, price, desc ->
                    viewModel.addBook(title, author, price.toDoubleOrNull() ?: 0.0, desc)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun BookItem(book: Book, isAdmin: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cover Image
            Box(
                modifier = Modifier
                    .size(60.dp, 80.dp)
                    .padding(end = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                 if (!book.coverUri.isNullOrEmpty()) {
                     AsyncImage(
                         model = book.coverUri,
                         contentDescription = null,
                         modifier = Modifier.fillMaxSize()
                     )
                 } else {
                     Surface(
                         color = Color.LightGray,
                         shape = RoundedCornerShape(4.dp),
                         modifier = Modifier.fillMaxSize()
                     ) {}
                 }
            }

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
                if (book.rating > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFCC00),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", book.rating),
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "￥${book.price}",
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
}

@Composable
fun AddBookDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Book") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppleStyleTextField(value = title, onValueChange = { title = it }, placeholder = "Title")
                AppleStyleTextField(value = author, onValueChange = { author = it }, placeholder = "Author")
                AppleStyleTextField(value = price, onValueChange = { price = it }, placeholder = "Price")
                AppleStyleTextField(value = description, onValueChange = { description = it }, placeholder = "Description (Optional)")
            }
        },
        confirmButton = {
            TextButton(onClick = { onAdd(title, author, price, description) }) {
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
