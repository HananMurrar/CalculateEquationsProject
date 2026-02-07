package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main extends Application {
	private static int currentSectionIndex = 0;
	private static String[] sections;

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) {
		welcomeFrame(stage);
	}

	public Button designButton(String name, int width, int height) {
		Button button = new Button(name);
		button.setPrefSize(width, height);
		button.setStyle("-fx-background-color: #014920; -fx-background-radius: 15px; -fx-text-fill: #fff");
		return button;
	}

	public void welcomeFrame(Stage stage) {
		BorderPane pane = new BorderPane();

		Label label1 = new Label("File Path :");
		Label label2 = new Label("Equations :");

		TextArea text1 = new TextArea();
		TextArea text2 = new TextArea();

		text1.setMaxSize(380, 55);
		text2.setMaxSize(380, 200);

		Button load = designButton("Load", 120, 50);
		Button previous = designButton("Previous", 120, 50);
		Button next = designButton("Next", 120, 50);

		HBox hbox = new HBox(5);
		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().addAll(load, previous, next);

		VBox vbox = new VBox(10);
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(label1, text1, label2, text2, hbox);

		pane.setCenter(vbox);

		pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

		load.setOnAction(event -> {
			readFile(stage, text1, text2);
			// display equations for the current section
			displayEquations(text2);
		});

		previous.setOnAction(event -> {
			if (currentSectionIndex > 0) {
				currentSectionIndex--;
			}
			displayEquations(text2);
		});

		next.setOnAction(event -> {
			if (currentSectionIndex < sections.length - 1) {
				currentSectionIndex++;
			}
			displayEquations(text2);
		});

		stage.setMaximized(true);
		Scene scene = new Scene(pane, 0, 0);
		stage.setScene(scene);
		stage.setTitle("Calculate Equations");
		stage.show();
	}

	// for equations

	private static int precedence(String operator) {
		switch (operator) {
		case "+":
		case "-":
			return 1;
		case "*":
		case "/":
			return 2;
		case "^":
			return 3;
		default:
			return -1;
		}
	}

	private static boolean isOperand(String token) {
		return !token.equals("+") && !token.equals("-") && !token.equals("*") && !token.equals("/")
				&& !token.equals("^");
	}

	private static boolean isOperator(String token) {
		return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
	}

	private static double applyOperator(String operator, double operand1, double operand2) {
		switch (operator) {
		case "+":
			return operand1 + operand2;
		case "-":
			return operand1 - operand2;
		case "*":
			return operand1 * operand2;
		case "/":
			if (operand2 != 0) {
				return operand1 / operand2;
			} else {
				throw new ArithmeticException("Division By Zero");
			}
		case "^":
			return Math.pow(operand1, operand2);
		default:
			throw new IllegalArgumentException("Invalid Operator");
		}
	}

	private static String infixToPostfix(String infix) {
		CursorArrayStack<String> stack = new CursorArrayStack<>(50);
		String[] tokens = infix.split(" ");
		String result = "";
		for (String token : tokens) {
			switch (token) {
			case "+":
			case "-":
			case "*":
			case "/":
			case "^":
				while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(token)) {
					result += stack.pop() + " ";
				}
				stack.push(token);
				break;
			case "(":
				stack.push(token);
				break;
			case ")":
				while (!stack.isEmpty() && !stack.peek().equals("(")) {
					result += stack.pop() + " ";
				}
				stack.pop();
				break;
			default:
				result += token + " ";
			}
		}
		while (!stack.isEmpty())
			result += stack.pop() + " ";
		return result;
	}

	private static double evaluatePostfix(String postfix) {
		CursorArrayStack<Double> stack = new CursorArrayStack<>(50);
		String[] tokens = postfix.split(" ");
		for (String token : tokens) {
			if (isOperand(token)) {
				stack.push(Double.parseDouble(token));
			} else {
				double operand2 = stack.pop();
				double operand1 = stack.pop();
				double result = applyOperator(token, operand1, operand2);
				stack.push(result);
			}
		}
		return stack.pop();
	}

	private static boolean isValidPostfix(String postfix) {
		String[] tokens = postfix.split(" ");
		int operandCount = 0;
		int operatorCount = 0;
		CursorArrayStack<String> stack = new CursorArrayStack<>(50);
		int stackSize = 0;
		for (String token : tokens) {
			if (isOperand(token)) {
				operandCount++;
				stack.push(token);
				stackSize++;
			} else if (isOperator(token)) {
				operatorCount++;
				if (stackSize < 2) {
					// not enough operands for the operator
					return false;
				}
				stack.pop();
				stackSize--;
			} else {
				// invalid token found
				return false;
			}
		}
		// at end there should be exactly one item in the stack which is the final result
		return operandCount == operatorCount + 1 && stackSize == 1;
	}

	private static String postfixToPrefix(String postfix) {
		if (!isValidPostfix(postfix)) {
			return "Undefined";
		}
		CursorArrayStack<String> stack = new CursorArrayStack<>(50);
		String[] tokens = postfix.split(" ");
		for (String token : tokens) {
			if (isOperand(token)) {
				stack.push(token);
			} else {
				String operand2 = stack.pop();
				String operand1 = stack.pop();
				String result = token + " " + operand1 + " " + operand2;
				stack.push(result);
			}
		}
		return stack.pop();
	}

	private static double evaluatePrefix(String prefix) {
		CursorArrayStack<Double> stack = new CursorArrayStack<>(50);
		String[] tokens = prefix.split(" ");
		for (int i = tokens.length - 1; i >= 0; i--) {
			String token = tokens[i];
			if (isOperand(token)) {
				stack.push(Double.parseDouble(token));
			} else {
				double operand1 = stack.pop();
				double operand2 = stack.pop();
				double result = applyOperator(token, operand1, operand2);
				stack.push(result);
			}
		}
		return stack.pop();
	}

	// for file

	private static boolean checkBalance(String text) {
		CursorArrayStack<String> stack = new CursorArrayStack<>(500);
		String[] tags = text.split("(?=<)|(?<=>)");
		for (String tag : tags) {
			String currentTag = tag.trim();
			if (!currentTag.isEmpty()) {
				if (currentTag.startsWith("<") && currentTag.endsWith(">")) {
					if (currentTag.startsWith("</")) {
						String openingTag = stack.pop();
						if (!openingTag.equalsIgnoreCase(currentTag.substring(2, currentTag.length() - 1))) {
							return false;
						}
					} else {
						stack.push(currentTag.substring(1, currentTag.length() - 1));
					}
				}
			}
		}
		return stack.isEmpty();
	}

	private static void readFile(Stage stage, TextArea text1, TextArea text2) {
		FileChooser fileChooser = new FileChooser();
		File file1 = fileChooser.showOpenDialog(stage);
		if (file1 != null) {
			text1.setText(file1.getPath());
			try {
				Scanner scanner = new Scanner(file1);
				StringBuilder fileContentStringBuilder = new StringBuilder();
				while (scanner.hasNextLine()) {
					fileContentStringBuilder.append(scanner.nextLine().trim()).append("\n");
				}
				scanner.close();
				String fileContentString = fileContentStringBuilder.toString();
				// check if the file content is balanced
				if (!checkBalance(fileContentString)) {
					Platform.runLater(() -> text2.appendText("Unbalanced File\n"));
					return;
				}
				// parse sections from the file content
				String[] sectionTags = fileContentString.split("<section>");
				List<String> sectionList = new ArrayList<>();
				for (String sectionTag : sectionTags) {
					if (sectionTag.contains("<infix>") && sectionTag.contains("<postfix>")) {
						sectionList.add("<section>" + sectionTag);
					}
				}
				sections = sectionList.toArray(new String[0]);
				currentSectionIndex = 0;
				// display equations for the current section
				displayEquations(text2);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private static void displayEquations(TextArea text2) {
		text2.clear();
		if (sections != null && currentSectionIndex < sections.length) {
			text2.appendText(String.format("Current Section: %d & Total Sections (Containe Data): %d%n",
					currentSectionIndex + 1, sections.length));
			// get the equations for the current section
			String currentSection = sections[currentSectionIndex];
			// parse and display equations for the current section
			Scanner sectionScanner = new Scanner(currentSection);
			boolean isInfixSection = false;
			boolean isPostfixSection = false;
			while (sectionScanner.hasNextLine()) {
				String sectionLine = sectionScanner.nextLine().trim();
				if (sectionLine.startsWith("<infix>")) {
					isInfixSection = true;
					isPostfixSection = false;
					continue;
				} else if (sectionLine.startsWith("<postfix>")) {
					isInfixSection = false;
					isPostfixSection = true;
					continue;
				} else if (sectionLine.endsWith("</infix>") || sectionLine.endsWith("</postfix>")) {
					isInfixSection = false;
					isPostfixSection = false;
					continue;
				}
				if (isInfixSection && sectionLine.startsWith("<equation>")) {
					String infixEquation = sectionLine
							.substring("<equation>".length(), sectionLine.length() - "</equation>".length()).trim();
					// display infix equation
					String infixResult = String.format("Infix: %s => ", infixEquation);
					text2.appendText(infixResult);
					// convert infix to postfix
					String postfixEquation = infixToPostfix(infixEquation);
					text2.appendText(String.format("Postfix: %s => ", postfixEquation));
					// display result
					if (postfixEquation == "Undefined") {
						text2.appendText(String.format("Result: %s%n", "Undefined"));
					} else {
						double result = evaluatePostfix(postfixEquation);
						text2.appendText(String.format("Result: %f%n", result));
					}
				}
				if (isPostfixSection && sectionLine.startsWith("<equation>")) {
					String postfixEquation = sectionLine
							.substring("<equation>".length(), sectionLine.length() - "</equation>".length()).trim();
					// display postfix equation
					String postfixResult = String.format("Postfix: %s => ", postfixEquation);
					text2.appendText(postfixResult);
					// convert postfix to prefix
					String prefixEquation = postfixToPrefix(postfixEquation);
					text2.appendText(String.format("Prefix: %s => ", prefixEquation));
					// display result
					if (prefixEquation == "Undefined") {
						text2.appendText(String.format("Result: %s%n", "Undefined"));
					} else {
						double result = evaluatePrefix(prefixEquation);
						text2.appendText(String.format("Result: %f%n", result));
					}
				}
			}
			sectionScanner.close();
		} else {
			text2.appendText("No Sections Found In File\n");
		}
	}
}
