# 🚀 JobHunter API - Backend Nền Tảng Tuyển Dụng & Ứng Tuyển Việc Làm

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange.svg?style=for-the-badge&logo=openjdk" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg?style=for-the-badge&logo=springboot" alt="Spring Boot 3.2.4" />
  <img src="https://img.shields.io/badge/Spring%20Security-6.x-blue.svg?style=for-the-badge&logo=springsecurity" alt="Spring Security" />
  <img src="https://img.shields.io/badge/JWT-OAuth2%20Resource%20Server-green.svg?style=for-the-badge&logo=jsonwebtokens" alt="JWT" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue.svg?style=for-the-badge&logo=mysql" alt="MySQL" />
  <img src="https://img.shields.io/badge/Gradle-Kotlin%20DSL-02303A.svg?style=for-the-badge&logo=gradle" alt="Gradle" />
</p>

---

## 📖 Giới thiệu (Overview)

**JobHunter API** là hệ thống Backend RESTful API được xây dựng trên nền tảng **Spring Boot 3** và **Java 17**, phục vụ cho nền tảng tuyển dụng việc làm trực tuyến (Job Portal / CV Top). 

Hệ thống cung cấp giải pháp toàn diện kết nối giữa **Nhà tuyển dụng (Employers / Companies)** và **Ứng viên (Job Seekers / Candidates)**: từ khâu quản lý tài khoản, đăng tin tuyển dụng, tìm kiếm & lọc công việc động, cho đến nộp và theo dõi trạng thái xử lý hồ sơ (CV/Resume).

---

## ✨ Tính năng nổi bật (Key Features)

### 🔐 1. Xác thực & Bảo mật (Authentication & Authorization)
- **Kiến trúc Stateless JWT**: Sử dụng Spring Security 6 kết hợp OAuth2 Resource Server & Nimbus JOSE + JWT.
- **Cơ chế Token kép**:
  - **Access Token**: Cung cấp xác thực nhanh chóng qua Header `Authorization: Bearer <token>`.
  - **Refresh Token**: Lưu trữ an toàn trong `HttpOnly Secure Cookie` để phòng chống tấn công XSS, tự động gia hạn phiên làm việc mà không yêu cầu người dùng đăng nhập lại.
- **Hỗ trợ đầy đủ luồng Auth**: Đăng ký (`Register`), Đăng nhập (`Login`), Lấy thông tin tài khoản hiện tại (`/auth/account`), Làm mới token (`/auth/refresh`), Đăng xuất an toàn (`Logout` - vô hiệu hóa token và xóa cookie).

### 👥 2. Quản lý Người dùng (User Management)
- CRUD tài khoản người dùng, phân loại theo vai trò (Ứng viên, Nhà tuyển dụng thuộc Công ty).
- Mã hóa mật khẩu bảo mật với thuật toán **BCrypt**.
- Hỗ trợ phân trang và tìm kiếm linh hoạt với bộ lọc động.

### 🏢 3. Quản lý Công ty (Company Management)
- Thêm, sửa, xóa, tra cứu thông tin công ty/doanh nghiệp tuyển dụng.
- Quản lý thông tin chi tiết: tên, địa chỉ, logo, mô tả doanh nghiệp.
- Thiết lập quan hệ One-to-Many với nhân viên (`Users`) và các tin tuyển dụng (`Jobs`).

### 💼 4. Quản lý Tin Tuyển Dụng (Job Management)
- Đăng tải và cập nhật tin tuyển dụng với thông tin đa dạng: Mức lương, Cấp bậc tuyển dụng (`Level`: INTERN, FRESHER, JUNIOR, MIDDLE, SENIOR), Số lượng cần tuyển, Địa điểm làm việc, Thời hạn nộp hồ sơ.
- Gắn thẻ kỹ năng (`Skills`) cho từng công việc qua liên kết nhiều - nhiều (Many-to-Many).
- Bộ lọc động đa tiêu chí: Lọc theo mức lương, địa điểm làm việc, cấp bậc, kỹ năng yêu cầu.

### 📄 5. Quản lý Hồ sơ Ứng tuyển (Resume / CV Management)
- Ứng viên nộp hồ sơ ứng tuyển theo vị trí công việc cụ thể kèm liên kết file CV.
- Vòng đời trạng thái xử lý hồ sơ: `PENDING` ➔ `REVIEWING` ➔ `APPROVED` / `REJECTED`.
- **Phân quyền truy cập hồ sơ thông minh**:
  - Nhà tuyển dụng chỉ xem và quản lý các hồ sơ ứng tuyển vào các vị trí công việc thuộc công ty của mình.
  - Ứng viên xem lại toàn bộ lịch sử các hồ sơ mình đã nộp.

