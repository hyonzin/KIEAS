package kr.or.kpew.kieas.issuer.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import kr.or.kpew.kieas.common.IKieasMessageBuilder;
import kr.or.kpew.kieas.common.Item;
import kr.or.kpew.kieas.common.KieasMessageBuilder;
import kr.or.kpew.kieas.common.KieasMessageBuilder.AlertElementNames;
import kr.or.kpew.kieas.common.Profile.AlertSystemType;
import kr.or.kpew.kieas.issuer.controller.IssuerController;

public class SensorPanel {

	private static final int BASE_LINE = 100;
	private static final String TITLE = "Sensor";
	
	private static final String ADD = "+";	
	private static final String INFO_ADDER_BUTTON = "Info Add";
	private static final String RESOURCE_ADDER_BUTTON = "ResourceAdder";
	private static final String AREA_ADDER_BUTTON = "AreaAdder";	
	
	private IKieasMessageBuilder kieasMessageBuilder;
	
	private JComponent mainPanel;
	
	private Map<String, JComponent> alertComponentMap;
	private List<Map<String, JComponent>> infoComponentMaps;
	private List<JButton> buttons;

	private IssuerController controller;
	
	JSlider framesPerSecond = null;
	JSpinner spinner = null;
	
	
	public SensorPanel()
	{
		init();
	}

	private void init()
	{
		this.kieasMessageBuilder = new KieasMessageBuilder();
		this.buttons = new ArrayList<>();
		
		this.mainPanel = createCapAlertPanel();
		
	}
	
