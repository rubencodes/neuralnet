import java.io.*;
import java.util.*;

public class Neural {
	public static Random random = new Random();
	public static BufferedReader fileReader;
    public static Vector<Double> inputs = new Vector<Double>(); //input vector
	public static Vector<Vector<Double>> setOfNodes = new Vector<Vector<Double>>();
	public static int countCorrect;
    public static int countTotal;
    public static int trainCountCorrect;
    public static int trainCountTotal;
    public static int positionCounter = 0;
    public static boolean firstRead = true;
    public static Vector<Vector<Double>> errorVect = new Vector<Vector<Double>>();
    public static ArrayList<String> allOutputs = new ArrayList<String>(10);
	public static void main(String args[]) {
		String trainFilename = args[0]; //name of file to train on
        String testFilename = args[1]; //name of file to train on
		int epochs = Integer.parseInt(args[2]); //number of epochs to run
		double learningRate = Double.parseDouble(args[3]); //learning rate
		String inputRepresentation = args[4]; //format of input
		int outputRepresentation = Integer.parseInt(args[5]); //format of output
		
        int target = 0; //initialize target
		
		setOfNodes.setSize(outputRepresentation); //set size to number of nodes
		if(outputRepresentation == 1) {
            //get trained weights from one node
            setOfNodes.setElementAt(trainNode(trainFilename, outputRepresentation, inputRepresentation, target, learningRate, epochs), 0);
            
            double tempError = 0;
            double totalError = 0;
            for (int i = 0; i < errorVect.size(); i++) {
                for (int j = 0; j < errorVect.get(i).size(); j++) {
                    tempError += Math.pow(errorVect.get(i).get(j), 2);
                    
                }
                totalError += tempError;
            }
            totalError = Math.sqrt(totalError);
            System.out.println("Training Mean Squared Error: "+totalError);
       	}
        
        else if(outputRepresentation == 4){
            for(int g = 0; g < setOfNodes.size(); g++) { //for each (of 4) nodes
                setOfNodes.setElementAt(trainNode(trainFilename, outputRepresentation, inputRepresentation, g, learningRate, epochs), g);
            }
            int tempError = 0;
            double totalError = 0;
            for (int i = 0; i < errorVect.size(); i++) {
                for (int j = 0; j < errorVect.get(i).size(); j++) {
                    tempError += Math.pow(errorVect.get(i).get(j), 2);
                    
                }
                totalError += tempError;
            }
            totalError = Math.sqrt(totalError);
            System.out.println("Training Mean Squared Error: "+totalError);
        }
        else if(outputRepresentation == 10){
            setOfNodes.setSize(10); //create vector to hold nodes set to 10
            for(int a = 0; a < setOfNodes.size(); a++) {
                setOfNodes.setElementAt(trainNode(trainFilename, outputRepresentation, inputRepresentation, a, learningRate, epochs), a);
            }
            double tempError = 0;
            double totalError = 0;
            for (int i = 0; i < errorVect.size(); i++) {
                for (int j = 0; j < errorVect.get(i).size(); j++) {
                    tempError += Math.pow(errorVect.get(i).get(j), 2);
                    
                }
                totalError += tempError;
            }
            totalError = Math.sqrt(totalError);
            System.out.println("Training Mean Squared Error: "+totalError);
        }
		
		openFile(testFilename);
        
        test(testFilename, setOfNodes, inputRepresentation, outputRepresentation);
        
        closeFile();
	}
    
