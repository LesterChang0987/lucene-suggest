package com.example.lucenesuggest;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
class LuceneSuggestApplicationTests {

    @Test
    void contextLoads() {
        try {
            Directory indexDir = FSDirectory.open(Paths.get("suggestPath"));
            StandardAnalyzer analyzer = new StandardAnalyzer();
            AnalyzingInfixSuggester suggester = new AnalyzingInfixSuggester(indexDir, analyzer);
            ArrayList<Product> products = new ArrayList<>();
            products.add(new Product("Electric Guitar",
                    "http://images.example/electric-guitar.jpg", new String[] {
                    "US", "CA" }, 100));
            products.add(new Product("Electric Train",
                    "http://images.example/train.jpg", new String[] { "US",
                    "CA" }, 100));
            products.add(new Product("Acoustic Guitar",
                    "http://images.example/acoustic-guitar.jpg", new String[] {
                    "US", "ZA" }, 80));
            products.add(new Product("Guarana Soda",
                    "http://images.example/soda.jpg",
                    new String[] { "ZA", "IE","US" }, 130));
            suggester.build(new ProductIterator(products.iterator()));
            lookup(suggester, "Gu", "US");
            lookup(suggester, "Gu", "ZA");
            lookup(suggester, "Gui", "CA");
            lookup(suggester, "Electric guit", "US");
            suggester.refresh();
        } catch (IOException e) {
            System.err.println("Error!");
        }
    }
    private static void lookup(AnalyzingInfixSuggester suggester, String name,
                               String region) throws IOException {
        HashSet<BytesRef> contexts = new HashSet<>();
        contexts.add(new BytesRef(region.getBytes("UTF8")));
        //这里的num参数指的是返回的MAX个数
        List<Lookup.LookupResult> results = suggester.lookup(name, contexts, 3, true, false);
        System.out.println("-- \"" + name + "\" (" + region + "):");
        for (Lookup.LookupResult result : results) {
            System.out.println(result.key);
            BytesRef bytesRef = result.payload;
            ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bytesRef.bytes));
            Product product = null;
            try {
                product = (Product)is.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("product-Name:" + product.getName());
            System.out.println("product-regions:" + product.getRegions());
            System.out.println("product-image:" + product.getImage());
            System.out.println("product-numberSold:" + product.getNumberSold());
        }
        System.out.println();
    }
}
