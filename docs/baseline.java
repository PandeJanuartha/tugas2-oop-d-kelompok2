// Jangan pake wildcard import (cth java.util.*) yaaa.
import java.util.Scanner;

/**
 * Nama kelas 
 * Deskripsi 
 * 
 * @author nama_author 
 */

// GANTI <temp> jdi nama kelas masing-masing
public class temp {

    // Janlup buat instance variable
    // Semua jenis constructor dibawah ini usahain ada yaa

    /**
     * No-Arg Constructor
     */
    public <temp>() {

    }

    /**
     * Parameterized Constructor
     * 
     * @param namaField Nilai
     * @param angkaField Nilai
     * @param isValid Nilai
     */
    public <temp>(String namaField, int angkaField, boolean isValid) {
        this.namaField = namaField;
        this.angkaField = angkaField;
        this.isValid = isValid;
    }

    /**
     * Copy Constructor
     * 
     * @param <ori> Objek yang akan disalin
     */
    public <temp>(<ori> original) {
        if (<ori> != null) {
            this.namaField = <ori>.namaField; // String aman langsung disalin (immutable)
            this.angkaField = <ori>.angkaField;
            this.isValid = <ori>.isValid;
        }
    }


    // Dibawah ini contoh, silahkan ganti 
    
    /**
     * Getter untuk namaField.
     * 
     * @return String namaField (return deep copy kalo tipe data mutable)
     */
    public String getNamaField() {
        return this.namaField;
    }

    /**
     * Setter untuk angkaField dengan validasi nilai input.
     * 
     * @param angkaField Nilai baru
     */
    public void setAngkaField(int angkaField) {
        if (angkaField >= 0) {
            this.angkaField = angkaField;
        }
    }

    /**
     * String status internal objek.
     * Cukup panggil System.out.println(objek) buat late binding otomatis.
     * 
     * @return String deskripsi status objek
     */
    @Override
    public String toString() {
        return "BaselineState [nama: " + this.namaField + ", angka: " + this.angkaField + "]";
    }

    /**
     * Buat bandingin nilai konten objek.
     * 
     * @param obj Objek pembanding
     * @return boolean True kalo sama
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Baseline other = (Baseline) obj;
        return this.namaField.equals(other.namaField) &&
               this.angkaField == other.angkaField &&
               this.isValid == other.isValid;
    }
}

/**
 * Template struktur kelas Custom Exception.
 */
class CustomExceptionExample extends Exception {
    
    /** No-Arg Constructor */
    public CustomExceptionExample() {
        super("Pesan error.");
    }

    /** Parameterized Constructor */
    public CustomExceptionExample(String message) {
        super(message);
    }
}

/**
 * Template tutup I/O Stream.
 */
class FileIOExample {
    public void readExample() {
        java.io.FileReader reader = null; // Dideklarasikan di luar try
        try {
            reader = new java.io.FileReader("file.txt"); // Instansiasi di dalam try
            // Proses file ...
        } catch (java.io.FileNotFoundException e) {
            // Tangkap exception spesifik
        } catch (java.io.IOException e) {
            // Tangkap exception umum
        } finally {
            if (reader != null) {
                try {
                    reader.close(); // Pastikan ditutup di blok finally
                } catch (java.io.IOException e) {
                    // Handling penutupan gagal
                }
            }
        }
    }
}