	//given the file type argument, this method reads the file into a vector
	public static int readFileIntoInputVector(String inputRepresentation) {
		int target = 0; //target integer
        String digitAsString = "";
        
		try {
            if (inputRepresentation.equalsIgnoreCase("bmp")) {
                String currentLine = "CurrentLine";
                if(firstRead) {
                    fileReader.readLine();//skip first few lines, as they are not relevant
                    fileReader.readLine();
                    currentLine = fileReader.readLine();
                    firstRead = false;//only skip lines on initial entry into the file
                }
                if(currentLine == null) return -2;//ensures the file is being read
                for(int i = 0; i < 32; i++) { //for each 32 rows
                    currentLine = fileReader.readLine();
                    if(currentLine == null) return -2;
                    currentLine = currentLine.trim();//removes whitespace
                    char[] inputSplitter = currentLine.toCharArray();//separates each digit into its own array element
                    
                    for (int j = 0; j < inputSplitter.length; j++) {
                       inputs.addElement(new Double(Character.getNumericValue(inputSplitter[j])));//add the line to inputs
                    }
                }
                currentLine = fileReader.readLine();
                target = Character.getNumericValue(currentLine.charAt(1));//get solution into target
            }
            else if (inputRepresentation.equalsIgnoreCase("ndsi")) {
                String currentLine = fileReader.readLine();
                if(currentLine == null) return -2;//ensures file is being read
                currentLine = currentLine.substring(2, currentLine.length()-2);//leaves out parentheses and whitespace
                String[] inputSplitter = currentLine.split(" ");//fills array with actual values from line (they are delimited by whitespace initially)
                for (int i = 0; i < inputSplitter.length; i++) {
                    inputs.addElement(new Double(Double.parseDouble(inputSplitter[i])));//add the line to inputs
                }
                
                currentLine = fileReader.readLine();
                currentLine = currentLine.substring(2, currentLine.length()-2);//leaves out parentheses and whitespace
                String[] outputSplitter = currentLine.split(" ");////fills array with actual values from line (they are delimited by whitespace initially)
                for (int i = 0; i < outputSplitter.length; i++) {
                    if (outputSplitter[i].equals("1.0"))
                        target = i;//sets target to solution value for this problem
                }
            }
            else if (inputRepresentation.equalsIgnoreCase("dsi")) {
                String currentLine = fileReader.readLine();
                if(currentLine == null) return -2;//ensures file is being read
                String[] inputSplitter = currentLine.split(",");//file has values delimited by commas, so fill array with actual values
                for (int i = 0; i < inputSplitter.length-1; i++) {
                    inputs.addElement(new Double(Double.parseDouble(inputSplitter[i])/16));//effectively converts the file into an ndsi
                }
                target = Integer.parseInt(inputSplitter[inputSplitter.length-1]);
            }
            
        } catch(IOException e) {
            e.printStackTrace();
        }
		return target;
	}
    
