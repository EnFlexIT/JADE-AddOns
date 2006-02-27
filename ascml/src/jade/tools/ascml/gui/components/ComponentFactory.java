package jade.tools.ascml.gui.components;

import jade.tools.ascml.repository.loader.ImageIconLoader;

import javax.swing.*;
import java.awt.*;

/**
 * 
 */
public class ComponentFactory
{
	public static JButton createApplyButton(String buttonText)
	{
		JButton button = new JButton(buttonText, ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_APPLY, 16, 16));
		button.setMargin(new Insets(1,1,1,1));
		button.setPreferredSize(new Dimension(145,22));
		button.setMinimumSize(new Dimension(145,22));
		button.setMaximumSize(new Dimension(145,22));
		return button;
	}

	public static JButton createStartButton(String buttonText)
	{
		JButton button = new JButton(buttonText, ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_START, 16, 16));
		button.setMargin(new Insets(1,1,1,1));
		button.setPreferredSize(new Dimension(170,22));
		button.setMinimumSize(new Dimension(170,22));
		button.setMaximumSize(new Dimension(170,22));
		return button;
	}

	public static JButton createSaveButton(String buttonText)
	{
		JButton button = new JButton(buttonText, ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_SAVE, 16, 16));
		button.setMargin(new Insets(1,1,1,1));
		button.setPreferredSize(new Dimension(170,22));
		button.setMinimumSize(new Dimension(170,22));
		button.setMaximumSize(new Dimension(170,22));
		return button;
	}

	public static JButton createNewButton(String buttonText)
	{
		JButton button = new JButton(buttonText, ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_SAVE, 16, 16));
		button.setMargin(new Insets(1,1,1,1));
		button.setPreferredSize(new Dimension(170,22));
		button.setMinimumSize(new Dimension(170,22));
		button.setMaximumSize(new Dimension(170,22));
		return button;
	}

	public static JButton createChangeIconButton()
	{
		JButton button = new JButton("Change Icon");
		button.setMargin(new Insets(1,1,1,1));
		button.setPreferredSize(new Dimension(90,18));
		button.setMinimumSize(new Dimension(90,18));
		button.setMaximumSize(new Dimension(90,18));
		return button;
	}

	public static JButton createThreePointButton()
	{
		JButton button = new JButton("...");
		button.setMargin(new Insets(1,3,1,3));
		button.setPreferredSize(new Dimension(30,20));
		button.setMinimumSize(new Dimension(30,20));
		button.setMaximumSize(new Dimension(30,20));
		return button;
	}

	public static JButton createAddButton(String buttonText)
	{
        JButton button = new JButton(buttonText, ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_ADD, 16, 16));
		button.setMargin(new Insets(1,1,1,1));
		button.setPreferredSize(new Dimension(110,20));
		button.setMinimumSize(new Dimension(110,20));
		button.setMaximumSize(new Dimension(110,20));
		return button;
	}

	public static JButton createRemoveButton(String buttonText)
	{
        JButton button = new JButton(buttonText, ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_REMOVE, 16, 16));
		button.setMargin(new Insets(1,1,1,1));
		button.setPreferredSize(new Dimension(110,20));
		button.setMinimumSize(new Dimension(110,20));
		button.setMaximumSize(new Dimension(110,20));
		return button;
	}

	public static JButton createEditButton(String buttonText)
	{
        JButton button = new JButton(buttonText, ImageIconLoader.createImageIcon(ImageIconLoader.BUTTON_EDIT, 16, 16));
		button.setMargin(new Insets(1,1,1,1));
		button.setPreferredSize(new Dimension(110,20));
		button.setMinimumSize(new Dimension(110,20));
		button.setMaximumSize(new Dimension(110,20));
		return button;
	}

	public static JScrollPane createTextAreaScrollPane(JTextArea textArea)
	{
		textArea.setFont(new Font("Arial", Font.PLAIN, 12));
		textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		textArea.setPreferredSize(new Dimension((int)textArea.getPreferredSize().getWidth(), 60));
		textArea.setMinimumSize(new Dimension((int)textArea.getPreferredSize().getWidth(), 60));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBackground(Color.WHITE);

		// put the textarea into a scrollpane
		JScrollPane textDescScrollPane = new JScrollPane(textArea);
		textDescScrollPane.getViewport().setBackground(Color.WHITE);
		textDescScrollPane.setPreferredSize(new Dimension((int)textArea.getPreferredSize().getWidth(), 60));
		textDescScrollPane.setMinimumSize(new Dimension((int)textArea.getPreferredSize().getWidth(), 60));

		return textDescScrollPane;
	}

	public static JSpinner createQuantitySpinner(long quantity)
	{
		JSpinner spinnerQuantity = new JSpinner(new SpinnerNumberModel(quantity, 0, 10000, 1));
		spinnerQuantity.setPreferredSize(new Dimension(60, (int)spinnerQuantity.getPreferredSize().getHeight()));
		spinnerQuantity.setMinimumSize(new Dimension(60, (int)spinnerQuantity.getPreferredSize().getHeight()));
		spinnerQuantity.setMaximumSize(new Dimension(60, (int)spinnerQuantity.getPreferredSize().getHeight()));
		spinnerQuantity.setBackground(Color.WHITE);
		return spinnerQuantity;
	}

	public static JScrollPane createTableScrollPane(JTable table)
	{
		table.setPreferredSize(new Dimension(200, 200));
		table.setMinimumSize(new Dimension(200, (int)table.getPreferredSize().getHeight()));
		table.setMaximumSize(new Dimension(200, (int)table.getPreferredSize().getHeight()));
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panel.setPreferredSize(new Dimension(200, 200));
		panel.setMinimumSize(new Dimension(200, (int)panel.getPreferredSize().getHeight()));
		panel.setMaximumSize(new Dimension(200, (int)panel.getPreferredSize().getHeight()));
		panel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		panel.add(table, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setWheelScrollingEnabled(true);
		// agentInstanceScrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setPreferredSize(new Dimension(200, 200));
		scrollPane.setMinimumSize(new Dimension(200, (int)scrollPane.getPreferredSize().getHeight()));
		scrollPane.setMaximumSize(new Dimension(200, (int)scrollPane.getPreferredSize().getHeight()));

		return scrollPane;
	}

	public static JScrollPane createListScrollPane(JList list)
	{
		list.setBackground(Color.WHITE);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);

		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.getViewport().setBackground(Color.WHITE);
		listScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		listScrollPane.setPreferredSize(new Dimension((int)listScrollPane.getPreferredSize().getWidth(), 60));
		listScrollPane.setMinimumSize(new Dimension((int)listScrollPane.getPreferredSize().getWidth(), 60));

		return listScrollPane;
	}
}