### 📁 6. Quản lý Tệp tin & Lưu trữ (File Upload & Storage)
- Tải lên các định dạng CV (`.pdf`, `.doc`, `.docx`) và hình ảnh (`.jpg`, `.jpeg`, `.png`) với dung lượng tối đa lên đến **50MB**.
- Cấu hình phục vụ file tĩnh trực tiếp qua URL `/storage/**`.
- Endpoint hỗ trợ tải file trực tiếp với kiểm tra định dạng và kích thước an toàn.

### 🎯 7. Chuẩn hóa Dữ liệu & Xử lý Ngoại lệ Toàn cục (Global Handling)
- **FormatRestResponse**: Chuẩn hóa toàn bộ phản hồi trả về từ API theo định dạng JSON thống nhất (`statusCode`, `message`, `data`, `error`).
- **GlobalException**: Bắt và xử lý tập trung các lỗi xác thực, sai tham số, trùng lặp dữ liệu, lỗi lưu trữ tệp, v.v.

---

## 🛠️ Công nghệ & Thư viện sử dụng (Tech Stack)

| Thành phần | Công nghệ / Thư viện | Phiên bản |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Java | 17 |
| **Framework chính** | Spring Boot | 3.2.4 |
| **Build Tool** | Gradle (Kotlin DSL) | 8.x |
| **Security** | Spring Security & OAuth2 Resource Server | 6.x |
| **JWT** | Nimbus JOSE + JWT | Tích hợp sẵn trong Spring OAuth2 |
| **Cơ sở dữ liệu** | MySQL Server | 8.0+ |
| **ORM / Data Access** | Spring Data JPA / Hibernate | 6.x |
| **Bộ lọc dữ liệu động** | Turkraft SpringFilter | 3.1.7 |
| **Validation** | Jakarta Bean Validation | 3.x |
| **Tiện ích** | Project Lombok | 1.18.30 |

---

## 📂 Cấu trúc dự án (Project Structure)

```plaintext
C:/CV_TOP_BE/
├── 01-java-spring-jobhunter-starter-main/
│   ├── build.gradle.kts                # Cấu hình dependencies và build Gradle
│   ├── settings.gradle.kts             # Thiết lập tên dự án
│   ├── gradlew / gradlew.bat           # Gradle Wrapper cho Linux/macOS & Windows
│   └── src/
│       ├── main/
│       │   ├── java/vn/laivu/jobhunter/
│       │   │   ├── JobhunterApplication.java     # Main Application Entrypoint
│       │   │   ├── config/                      # Cấu hình Spring (Security, CORS, Resource, JWT)
│       │   │   ├── controller/                  # REST Controllers (Auth, User, Company, Job, Resume, Skill, File)
│       │   │   ├── domain/                      # DTOs (Request, Response, Pagination)
│       │   │   │   ├── request/
│       │   │   │   └── response/
│       │   │   ├── repository/                  # Spring Data JPA Repositories
│       │   │   ├── service/                     # Service Interfaces & Implementations
│       │   │   ├── unity/                       # Entities (User, Company, Job, Resume, Skill, Admin)
│       │   │   │   └── constant/                # Enums (Gender, Level, ResumeStateEnum, State)
│       │   │   └── util/                        # Helpers, FormatRestResponse, SecurityUtil, Exceptions
│       │   │       ├── Annotation/
│       │   │       └── error/
│       │   └── resources/
│       │       └── application.properties       # Cấu hình kết nối DB, JWT Secret, File Upload
│       └── test/                                # Unit Test & Integration Test
└── README.md
```

---

## ⚙️ Hướng dẫn cài đặt & Khởi chạy (Getting Started)

### 1. Yêu cầu môi trường (Prerequisites)
- **Java Development Kit (JDK)**: Phiên bản 17 trở lên.
- **MySQL Database**: Phiên bản 8.0 hoặc mới hơn.
- IDE khuyến nghị: **IntelliJ IDEA** hoặc **VS Code**.

### 2. Cấu hình cơ sở dữ liệu
Tạo cơ sở dữ liệu MySQL trên máy của bạn:
```sql
CREATE DATABASE jobhunter CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Kiểm tra và cập nhật file `01-java-spring-jobhunter-starter-main/src/main/resources/application.properties`:
```properties
# Cấu hình MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/jobhunter
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Cấu hình đường dẫn upload file
laivu.upload-file.base-uri=file:///D:/upload/

# Cấu hình JWT Secret & thời hạn Token
hoidanit.jwt.base64-secret=IcfilUPQ5swwFE+ZnRFtE9TjT40Exi4Fsy2IcH2AjbKv/i/M0M7kerio0qyFc0m2eMHzULrnf72ZpfD0XljR9Q==
hoidanit.jwt.access-token-validity-in-seconds=864000
hoidanit.jwt.refresh-token-validity-in-seconds=864000
```

### 3. Khởi chạy ứng dụng
Di chuyển vào thư mục dự án và chạy bằng Gradle Wrapper:

**Trên Windows (PowerShell / CMD):**
```powershell
cd 01-java-spring-jobhunter-starter-main
.\gradlew.bat bootRun
```

**Trên Linux / macOS:**
```bash
cd 01-java-spring-jobhunter-starter-main
chmod +x gradlew
./gradlew bootRun
```

Ứng dụng sẽ khởi động tại địa chỉ: `http://localhost:8080`.

