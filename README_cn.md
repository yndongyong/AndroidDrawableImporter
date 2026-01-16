# Android Drawable Importer

一个简单高效的 Android Studio / IntelliJ IDEA 插件，用于批量导入 Android Drawable 资源。

[English](README.md) | 中文

---

## 功能特性

- ✅ **批量导入** - 一次性导入多密度的 drawable 资源
- ✅ **拖拽操作** - 直接拖拽 ZIP 文件到工具窗口
- ✅ **图片预览** - 导入前预览所有图片
- ✅ **冲突检测** - 自动检测文件冲突并提供覆盖确认
- ✅ **重命名支持** - 单图片导入时支持重命名
- ✅ **灵活目标** - 可选择导入到 drawable 或 mipmap 目录

## 使用说明

在 Android Studio 或 IntelliJ IDEA 右侧边栏找到 **"Drawable Importer"** 工具窗口并打开。

![android_drawable_importer.gif](doc/images/android_drawable_importer.gif)

## 安装方法

### 方式一：从 JetBrains Marketplace 安装（推荐）

1. 打开 Android Studio / IntelliJ IDEA
2. 进入 `Settings/Preferences` → `Plugins`
3. 搜索 "Android Drawable Importer"
4. 点击 `Install` 安装

### 方式二：手动安装

1. 下载最新的 [Release](https://github.com/yndongyong/AndroidDrawableImporter/releases)
2. 打开 Android Studio / IntelliJ IDEA
3. 进入 `Settings/Preferences` → `Plugins`
4. 点击齿轮图标 ⚙️ → `Install Plugin from Disk...`
5. 选择下载的 ZIP 文件并安装

## 系统要求

- Android Studio Narwhal (2025.1.3) 或更高版本
- IntelliJ IDEA 2021.1 或更高版本（需要安装 Android 插件）

## 常见问题

**Q: 为什么拖拽 ZIP 文件后没有反应？**  
A: 请确保 ZIP 文件的目录结构符合要求，必须包含 `drawable-xxx` 或 `mipmap-xxx` 格式的文件夹。

**Q: 支持哪些图片格式？**  
A: 目前支持 png, jpg, jpeg, webp 格式。

## 更新日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解版本更新历史。

## 作者

**yndongyong**
- Email: yndongyong@gmail.com
- Website: https://yndongyong.github.io
