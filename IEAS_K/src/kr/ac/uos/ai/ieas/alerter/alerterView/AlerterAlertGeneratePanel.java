package kr.ac.uos.ai.ieas.alerter.alerterView;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kr.ac.uos.ai.ieas.alerter.alerterController.AleterViewActionListener;
import kr.ac.uos.ai.ieas.resource.KieasMessageBuilder;
import kr.ac.uos.ai.ieas.resource.KieasMessageBuilder.Item;

public class AlerterAlertGeneratePanel {

	private static final String NEW_ALERT_BUTTON = null;

	private static final String ORGANIZATION_NAME = null;
	private static final String USER_NAME = null;


	public static final String TEXT_AREA = "TextArea";
	public static final String TEXT_FIELD = "TextField";
	public static final String COMBO_BOX = "ComboBox";

	private static final int BASE_LINE = 100;

	private static AlerterAlertGeneratePanel alerterAlertGeneratePanel;
	private AleterViewActionListener alerterActionListener;	
	private KieasMessageBuilder kieasMessageBuilder;
	
	private Vector<Object> mViewComponents;
	private HashMap<String, Component> panelComponenets;
	private JPanel alertGeneratePanel;
	private JScrollPane alertGenerateScrollPanel;

	private JPanel buttonPane;

	private JButton newAlertButton;

	private HashMap<Object, Object> alertComponents;


	

	public static AlerterAlertGeneratePanel getInstance(AleterViewActionListener alerterActionListener)
	{
		if (alerterAlertGeneratePanel == null)
		{
			alerterAlertGeneratePanel = new AlerterAlertGeneratePanel(alerterActionListener);
		}
		return alerterAlertGeneratePanel;
	}

	
	private AlerterAlertGeneratePanel(AleterViewActionListener alerterActionListener)
	{
		this.alerterActionListener = alerterActionListener;
		this.kieasMessageBuilder = new KieasMessageBuilder();
		
		initMainPanel();
	}

	private void initMainPanel()
	{		
		this.mViewComponents = new Vector<>();		
		this.panelComponenets = new HashMap<>();		
		this.alertGeneratePanel = new JPanel();
		alertGeneratePanel.setLayout(new BoxLayout(alertGeneratePanel, BoxLayout.Y_AXIS));

		alertGeneratePanel.add(initButtonPanel());
		alertGeneratePanel.add(initAlertGeneratePanel());
		
		mViewComponents.addElement(panelComponenets);

		this.alertGenerateScrollPanel = new JScrollPane(alertGeneratePanel);
	}
	
	private Component initButtonPanel()
	{
		this.buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));

		Box box = Box.createHorizontalBox();		
		this.newAlertButton = createButton(NEW_ALERT_BUTTON);
		box.add(newAlertButton);		
		
		JComboBox patternList = new JComboBox();
		patternList.setEditable(true);
		patternList.addActionListener(alerterActionListener);		
		box.setBorder(BorderFactory.createLoweredBevelBorder());
		buttonPane.add(box);

		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		return buttonPane;
	}
	
	private JPanel initAlertGeneratePanel()
	{
		this.alertGeneratePanel = new JPanel();
		alertGeneratePanel.setLayout(new BoxLayout(alertGeneratePanel, BoxLayout.Y_AXIS));
		
		this.alertComponents = new HashMap<>();

		alertGeneratePanel.add(addBox(ORGANIZATION_NAME, TEXT_FIELD));
		alertGeneratePanel.add(addBox(USER_NAME, TEXT_FIELD));
		alertGeneratePanel.setBorder(BorderFactory.createTitledBorder("경보메시지"));
		
		return alertGeneratePanel;
	}
	
	private Box addBox(String labelName, String type)
	{
		Box box = Box.createHorizontalBox();

		JLabel label = new JLabel(labelName);
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		int offset = (int) (BASE_LINE - label.getPreferredSize().getWidth());		
		box.add(Box.createRigidArea(new Dimension(offset, 0)));
		
		box.add(label);

		switch (type)
		{
		case COMBO_BOX:
			Vector<Item> comboboxModel = new Vector<>();
			for (Item value : kieasMessageBuilder.getCapEnumMap().get(labelName))
			{
				comboboxModel.addElement(value);
			}
			JComboBox<Item> comboBox = new JComboBox<>(comboboxModel);
			alertComponents.put(labelName, comboBox);
			box.add(comboBox);
			return box;			
		case TEXT_FIELD:
			JTextField textField = new JTextField();
			alertComponents.put(labelName, textField);
			box.add(textField);
			return box;
		case TEXT_AREA:
			JTextArea textArea = new JTextArea();
			alertComponents.put(labelName, textArea);
			box.add(textArea);
			return box;
		default:
			return box;
		}
	}


	private JButton createButton(String name)
	{
		JButton button = new JButton(name);
		button.addActionListener(alerterActionListener);
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		return button;
	}


	public Component getPanel()
	{
		return alertGenerateScrollPanel;
	}
}
