import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.*;

public class DESAlgorithm {
    // Các bảng hoán vị và S-boxes
    private static final int[] INITIAL_PERMUTATION_TABLE = { 
        58, 50, 42, 34, 26, 18, 10, 2, 
        60, 52, 44, 36, 28, 20, 12, 4, 
        62, 54, 46, 38, 30, 22, 14, 6, 
        64, 56, 48, 40, 32, 24, 16, 8, 
        57, 49, 41, 33, 25, 17, 9, 1, 
        59, 51, 43, 35, 27, 19, 11, 3, 
        61, 53, 45, 37, 29, 21, 13, 5, 
        63, 55, 47, 39, 31, 23, 15, 7
    };

    private static final int[] FINAL_PERMUTATION_TABLE = { 
        40, 8, 48, 16, 56, 24, 64, 32, 
        39, 7, 47, 15, 55, 23, 63, 31, 
        38, 6, 46, 14, 54, 22, 62, 30, 
        37, 5, 45, 13, 53, 21, 61, 29, 
        36, 4, 44, 12, 52, 20, 60, 28, 
        35, 3, 43, 11, 51, 19, 59, 27, 
        34, 2, 42, 10, 50, 18, 58, 26, 
        33, 1, 41, 9, 49, 17, 57, 25
    };

    private static final int[][] S_BOXES = {
        // S1
        {
            14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
            0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
            4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
            15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
        },
        // S2
        {
            15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
            3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
            0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
            13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
        },
        // S3
        {
            10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
            13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
            13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
            1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
        },
        // S4
        {
            7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
            13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
            10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
            3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
        },
        // S5
        {
            2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
            14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
            4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
            11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
        },
        // S6
        {
            12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
            10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
            9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
            4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
        },
        // S7
        {
            4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
            13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
            1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
            6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
        },
        // S8
        {
            13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
            1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
            7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
            2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
        }
    };
    
 // Các bước và bảng khóa con
    private static final int[] KEY_PERMUTATION_TABLE = {
        57, 49, 41, 33, 25, 17, 9, 1,
        58, 50, 42, 34, 26, 18, 10, 2,
        59, 51, 43, 35, 27, 19, 11, 3,
        60, 52, 44, 36, 63, 55, 47, 39,
        31, 23, 15, 7, 62, 54, 46, 38,
        30, 22, 14, 6, 61, 53, 45, 37,
        29, 21, 13, 5, 28, 20, 12, 4
    };

    private static final int[] KEY_SHIFT_TABLE = {
        1, 1, 2, 2, 2, 2, 2, 2,
        1, 2, 2, 2, 2, 2, 2, 1
    };

    private static final int[] KEY_COMPRESSION_TABLE = {
        14, 17, 11, 24, 1, 5, 3, 28,
        15, 6, 21, 10, 23, 19, 12, 4,
        26, 8, 16, 7, 27, 20, 13, 2,
        41, 52, 31, 37, 47, 55, 30, 40,
        51, 45, 33, 48, 44, 49, 39, 56,
        34, 53, 46, 42, 50, 36, 29, 32
    };
    
 // Các bước và bảng hoán vị/chuyển đổi
    private static final int[] EXPANSION_TABLE = {
        32, 1, 2, 3, 4, 5, 4, 5,
        6, 7, 8, 9, 8, 9, 10, 11,
        12, 13, 12, 13, 14, 15, 16, 17,
        16, 17, 18, 19, 20, 21, 20, 21,
        22, 23, 24, 25, 24, 25, 26, 27,
        28, 29, 28, 29, 30, 31, 32, 1
    };
    
