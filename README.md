# ClackersMod

## 簡介

ClackersMod 是一個基於 Minecraft Forge 的模組專案。本文件描述了如何設置開發環境，確保所有開發者使用相同的工具版本，以避免構建問題。

## 先決條件

- Java Development Kit (JDK) 17
- [Gradle](https://gradle.org/)
- [Visual Studio Code](https://code.visualstudio.com/)

## 安裝與設置

### 1. 下載並安裝 JDK

請確保你已安裝 Java Development Kit (JDK) 17。
驗證安裝：

```sh
java -version

```

### 2. 下載並安裝 Gradle

如果尚未安裝 Gradle，可以從 [Gradle 官方網站](https://gradle.org/releases/) 下載並安裝。

驗證安裝：

```sh
gradle -v

```

### 3. 設置 Gradle 包裝器

為了確保所有開發者使用相同的 Gradle 版本，我們使用 Gradle 包裝器。
運行以下命令來生成或更新 Gradle 包裝器：

```sh
gradle wrapper --gradle-version 8.8

```

### 4. 生成 VSCode 運行配置

運行以下命令生成 VSCode 的運行配置：

```sh
./gradlew genVSCodeRuns

```

### 5. 使用Vscode開啟專案

## 構建與運行

使用以下命令來構建專案：

```sh
./gradlew build

```

## 常見問題

如果在設置或構建過程中遇到問題，請參考以下步驟：

- 確保你在專案根目錄中運行命令。
- 檢查 Java 和 Gradle 的版本是否正確。
- 確保所有相關文件已被正確提交到版本控制。

## 許可

此項目基於 MIT 許可證進行分發。詳見 [LICENSE](notion://www.notion.so/singularmaker/LICENSE.txt) 文件。
