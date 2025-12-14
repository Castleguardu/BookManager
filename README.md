# BookManager (Material3ExpressiveGuide)

## Overview (EN)
Offline-first Android demo that packs a client-server split into one app: UI in the main process (Jetpack Compose) and data in a remote process (Room DB) via AIDL. Shows OCR note-taking with CameraX + ML Kit and Douban scraping with offline cover caching.

## 概览 (ZH)
离线优先的 Android 示例，单 App 内模拟前后端分离：主进程负责 Compose UI，远程进程持有 Room 数据库并通过 AIDL 交互。支持 CameraX + ML Kit OCR 做笔记，以及豆瓣 Top250 抓取与封面离线缓存。

## Key Tech (EN)
- Multi-process IPC: AIDL + Binder + RemoteCallbackList
- Data: Room (Book, Note) with duplicate guard on insert
- UI: Jetpack Compose (Material3), type-safe Navigation (Kotlin Serialization)
- OCR: CameraX + ML Kit Text Recognition (Chinese, single-shot capture)
- Scraping: Jsoup for Douban Top 250, local cover caching
- Image: Coil; Async: Coroutines/Flow

## 关键技术 (ZH)
- 多进程 IPC：AIDL + Binder + RemoteCallbackList
- 数据：Room（书籍/笔记），插入防重复
- 界面：Jetpack Compose (Material3)，类型安全导航（Kotlin Serialization）
- OCR：CameraX + ML Kit 文字识别（中文，单次拍照）
- 抓取：Jsoup 豆瓣 Top250，封面本地缓存
- 图片：Coil；异步：Coroutines/Flow

## Features (EN)
- Book list served from remote process Room DB; listener updates via RemoteCallbackList
- Douban scraper populates books; covers cached for offline use
- OCR reading session: tap-to-capture → recognize → save as note (notes stored per book)
- Notes list with fixed-height cards, preview lines, timestamp, tap to open full dialog

## 功能 (ZH)
- 书库在远程进程的 Room 中维护；监听回调用 RemoteCallbackList 保持同步
- 豆瓣抓取填充书籍，封面缓存以便离线浏览
- 阅读场景 OCR：点击拍照→识别→保存为笔记（按书籍归档）
- 笔记列表固定高度卡片，预览+时间戳，点开查看全文

## Run (EN)
1) Clone  
2) Open in Android Studio (use bundled JDK or set `JAVA_HOME`)  
3) Build & Run (minSdk 24, targetSdk 36)  
4) Refresh to scrape Douban; use camera to OCR and save notes

## 运行 (ZH)
1) 克隆项目  
2) 用 Android Studio 打开（使用自带 JDK 或配置 `JAVA_HOME`）  
3) 编译运行（minSdk 24，targetSdk 36）  
4) 右上刷新抓取豆瓣；打开相机拍照识别并保存笔记
