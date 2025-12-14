# BookManager (Material3ExpressiveGuide)

An advanced, offline-first Android application designed to demonstrate **Modern Android Architecture** and **Inter-Process Communication (IPC)**.

一个先进的、离线优先的 Android 应用程序，旨在演示 **现代 Android 架构** 和 **跨进程通信 (IPC)**。

This project simulates a Client-Server architecture within a single app, mimicking a robust system design often found in complex enterprise or framework-level Android development.

本项目在一个 App 内部模拟了 Client-Server 架构，复现了在复杂企业级或框架级 Android 开发中常见的稳健系统设计。

## 🌟 Key Features & Highlights (核心亮点)

### 1. Multi-Process Architecture (IPC with AIDL) | 多进程架构
Unlike standard apps that run in a single process, BookManager splits its responsibilities:
与运行在单一进程的普通 App 不同，BookManager 将职责分离：

-   **UI Process (`com.plcoding.BookApp`)**: Handles all Jetpack Compose UI and user interactions.
    -   **UI 进程**: 处理所有 Jetpack Compose UI 和用户交互。
-   **Service Process (`:remote`)**: Runs `BookManagerService` in a separate background process. This service owns the Room Database and acts as the "Server".
    -   **Service 进程**: 在独立的后台进程中运行 `BookManagerService`。该服务持有 Room 数据库并充当“服务端”。

**Core Technologies (核心技术):**
-   **AIDL**: Used for defining the interface between the UI and the remote Service.
-   **Binder**: The underlying mechanism for data transport.
-   **Thread Management**: Handles Binder thread pool concurrency when accessing the database.

### 2. 💡 Highlight: Robust Callback Management with `RemoteCallbackList` | 核心难点：RemoteCallbackList
One of the most challenging aspects of IPC is managing listeners across process boundaries.
跨进程通信中最具挑战性的方面之一是如何跨越进程边界管理监听器。

-   **The Problem (痛点)**: When you pass a listener object (e.g., `INewBookArrivedListener`) from Client to Server, the Binder mechanism generates a *new proxy object* in the Server process. This means `clientListener != serverListener`. Standard `List.remove(listener)` calls fail because the object references don't match.
    -   **问题**: 当你将一个监听器对象从客户端传递给服务端时，Binder 机制会在服务端生成一个*新的代理对象*。这意味着 `clientListener != serverListener`。普通的 `List.remove()` 会失败，因为对象引用不一致。

-   **The Solution (解决方案)**: We utilize `RemoteCallbackList`.
    -   It automatically tracks the mapping between the client's original Binder and the server's proxy.
        -   它自动跟踪客户端原始 Binder 和服务端代理对象之间的映射关系。
    -   It handles **Death Recipient** automatically: if the Client process crashes, `RemoteCallbackList` automatically removes the dead listener, preventing memory leaks and `DeadObjectException` on the Server side.
        -   它自动处理 **Death Recipient**：如果客户端进程崩溃，它会自动移除死掉的监听器，防止服务端出现内存泄漏和 `DeadObjectException`。
    -   This is the standard, production-grade way to implement Observer pattern across processes in Android.
        -   这是在 Android 中实现跨进程观察者模式的标准、生产级方案。

### 3. Modern UI with Type-Safe Navigation | 现代 UI 与类型安全导航
-   **Jetpack Compose**: 100% declarative UI (100% 声明式 UI).
-   **Type-Safe Navigation (Compose 2.8.0+)**: Moved away from error-prone string routes (e.g., `"detail/{id}"`) to **Kotlin Serialization**.
    -   摒弃了容易出错的字符串路由，全面转向 **Kotlin Serialization**。
    -   Routes are defined as `@Serializable` objects. (路由定义为序列化对象)
    -   Arguments are passed as type-safe data classes, ensuring compile-time safety. (参数通过类型安全的数据类传递，确保编译期安全)

### 4. Rich Content & Web Scraping | 内容抓取
-   **Jsoup Integration**: Implements a custom scraper to fetch Top 250 books from Douban.
    -   集成 Jsoup 实现自定义爬虫，抓取豆瓣 Top 250 书籍。
-   **Offline-First Images**: Scraped cover images are downloaded and cached locally, ensuring the app works perfectly without internet access after the initial sync.
    -   抓取的封面图片会自动本地缓存，确保首次同步后，即使断网也能完美运行。

## Tech Stack (技术栈)
-   **Language**: Kotlin
-   **UI**: Jetpack Compose (Material3)
-   **Architecture**: MVVM + Clean Architecture principles
-   **Data**: Room Database (SQLite)
-   **IPC**: AIDL, Binder, RemoteCallbackList
-   **Async**: Coroutines & Flow
-   **Network**: Jsoup (HTML Parsing)
-   **Image Loading**: Coil
-   **Serialization**: Kotlinx Serialization

## Getting Started (如何运行)
1.  Clone the repository. (克隆仓库)
2.  Build and run the app. (编译并运行)
3.  Login with default credentials:
    -   **User**: `user`
    -   **Password**: `123456`
4.  Click the "Refresh" icon in the top bar to scrape book data. (点击右上角刷新图标抓取数据)