    public static void closeFile() {
        try {
            if (fileReader != null)fileReader.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
	public static void openFile(String filename) {
        try { //try to read from file
            fileReader = new BufferedReader(new FileReader(filename));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    //runs activation function on weighted sum
	public static double activation(double weightedSum) {
		double output = 1/(1+Math.exp(((-1)*weightedSum)+0.5));
		return output;
	}
    
    public static Vector<Double> trainNode(String filename, int outputRepresentation, String inputRepresentation, int filter, double learningRate, int epochs) {
        Vector<Double> node = null; //initialize node to null
		double error = 1; //initialize error
        double output = 0; //initialize output
        Vector<Double> trainError = new Vector<Double>();
        double meansq = 0;
        String binTarget = "";
        String binaryRep = "";
        int target;
        openFile(filename); //open our file for training
		firstRead = true;
        int targetCounter = 0;
        while((target = readFileIntoInputVector(inputRepresentation)) != -2) { //while we have not reached out end of file error
            if(outputRepresentation == 4) {
                binTarget = Integer.toBinaryString(target); //convert target to binary
                int[] targetArray = new int[4]; //array to hold binary ints
                for(int h = 0; h < targetArray.length; h++) { //add binary elements to int array
                    try {
                        targetArray[targetArray.length-h-1] = Character.digit(binTarget.charAt(binTarget.length()-h-1), 10);
                    } catch(IndexOutOfBoundsException e) { //if there is empty spaces, fill with zeros
                        targetArray[targetArray.length-h-1] = 0;
                    }
                }
                target = targetArray[filter];
                binTarget = "";
                for(int i = 0; i < targetArray.length; i++) {
                    binTarget = binTarget+targetArray[i];
                }
            }
            
            if((filter == target && outputRepresentation == 10) || outputRepresentation != 10) {
                if (node == null) {
                    node = new Vector<Double>();
                    for (int i = 0; i < inputs.size(); i++) { //for each input
                        double randWeight = 1; //initialize random weight
                        while (randWeight > 0.15) //while out random isn't less than 0.15
                            randWeight = random.nextDouble(); //get a new random double
                        if(random.nextBoolean()) randWeight = randWeight*-1; //randomly set to negative
                        node.addElement(new Double (randWeight)); //store random weight
                    }
                }
                targetCounter++;
                //train for number of epochs
                for(int i = 0; i < epochs; i++) { //for each epoch
                    double weightedSum = 0; //set/reset the weighted sum to 0
                    for(int j = 0; j < inputs.size(); j++) {
                        weightedSum += (inputs.elementAt(j) * node.elementAt(j)); //calculate weighted sum
                    }
                    output = activation(weightedSum); //retrieve output
                    if(outputRepresentation == 1 || outputRepresentation == 10)
                        output = output*10;
                    else if(outputRepresentation == 4) { //each node must have a whole number
                        if(output > 0.5) output = 1;
                        else output = 0;
                    }
                    
                    error = target - output; //calculate error
                    
                    if (i == epochs-1) {
                        if(outputRepresentation == 4) {
                            if(filter == 0) {
                                allOutputs.ensureCapacity(targetCounter);
                                allOutputs.add(Integer.toString((int)output));
                            }
                            else {
                                allOutputs.set(targetCounter-1, allOutputs.get(targetCounter-1)+((int)output));
                            }
                        }
                        trainError.add(error);
                        if(Math.round(error) == 0 && outputRepresentation != 4){
                            trainCountCorrect++;
                        }
                        else if (outputRepresentation == 4 && filter == 3) {
                            if(allOutputs.get(targetCounter-1).equals(binTarget)) {
                                trainCountCorrect++;
                            }
                        }
                    }
                    
                    for (int k = 0; k < inputs.size(); k++) { //for each input
                        double derivative = (1.64872*Math.exp(weightedSum))/(Math.pow(1.64872+Math.exp(weightedSum), 2)); //calculate new weights
                        double newWeight = node.elementAt(k) + (learningRate * inputs.elementAt(k) * error * derivative); //cont.
                        node.setElementAt(newWeight, k); //update weights
                    }
                }
            }
            inputs.clear();
		}
        errorVect.addElement((Vector<Double>)trainError.clone());
        trainError.clear();
        if (outputRepresentation == 4 && filter == 3)
            System.out.println("Training Percent: "+(100*(double)trainCountCorrect/(double)targetCounter));
        if (outputRepresentation == 10 && filter == 9)
            System.out.println("Training Percent: "+(10*(double)trainCountCorrect/(double)targetCounter));
        if (outputRepresentation == 1)
            System.out.println("Training Percent: "+(100*(double)trainCountCorrect/(double)targetCounter));
        closeFile();
        return node;//node is now trained for an epoch
    }
    
    public static double test(String filename, Vector<Vector<Double>> setOfNodes, String inputRepresentation, int outputRepresentation) {
        Vector<Double> testError = new Vector<Double>();
        int target = 0;
        String binary = "";
        int baseTen = 0;
        inputs.clear();
        firstRead = true;
        
        while((target = readFileIntoInputVector(inputRepresentation)) != -2) {
            firstRead = false;
            double minError = 10;
			int solution = 0;
            for (int i = 0; i < setOfNodes.size(); i++) {
                double currentError = testNode(outputRepresentation, target, setOfNodes.get(i));
                double absValCurrentError = Math.abs(currentError);
				if (absValCurrentError < minError) {
                    minError = absValCurrentError;//update minimum error across function if we find a smaller one
                }
                if(outputRepresentation == 4) {
                    binary = binary+((int)(target-currentError));//fills with binary representation
                    baseTen = Integer.parseInt(binary, 2);
                }
            }
            if (outputRepresentation == 4) {
                if (target-baseTen == 0) {
                    countCorrect++;
                }
            }
            else if(target-Math.round(minError) == target) {//if error is low enough that we will get the right digit for this node
                countCorrect++;
            }
            baseTen = 0;
            binary = "";
            
            countTotal++;
            testError.add(minError);
			inputs.clear();
        }
        
        double euclidianDistance = 0;
        for(double err : testError) {
            euclidianDistance += Math.pow(err, 2);
        }
        euclidianDistance = Math.sqrt(euclidianDistance);
        System.out.println("Test Mean Squared Error: "+euclidianDistance);
        System.out.println("Test Percent: "+(100*(double)countCorrect/(double)countTotal));
        return 0;
    }
    
    public static double testNode(int outputRepresentation, int target, Vector<Double> testWeights) {
        double error = 1; //initialize error
        double output = 0; //initialize output
        
        //train for number of epochs
        double weightedSum = 0; //set/reset the weighted sum to 0
        for(int j = 0; j < inputs.size(); j++) {
            weightedSum += (inputs.elementAt(j) * testWeights.elementAt(j)); //calculate weighted sum
        }
        
        output = activation(weightedSum); //retrieve output
        if(outputRepresentation == 1 || outputRepresentation == 10)
            output = output*10;
        else if(outputRepresentation == 4) {
            if(output > 0.5) output = 1;
            else output = 0;
        }
        error = target - output; //calculate error
        
        return error;
    }
}