---

## 📡 Danh sách API Endpoints chính (API Reference)

Tất cả các API được định tuyến với tiền tố `/api/v1`.

### 1. Xác thực (Authentication)
| Method | Endpoint | Mô tả | Yêu cầu quyền |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Đăng nhập tài khoản, nhận Access Token & Cookie Refresh Token | Public |
| `POST` | `/api/v1/auth/register` | Đăng ký tài khoản người dùng mới | Public |
| `GET` | `/api/v1/auth/account` | Lấy thông tin tài khoản đang đăng nhập | Bearer Token |
| `GET` | `/api/v1/auth/refresh` | Cấp lại Access Token mới từ Refresh Token trong Cookie | Cookie `refresh_token` |
| `POST` | `/api/v1/auth/logout` | Đăng xuất tài khoản và xóa Cookie Refresh Token | Bearer Token |

### 2. Người dùng (Users)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/v1/users` | Tạo mới người dùng |
| `GET` | `/api/v1/users` | Lấy danh sách người dùng (hỗ trợ lọc & phân trang) |
| `GET` | `/api/v1/users/{id}` | Lấy chi tiết thông tin người dùng theo ID |
| `PUT` | `/api/v1/users/{id}` | Cập nhật thông tin người dùng |
| `DELETE` | `/api/v1/users/{id}` | Xóa người dùng theo ID |

### 3. Công ty (Companies)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/v1/companies` | Thêm mới công ty tuyển dụng |
| `GET` | `/api/v1/companies` | Lấy danh sách công ty (phân trang, lọc theo tên, địa chỉ...) |
| `GET` | `/api/v1/companies/{id}` | Lấy thông tin chi tiết công ty |
| `PUT` | `/api/v1/companies/{id}` | Cập nhật thông tin công ty |
| `DELETE` | `/api/v1/companies/{id}` | Xóa công ty theo ID |

### 4. Tin tuyển dụng (Jobs)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/v1/jobs` | Tạo tin tuyển dụng mới kèm kỹ năng |
| `GET` | `/api/v1/jobs` | Lấy danh sách công việc (phân trang, lọc động với SpringFilter) |
| `GET` | `/api/v1/jobs/{id}` | Lấy chi tiết tin tuyển dụng |
| `PUT` | `/api/v1/jobs/{id}` | Cập nhật tin tuyển dụng |

### 5. Hồ sơ ứng tuyển (Resumes / CV)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/v1/resumes` | Nộp hồ sơ ứng tuyển (CV) vào một công việc |
| `PUT` | `/api/v1/resumes/{id}` | Cập nhật trạng thái hồ sơ (`PENDING`, `REVIEWING`, `APPROVED`, `REJECTED`) |
| `GET` | `/api/v1/resumes/{id}` | Lấy chi tiết hồ sơ ứng tuyển |
| `GET` | `/api/v1/resumes` | Lấy danh sách hồ sơ ứng tuyển (tự động lọc theo jobs của công ty hiện tại) |
| `POST` | `/api/v1/resumes/by-user` | Lấy danh sách hồ sơ mà ứng viên hiện tại đã nộp |
| `DELETE` | `/api/v1/resumes/{id}` | Xóa hồ sơ ứng tuyển |

### 6. Kỹ năng (Skills)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/v1/skills` | Thêm kỹ năng mới |
| `GET` | `/api/v1/skills` | Lấy toàn bộ danh sách kỹ năng |

### 7. Tệp tin (Files)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/v1/files` | Tải lên tệp tin (Multipart form-data: `file`, `folder`) |
| `GET` | `/api/v1/files` | Tải xuống tệp tin (`fileName`, `folder`) |

---

## 📦 Định dạng phản hồi mẫu (API Response Standard)

### Phản hồi thành công (Success Response):
```json
{
  "statusCode": 200,
  "error": null,
  "message": "Update a resume",
  "data": {
    "id": 1,
    "updatedAt": "2026-09-03T09:30:02.123Z",
    "updatedBy": "admin@gmail.com"
  }
}
```

### Phản hồi phân trang (Pagination Response):
```json
{
  "statusCode": 200,
  "error": null,
  "message": "Fetch all resume with paginate",
  "data": {
    "meta": {
      "page": 1,
      "pageSize": 10,
      "pages": 3,
      "total": 25
    },
    "result": [
      {
        "id": 1,
        "email": "candidate@gmail.com",
        "url": "candidate-cv.pdf",
        "status": "REVIEWING"
      }
    ]
  }
}
```

---

## 👤 Tác giả (Author)

* **Lại Hoàng Vũ**
* Dự án: **JobHunter / CV Top Backend API**
* Repository: [https://github.com/LaiVu2804/CV_TOP_BE](https://github.com/LaiVu2804/CV_TOP_BE)
