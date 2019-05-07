package trainerapp.gui.model;

import neuralnetwork.NeuralNetwork;
import static trainerapp.gui.testutil.TestUtils.assertNNEquals;
import static trainerapp.gui.testutil.TestUtils.assertNNNotEquals;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for NamedNeuralNetwork class
 * @author Konstantin Zhdanov
 */
public class NamedNeuralNetworkTest {
    
    @Test
    public void testConstructorFourArgs_ValidValuesPassed_CreateRequestedStructureAndName() {
        int nInputs = 2;
        int[] hiddenLayerSizes = {2, 3};
        int nOutputs = 4;
        String name = "Placeholder_Name";
        NamedNeuralNetwork nn = new NamedNeuralNetwork(nInputs, hiddenLayerSizes, 
                nOutputs, name);
        
        Assert.assertEquals(nInputs, nn.getNumberInputs());
        Assert.assertEquals(hiddenLayerSizes.length, nn.getNumberHiddenLayers());
        Assert.assertArrayEquals(hiddenLayerSizes, nn.getHiddenLayerSizes());
        Assert.assertEquals(nOutputs, nn.getNumberOutputs());
        Assert.assertEquals(name, nn.getName());
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorFourArgs_NullNamePassed_Throw() {
        int nInputs = 2;
        int[] hiddenLayerSizes = {2, 3};
        int nOutputs = 4;
        NamedNeuralNetwork nn = new NamedNeuralNetwork(nInputs, hiddenLayerSizes, 
                nOutputs, null);
        
        Assert.fail("The test case must throw");
    }
    
    @Test
    public void testConstructorTwoArgs_ValidValuesPassed_CreateRequestedStructureAndName() {
        int nInputs = 2;
        int[] hiddenLayerSizes = {2, 3};
        int nOutputs = 4;
        String name = "Placeholder_Name";
        NeuralNetwork parent = new NeuralNetwork(nInputs, hiddenLayerSizes, nOutputs);
        NamedNeuralNetwork nn = new NamedNeuralNetwork(parent, name);
        
        Assert.assertEquals(nInputs, nn.getNumberInputs());
        Assert.assertEquals(hiddenLayerSizes.length, nn.getNumberHiddenLayers());
        Assert.assertArrayEquals(hiddenLayerSizes, nn.getHiddenLayerSizes());
        Assert.assertEquals(nOutputs, nn.getNumberOutputs());
        Assert.assertEquals(name, nn.getName());
    }
    
    @Test
    public void testConstructorTwoArgs_NamedNNPassed_CreateRequestedStructureAndName() {
        int nInputs = 2;
        int[] hiddenLayerSizes = {2, 3};
        int nOutputs = 4;
        String name = "Placeholder_Name";
        String newName = "New Name";
        NamedNeuralNetwork parent = new NamedNeuralNetwork(nInputs, hiddenLayerSizes, nOutputs, name);
        NamedNeuralNetwork nn = new NamedNeuralNetwork(parent, newName);
        
        Assert.assertEquals(nInputs, nn.getNumberInputs());
        Assert.assertEquals(hiddenLayerSizes.length, nn.getNumberHiddenLayers());
        Assert.assertArrayEquals(hiddenLayerSizes, nn.getHiddenLayerSizes());
        Assert.assertEquals(nOutputs, nn.getNumberOutputs());
        Assert.assertEquals(newName, nn.getName());
        Assert.assertEquals(name, parent.getName());
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorTwoArgs_NullFirstArgument_Throw() {

        NamedNeuralNetwork nn = new NamedNeuralNetwork(null, "Name");
        
        Assert.fail("The test case must throw");
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorTwoArgs_NullSecondArgument_Throw() {
        NeuralNetwork nn = new NeuralNetwork(1, new int[] {1, 2}, 1);
        
        NamedNeuralNetwork namedNN = new NamedNeuralNetwork(nn, null);
        
        Assert.fail("The test case must throw");
    }
    
    @Test
    public void testConstructorOneArg_NamedNNPassed_CreateRequestedStructureAndName() {
        int nInputs = 2;
        int[] hiddenLayerSizes = {2, 3};
        int nOutputs = 4;
        String name = "Placeholder_Name";
        NamedNeuralNetwork parent = new NamedNeuralNetwork(nInputs, hiddenLayerSizes, nOutputs, name);
        NamedNeuralNetwork nn = new NamedNeuralNetwork(parent);
        
        Assert.assertEquals(nInputs, nn.getNumberInputs());
        Assert.assertEquals(hiddenLayerSizes.length, nn.getNumberHiddenLayers());
        Assert.assertArrayEquals(hiddenLayerSizes, nn.getHiddenLayerSizes());
        Assert.assertEquals(nOutputs, nn.getNumberOutputs());
        Assert.assertEquals(name, nn.getName());
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorOneArg_NullArgument_Throw() {

        NamedNeuralNetwork nn = new NamedNeuralNetwork(null);
        
        Assert.fail("The test case must throw");
    }
    
    /**
     * Test of getName method, of class NamedNeuralNetwork.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        int nInputs = 2;
        int[] hiddenLayerSizes = {2, 3};
        int nOutputs = 4;
        String name = "Placeholder_Name";
        String expected = new String(name);
        NamedNeuralNetwork nn = new NamedNeuralNetwork(nInputs, hiddenLayerSizes, 
                nOutputs, name);
        
        String actual = nn.getName();
        
        Assert.assertEquals("Name is not correct", expected, actual);
    }

    /**
     * Test of setName method, of class NamedNeuralNetwork.
     */
    @Test
    public void testSetName_Invoked_GetNameReturnNewName() {
        System.out.println("setName");
        int nInputs = 2;
        int[] hiddenLayerSizes = {2, 3};
        int nOutputs = 4;
        String name = "Placeholder_Name";
        String expected = "Test Network";
        NamedNeuralNetwork nn = new NamedNeuralNetwork(nInputs, hiddenLayerSizes, 
                nOutputs, name);
        
        nn.setName(new String(expected));
        
        String actual = nn.getName();
        
        Assert.assertEquals("Name is not changed", expected, actual);
    }

    /**
     * Test of toString method, of class NamedNeuralNetwork.
     */
    @Test
    public void testToString_Invoked_ReturnCorrectNameAndStructure() {
        System.out.println("toString");
        int nInputs = 2;
        int[] hiddenLayerSizes = {2, 3};
        int nOutputs = 4;
        String name = "Test Network";
        String expected = name + " (2, 2, 3, 4)";
        
        NamedNeuralNetwork nn = new NamedNeuralNetwork(nInputs, hiddenLayerSizes, 
                nOutputs, name);
        
        String actual = nn.toString();
        
        Assert.assertEquals("String format is different", expected, actual);
    }
    
    @Test
    public void testSerialization_WriteToObjectStream_ReadEqualStructureAndWeightsAndNameObject() {
        int nInputs = 2;
        int[] hiddenLayerSizes = {2, 3};
        int nOutputs = 4;
        String name = "Test Network";
        
        NamedNeuralNetwork nn = new NamedNeuralNetwork(nInputs, hiddenLayerSizes, 
                nOutputs, name);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(nn);
        }
        catch(IOException e) {
            Assert.fail("Exception caught: " + e.toString());
        }
        
        NamedNeuralNetwork readNN = null;
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            readNN = (NamedNeuralNetwork)ois.readObject();
        }
        catch(IOException e) {
            Assert.fail("Exception caught: " + e.toString());
        }
        catch(ClassNotFoundException e) {
            Assert.fail("Class not found while reading: " + e.toString());
        }
        
        Assert.assertNotSame(nn, readNN);
        assertNNEquals(nn, readNN);
        Assert.assertEquals(nn.getName(), readNN.getName());
        
        readNN.setWeight(0, 0, 1, 10.0);
        assertNNNotEquals(nn, readNN);
        
        nn.setName("New Name");
        Assert.assertNotEquals(nn.getName(), readNN.getName());
    }
    
}