    private static final int[] P_BOX_TABLE = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25
        };

    public static void main(String[] args) {
    	// Dữ liệu và khóa
        String plaintext = "Hello World";
        String key = "SecretKey";

        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        long plaintextBits = bytesToLong(plaintextBytes);
        long keyBits = bytesToLong(keyBytes);

        // Bước hoán vị ban đầu (Initial Permutation)
        long initialPermutation = permute(plaintextBits, INITIAL_PERMUTATION_TABLE);

        // Tạo khóa con
        long[] subKeys = generateSubKeys(keyBits);

        // Áp dụng các phép XOR
        long ciphertext = applyXOR(initialPermutation, subKeys);

        // Hoán vị cuối cùng (Final Permutation)
        long finalPermutation = permute(ciphertext, FINAL_PERMUTATION_TABLE);

        System.out.println("Plaintext: " + plaintext);
        System.out.println("Key: " + key);
        System.out.println("Ciphertext: " + Long.toHexString(finalPermutation));
        
        // Mã hóa
        long ciphertext1 = encrypt(plaintextBits, subKeys);
        
        // Giải mã
        long decryptedText = decrypt(ciphertext1, subKeys);
        
        System.out.println("Plaintext: " + plaintext);
        System.out.println("Key: " + key);
        System.out.println("Ciphertext: " + Long.toHexString(ciphertext1));
        System.out.println("Decrypted Text: " + longToPlainText(decryptedText));
    }

    private static long permute(long value, int[] table) {
        long result = 0;
        for (int i = 0; i < table.length; i++) {
            int index = table[i] - 1;
            long bit = (value >> index) & 1;
            result = (result << 1) | bit;
        }
        return result;
    }

    private static long bytesToLong(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) | (bytes[i] & 0xFF);
        }
        return value;
    }
    
    private static long[] generateSubKeys(long key) {
        long[] subKeys = new long[16];

        // Bước hoán vị khóa (PC-1)
        long permutedKey = permute(key, KEY_PERMUTATION_TABLE);

        // Tách khóa thành hai nửa
        long leftKey = permutedKey >>> 28;
        long rightKey = permutedKey & 0x0FFFFFFF;

        // Tạo khóa con
        for (int i = 0; i < 16; i++) {
            // Dịch bit trái
            leftKey = circularLeftShift(leftKey, KEY_SHIFT_TABLE[i]);
            rightKey = circularLeftShift(rightKey, KEY_SHIFT_TABLE[i]);

            // Ghép nối hai nửa lại
            long combinedKey = (leftKey << 28) | rightKey;

            // Hoán vị nén khóa (PC-2)
            subKeys[i] = permute(combinedKey, KEY_COMPRESSION_TABLE);
        }

        return subKeys;
    }
    
    public static int circularLeftShift(long leftKey, int shift) {
        int shiftedValue = (int) ((leftKey << shift) | (leftKey >>> (Integer.SIZE - shift)));
        return shiftedValue;
    }


    private static long applyXOR(long value, long[] subKeys) {
        long left = value >>> 32;
        long right = value & 0xFFFFFFFF;

        for (int i = 0; i < 16; i++) {
            long temp = left;
            left = right;
            right = temp ^ feistelFunction(right, subKeys[i]);
        }

        // Hoán vị cuối cùng
        long combined = (right << 32) | left;
        long ciphertext = permute(combined, FINAL_PERMUTATION_TABLE);

        return ciphertext;
    }
    
    private static long feistelFunction(long value, long subKey) {
        // Mở rộng giá trị từ 32 bit thành 48 bit
        long expanded = permute(value, EXPANSION_TABLE);

        // Áp dụng phép XOR với khóa con
        long xored = expanded ^ subKey;

        // Áp dụng S-boxes
        long sBoxed = sBoxSubstitution(xored);

        // Hoán vị P
        long permuted = permute(sBoxed, P_BOX_TABLE);

        return permuted;
    }

    // Phương thức thực hiện phép thay thế S-boxes
    private static long sBoxSubstitution(long value) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            int row = (int) (((value >>> (42 - i * 6)) & 0x1) | ((value >>> (47 - i * 6)) & 0x2));
            int column = (int) ((value >>> (43 - i * 6)) & 0xF);
            int sBoxValue = S_BOXES[i][row * 16 + column];
            result = (result << 4) | sBoxValue;
        }
        return result;
    }
    
    private static long encrypt(long plaintext, long[] subKeys) {
        // Bước hoán vị ban đầu (Initial Permutation)
        long initialPermutation = permute(plaintext, INITIAL_PERMUTATION_TABLE);
        
        // Áp dụng các phép XOR
        long ciphertext = applyXOR(initialPermutation, subKeys);
        
        // Hoán vị cuối cùng (Final Permutation)
        long finalPermutation = permute(ciphertext, FINAL_PERMUTATION_TABLE);
        
        return finalPermutation;
    }
    
    private static long decrypt(long ciphertext, long[] subKeys) {
        // Bước hoán vị ban đầu (Initial Permutation)
        long initialPermutation = permute(ciphertext, INITIAL_PERMUTATION_TABLE);
        
        // Đảo ngược khóa con
        long[] reversedSubKeys = reverseSubKeys(subKeys);
        
        // Áp dụng các phép XOR
        long plaintext = applyXOR(initialPermutation, reversedSubKeys);
        
        // Hoán vị cuối cùng (Final Permutation)
        long finalPermutation = permute(plaintext, FINAL_PERMUTATION_TABLE);
        
        return finalPermutation;
    }
    
    private static long[] reverseSubKeys(long[] subKeys) {
        long[] reversedSubKeys = new long[subKeys.length];
        System.arraycopy(subKeys, 0, reversedSubKeys, 0, subKeys.length);
        ArrayUtils.reverse(reversedSubKeys);
        return reversedSubKeys;
    }
    
    private static String longToPlainText(long value) {
        byte[] plaintextBytes = longToBytes(value);
        return new String(plaintextBytes, StandardCharsets.UTF_8);
    }
    
    private static byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bytes[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return bytes;
    }
}
