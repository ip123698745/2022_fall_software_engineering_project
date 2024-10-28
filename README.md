# 2022_fall_software_engineering_project
## 專案介紹
- 本系統目的是提供一個可以管理多項專案的網頁平台，讓使用者可以簡單的追蹤公開的 Repository 資訊，例如 GitHub 上的 Repository 之 Issue、Commit 等。並透過視覺化資訊圖表，讓使用者更容易進行專案管理。
- 此系統對於專案的發展可以持續保持著追蹤，透過瀏覽器的互動式介面，將資訊輸出成專案進度報表，並透過各種不同方式查看所呈現的圖表，藉此得知專案的狀態，並適時地對專案做調整控制，來達到專案管理追蹤的效果。
## 功能概述
- 使用者帳號管理子系統 (User Account Management Subsystem, UAMS)
  - 帳號管理(Account Management)之功能為建立、修改、刪除所有使用者的登入帳戶。
  - 登入管理(Login Management)的主要功能為驗證系統使用者是否已註冊。
- Repository 管理子系統 (Repository Management Subsystem, RMS)
  - 使用者必須要經過 UAMS 權限驗證後才能觀看允許的 Repository。
  - Repository 管理(Repository Management)之功能為新增、編輯、讀取和刪除使用者的 Project。
  - 使用者可新增、編輯、讀取和刪除 Project 底下的 Repository。
  - 資料庫(Database)可儲存 Repository URL。
- Repository 資訊顯示子系統 (Repository Information Visualization Subsystem, RIVS)
  - 提供使用者可以擴充相關來源，例如：Github、Gitlab 等。第二版本新增 Jira。
  - 提供使用者可以查看 Repository 相關資訊。
## 安裝與使用說明
- 詳細見兩個資料夾內的 Readme
  - `backend-Refactor_to_Java/`: 包含後端安裝說明文件及 Java 原始程式碼
  - `frontend-master/`: 包含前端安裝說明文件及 Vue.js 原始程式碼
## 主要技術與工具
- 前端: TypeScript + Vue.js
- 後端: Java Spring Boot MVC
- 資料庫: SQLite
- 容器化: Docker
