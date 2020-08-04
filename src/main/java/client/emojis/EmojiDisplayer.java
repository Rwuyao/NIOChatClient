package client.emojis;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.Queue;

/**
 *
 * Emoji显示器：
 * ①为emoji实体集创建emoji图片节点，
 * ②为包含emoji的字符串分离并创建emoji图片节点和文本节点。
 * @Title: EmojiParser.java
 */
public class EmojiDisplayer {
	/**
	 * 将字符串解析分离成emoji图片节点和文本节点
	 * @param input 输入字符串
	 * @return
	 */
	public static Node[] createEmojiAndTextNode(String input) {
		Queue<Object> queue = EmojiHandler.getInstance().toEmojiAndText(input);
		Node[] nodes = new Node[queue.size()];
		int i = 0;
		while (!queue.isEmpty()) {
			Object ob = queue.poll();
			if (ob instanceof String) {
				String text = (String) ob;
				nodes[i++] = createTextNode(text);
			} else if (ob instanceof Emoji) {
				Emoji emoji = (Emoji) ob;
				nodes[i++] = createEmojiNode(emoji, 24, 0);
			}
		}
		return nodes;
	}

	/**
	 * 创建emoji图片节点
	 *
	 * @param emoji
	 *            emoji
	 * @param size
	 *            图片显示大小
	 * @param pad
	 *            图片间距
	 * @return
	 */
	public static Node createEmojiNode(Emoji emoji, int size, int pad) {
		// 将表情放到stackpane中
		StackPane stackPane = new StackPane();
		stackPane.setPrefSize(size, size);
		stackPane.setPadding( new Insets(0,0,0,0));
		ImageView imageView = new ImageView();
		imageView.setFitWidth(size);
		imageView.setFitHeight(size);
		imageView.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(emoji.getHex())));
		stackPane.getChildren().add(imageView);
		stackPane.setAlignment(Pos.CENTER);
		return stackPane;
	}

	/**
	 * 创建文本节点
	 *
	 * @param text
	 * @return
	 */
	private static Node createTextNode(String text) {

		Text textNode = new Text(text);
		textNode.setStyle("  -fx-font-size: 12pt;" +
				"-fx-font-family: \"微软雅黑\";" );
		return textNode;
	}

	/**
	 * 通过emoji的hex得到表情的路径
	 *
	 * @param hexStr
	 * @return
	 */
	private static String getEmojiImagePath(String hexStr) {
		return "/png_40/" + hexStr + ".png";
	}
}
