package com.company;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("/home/jzegota/Code/JAZZ/HuffmanCodes/textToEncode.txt");
        String textToEncode = Files.readString(Path.of("/home/jzegota/Code/JAZZ/HuffmanCodes/textToEncode.txt"));
        HuffmanCode encodeText = new HuffmanCode(textToEncode);
        encodeText.keyFile();
        encodeText.encodedFile();
        File file2 = new File("/home/jzegota/Code/JAZZ/HuffmanCodes/encodedFile.txt");

        System.out.println("File textToEncode.txt of " + file.length() + " bytes converted into a file encodedFile.txt of " + file2.length() + " bytes.");
    }
}


class HuffmanCode {
    private List<Symbol> symbols = new ArrayList<>();
    private List<Symbol> symbolsChart;  
    private String sampleText;

    public HuffmanCode(String sampleText) {
        this.sampleText = sampleText;
        for (char c : sampleText.toCharArray()) insertString(String.valueOf(c));
        symbolsChart = new ArrayList<>(symbols);
        this.buildTree();
    }

    public void keyFile() throws IOException {
        FileWriter keyFile = new FileWriter("keyFile.txt");
        for (Symbol symbol : symbolsChart) {
            String singleKeyLine = symbol.getString() + "," + symbols.get(0).lookForCode(symbol) + ";";
            keyFile.write(singleKeyLine);
        }
        keyFile.close();
    }

    public void encodedFile() throws IOException {

        FileWriter encodedFile = new FileWriter("encodedFile.txt");
        for (char c : this.sampleText.toCharArray()) {
            for (Symbol symbol : symbolsChart) {
                if (symbol.getString().equals(String.valueOf(c))) {
                    encodedFile.write(symbol.getCode());
                }
            }
        }
        encodedFile.close();
        var encoded = Files.readString(Path.of("/home/jzegota/Code/JAZZ/HuffmanCodes/encodedFile.txt"));
        BitSet bitset = new BitSet(encoded.length());
        int bitcounter = 0;
        for (Character c : encoded.toCharArray()) {
            if (c.equals('1')) {
                bitset.set(bitcounter);
            }
            bitcounter++;
        }
        try (FileOutputStream fos = new FileOutputStream("encodedFile.txt")) {
            fos.write(bitset.toByteArray());
        }

    }

    private void buildTree() {
        while (symbols.size() > 1) {
            var sym1 = extractMin(symbols);
            var sym2 = extractMin(symbols);
            build(sym1, sym2);
        }
        symbols.get(0).setCode("");
        setCodes(symbols.get(0));
    }

    private void setCodes(Symbol s) {
        if (s.getLeftChild() != null) {
            s.getLeftChild().setCode(s.getCode() + "0");
            setCodes(s.getLeftChild());
        }
    }

    private void build(Symbol s1, Symbol s2) {
        var parent = new Symbol(s1.getString() + s2.getString(), s1.getOccurence() + s2.getOccurence());
        parent.setLeftChild(s1);
        parent.setRightChild(s2);
        symbols.add(parent);
        symbols.remove(s1);
        symbols.remove(s2);

    }

    private void insertString(String s) {
        for (Symbol symbol : symbols) {
            if (s.equals(symbol.getString())) {
                symbol.increment();
                return;
            }
        }
        symbols.add(new Symbol(s, 1));
    }

    private Symbol extractMin(List<Symbol> symbolList) {

        Symbol comparingSymbol = symbolList.get(0);
        for (Symbol symbol : symbolList) {
            if (symbol.getOccurence() < comparingSymbol.getOccurence()) {
                comparingSymbol = symbol;
            }
        }
        symbolList.remove(comparingSymbol);
        return comparingSymbol;
    }

}

class Symbol {
    private String string;
    private int occurence;
    private Symbol leftChild;
    private Symbol rightChild;
    private String code;


    public Symbol(String string, int occurence) {
        this.string = string;
        this.occurence = occurence;
        this.code = "";
    }


    public String lookForCode(Symbol symbol) {
        if (this.equals(symbol)) {
            symbol.setCode(this.code);
            return "";
        } else if (this.leftChild != null) {
            String code = this.leftChild.lookForCode(symbol);
            if (code != null) {
                symbol.setCode(this.code + code);
                return "0" + code;
            }
        }
        if (this.rightChild != null) {
            String code = this.rightChild.lookForCode(symbol);
            if (code != null) {
                symbol.setCode(this.code + code);
                return "1" + code;
            }
        }
        return null;
    }

    public void increment() {
        this.occurence++;
    }

    public void setLeftChild(Symbol leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(Symbol rightChild) {
        this.rightChild = rightChild;
    }

    public Symbol getLeftChild() {
        return leftChild;
    }

    public Symbol getRightChild() {
        return rightChild;
    }

    public String getString() {
        return string;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getOccurence() {
        return occurence;
    }
}

