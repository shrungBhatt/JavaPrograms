package encryption_algorithms;

import java.util.ArrayList;
import java.util.Scanner;

public class PlayFairCipherEncryption {

    /**
     * Steps of play fair cipher encryption
     * <p>
     * 1) Take a key and put that key in an matrix/array of 25(Letters should not repeat)
     * 2) Fill the rest of the slots of matrix using other alphabets and i,j exists together in a single cell.
     * 3) Take plain text combine all the letters and split consecutive repeating letters with X.
     * 4) Divide the combined plain text in 2 letters each and obtain cipher text by applying the following rules:
     * a. If both the letter are in same row, shift one index to the right of each letter.
     * b. If both the letter are in same column, shift one index below of each letter.
     * c. If both the letter are in different rows and columns, form a rectangle with two diagonal endpoints
     * of plain text letter and the remaining two diagonal endpoints
     *
     * @param args
     */

    private final char[] mKeyMatrix = new char[25];
    private final char[][] mFinalKeyMatrix = new char[5][5];
    private char[] mPlainTextArray;
    private Scanner mScanner;
    private String mKey;
    private ArrayList<Character> mAlphabetDictionary = new ArrayList<>();
    private ArrayList<Character> mKeyMatrixList = new ArrayList<>();
    char[] mAlphabets = "abcdefghiklmnopqrstuvwxyz".toCharArray();
    private String mPlainText;


    public PlayFairCipherEncryption(){
        mScanner = new Scanner(System.in);
    }


    void enterKey() {
        System.out.print("Enter the key: ");
        mKey = mScanner.nextLine();

        formMatrixOfKey(mKey);

    }

    private void formMatrixOfKey(String key) {

        char[] alphabets = key.toCharArray();
        ArrayList<Character> temp = new ArrayList<>();

        for (char alphabet : alphabets) {
            if (mAlphabetDictionary != null) {
                if (!mAlphabetDictionary.contains(alphabet)) {
                    mAlphabetDictionary.add(alphabet);
                }
            }
        }


        if (mAlphabetDictionary != null) {
            for (int i = 0; i < mAlphabetDictionary.size(); i++) {
                mKeyMatrix[i] = mAlphabetDictionary.get(i);
            }

            temp.addAll(mAlphabetDictionary);
        }

        for (int i = mAlphabetDictionary.size(); i < mKeyMatrix.length; i++) {
            for (char alphabet : mAlphabets) {
                if (!temp.contains(alphabet)) {
                    temp.add(alphabet);
                    mKeyMatrix[i] = alphabet;
                    break;
                }
            }
        }

        for (char alphabet : mKeyMatrix)
            mKeyMatrixList.add(alphabet);

        int totalIndex = 0;

        System.out.println("\n" + "The Key matrix is...." + "\n");

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                mFinalKeyMatrix[i][j] = mKeyMatrix[totalIndex];
                System.out.print(mKeyMatrix[totalIndex] + "\t");
                totalIndex++;
            }
            System.out.print("\n");
        }

    }

    public void convertPlainTextToCipherText(){

//        mPlainText = mScanner.nextLine();
        mPlainText = "aaa dss abcd";

        String plainText = mPlainText.replaceAll("\\s+","");

        StringBuilder plainTextSb = new StringBuilder(plainText);

        mPlainTextArray = plainText.toCharArray();


        int indexCount = 1;

        for(int i = 0; i<mPlainTextArray.length;i++){
            char currentChar = mPlainTextArray[i];
            char nextChar = 0;

            if(i+1<mPlainTextArray.length) {
                nextChar = mPlainTextArray[i + 1];
            }

            if(currentChar == nextChar){
                plainTextSb.insert(i+indexCount,'x');
                indexCount++;

            }



        }

        System.out.println(plainTextSb.toString());


    }

}
