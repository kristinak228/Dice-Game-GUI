package application;
	
import javafx.application.Application;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import javafx.application.Platform;
import javafx.event.*;  // Contains EventHandler & ActionEvent
import javafx.stage.*; // Contains Stage and StageStyle
import javafx.scene.*; // Contains Scene, Group, and Node
import javafx.scene.control.*; // Contains Button
import javafx.scene.image.*; // Contains ImageView & Image
import javafx.scene.layout.*; // Contains subclasses of anchorPane
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;

/**
 * Implemented on October 21st, 2016
 * 
 * @author Kristina Kolibab
 * 
 * This program holds three buttons, one to start and create the die, one to roll the
 * die, and one to end the die game.  The player may create a loaded or fair die, and
 * if loaded, the player will be prompted to input the loadFactor and LoadedSide.  For
 * the game itself, the first value rolled becomes their point value, they must roll 
 * this again to win.  It at any time during the game the player rolls a 1, they lose. 
 */

public class Main extends Application {
	
	//STEP 0: INITIALIZE GLOBAL VARIABLES
	private int dieTop; // the number at the top of the die
	private int highestRoll; // the largest number you can roll, allowed to change 
	final protected int lowestRoll = 1; // the lowest number you can roll, doesn't change
	private int numberOfSides; //number of sides on die
	protected static Random rand = new Random(); // the random generator for the die
	private int loadFactor; //the percentage of rolling the loaded die side
	private int loadedSide; //which side of the die will be weighted
	private int rollValue; //the value that has been rolled
	private String typeAnswer; //specifies whether or not the die is loaded/fair
	private int point; //the first value rolled
	private boolean btnClicked = false; //tests to see if rollDieButton has been clicked, so that the point value may be saved
	private boolean firstTimeThrough = true; //used to know when to compare point value to future rolled die's	
	
