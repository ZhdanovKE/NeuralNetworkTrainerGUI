/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainerapp.gui.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import neuralnetwork.NeuralNetwork;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import trainerapp.gui.model.NamedNeuralNetwork;
import trainerapp.gui.testutil.TestUtils;

/**
 *
 * @author Konstantin Zhdanov
 */
public class NeuralNetworkLoaderTest {
    
    private String fileName = "./network.txt";
    
    public NeuralNetworkLoaderTest() {
    }

    @Before
    public void setUp() {
        
    }
    
    @After
    public void cleanUp() {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }
    
    /**
     * Test of saveAsText method, of class NeuralNetworkLoader.
     */
    @Test
    public void testSaveAsText() {
        System.out.println("saveAsText");
        int nInputs = 2;
        int[] hiddenSizes = {3, 4};
        int nOutputs = 5;
        String name = "Network test";
        NeuralNetwork nn = new NamedNeuralNetwork(nInputs, hiddenSizes, nOutputs, 
                name);
        nn.setWeight(0, 0, 0, 10.5); nn.setWeight(0, 0, 1, 4.1); nn.setWeight(0, 0, 2, 1);
        nn.setWeight(0, 1, 0, -1.9); nn.setWeight(0, 1, 1, 0.2); nn.setWeight(0, 1, 2, 0);
        nn.setBias(0, 0, 1.4);       nn.setBias(0, 1, -1.4);     nn.setBias(0, 2, 3.2);
        
        nn.setWeight(1, 0, 0, 2); nn.setWeight(1, 0, 1, 5); nn.setWeight(1, 0, 2, 8); nn.setWeight(1, 0, 3, 11);
        nn.setWeight(1, 1, 0, 3); nn.setWeight(1, 1, 1, 6); nn.setWeight(1, 1, 2, 9); nn.setWeight(1, 1, 3, 12);
        nn.setWeight(1, 2, 0, 4); nn.setWeight(1, 2, 1, 7); nn.setWeight(1, 2, 2, 10); nn.setWeight(1, 2, 3, 13);
        nn.setBias(1, 0, 1.4);    nn.setBias(1, 1, -1.4);   nn.setBias(1, 2, 3.2);     nn.setBias(1, 3, 4.2);
        
        nn.setWeight(2, 0, 0, -2); nn.setWeight(2, 0, 1, -6); nn.setWeight(2, 0, 2, 0.5); nn.setWeight(2, 0, 3, 4.5); nn.setWeight(2, 0, 4, 8.5);
        nn.setWeight(2, 1, 0, -3); nn.setWeight(2, 1, 1, -7); nn.setWeight(2, 1, 2, 1.5); nn.setWeight(2, 1, 3, 5.5); nn.setWeight(2, 1, 4, 9.5);
        nn.setWeight(2, 2, 0, -4); nn.setWeight(2, 2, 1, -8); nn.setWeight(2, 2, 2, 2.5); nn.setWeight(2, 2, 3, 6.5); nn.setWeight(2, 2, 4, 10.5);
        nn.setWeight(2, 3, 0, -5); nn.setWeight(2, 3, 1, -9); nn.setWeight(2, 3, 2, 3.5); nn.setWeight(2, 3, 3, 7.5); nn.setWeight(2, 3, 4, 11.5);
        nn.setBias(2, 0, 1.4);     nn.setBias(2, 1, -1.4);    nn.setBias(2, 2, 3.2);      nn.setBias(2, 3, 4.2);      nn.setBias(2, 4, 5.2);
        
        NeuralNetworkLoader instance = new NeuralNetworkLoader();
        instance.saveAsText(nn, fileName);
        
        List<String> expectedLines = new ArrayList<String>() {{
            add(name);
            add("2, 3, 4, 5");
            add("10.5 4.1 1.0");
            add("-1.9 0.2 0.0");
            add("1.4 -1.4 3.2");
            add("2.0 5.0 8.0 11.0");
            add("3.0 6.0 9.0 12.0");
            add("4.0 7.0 10.0 13.0");
            add("1.4 -1.4 3.2 4.2");
            add("-2.0 -6.0 0.5 4.5 8.5");
            add("-3.0 -7.0 1.5 5.5 9.5");
            add("-4.0 -8.0 2.5 6.5 10.5");
            add("-5.0 -9.0 3.5 7.5 11.5");
            add("1.4 -1.4 3.2 4.2 5.2");
        }};

        File file = new File(fileName);
        assertTrue(file.exists());
        try {
            List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
            assertThat(lines, CoreMatchers.is(expectedLines));
        }
        catch(IOException e) {
            fail("Cannot read file: " + e.toString());
        }
    }
    
    @Test
    public void testLoadFromTextFile_CorrectFile_CorrectNamedNetworkCreated() {
        System.out.println("loadFromTextFile");
        String fileName = getClass().getResource("/trainerapp/gui/util/named_2_3_4_correct.txt").getFile();
        NeuralNetworkLoader instance = new NeuralNetworkLoader();
        
        NeuralNetwork nn = instance.loadFromTextFile(fileName);
        
        assertTrue(nn instanceof NamedNeuralNetwork);
        NamedNeuralNetwork namedNN = (NamedNeuralNetwork)nn;
        assertEquals("Network Name", namedNN.getName());
        assertEquals(2, namedNN.getNumberInputs());
        assertEquals(1, namedNN.getNumberHiddenLayers());
        assertEquals(3, namedNN.getHiddenLayerSize(0));
        assertEquals(4, namedNN.getNumberOutputs());
        
        double[][][] weights = TestUtils.extractNNWeights(namedNN);
        double[][] biases = TestUtils.extractNNBiases(namedNN);
        
        double[][][] expectedWeights = {
            {{1, 4}, {2, 5}, {3, 6}}, 
            {{1.5, 5.5, 9.5}, {2.5, 6.5, 10.5}, {3.5, 7.5, 11.5}, {4.5, 8.5, 12.5}}
        };
    
        double[][] expectedBiases = {
            {-1, -2, -3}, 
            {-1.5, -2.5, -3.5, -4.5}
        };
        
        TestUtils.assertArraysEqual(expectedWeights, weights);
        TestUtils.assertArraysEqual(expectedBiases, biases);
    }
    
    @Test
    public void testLoadFromTextFile_CorrectNoNameFile_CorrectNetworkCreated() {
        System.out.println("loadFromTextFile");
        String fileName = getClass().getResource("/trainerapp/gui/util/no_named_2_3_4_correct.txt").getFile();
        NeuralNetworkLoader instance = new NeuralNetworkLoader();
        
        NeuralNetwork nn = instance.loadFromTextFile(fileName);
        
        assertTrue(!(nn instanceof NamedNeuralNetwork));

        assertEquals(2, nn.getNumberInputs());
        assertEquals(1, nn.getNumberHiddenLayers());
        assertEquals(3, nn.getHiddenLayerSize(0));
        assertEquals(4, nn.getNumberOutputs());
        
        double[][][] weights = TestUtils.extractNNWeights(nn);
        double[][] biases = TestUtils.extractNNBiases(nn);
        
        double[][][] expectedWeights = {
            {{1, 4}, {2, 5}, {3, 6}}, 
            {{1.5, 5.5, 9.5}, {2.5, 6.5, 10.5}, {3.5, 7.5, 11.5}, {4.5, 8.5, 12.5}}
        };
    
        double[][] expectedBiases = {
            {-1, -2, -3}, 
            {-1.5, -2.5, -3.5, -4.5}
        };
        
        TestUtils.assertArraysEqual(expectedWeights, weights);
        TestUtils.assertArraysEqual(expectedBiases, biases);
    }
}