	public JComponent createCapAlertPanel()
	{
		
		Box capElementPanel = Box.createVerticalBox();
		capElementPanel.setBorder(BorderFactory.createTitledBorder(TITLE));
		
		this.alertComponentMap = new HashMap<>();
		
//		capElementPanel.add(addBox(AlertElementNames.Identifier.toString(), IssuerView.TEXT_FIELD, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.Sender.toString(), IssuerView.TEXT_FIELD, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.Sent.toString(), IssuerView.TEXT_FIELD, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.Status.toString(), IssuerView.COMBO_BOX, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.MsgType.toString(), IssuerView.COMBO_BOX, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.Source.toString(), IssuerView.TEXT_FIELD, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.Scope.toString(), IssuerView.COMBO_BOX, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.Restriction.toString(), IssuerView.COMBO_BOX, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.Addresses.toString(), IssuerView.TEXT_FIELD, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.Code.toString(), IssuerView.TEXT_FIELD, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.Note.toString(), IssuerView.TEXT_FIELD, alertComponentMap));
//		capElementPanel.add(addBox(AlertElementNames.References.toString(), IssuerView.TEXT_FIELD, alertComponentMap));	
	
		framesPerSecond = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
		//Turn on labels at major tick marks.
		framesPerSecond.setName("hi");
		framesPerSecond.setMajorTickSpacing(10);
		framesPerSecond.setMinorTickSpacing(1);
		framesPerSecond.setPaintTicks(true);
		framesPerSecond.setPaintLabels(true);
		framesPerSecond.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					int val = (int)framesPerSecond.getValue();
					spinner.setValue(val);
					CapElementPanel.checkReservation(val);
				} catch (Exception exception) {
					//nothing to do.
				}
			}
		});
		
		SpinnerModel spinnerModel =
		        new SpinnerNumberModel(   0,  // initial value
		                               -100,  // min
		                                100,  // max
		                                  1); // step
		spinner = new JSpinner(spinnerModel);
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					int val = (int)spinner.getValue();
					framesPerSecond.setValue(val);
					CapElementPanel.checkReservation(val);
				} catch (Exception exception) {
					//nothing to do.
				}
			}
		});
		
		JPanel panelSpinner = new JPanel();
		panelSpinner.add(new JLabel("온도"));
		panelSpinner.add(spinner);

		capElementPanel.add(framesPerSecond);
		capElementPanel.add(panelSpinner);
		addController(controller);
		
		this.mainPanel = capElementPanel;
		
		return capElementPanel;
	}
	
	public void setCapAlertPanel(String capMessage)
	{
		kieasMessageBuilder.parse(capMessage);
		setCapElement(AlertElementNames.Identifier, kieasMessageBuilder.getIdentifier());
		setCapElement(AlertElementNames.Sender, kieasMessageBuilder.getSender());
		setCapElement(AlertElementNames.Sent, kieasMessageBuilder.getSent());
		setCapElement(AlertElementNames.Status, kieasMessageBuilder.getStatus().toString());
		setCapElement(AlertElementNames.MsgType, kieasMessageBuilder.getMsgType().toString());
		setCapElement(AlertElementNames.Source, kieasMessageBuilder.getSource().toString());
		setCapElement(AlertElementNames.Scope, kieasMessageBuilder.getScope().toString());
		setCapElement(AlertElementNames.Restriction, kieasMessageBuilder.getRestriction().toString());
		if(kieasMessageBuilder.getAddresses() != null && kieasMessageBuilder.getAddresses().size() > 0)
		{
			setCapElement(AlertElementNames.Addresses, kieasMessageBuilder.getAddresses().get(0).toString());			
		}
		setCapElement(AlertElementNames.Code, kieasMessageBuilder.getCode());	
		setCapElement(AlertElementNames.Note, kieasMessageBuilder.getNote());	
		setCapElement(AlertElementNames.References, kieasMessageBuilder.getReferences());	

	}
	
	private JButton createButton(String name, JComponent component)
	{
		JButton button = new JButton(name);
		
		switch (name)
		{
		case INFO_ADDER_BUTTON:
			button.setText(INFO_ADDER_BUTTON);
			break;
		case RESOURCE_ADDER_BUTTON:
			button.setText(ADD);
			break;
		case AREA_ADDER_BUTTON:
			button.setText(ADD);
			break;
		default:
			break;
		}
		
		buttons.add(button);

		return button;
	}

	private Box addBox(String labelName, String type, Map<String, JComponent> componentMap)
	{
		Box box = Box.createHorizontalBox();
		JLabel label = new JLabel(labelName);
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		int offset = (int) (BASE_LINE - label.getPreferredSize().getWidth());		
		box.add(Box.createRigidArea(new Dimension(offset, 0)));
		
		box.add(label);
		switch (type)
		{
		case IssuerView.COMBO_BOX:
			if (AlertElementNames.Restriction.toString().equals(labelName))
			{
				Vector<String> comboboxModel = new Vector<>();
				for (AlertSystemType systemtype : AlertSystemType.values())
				{
					comboboxModel.addElement(systemtype.name());

				}
				JComboBox<String> comboBox = new JComboBox<>(comboboxModel);
				alertComponentMap.put(labelName, comboBox);
				box.add(comboBox);
			}
			else
			{
				Vector<Item> comboboxModel = new Vector<>();
				for (Item value : kieasMessageBuilder.getCapEnumMap().get(AlertElementNames.valueOf(labelName)))
				{
					comboboxModel.addElement(value);
				}
				JComboBox<Item> comboBox = new JComboBox<>(comboboxModel);
				alertComponentMap.put(labelName, comboBox);
				box.add(comboBox);					
			}
			return box;			
		case IssuerView.TEXT_FIELD:
			JTextField textField = new JTextField();
			alertComponentMap.put(labelName, textField);
			box.add(textField);
			return box;
		case IssuerView.TEXT_AREA:
			JTextArea textArea = new JTextArea();
			alertComponentMap.put(labelName, textArea);
			box.add(textArea);
			return box;
		default:
			System.out.println("AO: Fail to add Box");
			return box;
		}
	}	

	@SuppressWarnings("rawtypes")
	public void setCapElement( Enum e, String value)
	{
		String target = e.toString();
		Object object = alertComponentMap.get(target);
		if (object instanceof JTextField)
		{
			((JTextField) object).setText(value);
			return;
		}
		if (object instanceof JComboBox<?>)
		{
			for(int i = 0; i < ((JComboBox<?>) object).getItemCount(); i++)
			{
				Object comboBoxItemObject = ((JComboBox<?>) object).getItemAt(i);
				if(comboBoxItemObject instanceof Item && ((Item) comboBoxItemObject).getKey().equals(value))
				{
					((JComboBox) object).setSelectedIndex(i);
					return;
				}
				else if(comboBoxItemObject instanceof String && comboBoxItemObject.equals(value))
				{
					((JComboBox) object).setSelectedIndex(i);
					return;
				}
			}
		}
		
		for(int j = 0; j < infoComponentMaps.size() ; j++)
		{
			Map<String, JComponent> infoComponentMap = infoComponentMaps.get(j);
			
			if (infoComponentMap.get(target) instanceof JTextField)
			{
				((JTextField) infoComponentMap.get(target)).setText(value);
				return;
			}
			if (infoComponentMap.get(target) instanceof JComboBox<?>)
			{
				for(int i = 0; i < ((JComboBox<?>) infoComponentMap.get(target)).getItemCount(); i++)
				{
					if((((Item) ((JComboBox<?>) infoComponentMap.get(target)).getItemAt(i)).getKey()).equals(value))
					{
						((JComboBox<?>) infoComponentMap.get(target)).setSelectedIndex(i);
						return;
					}
				}
			}
			if (infoComponentMap.get(target) instanceof JTextArea)
			{
				((JTextArea) infoComponentMap.get(target)).setText(value);
				return;
			}
		}
	}
	
	public String getCapElement()
	{
		for (AlertElementNames alertElement : AlertElementNames.values())
		{
			String key = alertElement.toString();
			if(alertComponentMap.containsKey(key))
			{
				String value = null;
				Object object = alertComponentMap.get(key);
				
				if(object instanceof JComboBox<?>)
				{
					Object comboBoxItemObject = ((JComboBox<?>) object).getSelectedItem();
					if(comboBoxItemObject instanceof Item)
					{
						value = ((Item) comboBoxItemObject).getKey();						
					}
					else if(comboBoxItemObject instanceof String)
					{
						value = comboBoxItemObject.toString();
					}
				}
				else if(object instanceof JTextField)
				{
					value = ((JTextField) object).getText();
				}
				else if(object instanceof JTextArea)
				{
					value = ((JTextArea) object).getText();
				}
				
				String methodName = "set" + key;
				try
				{
					if(value.trim().length() != 0)
					{
						Method method = kieasMessageBuilder.getClass().getMethod(methodName.trim(), new String().getClass());
						method.invoke(kieasMessageBuilder, value);
					}			
				}
				catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					e.printStackTrace();
				}
				catch (NoSuchMethodException ex)
				{
					System.out.println("AO: there is no such a method name : " + methodName);
					continue;
				}			
			}			
		}
		return kieasMessageBuilder.build();
	}
	
	public void addController(IssuerController controller)
	{
		this.controller = controller;
		for (JButton button : buttons)
		{
			button.addActionListener(controller);
		}
	}
	
	public void removeController(IssuerController controller)
	{
		for (JButton button : buttons)
		{
			button.removeActionListener(controller);
		}
	}
	
	public JComponent getPanel()
	{
		return mainPanel;		
	}

	public void setIdentifier(String message)
	{
		kieasMessageBuilder.parse(message);		
		String identifier = kieasMessageBuilder.getIdentifier();
		((JTextField) alertComponentMap.get(AlertElementNames.Identifier.toString())).setText(identifier);
	}
}
