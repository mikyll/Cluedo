module cluedo {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.base;
	requires transitive javafx.graphics;
	
	opens controller;
	
	exports application;
	/*exports view;
	exports model;
	exports persistence;*/
}