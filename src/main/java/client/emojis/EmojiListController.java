package client.emojis;


import client.App;
import client.Main;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;


/**
 * Created by UltimateZero on 9/12/2016.
 */
public class EmojiListController {

	private static final boolean SHOW_MISC = false;
	@FXML
	private ScrollPane searchScrollPane;
	@FXML
	private FlowPane searchFlowPane;
	@FXML
	private TabPane tabPane;
	@FXML
	private TextField txtSearch;

	private App app;

	public void setapp(App app) {
		this.app = app;
	}

	@FXML
	void initialize() {
		if(!SHOW_MISC) {
			tabPane.getTabs().remove(tabPane.getTabs().size()-2, tabPane.getTabs().size());
		}

		searchScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		searchFlowPane.prefWidthProperty().bind(searchScrollPane.widthProperty().subtract(5));
		searchFlowPane.setHgap(5);
		searchFlowPane.setVgap(5);

		txtSearch.textProperty().addListener(x-> {
			String text = txtSearch.getText();
			if(text.isEmpty() || text.length() < 2) {
				searchFlowPane.getChildren().clear();
				searchScrollPane.setVisible(false);
			} else {
				searchScrollPane.setVisible(true);
				List<Emoji> results = EmojiHandler.getInstance().search(text);
				searchFlowPane.getChildren().clear();
				results.forEach(emoji ->searchFlowPane.getChildren().add(createEmojiNode(emoji)));
			}
		});

		//初始化每个标签页
		for(Tab tab : tabPane.getTabs()) {
			ScrollPane scrollPane = (ScrollPane) tab.getContent();
			FlowPane pane = (FlowPane) scrollPane.getContent();
			pane.setPadding(new Insets(5));
			scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			pane.prefWidthProperty().bind(scrollPane.widthProperty().subtract(5));
			pane.setHgap(5);
			pane.setVgap(5);

			tab.setId(tab.getText());
			ImageView icon = new ImageView();
			icon.setFitWidth(20);
			icon.setFitHeight(20);
			switch (tab.getText().toLowerCase()) {
				case "frequently used":
					icon.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(EmojiHandler.getInstance().getEmoji("heart").getHex())));
					break;
				case "people":
					icon.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(EmojiHandler.getInstance().getEmoji("smiley").getHex())));
					break;
				case "nature":
					icon.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(EmojiHandler.getInstance().getEmoji("dog").getHex())));
					break;
				case "food":
					icon.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(EmojiHandler.getInstance().getEmoji("apple").getHex())));
					break;
				case "activity":
					icon.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(EmojiHandler.getInstance().getEmoji("soccer").getHex())));
					break;
				case "travel":
					icon.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(EmojiHandler.getInstance().getEmoji("airplane").getHex())));
					break;
				case "objects":
					icon.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(EmojiHandler.getInstance().getEmoji("bulb").getHex())));
					break;
				case "symbols":
					icon.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(EmojiHandler.getInstance().getEmoji("atom").getHex())));
					break;
				case "flags":
					icon.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(EmojiHandler.getInstance().getEmoji("flag_eg").getHex())));
					break;
			}

			if(icon.getImage() != null) {
				tab.setText("");
				tab.setGraphic(icon);
			}

			tab.setTooltip(new Tooltip(tab.getId()));
			/*tab.selectedProperty().addListener(ee-> {
				if(tab.getGraphic() == null) return;
				if(tab.isSelected()) {
					tab.setText(tab.getId());
				} else {
					tab.setText("");
				}
			});*/
		}


		tabPane.getSelectionModel().select(1);
		//填充Tab
		init();
	}




	private void init() {
		Platform.runLater(() -> {
			Map<String, List<Emoji>> map = EmojiHandler.getInstance().getCategorizedEmojis();
			for (Tab tab : tabPane.getTabs()) {
				ScrollPane scrollPane = (ScrollPane) tab.getContent();
				FlowPane pane = (FlowPane) scrollPane.getContent();
				pane.getChildren().clear();
				String category = tab.getId().toLowerCase();
				if (map.get(category) == null) continue;
				map.get(category).forEach(emoji -> pane.getChildren().add(createEmojiNode(emoji)));
			}
		});
	}

	private Node createEmojiNode(Emoji emoji) {

		Node stackPane = EmojiDisplayer.createEmojiNode(emoji, 32, 3);

		Tooltip tooltip = new Tooltip(emoji.getShortname());
		Tooltip.install(stackPane, tooltip);
		stackPane.setCursor(Cursor.HAND);
		ScaleTransition st = new ScaleTransition(Duration.millis(90), stackPane);

		stackPane.setOnMouseEntered(e-> {
			//stackPane.setStyle("-fx-background-color: #a6a6a6; -fx-background-radius: 3;");
			stackPane.setEffect(new DropShadow());
			st.setToX(1.2);
			st.setToY(1.2);
			st.playFromStart();
			if(txtSearch.getText().isEmpty())
				txtSearch.setPromptText(emoji.getShortname());
		});
		stackPane.setOnMouseExited(e-> {
			//stackPane.setStyle("");
			stackPane.setEffect(null);
			st.setToX(1.);
			st.setToY(1.);
			st.playFromStart();
		});
		// 设置光标的点击事件
		stackPane.setOnMouseClicked(e -> {
			// 获得emoji简称
			String shortname = emoji.getShortname();
			app.emojiToText(shortname);
		});
		return stackPane;
	}

	private String getEmojiImagePath(String hexStr) {
		//如果是绝对路径，则绝对路径的起点为ClassPath，如果为相对路径，则路径的起点为该类的所在目录
		return getClass().getResource("/png_40/" + hexStr + ".png").toExternalForm();
	}


}
