# Tugas Kecil 1 IF2211 Strategi Algoritma - Queens Solver

> Penyelesaian Permainan *LinkedIn Queens* menggunakan Algoritma Brute Force


![License](https://img.shields.io/badge/License-MIT-lightgrey)

## Deskripsi

**Queens Solver** adalah program berbasis GUI (JavaFX) yang dirancang untuk mencari solusi dari permainan logika *Queens* (yang populer di LinkedIn). Permainan ini mengharuskan pemain untuk menempatkan $N$ ratu pada papan berukuran $N \times N$ yang terdiri dari berbagai wilayah warna, dengan aturan:
1. Setiap baris harus memiliki tepat satu ratu.
2. Setiap kolom harus memiliki tepat satu ratu.
3. Setiap wilayah warna harus memiliki tepat satu ratu.
4. Tidak ada dua ratu yang boleh bersentuhan (termasuk secara diagonal).

Program ini mengimplementasikan dua pendekatan algoritma:
1. **Algoritma Brute Force Kombinasi Petak**: Mencoba seluruh kombinasi penempatan ratu secara *exhaustive*.
2. **Algoritma Brute Force Per Wilayah**: Pendekatan berbasis wilayah dengan pemangkasan (*pruning*) dan *backtracking* yang jauh lebih efisien.

## Fitur

* **Pilihan Algoritma**: Pilihan antara Algoritma Brute Force Kombinasi Petak atau Algoritma Brute Force Per Wilayah yang dioptimasi dengan *pruning* dan *backtracking*.
* **Visualisasi**: Melihat proses pencarian solusi langkah demi langkah (*live update*) dengan kecepatan yang dapat diatur.
* **Pilihan Input**:
    * **File Teks (.txt)**: Memuat papan dari format teks standar.
    * **Manual Input**: Mengetik konfigurasi papan langsung di aplikasi.
* **Validasi Input**: Mendeteksi kesalahan format, dimensi, dan lain-lain.
* **Simpan Solusi**: Menyimpan hasil solusi ke dalam file teks (.txt) atau gambar (.png).

## Prasyarat 

Sebelum menjalankan program, pastikan perangkat memiliki:

* **Java Development Kit (JDK) 21** atau lebih baru.
* **Apache Maven** (untuk manajemen dependensi dan *build*).

## Cara Instalasi dan Menjalankan

### 1. Clone Repositori
Unduh kode sumber atau clone repositori ini:
```bash
git clone https://github.com/NikSamSim/Tucil1_13524029.git
cd Tucil1_13524029
```

### 2. Build Project
Lakukan kompilasi dan instalasi dependensi menggunakan Maven:

```bash
mvn clean package
```
Perintah ini akan menghasilkan file executable .jar di dalam folder target/.

### 3. Menjalankan Program
Anda dapat menjalankan program dengan dua cara:

Cara 1: Menggunakan Maven
```bash
mvn javafx:run
```
Cara 1: Menjalankan File JAR
```bash
java -jar target/queens-solver-1.0.jar
```

### Catatan 
File executable .jar sudah tersedia dalam folder bin/ sehingga jika ingin langsung menggunakannya dapat jalankan perintah berikut
```bash
java -jar bin/queens-solver-1.0.jar
```
atau langsung double click file `queens-solver-1.0.jar` yang ada di folder bin/

## Cara Penggunaan

1.  **Pilih Input**:
    * Klik **Input .txt** untuk membuka file .txt.
    * Atau ketik manual di area teks lalu klik **Terapkan Input**.
2.  **Pilih Algoritma**:
    * Pilih "Brute Force Kombinasi Petak" atau "Brute Force Per Wilayah" pada *dropdown* **Pilih Algoritma**.
3.  **Visualisasi**:
    * Centang **Tampilkan Visualisasi** jika ingin melihat *live update*.
    * Atur kecepatan animasi menggunakan *slider*.
4.  **Eksekusi**:
    * Klik tombol **Solve**.
    * Status, waktu eksekusi, dan jumlah iterasi akan muncul di panel status.
5.  **Simpan**:
    * Jika solusi ditemukan, klik tombol **Simpan Solusi** untuk menyimpan hasil.
    * Solusi dapat disimpan dalam format .txt dan .png.
 
## Struktur Folder
```
Tucil1_13524029/
├── .git/
├── bin/
│   └── queens-solver-1.0.jar                           # File executable
├── doc/                                                # Laporan
├── src/
│   └── main/
│       ├── java/
│       │   └── stima/
│       │       ├── model/                              # Struktur Data Papan
│       │       │   └── Board.java                  
│       │       ├── solver/                             # Implementasi Algoritma
│       │       │   ├── BruteForce.java
│       │       │   ├── OptimizationBruteForce.java
│       │       │   ├── Solver.java
│       │       │   └── SolverAlgorithm.java
│       │       ├── utils/                              # Utilitas
│       │       │   └── InputLoader.java
│       │       ├── App.java
│       │       ├── Launcher.java
│       │       └── MainController.java
│       └── resources/                                  # Aset
│           └── stima/
│               ├── layout.fxml
│               └── logo.png
├── test/                                               # Folder Uji Coba
├── .gitignore
├── LICENSE
├── pom.xml
└── README.md
```

## Author 
Niko Samuel Simanjuntak (13524029)