	//keep everything in a try block
	public void start(Stage primaryStage) {
		try {
			primaryStage.show();
			
			//STEP 1: FIGURE OUT LAYOUT
			//STEP (A): CREATE SCENE ELEMENTS
			
			/* 
			 * The root layout manager object will be 
			 * a GridPane. Set its style to 'graytheme'
			 * in application.css. The style sets the 
			 * background to an off blue, and adjusts the padding
			 */
			GridPane root = new GridPane();
			root.getStyleClass().add("graytheme");
			
			/*
			 * The image viewer sits in an AnchorPane
			 * which in turn is added to the root GridPane.
			 * Fix its size to (300, 400)
			 */
			AnchorPane anchorPane = new AnchorPane();
			anchorPane.setMinSize(300.0, 400.0);
			anchorPane.setMaxSize(300.0, 400.0);
			anchorPane.setPrefSize(300.0, 400.0);
			
			/*
			 * The HBox is a layout manager object 
			 * that allows placing objects side-by-side
			 * (similar to this is the VBox). All buttons
			 * are placed in the HBox, and its style is
			 * also graytheme
			 */
			HBox buttonsBox = new HBox();
			buttonsBox.getStyleClass().add("graytheme");
			
			/*
			 * startButton begins the game by asking how
			 * many sides you would like, whether or not 
			 * you will create a loaded/fair die, and if 
			 * loaded, will then prompt for loaded side
			 * and load factor, its style is buttontheme
			 */
			Button startButton = new Button();
			startButton.setText("Start");
			startButton.getStyleClass().add("buttontheme");
			
			/*
			 * rollDieButton lets you begin to roll the die
			 * set your initial point value, and then roll
			 * for either a 1 or your point value to either
			 * win or lose, its style is buttontheme
			 */
			Button rollDieButton = new Button();
			rollDieButton.setText( "Click to roll" );
			rollDieButton.getStyleClass().add("buttontheme");
			
			/*
			 * quitButton lets you quit the program
			 */
			Button quitButton = new Button();
			quitButton.setText( "Quit" );
			quitButton.getStyleClass().add("buttontheme");
			
			/*
			 * The image viewer, the image will be loaded here 
			 */
			ImageView imageView = new ImageView();
						
			/*
			 * The scene is created from the GridanchorPane root
			 */
			Scene scene = new Scene( root, 500, 500 );
			
			/*
			 * Add the file application.css as a resource to the
			 * scene's style sheets
			 */
			scene.getStylesheets().add( getClass().getResource("application.css").toExternalForm() );
			
			//STEP (B): CREATE SCENE GRAPH
			
			/*
			 * Buttons go into the buttonsBox
			 */
			buttonsBox.getChildren().add( startButton );
			buttonsBox.getChildren().add( rollDieButton );
			buttonsBox.getChildren().add( quitButton );
			
			/*
			 * The imageView goes into anchorPane
			 */
			anchorPane.getChildren().add( imageView );
			
			/*
			 * The anchorPane and buttonsBox go into the
			 * GridPane root. The add() method of GridPane lets you 
			 * directly add elements at different indices
			 * in a grid, instead of using getChildren().add()
			 */
			root.add( anchorPane, 0, 0 );
			root.add( buttonsBox, 0, 1 );
			
			/*
			 * Force the image to fit within the bounds of the anchorPane
			 */
			imageView.fitHeightProperty().bind( anchorPane.heightProperty() );
			imageView.fitWidthProperty().bind( anchorPane.widthProperty() );
			
			/*
			 * Preserve the aspect ratio of the image
			 */
			imageView.setPreserveRatio(true);
			
			//STEP 2: SET UP EVENT HANDLING
				
			/**
			 * 
			 * This startButton sets the rollDieButton disable method and btnClicked
			 * variable to false, reset the point value to 0, then prompts the player for 
			 * what type of die they would like to create, whether it be loaded or fair.  
			 * Then an image containing all of the rules is displayed and remains so until 
			 * the player clicks roll.
			 * 
			 * @throws IllegalArgumentException if numberOfSides is less than 3 or greater 
			 * than 50, if the loadFactor is less than 1 or greater than 100, or if the 
			 * loadedSide is less than 1 or greater than their numberOfSides
			 * 
			 */
			startButton.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ) {
					//reset everything
					rollDieButton.setDisable(false); //if the player chooses to play again, make sure the roll button will work
					point = 0; //that way whichever number they roll first, won't match and return loser/winner
					//btnClicked = false; //resetting so the point value can again be saved for a new game
					
					//this is the first question the user is prompted for
					//creating a text dialog so user can choose numberOfSides
					TextInputDialog dialog = new TextInputDialog(); 
					dialog.setTitle("Die Game");
					dialog.setHeaderText("Specify how many sides you would like (between 1-50)"); 
					dialog.setContentText("Number of sides:");	
					
					//option for choosing loaded or fair die
					List<String> whatDie = new ArrayList<>();
					whatDie.add("Loaded Die");
					whatDie.add("Fair Die");
					
					//creating a choice dialog so user can choose between loaded/fair die
					ChoiceDialog<String> typeDialogue = new ChoiceDialog<>("Please choose an option", whatDie);
					typeDialogue.setTitle("Die Game");
					typeDialogue.setHeaderText("Specify your Die type");
					typeDialogue.setContentText("Choose your Die type");

					Optional<String> result = dialog.showAndWait(); // wait for the user's input
					Optional<String> result2 = typeDialogue.showAndWait();
					if ( (result.isPresent()) && (result2.isPresent()) ){ // if the user types a non-null value...
						
						try {
							String stringSides = result.get(); 
							numberOfSides = Integer.parseInt(stringSides);
							if( (numberOfSides < 3) || (numberOfSides > 50) ){
								throw new IllegalArgumentException("Error: n must be a positive number that is between 3-50\n");
							} //will throw exception if numberOfSides is out of bounds, i.e. < 3 or > 50
								
							typeAnswer = result2.get(); //made typeAnswer global so I could also reference it while rolling	
								
							if(typeAnswer == "Loaded Die"){
									//now ask for loadedSide and loadFactor
									Dialog<Pair<Integer, Integer>> loadDialogue = new Dialog<>();
									loadDialogue.setTitle("Die Game");
									loadDialogue.setHeaderText("Please choose your load factor and loaded side");
										
									//Creating text dialogs to choose a loadFactor and loadedSide
									TextInputDialog loadF = new TextInputDialog();
									loadF.setTitle("Die Game");
									loadF.setHeaderText("Choose a number between 1-100");
									loadF.setContentText("Load Factor");
									TextInputDialog loadS = new TextInputDialog();
									loadS.setTitle("Die Game");
									loadS.setHeaderText("Choose one of your sides");
									
									loadS.setContentText("Loaded Side (out of " + numberOfSides + ")"); 
										
									Optional<String> resultFactor = loadF.showAndWait();
									Optional<String> resultLoaded = loadS.showAndWait();
										
									if( (resultFactor.isPresent()) && (resultLoaded.isPresent()) ){
										String stringFactor = resultFactor.get(); 
										loadFactor = Integer.parseInt(stringFactor); //change loadFactor from a string to an int
										if((loadFactor < 1) || (loadFactor > 100)){
											throw new IllegalArgumentException("Error: load factor must be within 1-100");
										} //will throw an exception if user inputs a loadFactor < 1 or > 100
										String stringLoaded = resultLoaded.get(); 
										loadedSide = Integer.parseInt(stringLoaded); //change loadedSide from a string to an int
										if(loadedSide > numberOfSides){
											throw new IllegalArgumentException("Error: loaded side must be within 1-" + numberOfSides);
										} //will throw an exception if user inputs a loadedSide larger than the numberOfSides they specified
									}
							} 
							//setting initial picture to be the rules of the game, also setting numberOfSides and highestRoll
							highestRoll = numberOfSides; 
							Image imageName = new Image(Main.class.getResourceAsStream("DieGameRules.jpg")); 
							imageView.setImage( imageName );	
						} catch ( IllegalArgumentException iae ) {
							System.err.println(iae.getMessage());
						}
					}
				}	
			});
			
			/**
			 * 
			 * This rollDieButton will roll either a loaded or fair die and if this is the 
			 * users first roll, that value will be saved as the point value.  Then, every 
			 * other time the die is rolled, it will go through and check to see which value 
			 * was rolled so that it may display the correct image.  Next the point value 
			 * will be compared with the current value rolled, if equal, the player wins.  
			 * If at any point the player rolls a 1, they lose.
			 * 
			 * @throws NullPointerException if any of the images that are trying to be
			 * loaded fail to load, most likely this will occur if the image is not within
			 * the package directory
			 * 
			 */
			rollDieButton.setOnAction( new EventHandler<ActionEvent>() {
				public void handle( ActionEvent event ) {
					
					//The die is rolled here, either as a loaded or fair die
					if(typeAnswer == "Loaded Die"){
						//rolls from 1-100, compares with user given factor, if value is not bigger than roll factor, loaded side is rolled, 
						//else, roll as fair die, but will keep rolling as fair until it rolls a number that is not the loadedSide
						rollValue = rand.nextInt((100 - 1) + 1) + 1; // generates a new randomized integer
						if(rollValue <= loadFactor){ //checks to see if value is less than load factor, if so it will print loaded die
							dieTop = loadedSide;
						} else {
							dieTop = rand.nextInt((highestRoll - lowestRoll) + 1) + lowestRoll; // generates a new randomized integer
							while(dieTop == loadedSide){ //keeps rolling until it rolls anything but the loaded die
								dieTop = rand.nextInt((highestRoll - lowestRoll) + 1) + lowestRoll; // generates a new randomized integer
							}
						}
					} else { //it will roll a fair die
						dieTop = rand.nextInt((highestRoll - lowestRoll) + 1) + lowestRoll; // generates a new randomized integer
					}
					if(btnClicked == false){ //the first time a user rolls, it will set the point value, afterwards the btnClicked will
											 //be true and this portion of code will then always be skipped over
						btnClicked = true;
						point = dieTop; //keeps the first value that is rolled
						Image imageName = new Image(Main.class.getResourceAsStream("Die_1.jpg")); 
						imageView.setImage( imageName );
						if(point == 1){ 
							//set a timer, after 2s a new image will appear saying the player lost
							Image newImageName = new Image(Main.class.getResourceAsStream("Loser.jpg"));
							Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
							timeline.play();
							rollDieButton.setDisable(true);
						}
					} //else, continue down to display dieTop or end game
					try{
						//These if/else if blocks display whatever number has been rolled and test for the point value
						
						if(dieTop == 1){ //rolls a 1
							//if first roll, they lose, or if rolled before they roll their point value again, they lose
							//since this was not their first time rolling a 1, maybe print out their score?
							Image imageName = new Image(Main.class.getResourceAsStream("Die_1.jpg")); 
							imageView.setImage( imageName );
							//set a timer, after 2s a new image will appear saying the player lost
							Image newImageName = new Image(Main.class.getResourceAsStream("Loser.jpg"));
							Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
							timeline.play(); //allows the player to see their roll before being told they have lost the game
							rollDieButton.setDisable(true); //this way the player can no longer attempt to roll the die
						} 
						else if(dieTop == 2){ //rolls a 2
							Image imageName = new Image(Main.class.getResourceAsStream("Die_2.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 2){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play(); //allows the player to see their roll before being told they have lost the game
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 3){ //rolls a 3
							Image imageName = new Image(Main.class.getResourceAsStream("Die_3.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 3){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 4){ //rolls a 4
							Image imageName = new Image(Main.class.getResourceAsStream("Die_4.jpg")); 
							imageView.setImage( imageName );
							if(firstTimeThrough == false){
								if(point == 4){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 5){ //rolls a 5
							Image imageName = new Image(Main.class.getResourceAsStream("Die_5.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 5){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 6){ //rolls a 6
							Image imageName = new Image(Main.class.getResourceAsStream("Die_6.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 6){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 7){ //rolls a 7
							Image imageName = new Image(Main.class.getResourceAsStream("Die_7.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 7){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 8){ //rolls a 8
							Image imageName = new Image(Main.class.getResourceAsStream("Die_8.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 8){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 9){ //rolls a 9
							Image imageName = new Image(Main.class.getResourceAsStream("Die_9.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 9){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 10){ //rolls a 10
							Image imageName = new Image(Main.class.getResourceAsStream("Die_10.jpg")); 
							imageView.setImage( imageName );
							if(firstTimeThrough == false){
								if(point == 10){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 11){ //rolls a 11
							Image imageName = new Image(Main.class.getResourceAsStream("Die_11.jpg")); 
							imageView.setImage( imageName );
							if(firstTimeThrough == false){
								if(point == 11){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 12){ //rolls a 12
							Image imageName = new Image(Main.class.getResourceAsStream("Die_12.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 12){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 13){ //rolls a 13
							Image imageName = new Image(Main.class.getResourceAsStream("Die_13.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 13){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 14){ //rolls a 14
							Image imageName = new Image(Main.class.getResourceAsStream("Die_14.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 14){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 15){ //rolls a 15
							Image imageName = new Image(Main.class.getResourceAsStream("Die_15.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 15){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 16){ //rolls a 16
							Image imageName = new Image(Main.class.getResourceAsStream("Die_16.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 16){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 17){ //rolls a 17
							Image imageName = new Image(Main.class.getResourceAsStream("Die_17.jpg")); 
							imageView.setImage( imageName );
							if(firstTimeThrough == false){
								if(point == 17){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 18){ //rolls a 18
							Image imageName = new Image(Main.class.getResourceAsStream("Die_18.jpg")); 
							imageView.setImage( imageName );
							if(firstTimeThrough == false){
								if(point == 18){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 19){ //rolls a 19
							Image imageName = new Image(Main.class.getResourceAsStream("Die_19.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 19){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 20){ //rolls a 20
							Image imageName = new Image(Main.class.getResourceAsStream("Die_20.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 20){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 21){ //rolls a 21
							Image imageName = new Image(Main.class.getResourceAsStream("Die_21.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 21){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 22){ //rolls a 22
							Image imageName = new Image(Main.class.getResourceAsStream("Die_22.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 22){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 23){ //rolls a 23
							Image imageName = new Image(Main.class.getResourceAsStream("Die_23.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 23){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 24){ //rolls a 24
							Image imageName = new Image(Main.class.getResourceAsStream("Die_24.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 24){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 25){ //rolls a 25
							Image imageName = new Image(Main.class.getResourceAsStream("Die_25.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 25){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 26){ //rolls a 26
							Image imageName = new Image(Main.class.getResourceAsStream("Die_26.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 26){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 27){ //rolls a 27
							Image imageName = new Image(Main.class.getResourceAsStream("Die_27.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 27){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 28){ //rolls a 28
							Image imageName = new Image(Main.class.getResourceAsStream("Die_28.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 28){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 29){ //rolls a 29
							Image imageName = new Image(Main.class.getResourceAsStream("Die_29.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 29){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 30){ //rolls a 30
							Image imageName = new Image(Main.class.getResourceAsStream("Die_30.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 30){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 31){ //rolls a 31
							Image imageName = new Image(Main.class.getResourceAsStream("Die_31.jpg")); 
							imageView.setImage( imageName );
							if(firstTimeThrough == false){
								if(point == 31){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 32){ //rolls a 32
							Image imageName = new Image(Main.class.getResourceAsStream("Die_32.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 32){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 33){ //rolls a 33
							Image imageName = new Image(Main.class.getResourceAsStream("Die_33.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 33){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 34){ //rolls a 34
							Image imageName = new Image(Main.class.getResourceAsStream("Die_34.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 34){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 35){ //rolls a 35
							Image imageName = new Image(Main.class.getResourceAsStream("Die_35.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 35){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 36){ //rolls a 36
							Image imageName = new Image(Main.class.getResourceAsStream("Die_36.jpg")); 
							imageView.setImage( imageName );
							if(firstTimeThrough == false){
								if(point == 36){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 37){ //rolls a 37
							Image imageName = new Image(Main.class.getResourceAsStream("Die_37.jpg")); 
							imageView.setImage( imageName );
							if(firstTimeThrough == false){
								if(point == 37){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 38){ //rolls a 38
							Image imageName = new Image(Main.class.getResourceAsStream("Die_38.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 38){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 39){ //rolls a 39
							Image imageName = new Image(Main.class.getResourceAsStream("Die_39.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 39){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 40){ //rolls a 40
							Image imageName = new Image(Main.class.getResourceAsStream("Die_40.jpg")); 
							imageView.setImage( imageName );
							if(firstTimeThrough == false){
								if(point == 40){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 41){ //rolls a 41
							Image imageName = new Image(Main.class.getResourceAsStream("Die_41.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 41){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 42){ //rolls a 42
							Image imageName = new Image(Main.class.getResourceAsStream("Die_42.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 42){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 43){ //rolls a 43
							Image imageName = new Image(Main.class.getResourceAsStream("Die_43.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 43){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 44){ //rolls a 44
							Image imageName = new Image(Main.class.getResourceAsStream("Die_44.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 44){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 45){ //rolls a 45
							Image imageName = new Image(Main.class.getResourceAsStream("Die_45.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 45){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 46){ //rolls a 46
							Image imageName = new Image(Main.class.getResourceAsStream("Die_46.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 46){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 47){ //rolls a 47
							Image imageName = new Image(Main.class.getResourceAsStream("Die_47.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 47){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 48){ //rolls a 48
							Image imageName = new Image(Main.class.getResourceAsStream("Die_48.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 48){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 49){ //rolls a 49
							Image imageName = new Image(Main.class.getResourceAsStream("Die_49.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 49){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						} else if(dieTop == 50){ //rolls a 50
							Image imageName = new Image(Main.class.getResourceAsStream("Die_50.jpg")); 
							imageView.setImage( imageName ); 
							if(firstTimeThrough == false){
								if(point == 50){
									//set a timer, after 2s a new image will appear saying the player won
									Image newImageName = new Image(Main.class.getResourceAsStream("Winner.jpg"));
									Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(imageView.imageProperty(), newImageName)));
									timeline.play();
									rollDieButton.setDisable(true);
								}
							}
							firstTimeThrough = false;
						}						
					} catch ( NullPointerException npe ) {
						System.err.println( "Image not available in package directory." );
					}
				}
			});
			
			/**
			 * 
			 * This quitButton closes the window and exits out of the 
			 * application, this is put inside a catch block in case 
			 * any errors occur while closing out that need catching.
			 * 
			 * @throws Exception if any errors occur while closing
			 * the program 
			 *  
			 */
			quitButton.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ) {
					try {
						primaryStage.close(); // closes the window
						Platform.exit(); // exits the current application thread		
					} catch ( Exception e ) {
						e.printStackTrace(System.err);
					}
				}
			});	
			
			//STEP 3: SET SCENE OF STAGE AND SHOW THE STAGE
	
			primaryStage.setScene( scene );
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
