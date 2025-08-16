# tpmod for Minecraft Client & Server

[![GitHub release](https://img.shields.io/github/v/release/FPS1024/tpmod)](https://github.com/FPS1024/tpmod/releases)
[![Issues](https://img.shields.io/github/issues/FPS1024/tpmod)](https://github.com/FPS1024/tpmod/issues)
[![License](https://img.shields.io/github/license/FPS1024/tpmod)](LICENSE)

---

## 项目简介

**tpmod** 是一个适用于 Minecraft 客户端与服务器的高效传送模块。它基于 Java 开发，支持多种传送命令、权限控制，适合单人和多人服务器环境，助力玩家快速穿梭于各个世界和坐标。

---

## 目录结构

```
.
├── .gitignore
├── LICENSE
├── README.md
├── build.gradle
├── build.sh
├── gradle.properties
├── gradle/
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src/
    └── main/
        └── (Java 源代码与资源文件)
```

---

## 主要功能

- 支持 `/tp`、自定义传送命令
- 跨世界传送，适配多种服务端
- 完善的权限系统，支持与主流权限插件集成
- 兼容多版本 Minecraft
- 易于二次开发和扩展

---

## 安装教程

### 客户端安装步骤

1. 在 [Releases](https://github.com/FPS1024/tpmod/releases) 页面下载最新 tpmod `.jar` 文件。
2. 将 `.jar` 文件放入 Minecraft 的 `mods` 目录。
3. 启动游戏，确认 tpmod 正常加载。

### 服务器安装步骤

1. 下载适配你服务器版本的 tpmod。
2. 放入服务器 `mods` 文件夹。
3. 重启服务器即可生效。

---

## 使用说明

- 主要命令：  
  ```
  /tpm <玩家> <目标位置>
  /tpmod help             # 查看所有指令
  ```
- 支持自定义参数和权限配置，详细命令请见 `/tpmod help` 或 Wiki。

---

## 效果展示

> 建议上传 mod 功能截图或演示 GIF，提升项目吸引力。

---

## 贡献指南

欢迎任何形式的贡献！  
1. Fork 本仓库并创建分支。
2. 完成改动后提交 Pull Request。
3. 如有建议或 bug 可通过 [Issues](https://github.com/FPS1024/tpmod/issues) 反馈。

---

## 许可证

本项目采用 [MIT License](LICENSE)。

---

> 如有疑问或建议，欢迎在 Issues 区留言，或加入我们的交流群（QQ群/Discord 等）。
